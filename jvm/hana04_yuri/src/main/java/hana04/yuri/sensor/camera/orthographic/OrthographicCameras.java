package hana04.yuri.sensor.camera.orthographic;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.shakuyaku.sensor.camera.orthographic.OrthographicCamera;
import hana04.yuri.sensor.camera.CameraRayGenerator;

public class OrthographicCameras {
  public static class RayGeneratorVv extends DerivedVersionedValue<CameraRayGenerator> implements
      CameraRayGenerator.Vv {

    @HanaDeclareExtension(
        extensibleClass = OrthographicCamera.class,
        extensionClass = CameraRayGenerator.Vv.class)
    public RayGeneratorVv(OrthographicCamera camera) {
      super(
          ImmutableList.of(
              camera.toWorld(),
              camera.aspect(),
              camera.height(),
              camera.nearClip(),
              camera.farClip(),
              camera.cropOffsetX(),
              camera.cropOffsetY(),
              camera.cropSizeX(),
              camera.cropSizeY()),
          ChangePropUtil::largestBetweenIncSelfAndDeps,
          () -> new OrthographicCameraRayGenerator(camera));
    }
  }
}
