package hana04.mikumikubake.opengl.renderer00.camera;

import hana04.gfxbase.gfxtype.Transform;

public class YUpPerspectiveCamera implements Camera {
  private YUpCameraView view = new YUpCameraView();
  private PerspectiveProjection projection = new PerspectiveProjection();

  public YUpCameraView getView() {
    return view;
  }

  public PerspectiveProjection getProjection() {
    return projection;
  }

  @Override
  public Transform getProjectionXform(double shiftX, double shiftY, int screenWidth, int screenHeight) {
    return projection.getXform(shiftX, shiftY, screenWidth, screenHeight);
  }

  @Override
  public Transform getViewXform() {
    return view.getXform();
  }


  public void copy(YUpPerspectiveCamera other) {
    view.copy(other.view);
    projection.copy(other.projection);
  }
}
