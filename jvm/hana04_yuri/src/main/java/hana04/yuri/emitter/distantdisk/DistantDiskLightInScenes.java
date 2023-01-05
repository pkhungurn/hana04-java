package hana04.yuri.emitter.distantdisk;

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

public class DistantDiskLightInScenes {
  @HanaDeclareBuilder(DistantDiskLightInScene.class)
  public static class DistantDiskLightInSceneBuilder
      extends DistantDiskLightInScene__Impl__Builder<DistantDiskLightInSceneBuilder> {
    @Inject
    public DistantDiskLightInSceneBuilder(DistantDiskLightInScene__ImplFactory factory) {
      super(factory);
    }

    public static DistantDiskLightInSceneBuilder builder(Component component) {
      return component.uberFactory().create(DistantDiskLightInSceneBuilder.class);
    }
  }

  public static class EmitterSamplerRgbVv
      extends DerivedVersionedValue<EmitterSamplerRgb>
      implements EmitterSamplerRgb.Vv {
    @HanaDeclareExtension(
        extensibleClass = DistantDiskLightInScene.class,
        extensionClass = EmitterSamplerRgb.Vv.class)
    EmitterSamplerRgbVv(DistantDiskLightInScene light) {
      super(
          ImmutableList.of(
              light.emitter().radiance(),
              light.emitter().thetaA(),
              light.emitter().toWorld()),
          ChangePropUtil::largestBetweenIncSelfAndDeps,
          () -> new DistantDiskLightSampler.ForRgb(light.emitter()));
    }
  }

  public static class PatchIntervalProvider_ implements EmitterInScenes.PatchIntervalProvider {
    @HanaDeclareExtension(
        extensibleClass = DistantDiskLightInScene.class,
        extensionClass = EmitterInScenes.PatchIntervalProvider.class)
    public PatchIntervalProvider_(DistantDiskLightInScene instance) {
      // NO-OP
    }

    @Override
    public Optional<PatchInterval> getPatchInterval() {
      return Optional.empty();
    }
  }
}
