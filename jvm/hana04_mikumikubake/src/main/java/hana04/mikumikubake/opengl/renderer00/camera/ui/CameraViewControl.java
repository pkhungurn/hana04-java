package hana04.mikumikubake.opengl.renderer00.camera.ui;

import hana04.mikumikubake.opengl.renderer00.camera.CameraView;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

import javax.swing.JPanel;

public interface CameraViewControl<T extends CameraView> {
  T getCameraView();
  void updateUi();
  GLFWCursorPosCallbackI getMouseCursorCallback();
  GLFWMouseButtonCallbackI getMouseButtonCallback();
  JPanel getPanel();
}
