package hana04.yuri.emitter.envmap;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.changeprop.util.VvWrappedToVvAdaptor;
import hana04.yuri.emitter.EmitterInScenes;
import hana04.yuri.emitter.specspaces.EmitterSamplerRgb;
import hana04.shakuyaku.surface.PatchInterval;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;
import hana04.shakuyaku.texture.twodim.image.ImageTextureData;

import javax.inject.Inject;
import java.util.Optional;

public class EnvironmentMapLightInScenes {
  @HanaDeclareBuilder(EnvironmentMapLightInScene.class)
  public static class EnvironmentMapLightInSceneBuilder
      extends EnvironmentMapLightInScene__Impl__Builder<EnvironmentMapLightInSceneBuilder> {
    @Inject
    public EnvironmentMapLightInSceneBuilder(EnvironmentMapLightInScene__ImplFactory factory) {
      super(factory);
    }

    public static EnvironmentMapLightInSceneBuilder builder(Component component) {
      return component.uberFactory().create(EnvironmentMapLightInSceneBuilder.class);
    }
  }

  public static class ImageTextureDataVv
      extends VvWrappedToVvAdaptor<TextureTwoDim, ImageTextureData> {
    @HanaDeclareExtension(
        extensibleClass = EnvironmentMapLightInScene.class,
        extensionClass = ImageTextureDataVv.class)
    ImageTextureDataVv(EnvironmentMapLightInScene emitterInScene, HanaUnwrapper unwrapper) {
      super(
          emitterInScene.emitter().texture(),
          textureTwoDim -> textureTwoDim.getExtension(ImageTextureData.Vv.class),
          unwrapper);
    }
  }

  public static class EmitterSamplerRgbVv
      extends DerivedVersionedValue<EmitterSamplerRgb>
      implements EmitterSamplerRgb.Vv {
    @HanaDeclareExtension(
        extensibleClass = EnvironmentMapLightInScene.class,
        extensionClass = EmitterSamplerRgb.Vv.class)
    EmitterSamplerRgbVv(EnvironmentMapLightInScene emitterInScene) {
      super(
          ImmutableList.of(
              emitterInScene.getExtension(ImageTextureDataVv.class),
              emitterInScene.emitter().toWorld()),
          ChangePropUtil::largestBetweenIncSelfAndDeps,
          () -> new EnvironmentMapLightSampler.ForRgb(
              emitterInScene.getExtension(ImageTextureDataVv.class).value(),
              emitterInScene.emitter().toWorld().value()));
    }
  }

  public static class PatchIntervalProvider_ implements EmitterInScenes.PatchIntervalProvider {
    @HanaDeclareExtension(
        extensibleClass = EnvironmentMapLightInScene.class,
        extensionClass = EmitterInScenes.PatchIntervalProvider.class)
    public PatchIntervalProvider_(EnvironmentMapLightInScene instance) {
      // NO-OP
    }

    @Override
    public Optional<PatchInterval> getPatchInterval() {
      return Optional.empty();
    }
  }
}
