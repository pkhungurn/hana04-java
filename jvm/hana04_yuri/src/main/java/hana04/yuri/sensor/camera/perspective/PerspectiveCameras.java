package hana04.yuri.sensor.camera.perspective;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.shakuyaku.sensor.camera.perspective.PerspectiveCamera;
import hana04.yuri.sensor.camera.CameraRayGenerator;

public class PerspectiveCameras {
  public static class RayGeneratorVv extends DerivedVersionedValue<CameraRayGenerator> implements
      CameraRayGenerator.Vv {

    @HanaDeclareExtension(
        extensibleClass = PerspectiveCamera.class,
        extensionClass = CameraRayGenerator.Vv.class)
    public RayGeneratorVv(PerspectiveCamera camera) {
      super(
          ImmutableList.of(
              camera.toWorld(),
              camera.aspect(),
              camera.fovY(),
              camera.nearClip(),
              camera.farClip(),
              camera.cropOffsetX(),
              camera.cropOffsetY(),
              camera.cropSizeX(),
              camera.cropSizeY()),
          ChangePropUtil::largestBetweenIncSelfAndDeps,
          () -> new PerspectiveCameraRayGenerator(camera));
    }
  }
}
