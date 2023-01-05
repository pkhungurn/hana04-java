package hana04.mikumikubake.opengl.renderer00.camera;

import hana04.gfxbase.gfxtype.Transform;

import javax.vecmath.Point3d;

public interface CameraView {
  Transform getXform();

  void setCenter(double x, double y, double z);
  Point3d getCenter();

  double getXAngle();
  void setXAngle(double xAngle);

  double getYAngle();
  void setYAngle(double yAngle);

  double getZAngle();
  void setZAngle(double zAngle);

  double getDistance();
  void setDistance(double distance);
}
