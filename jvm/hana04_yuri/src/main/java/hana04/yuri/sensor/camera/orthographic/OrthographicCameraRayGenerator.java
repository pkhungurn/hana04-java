package hana04.yuri.sensor.camera.orthographic;

import hana04.gfxbase.gfxtype.Ray;
import hana04.gfxbase.gfxtype.Transform;
import hana04.shakuyaku.sensor.camera.orthographic.OrthographicCamera;
import hana04.yuri.sensor.SensorRay;
import hana04.yuri.sensor.camera.CameraRayGenerator;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple2d;

public class OrthographicCameraRayGenerator implements CameraRayGenerator {
  private final double top;
  private final double right;
  private final double nearClip;
  private final double farClip;
  private final double cropOffsetX;
  private final double cropOffsetY;
  private final double cropSizeX;
  private final double cropSizeY;
  private final Transform toWorld;

  public OrthographicCameraRayGenerator(OrthographicCamera camera) {
    top = camera.height().value() / 2;
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
  public SensorRay generate(Tuple2d imagePlacePosition, Tuple2d aperturePosition) {
    Point3d origin = new Point3d(
        (2 * (cropOffsetX + imagePlacePosition.x * cropSizeX) - 1) * right,
        (2 * (cropOffsetY + imagePlacePosition.y * cropSizeY) - 1) * top,
        0.0);

    /* Turn into a normalized ray direction, and adjust the ray interval accordingly */
    Ray ray = new Ray();
    ray.o.set(origin);
    ray.d.set(0, 0, -1);

    toWorld.m.transform(ray.o);
    toWorld.m.transform(ray.d);
    ray.mint = -nearClip;
    ray.maxt = -farClip;

    return new SensorRay(ray, 1.0);
  }
}
