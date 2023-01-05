package hana04.mikumikubake.opengl.renderer00.camera;

import hana04.gfxbase.gfxtype.Transform;

public interface CameraProjection {
  Transform getXform(double shiftX, double shiftY, int screenWidth, int screenHeight);
  double getNear();
  double getFar();
}
