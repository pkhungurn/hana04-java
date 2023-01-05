package hana04.shakuyaku.sensor.camera.orthographic;

import com.google.common.base.Preconditions;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.extension.validator.Validator;
import hana04.gfxbase.gfxtype.Matrix4dUtil;
import hana04.gfxbase.gfxtype.Transform;

import javax.inject.Inject;

public class OrthographicCameras {

  @HanaDeclareBuilder(OrthographicCamera.class)
  public static class OrthographicCameraBuilder extends OrthographicCamera__Impl__Builder<OrthographicCameraBuilder> {
    @Inject
    public OrthographicCameraBuilder(OrthographicCamera__ImplFactory factory) {
      super(factory);
      toWorld(new Transform(Matrix4dUtil.createIdentity()));
      aspect(1.0);
      height(10.0);
      nearClip(1e-4);
      farClip(1e4);
      cropOffsetX(0.0);
      cropOffsetY(0.0);
      cropSizeX(1.0);
      cropSizeY(1.0);
    }

    public static OrthographicCameraBuilder builder(Component component) {
      return component.uberFactory().create(OrthographicCameraBuilder.class);
    }
  }

  public static class OrthographicCameraValidator implements Validator {
    private final OrthographicCamera instance;

    @HanaDeclareExtension(
        extensibleClass = OrthographicCamera.class,
        extensionClass = Validator.class)
    public OrthographicCameraValidator(OrthographicCamera instance) {
      this.instance = instance;
    }

    @Override
    public void validate() {
      Preconditions.checkArgument(
          instance.nearClip().value() < instance.farClip().value(),
          "nearClip().value() < farClip().value()");
      Preconditions.checkArgument(
          instance.cropOffsetX().value() >= 0 && instance.cropOffsetX().value() < 1,
          "cropOffsetX().value() >= 0 && cropOffsetX().value() < 1");
      Preconditions.checkArgument(
          instance.cropOffsetY().value() >= 0 && instance.cropOffsetY().value() < 1,
          "cropOffsetY().value() >= 0 && cropOffsetY().value() < 1");
      Preconditions.checkArgument(
          instance.cropSizeX().value() >= 0 && instance.cropOffsetX().value() + instance.cropSizeX().value() <= 1,
          "cropSizeX().value() >= 0 && cropOffsetX().value() + cropSizeX().value() <= 1");
      Preconditions.checkArgument(
          instance.cropSizeY().value() >= 0 && instance.cropOffsetY().value() + instance.cropSizeY().value() <= 1,
          "cropSizeY().value() >= 0 && cropOffsetY().value() + cropSizeY().value() <= 1");
      Preconditions.checkArgument(instance.height().value() > 0, "height().value() > 0");
      Preconditions.checkArgument(instance.aspect().value() > 0, "aspect().value() > 0");
    }
  }
}
