package hana04.mikumikubake.opengl.renderer00.camera.ui;

import hana04.gfxbase.gfxtype.Transform;
import hana04.mikumikubake.opengl.renderer00.camera.YUpCameraView;
import hana04.mikumikubake.ui.MouseButtonEvent;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class YUpCameraViewControl extends AbstractYUpCameraViewControl {
  enum State {
    DO_NOTHING,
    CHANGE_ROTATION,
    CHANGE_CENTER,
    CHANGE_DISTANCE
  }

  private final YUpCameraView startClickView = new YUpCameraView();

  // Start click states
  private State state;

  private double startClickXPos, startClickYPos;
  private double xPos, yPos;

  public YUpCameraViewControl(YUpCameraView cameraView) {
    super(cameraView);
    this.state = State.DO_NOTHING;
  }

  public void handleMouseButtonEvent(MouseButtonEvent event) {
    if (event.action().equals(MouseButtonEvent.Action.RELEASE)) {
      state = State.DO_NOTHING;
      return;
    }

    startClickView.copy(cameraView);
    startClickXPos = xPos;
    startClickYPos = yPos;

    if (!event.button().equals(MouseButtonEvent.Button.RIGHT)) {
      return;
    }
    if (event.noModifiers()) {
      state = State.CHANGE_ROTATION;
    } else if (event.shiftPressed()) {
      state = State.CHANGE_CENTER;
    } else if (event.onlyCtrlPressed()) {
      state = State.CHANGE_DISTANCE;
    } else {
      state = State.DO_NOTHING;
    }
  }

  public void handleMouseMoveEvent(double xPos, double yPos) {
    this.xPos = xPos;
    this.yPos = yPos;

    switch(state) {
      case CHANGE_ROTATION:
        handleChangeRotation();
        break;
      case CHANGE_CENTER:
        handleChangeCenter();
        break;
      case CHANGE_DISTANCE:
        handleChangeDistance();
        break;
    }
  }

  private static final double CHANGE_ANGLE_SPEED = 0.1;

  private void handleChangeRotation() {
    double dx = xPos - startClickXPos;
    double dy = yPos - startClickYPos;
    cameraView.setXAngle(startClickView.getXAngle() - dy * CHANGE_ANGLE_SPEED);
    cameraView.setYAngle(startClickView.getYAngle() - dx * CHANGE_ANGLE_SPEED);
    updateUi();
  }

  private static final double CHANGE_CENTER_SPEED = 0.1;

  private void handleChangeCenter() {
    double dx = xPos - startClickXPos;
    double dy = yPos - startClickYPos;

    Transform view = cameraView.getXform();
    Vector3d x = new Vector3d(view.mi.m00, view.mi.m10, view.mi.m20);
    Vector3d y = new Vector3d(view.mi.m01, view.mi.m11, view.mi.m21);

    Point3d newCenter = new Point3d(startClickView.getCenter());
    newCenter.scaleAdd(-dx * CHANGE_CENTER_SPEED, x, newCenter);
    newCenter.scaleAdd(dy * CHANGE_CENTER_SPEED, y, newCenter);

    cameraView.setCenter(newCenter.x, newCenter.y, newCenter.z);

    updateUi();
  }

  private static final double CHANGE_DISTANCE_SPEED = 0.1;

  private void handleChangeDistance() {
    double dy = yPos - startClickYPos;
    cameraView.setDistance(Math.max(0.001, startClickView.getDistance() + CHANGE_DISTANCE_SPEED * dy));
    updateUi();
  }
}
