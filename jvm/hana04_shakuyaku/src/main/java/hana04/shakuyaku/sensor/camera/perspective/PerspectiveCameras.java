package hana04.shakuyaku.sensor.camera.perspective;

import com.google.common.base.Preconditions;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.extension.validator.Validator;
import hana04.gfxbase.gfxtype.Matrix4dUtil;
import hana04.gfxbase.gfxtype.Transform;

import javax.inject.Inject;

public class PerspectiveCameras {

  @HanaDeclareBuilder(PerspectiveCamera.class)
  public static class PerspectiveCameraBuilder extends PerspectiveCamera__Impl__Builder<PerspectiveCameraBuilder> {
    @Inject
    public PerspectiveCameraBuilder(PerspectiveCamera__ImplFactory factory) {
      super(factory);
      toWorld(new Transform(Matrix4dUtil.createIdentity()));
      aspect(1.0);
      fovY(30.0);
      nearClip(1e-4);
      farClip(1e4);
      cropOffsetX(0.0);
      cropOffsetY(0.0);
      cropSizeX(1.0);
      cropSizeY(1.0);
    }

    public static PerspectiveCameraBuilder builder(Component component) {
      return component.uberFactory().create(PerspectiveCameraBuilder.class);
    }
  }

  public static class PerspectiveCameraValidator implements Validator {
    private final PerspectiveCamera instance;

    @HanaDeclareExtension(
        extensibleClass = PerspectiveCamera.class,
        extensionClass = Validator.class)
    public PerspectiveCameraValidator(PerspectiveCamera instance) {
      this.instance = instance;
    }

    @Override
    public void validate() {
      Preconditions.checkArgument(instance.nearClip().value() < instance.farClip().value());
      Preconditions.checkArgument(instance.cropOffsetX().value() >= 0 && instance.cropOffsetX().value() < 1);
      Preconditions.checkArgument(instance.cropOffsetY().value() >= 0 && instance.cropOffsetY().value() < 1);
      Preconditions.checkArgument(instance.cropSizeX().value() >= 0 && instance.cropOffsetX().value() + instance.cropSizeX().value() <= 1);
      Preconditions.checkArgument(instance.cropSizeY().value() >= 0 && instance.cropOffsetY().value() + instance.cropSizeY().value() <= 1);
      Preconditions.checkArgument(instance.fovY().value() > 0 && instance.fovY().value() < 180);
      Preconditions.checkArgument(instance.aspect().value() > 0);
    }
  }
}
