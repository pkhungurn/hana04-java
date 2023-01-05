package hana04.mikumikubake.modelview;

import hana04.mikumikubake.ui.BasicAction;
import info.clearthought.layout.TableLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;

public class ModelMotionPanel extends JPanel {
  static final String LOAD_MODEL_ACTION_COMMAND = "Load Model...";
  static final String CLEAR_MODEL_ACTION_COMMAND = "Clear Model";
  static final String LOAD_MOTION_ACTION_COMMAND = "Load Motion/Pose...";
  static final String CLEAR_MOTION_ACTION_COMMAND = "Clear Motion/Pose";
  static final String PLAY_COMMAND = "Play";
  static final String PAUSE_COMMAND = "Pause";

  // Model UI
  JTextField modelFileTextField;
  JButton loadModelButton;
  JButton clearModelButton;
  // Motion UI
  JTextField motionFileTextField;
  JButton loadMotionButton;
  JButton clearMotionButton;
  JSlider frameSlider;
  JTextField frameTextField;
  JButton playPauseButton;

  public ModelMotionPanel(ActionListener actionListener,
                          ChangeListener frameSliderChangeListener,
                          DocumentListener frameTextFieldDocumentListener,
                          FocusListener frameTextFieldFocusListener) {
    setLayout(createModelFilePanelLayout());

    JLabel pmdFileLabel = new JLabel("Model File:");
    add(pmdFileLabel, "0,0,0,0");

    modelFileTextField = new JTextField();
    modelFileTextField.setEnabled(false);
    modelFileTextField.setPreferredSize(new Dimension(450, 50));
    add(modelFileTextField, "2, 0, 2, 0");

    BasicAction loadPmdAction = new BasicAction(LOAD_MODEL_ACTION_COMMAND, actionListener);
    loadModelButton = new JButton(loadPmdAction);
    add(loadModelButton, "4, 0, 4, 0");

    BasicAction clearPmdAction = new BasicAction(CLEAR_MODEL_ACTION_COMMAND, actionListener);
    clearModelButton = new JButton(clearPmdAction);
    add(clearModelButton, "6, 0, 6, 0");

    JLabel motionFileLabel = new JLabel("Motion/Pose File:");
    add(motionFileLabel, "0,2,0,2");

    motionFileTextField = new JTextField();
    motionFileTextField.setEnabled(false);
    motionFileTextField.setPreferredSize(new Dimension(450, 50));
    add(motionFileTextField, "2, 2, 2, 2");

    BasicAction loadMotionAction = new BasicAction(LOAD_MOTION_ACTION_COMMAND, actionListener);
    loadMotionButton = new JButton(loadMotionAction);
    add(loadMotionButton, "4, 2, 4, 2");

    BasicAction clearMotionAction = new BasicAction(CLEAR_MOTION_ACTION_COMMAND, actionListener);
    clearMotionButton = new JButton(clearMotionAction);
    add(clearMotionButton, "6, 2, 6, 2");

    add(new JLabel("Frame:"), "0, 4, 0, 4");

    frameSlider = new JSlider();
    frameSlider.setEnabled(false);
    frameSlider.setMinorTickSpacing(1);
    frameSlider.setMajorTickSpacing(30);
    frameSlider.addChangeListener(frameSliderChangeListener);
    add(frameSlider, "2, 4, 2, 4");

    frameTextField = new JTextField();
    frameTextField.setEnabled(false);
    frameTextField.setMinimumSize(new Dimension(100, 10));
    frameTextField.getDocument().addDocumentListener(frameTextFieldDocumentListener);
    frameTextField.addFocusListener(frameTextFieldFocusListener);
    add(frameTextField, "4, 4, 4, 4");

    BasicAction playPauseAction = new BasicAction(PLAY_COMMAND, actionListener);
    playPauseButton = new JButton(playPauseAction);
    add(playPauseButton, "6, 4, 6, 4");
    playPauseButton.setEnabled(false);
  }

  private LayoutManager createModelFilePanelLayout() {
    double[][] tableLayoutSizes =
      {
        {
          TableLayout.MINIMUM, 5,
          TableLayout.FILL, 5,
          TableLayout.MINIMUM, 5,
          TableLayout.MINIMUM,
        },
        {
          TableLayout.MINIMUM, 5,
          TableLayout.MINIMUM, 5,
          TableLayout.MINIMUM, 5,
          TableLayout.MINIMUM, 5,
          TableLayout.MINIMUM, 5,
        }
      };
    return new TableLayout(tableLayoutSizes);
  }
}
