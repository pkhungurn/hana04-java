package hana04.mikumikubake.opengl.renderer00.camera.ui;

import hana04.mikumikubake.opengl.renderer00.camera.YUpPerspectiveCamera;
import info.clearthought.layout.TableLayout;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

import javax.swing.JPanel;
import javax.swing.JSeparator;
import java.awt.LayoutManager;

public class YUpPerspectiveCameraControl implements CameraControl<YUpPerspectiveCamera> {
  private YUpPerspectiveCamera camera;
  private PerspectiveProjectionControl projectionControl;
  private YUpCameraViewControl viewControl;

  private JPanel cameraPanel;

  public YUpPerspectiveCameraControl() {
    this.camera = new YUpPerspectiveCamera();
    this.viewControl = new YUpCameraViewControl(this.camera.getView());
    this.projectionControl = new PerspectiveProjectionControl(this.camera.getProjection());
    this.cameraPanel = createCameraPanel();

  }

  private JPanel createCameraPanel() {
    JPanel cameraPanel = new JPanel();
    cameraPanel.setLayout(createCameraPanelLayout());
    cameraPanel.add("0,0,0,0", viewControl.getPanel());
    cameraPanel.add("0,2,0,2", new JSeparator());
    cameraPanel.add("0,4,0,4", projectionControl.getPanel());
    return cameraPanel;
  }

  private LayoutManager createCameraPanelLayout() {
    double[][] tableLayoutSizes = {
      {
        TableLayout.FILL
      },
      {
        TableLayout.MINIMUM, 5,
        TableLayout.MINIMUM, 5,
        TableLayout.MINIMUM
      }
    };
    return new TableLayout(tableLayoutSizes);
  }

  @Override
  public YUpPerspectiveCamera getCamera() {
    return camera;
  }

  @Override
  public void updateCameraUi() {
    viewControl.updateUi();
    projectionControl.updateUi();
  }

  @Override
  public GLFWCursorPosCallbackI getMouseCursorCallback() {
    return viewControl.getMouseCursorCallback();
  }

  @Override
  public GLFWMouseButtonCallbackI getMouseButtonCallback() {
    return viewControl.getMouseButtonCallback();
  }

  @Override
  public JPanel getCameraPanel() {
    return cameraPanel;
  }
}
