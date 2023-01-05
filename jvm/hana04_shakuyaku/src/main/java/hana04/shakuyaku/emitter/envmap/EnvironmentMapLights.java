package hana04.shakuyaku.emitter.envmap;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.gfxbase.gfxtype.Matrix4dUtil;
import hana04.gfxbase.gfxtype.Transform;
import hana04.shakuyaku.emitter.EmitterType;
import hana04.shakuyaku.emitter.Emitters;

import javax.inject.Inject;

public class EnvironmentMapLights {
  @HanaDeclareBuilder(EnvironmentMapLight.class)
  public static class EnvironmentMapLightBuilder
      extends EnvironmentMapLight__Impl__Builder<EnvironmentMapLightBuilder> {
    @Inject
    public EnvironmentMapLightBuilder(EnvironmentMapLight__ImplFactory factory) {
      super(factory);
      samplingWeight(1.0);
      toWorld(new Transform(Matrix4dUtil.IDENTITY_MATRIX));
    }

    public static EnvironmentMapLightBuilder builder(Component component) {
      return component.uberFactory().create(EnvironmentMapLightBuilder.class);
    }
  }

  public static class EnvironmentMapLightInfo implements Emitters.EmitterInfo {
    @HanaDeclareExtension(
        extensibleClass = EnvironmentMapLight.class,
        extensionClass = Emitters.EmitterInfo.class)
    public EnvironmentMapLightInfo(EnvironmentMapLight instance) {
      // NO-OP
    }

    @Override
    public EmitterType getType() {
      return EmitterType.Environmental;
    }
  }
}
