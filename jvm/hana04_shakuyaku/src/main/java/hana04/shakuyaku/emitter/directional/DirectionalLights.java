package hana04.shakuyaku.emitter.directional;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.gfxtype.Transform;
import hana04.shakuyaku.emitter.EmitterType;
import hana04.shakuyaku.emitter.Emitters;

import javax.inject.Inject;

public class DirectionalLights {

  @HanaDeclareBuilder(DirectionalLight.class)
  public static class DirectionalLightBuilder extends DirectionalLight__Impl__Builder<DirectionalLightBuilder> {
    @Inject
    public DirectionalLightBuilder(DirectionalLight__ImplFactory factory) {
      super(factory);
      samplingWeight(1.0);
      radiance(new Rgb(1, 1, 1));
      toWorld(Transform.builder().build());
    }

    public static DirectionalLightBuilder builder(Component component) {
      return component.uberFactory().create(DirectionalLightBuilder.class);
    }
  }

  public static class DirectionalLightInfo implements Emitters.EmitterInfo {
    @HanaDeclareExtension(
        extensibleClass = DirectionalLight.class,
        extensionClass = Emitters.EmitterInfo.class)
    public DirectionalLightInfo(DirectionalLight instance) {
      // NO-OP
    }

    @Override
    public EmitterType getType() {
      return EmitterType.Environmental;
    }
  }
}
