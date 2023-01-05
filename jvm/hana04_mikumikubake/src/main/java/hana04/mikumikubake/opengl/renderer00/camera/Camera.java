package hana04.mikumikubake.opengl.renderer00.camera;

import hana04.gfxbase.gfxtype.Transform;

public interface Camera {
  Transform getProjectionXform(double shiftX, double shiftY, int screenWidth, int screenHeight);
  Transform getViewXform();
}
