package hana04.shakuyaku.emitter.sumenv;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.shakuyaku.emitter.Emitter;
import hana04.shakuyaku.emitter.EmitterType;
import hana04.shakuyaku.emitter.Emitters;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class SumEnvironmentalLights {

  public static class UnwrappedEmittersVv extends DerivedVersionedValue<List<Emitter>> {
    @HanaDeclareExtension(
        extensibleClass = SumEnvironmentalLight.class,
        extensionClass = UnwrappedEmittersVv.class)
    UnwrappedEmittersVv(SumEnvironmentalLight light, HanaUnwrapper unwrapper) {
      super(
          ImmutableList.of(light.emitters()),
          ChangePropUtil::largestBetweenIncSelfAndDeps,
          () -> light.emitters().value().stream().map(unwrapper::unwrap).collect(Collectors.toList()));
    }
  }

  @HanaDeclareBuilder(SumEnvironmentalLight.class)
  public static class SumEnvironmentalLightBuilder
      extends SumEnvironmentalLight__Impl__Builder<SumEnvironmentalLightBuilder> {
    @Inject
    public SumEnvironmentalLightBuilder(SumEnvironmentalLight__ImplFactory factory) {
      super(factory);
      samplingWeight(1.0);
    }

    public static SumEnvironmentalLightBuilder builder(Component component) {
      return component.uberFactory().create(SumEnvironmentalLightBuilder.class);
    }
  }

  public static class SumEnvironmentalLightInfo implements Emitters.EmitterInfo {
    @HanaDeclareExtension(
        extensibleClass = SumEnvironmentalLight.class,
        extensionClass = Emitters.EmitterInfo.class)
    public SumEnvironmentalLightInfo(SumEnvironmentalLight instance) {
      // NO-OP
    }

    @Override
    public EmitterType getType() {
      return EmitterType.Environmental;
    }
  }
}
