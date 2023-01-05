package hana04.yuri.emitter.area;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.yuri.emitter.EmitterInScenes;
import hana04.yuri.emitter.specspaces.EmitterSamplerRgb;
import hana04.shakuyaku.surface.PatchInterval;
import hana04.yuri.surface.PatchIntervalPointSampler;

import javax.inject.Inject;
import java.util.Optional;

public class AreaEmitterInScenes {
  public static class EmitterSamplerRgbVv
      extends DerivedVersionedValue<EmitterSamplerRgb>
      implements EmitterSamplerRgb.Vv {
    @HanaDeclareExtension(
        extensibleClass = AreaEmitterInScene.class,
        extensionClass = EmitterSamplerRgb.Vv.class)
    EmitterSamplerRgbVv(AreaEmitterInScene light) {
      super(
          ImmutableList.of(
              light.emitter().radiance(),
              light.getExtension(EmitterInScenes.PatchIntervalProvider.class)
                  .getPatchInterval()
                  .orElseThrow()
                  .getExtension(PatchIntervalPointSampler.Vv.class)),
          ChangePropUtil::largestBetweenIncSelfAndDeps,
          () -> new AreaEmitterSampler.ForRgb(
              light.getExtension(EmitterInScenes.PatchIntervalProvider.class)
                  .getPatchInterval().orElseThrow()
                  .getExtension(PatchIntervalPointSampler.Vv.class)
                  .value(),
              light.emitter().radiance().value()));
    }
  }

  @HanaDeclareBuilder(AreaEmitterInScene.class)
  public static class AreaEmitterInSceneBuilder extends AreaEmitterInScene__Impl__Builder<AreaEmitterInSceneBuilder> {
    @Inject
    public AreaEmitterInSceneBuilder(AreaEmitterInScene__ImplFactory factory) {
      super(factory);
    }

    public static AreaEmitterInSceneBuilder builder(Component component) {
      return component.uberFactory().create(AreaEmitterInSceneBuilder.class);
    }
  }

  public static class PatchIntervalProvider_ implements EmitterInScenes.PatchIntervalProvider {
    private final AreaEmitterInScene instance;

    @HanaDeclareExtension(
        extensibleClass = AreaEmitterInScene.class,
        extensionClass = EmitterInScenes.PatchIntervalProvider.class)
    PatchIntervalProvider_(AreaEmitterInScene instance) {
      this.instance = instance;
    }

    @Override
    public Optional<PatchInterval> getPatchInterval() {
      return instance.patchInterval();
    }
  }
}
