package hana04.yuri.integrand.directillum;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.gfxbase.spectrum.rgb.RgbSpace;
import hana04.yuri.bsdf.specspaces.BsdfEvaluatorRgb;
import hana04.yuri.bsdf.specspaces.BsdfSamplerRgb;
import hana04.yuri.emitter.specspaces.EmitterSamplerRgb;
import hana04.yuri.film.recorder.RgbFilmRecorder;
import hana04.yuri.integrand.SensorIntegrandEvaluator;
import hana04.yuri.scene.specspaces.SceneEmitterSamplerRgb;

public class DirectIlluminationIntegrandRgbExtensions {
  public static class EvaluatorVv
    extends DerivedVersionedValue<SensorIntegrandEvaluator>
    implements SensorIntegrandEvaluator.Vv {

    @HanaDeclareExtension(
      extensibleClass = DirectIlluminationIntegrandRgb.class,
      extensionClass = SensorIntegrandEvaluator.Vv.class)
    EvaluatorVv(DirectIlluminationIntegrandRgb integrand) {
      super(
          ImmutableList.of(integrand.strategy()), ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> new DirectIlluminationIntegrandEvaluator<>(
          integrand.strategy().value(),
          RgbSpace.I,
          BsdfEvaluatorRgb.Vv.class,
          BsdfSamplerRgb.Vv.class,
          EmitterSamplerRgb.Vv.class,
          SceneEmitterSamplerRgb.class,
          RgbFilmRecorder.class));
    }
  }
}
