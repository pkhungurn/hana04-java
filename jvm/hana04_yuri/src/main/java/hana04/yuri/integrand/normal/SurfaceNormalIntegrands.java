package hana04.yuri.integrand.normal;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.Constant;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.extension.validator.Validator;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.gfxtype.Ray;
import hana04.yuri.film.FilmRecorder;
import hana04.yuri.film.recorder.RgbFilmRecorder;
import hana04.yuri.integrand.ScenePreparer;
import hana04.yuri.integrand.SensorIntegrandEvaluator;
import hana04.yuri.sampler.RandomNumberGenerator;
import hana04.shakuyaku.scene.Scene;
import hana04.yuri.scene.SceneRayIntersector;
import hana04.yuri.surface.Intersection;
import hana04.shakuyaku.surface.Surface;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import javax.vecmath.Vector3d;
import java.util.Optional;

public class SurfaceNormalIntegrands {
  public static class ScenePreparerVv extends Constant<ScenePreparer> implements ScenePreparer.Vv {
    @HanaDeclareExtension(
        extensibleClass = SurfaceNormalIntegrand.class,
        extensionClass = ScenePreparer.Vv.class)
    ScenePreparerVv(SurfaceNormalIntegrand integrand) {
      super(scene -> {
        scene.prepareExtension(SceneRayIntersector.class);
      });
    }
  }

  public static class Evaluator implements SensorIntegrandEvaluator {
    private final boolean showGeometricNormal;

    Evaluator(String normalToRecord) {
      showGeometricNormal = normalToRecord.equals("geometric");
    }

    @Override
    public void eval(Scene scene, Ray ray, RandomNumberGenerator rng, FilmRecorder recorder) {
      if (!(recorder instanceof RgbFilmRecorder)) {
        return;
      }
      RgbFilmRecorder rgbFilmRecorder = (RgbFilmRecorder) recorder;
      SceneRayIntersector intersector = scene.getExtension(SceneRayIntersector.class);
      Optional<Pair<Surface, Intersection>> surfaceIntersection = intersector.rayIntersect(ray);
      if (!surfaceIntersection.isPresent()) {
        return;
      }
      Intersection its = surfaceIntersection.get().getRight();
      Vector3d normal = showGeometricNormal ? its.geoFrame.n : its.shFrame.n;
      rgbFilmRecorder.record(new Rgb(
          (normal.x + 1.0) / 2,
          (normal.y + 1.0) / 2,
          (normal.z + 1.0) / 2));
    }
  }

  public static class EvaluatorVv
      extends DerivedVersionedValue<SensorIntegrandEvaluator>
      implements SensorIntegrandEvaluator.Vv {
    @HanaDeclareExtension(
        extensibleClass = SurfaceNormalIntegrand.class,
        extensionClass = SensorIntegrandEvaluator.Vv.class)
    EvaluatorVv(SurfaceNormalIntegrand integrand) {
      super(
          ImmutableList.of(integrand.normalToRecord()),
          ChangePropUtil::largestBetweenIncSelfAndDeps,
          () -> new Evaluator(integrand.normalToRecord().value()));
    }
  }

  @HanaDeclareBuilder(SurfaceNormalIntegrand.class)
  public static class SurfaceNormalIntegrandBuilder
      extends SurfaceNormalIntegrand__Impl__Builder<SurfaceNormalIntegrandBuilder> {
    @Inject
    public SurfaceNormalIntegrandBuilder(SurfaceNormalIntegrand__ImplFactory factory) {
      super(factory);
      normalToRecord("shading");
    }

    public static SurfaceNormalIntegrandBuilder builder(Component component) {
      return component.uberFactory().create(SurfaceNormalIntegrandBuilder.class);
    }
  }

  public static class SurfaceNormalIntegrandValidator implements Validator {
    private final SurfaceNormalIntegrand instance;

    @HanaDeclareExtension(
        extensibleClass = SurfaceNormalIntegrand.class,
        extensionClass = Validator.class)
    public SurfaceNormalIntegrandValidator(SurfaceNormalIntegrand instance) {
      this.instance = instance;
    }

    @Override
    public void validate() {
      Preconditions.checkState(
          instance.normalToRecord().value().equals("shading")
              || instance.normalToRecord().value().equals("geometric"));
    }
  }
}
