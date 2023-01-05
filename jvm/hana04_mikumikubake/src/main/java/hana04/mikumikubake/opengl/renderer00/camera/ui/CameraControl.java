package hana04.mikumikubake.opengl.renderer00.camera.ui;

import hana04.mikumikubake.opengl.renderer00.camera.Camera;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

import javax.swing.JPanel;

public interface CameraControl<T extends Camera> {
  T getCamera();
  void updateCameraUi();
  GLFWCursorPosCallbackI getMouseCursorCallback();
  GLFWMouseButtonCallbackI getMouseButtonCallback();
  JPanel getCameraPanel();
}
