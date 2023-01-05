package hana04.mikumikubake.modelview;

import hana04.mikumikubake.opengl.common.AmbientLightPanel;
import hana04.mikumikubake.opengl.common.DirectionalLightPanel;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class UiFrame extends JFrame {
  static final String LOAD_MODEL_ACTION_COMMAND = "Load Model...";
  static final String CLEAR_MODEL_ACTION_COMMAND = "Clear Model";
  static final String LOAD_MOTION_ACTION_COMMAND = "Load Motion/Pose...";
  static final String CLEAR_MOTION_ACTION_COMMAND = "Clear Motion/Pose";
  static final String PLAY_COMMAND = "Play";
  static final String PAUSE_COMMAND = "Pause";

  private MmdModelViewer mmdModelViewer;
  ModelMotionPanel modelMotionPanel;
  AmbientLightPanel ambientLightPanel;
  DirectionalLightPanel directionalLightPanel;

  public UiFrame(MmdModelViewer mmdModelViewer) {
    super();
    this.mmdModelViewer = mmdModelViewer;
    setTitle("MMD Model Viewer UI");
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setResizable(false);
    initializeControlPanel();
    pack();
  }

  private void initializeControlPanel() {
    JTabbedPane tabbedPane = new JTabbedPane();
    getContentPane().add(tabbedPane, BorderLayout.CENTER);

    int borderSize = 10;

    modelMotionPanel = new ModelMotionPanel(mmdModelViewer,
      mmdModelViewer.frameSliderChangeListener,
      mmdModelViewer.frameTextFieldListenter,
      mmdModelViewer.frameTextFieldListenter);
    modelMotionPanel.setBorder(BorderFactory.createEmptyBorder(borderSize, borderSize, borderSize, borderSize));
    tabbedPane.add("Model and Motion", modelMotionPanel);

    mmdModelViewer.cameraControl.getCameraPanel().setBorder(
      BorderFactory.createEmptyBorder(borderSize, borderSize, borderSize, borderSize));
    tabbedPane.addTab("Camera", mmdModelViewer.cameraControl.getCameraPanel());

    ambientLightPanel = new AmbientLightPanel();
    ambientLightPanel.setPreferredSize(new Dimension(800, 100));
    ambientLightPanel.setBorder(BorderFactory.createEmptyBorder(borderSize, borderSize, borderSize, borderSize));
    tabbedPane.addTab("Ambient Light", ambientLightPanel);

    directionalLightPanel = new DirectionalLightPanel();
    directionalLightPanel.setPreferredSize(new Dimension(800, 100));
    directionalLightPanel.setBorder(BorderFactory.createEmptyBorder(borderSize, borderSize, borderSize, borderSize));
    tabbedPane.addTab("Directional Light", directionalLightPanel);
  }
}
