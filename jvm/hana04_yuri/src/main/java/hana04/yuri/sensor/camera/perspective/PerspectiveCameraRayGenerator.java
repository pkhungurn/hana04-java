package hana04.yuri.sensor.camera.perspective;

import hana04.gfxbase.gfxtype.Ray;
import hana04.gfxbase.gfxtype.Transform;
import hana04.shakuyaku.sensor.camera.perspective.PerspectiveCamera;
import hana04.yuri.sensor.SensorRay;
import hana04.yuri.sensor.camera.CameraRayGenerator;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple2d;

public class PerspectiveCameraRayGenerator implements CameraRayGenerator {
  private final double top;
  private final double right;
  private final double nearClip;
  private final double farClip;
  private final double cropOffsetX;
  private final double cropOffsetY;
  private final double cropSizeX;
  private final double cropSizeY;
  private final Transform toWorld;

  public PerspectiveCameraRayGenerator(PerspectiveCamera camera) {
    top = Math.tan(camera.fovY().value() / 2 / 180 * Math.PI) * camera.nearClip().value();
    right = camera.aspect().value() * top;
    this.cropOffsetX = camera.cropOffsetX().value();
    this.cropOffsetY = camera.cropOffsetY().value();
    this.cropSizeX = camera.cropSizeX().value();
    this.cropSizeY = camera.cropSizeY().value();
    this.nearClip = camera.nearClip().value();
    this.farClip = camera.farClip().value();
    this.toWorld = new Transform(camera.toWorld().value().m);
  }

  @Override
  public SensorRay generate(Tuple2d imagePlanePosition, Tuple2d aperturePosition) {
    /* Compute the corresponding position on the near plane (in local camera space) */
    Point3d nearP = new Point3d(
      (2 * (cropOffsetX + imagePlanePosition.x * cropSizeX) - 1) * right,
      (2 * (cropOffsetY + imagePlanePosition.y * cropSizeY) - 1) * top,
      -nearClip);

    /* Turn into a normalized ray direction, and adjust the ray interval accordingly */
    Ray ray = new Ray();
    ray.d.set(nearP);
    ray.d.normalize();
    double invZ = 1.0 / ray.d.z;

    ray.o.set(0, 0, 0);
    toWorld.m.transform(ray.o);
    toWorld.m.transform(ray.d);
    ray.mint = -nearClip * invZ;
    ray.maxt = -farClip * invZ;

    return new SensorRay(ray, 1.0);
  }
}
