package hana04.yuri.integrand.position;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.Constant;
import hana04.base.changeprop.util.DerivedVersionedValue;
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

import javax.vecmath.Vector3d;
import java.util.Optional;

public class SurfacePositionIntegrandExtensions {
  public static class ScenePreparerVv extends Constant<ScenePreparer> implements ScenePreparer.Vv {
    @HanaDeclareExtension(
      extensibleClass = SurfacePositionIntegrand.class,
      extensionClass = ScenePreparer.Vv.class)
    ScenePreparerVv(SurfacePositionIntegrand integrand) {
      super(scene -> {
        scene.prepareExtension(SceneRayIntersector.class);
      });
    }
  }

  public static class Evaluator implements SensorIntegrandEvaluator {
    private double infinityDistance;

    public Evaluator(double infinityDistance) {
      this.infinityDistance = infinityDistance;
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
        Vector3d d = new Vector3d(ray.d);
        d.normalize();
        d.scale(infinityDistance);
        rgbFilmRecorder.record(new Rgb(d.x, d.y, d.z));
      } else {
        Intersection its = surfaceIntersection.get().getRight();
        rgbFilmRecorder.record(new Rgb(its.p));
      }
    }
  }

  public static class EvaluatorVv
    extends DerivedVersionedValue<SensorIntegrandEvaluator>
    implements SensorIntegrandEvaluator.Vv {

    @HanaDeclareExtension(
      extensibleClass = SurfacePositionIntegrand.class,
      extensionClass = SensorIntegrandEvaluator.Vv.class)
    EvaluatorVv(SurfacePositionIntegrand integrand) {
      super(
        ImmutableList.of(integrand.infinityDistance()),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> new Evaluator(integrand.infinityDistance().value()));
    }
  }
}
