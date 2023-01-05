package hana04.yuri.emitter.sumenv;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.Constant;
import hana04.base.changeprop.VersionedValue;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.changeprop.util.VvListVvReduce;
import hana04.shakuyaku.emitter.Emitter;
import hana04.shakuyaku.emitter.sumenv.SumEnvironmentalLight;
import hana04.yuri.emitter.EmitterInSceneFactory;
import hana04.yuri.util.DiscretePdf;

import javax.inject.Provider;
import java.util.List;
import java.util.stream.Collectors;

public class SumEnvironmentalLights {
  public static class EmitterInSceneFactoryVv
      extends Constant<EmitterInSceneFactory>
      implements EmitterInSceneFactory.Vv {
    @HanaDeclareExtension(
        extensibleClass = SumEnvironmentalLight.class,
        extensionClass = EmitterInSceneFactory.Vv.class)
    EmitterInSceneFactoryVv(
        SumEnvironmentalLight light,
        Provider<SumEnvironmentalLightInScenes.SumEnvironmentalLightInSceneBuilder> emitterInSceneBuilder) {
      super((scene, patchInterval) ->
          emitterInSceneBuilder.get().emitter(light).scene(scene).build());
    }
  }

  public static class SamplingWeightListVv
      extends DerivedVersionedValue<List<VersionedValue<Double>>> {
    @HanaDeclareExtension(
        extensibleClass = SumEnvironmentalLight.class,
        extensionClass = SamplingWeightListVv.class)
    SamplingWeightListVv(SumEnvironmentalLight light) {
      super(
          ImmutableList.of(light.getExtension(hana04.shakuyaku.emitter.sumenv.SumEnvironmentalLights.UnwrappedEmittersVv.class)),
          ChangePropUtil::largestBetweenIncSelfAndDeps,
          () -> light
              .getExtension(hana04.shakuyaku.emitter.sumenv.SumEnvironmentalLights.UnwrappedEmittersVv.class).value().stream()
              .map(Emitter::samplingWeight)
              .collect(Collectors.toList()));
    }
  }

  public static class DiscretePdfVv
      extends VvListVvReduce<Double, DiscretePdf> {
    @HanaDeclareExtension(
        extensibleClass = SumEnvironmentalLight.class,
        extensionClass = DiscretePdfVv.class)
    DiscretePdfVv(SumEnvironmentalLight light) {
      super(
          light.getExtension(SamplingWeightListVv.class),
          samplingWeights -> {
            DiscretePdf.Builder builder = DiscretePdf.builder();
            for (VersionedValue<? extends Double> weight : samplingWeights) {
              builder.add(weight.value());
            }
            return builder.build();
          }
      );
    }
  }
}
