package hana04.yuri.integrand.pathtracer;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.Constant;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.gfxbase.spectrum.rgb.RgbSpace;
import hana04.yuri.bsdf.shader.AmbientLightShader;
import hana04.yuri.bsdf.specspaces.BsdfEvaluatorRgb;
import hana04.yuri.bsdf.specspaces.BsdfSamplerRgb;
import hana04.yuri.emitter.specspaces.EmitterSamplerRgb;
import hana04.yuri.film.recorder.RgbFilmRecorder;
import hana04.yuri.integrand.ScenePreparer;
import hana04.yuri.integrand.SensorIntegrandEvaluator;
import hana04.yuri.scene.SceneRayIntersector;
import hana04.yuri.scene.specspaces.SceneEmitterSamplerRgb;
import hana04.yuri.scene.specspaces.SceneSurfaceShadingRgb;

import javax.inject.Inject;

public class PathTracerRgbs {
  public static class ScenePreparerVv extends Constant<ScenePreparer> implements ScenePreparer.Vv {
    @HanaDeclareExtension(
        extensibleClass = PathTracerRgb.class,
        extensionClass = ScenePreparer.Vv.class)
    ScenePreparerVv(PathTracerRgb integrand) {
      super(scene -> {
        scene.prepareExtension(SceneRayIntersector.class);
        scene.prepareExtension(SceneSurfaceShadingRgb.class);
        scene.prepareExtension(SceneEmitterSamplerRgb.class);
      });
    }
  }

  public static class EvaluatorVv
      extends DerivedVersionedValue<SensorIntegrandEvaluator>
      implements SensorIntegrandEvaluator.Vv {

    @HanaDeclareExtension(
        extensibleClass = PathTracerRgb.class,
        extensionClass = SensorIntegrandEvaluator.Vv.class)
    EvaluatorVv(PathTracerRgb pathTracer) {
      super(PathTracers.allParameters(pathTracer), ChangePropUtil::largestBetweenIncSelfAndDeps,
          () -> new PathTracerEvaluator<>(
              pathTracer,
              RgbSpace.I,
              RgbFilmRecorder.class,
              BsdfEvaluatorRgb.Vv.class,
              BsdfSamplerRgb.Vv.class,
              EmitterSamplerRgb.Vv.class,
              SceneEmitterSamplerRgb.class,
              AmbientLightShader.ForRgb.Vv.class));
    }
  }

  @HanaDeclareBuilder(PathTracerRgb.class)
  public static class PathTracerRgbBuilder extends PathTracerRgb__Impl__Builder<PathTracerRgbBuilder> {
    @Inject
    public PathTracerRgbBuilder(PathTracerRgb__ImplFactory factory) {
      super(factory);
      terminationProb(0.01);
      computeAmbientIllumination(false);
    }

    public static PathTracerRgbBuilder builder(Component component) {
      return component.uberFactory().create(PathTracerRgbBuilder.class);
    }
  }
}
