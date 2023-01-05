package hana04.shakuyaku.emitter.distantdisk;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.gfxtype.Matrix4dUtil;
import hana04.gfxbase.gfxtype.Transform;
import hana04.shakuyaku.emitter.EmitterType;
import hana04.shakuyaku.emitter.Emitters;

import javax.inject.Inject;

public class DistantDiskLights {

  @HanaDeclareBuilder(DistantDiskLight.class)
  public static class DistantDiskLightBuilder extends DistantDiskLight__Impl__Builder<DistantDiskLightBuilder> {
    @Inject
    public DistantDiskLightBuilder(DistantDiskLight__ImplFactory factory) {
      super(factory);
      samplingWeight(1.0);
      thetaA(180.0);
      radiance(new Rgb(1.0, 1.0, 1.0));
      toWorld(new Transform(Matrix4dUtil.IDENTITY_MATRIX));
    }

    public static DistantDiskLightBuilder builder(Component component) {
      return component.uberFactory().create(DistantDiskLightBuilder.class);
    }
  }

  public static class DistantDiskLightInfo implements Emitters.EmitterInfo {
    @HanaDeclareExtension(
        extensibleClass = DistantDiskLight.class,
        extensionClass = Emitters.EmitterInfo.class)
    public DistantDiskLightInfo(DistantDiskLight instance) {
      // NO-OP
    }

    @Override
    public EmitterType getType() {
      return EmitterType.Environmental;
    }
  }
}
