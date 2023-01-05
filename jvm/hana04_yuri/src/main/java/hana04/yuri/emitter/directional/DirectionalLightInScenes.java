package hana04.yuri.emitter.directional;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.yuri.emitter.EmitterInScenes;
import hana04.yuri.emitter.specspaces.EmitterSamplerRgb;
import hana04.shakuyaku.surface.PatchInterval;

import javax.inject.Inject;
import java.util.Optional;

public class DirectionalLightInScenes {
  @HanaDeclareBuilder(DirectionalLightInScene.class)
  public static class DirectionalLightInSceneBuilder
      extends DirectionalLightInScene__Impl__Builder<DirectionalLightInSceneBuilder> {
    @Inject
    public DirectionalLightInSceneBuilder(DirectionalLightInScene__ImplFactory factory) {
      super(factory);
    }

    public static DirectionalLightInSceneBuilder builder(Component component) {
      return component.uberFactory().create(DirectionalLightInSceneBuilder.class);
    }
  }

  public static class EmitterSamplerRgbVv
      extends DerivedVersionedValue<EmitterSamplerRgb>
      implements EmitterSamplerRgb.Vv {
    @HanaDeclareExtension(
        extensibleClass = DirectionalLightInScene.class,
        extensionClass = EmitterSamplerRgb.Vv.class)
    EmitterSamplerRgbVv(DirectionalLightInScene light) {
      super(
          ImmutableList.of(
              light.emitter().radiance(),
              light.emitter().toWorld()),
          ChangePropUtil::largestBetweenIncSelfAndDeps,
          () -> new DirectionalLightSampler.ForRgb(
              light.emitter().radiance().value(),
              light.emitter().toWorld().value()));
    }
  }

  public static class PatchIntervalProvider_ implements EmitterInScenes.PatchIntervalProvider {
    @HanaDeclareExtension(
        extensibleClass = DirectionalLightInScene.class,
        extensionClass = EmitterInScenes.PatchIntervalProvider.class)
    public PatchIntervalProvider_(DirectionalLightInScene instance) {
      // NO-OP
    }

    @Override
    public Optional<PatchInterval> getPatchInterval() {
      return Optional.empty();
    }
  }
}
