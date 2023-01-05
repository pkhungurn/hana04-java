package hana04.mikumikubake.opengl.renderer00.camera.ui;

import hana04.mikumikubake.opengl.renderer00.camera.CameraProjection;

import javax.swing.JPanel;

public interface CameraProjectionControl<T extends CameraProjection> {
  T getProjection();
  JPanel getPanel();
  void updateUi();
}
