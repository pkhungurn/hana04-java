package hana04.mikumikubake.opengl.common;

import hana04.mikumikubake.ui.JSpinnerSlider;
import info.clearthought.layout.TableLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;

public class PosingPanel extends JPanel {
  static final String BUST_ZOOM_COMMAND = "Bust Zoom";

  private ActionListener actionListener;

  public JSpinnerSlider headXRotationSs;
  public JSpinnerSlider headYRotationSs;
  public JSpinnerSlider headZRotationSs;

  public JSpinnerSlider leftEyelidSs;
  public JSpinnerSlider rightEyelidSs;
  public JSpinnerSlider eyeYRotationSs;
  public JSpinnerSlider eyeXRotationSs;

  public JSpinnerSlider mouthSs;

  public JSpinnerSlider bodyYRotationSs;
  public JSpinnerSlider bodyZRotationSs;

  public JSpinnerSlider extentMultiplierSs;
  public JSpinnerSlider sideShiftSs;

  public JButton bustZoomButton;

  public PosingPanel(ActionListener actionListener) {
    this.actionListener = actionListener;
    setLayout(createLayout());

    int row = 0;
    add(new JLabel("Head X rotation"), String.format("0,%d,0,%d", row, row));
    headXRotationSs = new JSpinnerSlider(-15, 15, 3000, 0.0);
    add(headXRotationSs, String.format("2, %d, 2, %d", row, row));

    add(new JLabel("Head Y rotation"), String.format("4,%d,4,%d", row, row));
    headYRotationSs = new JSpinnerSlider(-20, 20, 4000, 0.0);
    add(headYRotationSs, String.format("6, %d, 6, %d", row, row));

    add(new JLabel("Head Z rotation"), String.format("8,%d,8,%d", row, row));
    headZRotationSs = new JSpinnerSlider(-15, 15, 3000, 0.0);
    add(headZRotationSs, String.format("10, %d, 10, %d", row, row));

    row += 2;
    add(new JSeparator(), String.format("0, %d, 10, %d", row, row));

    row += 2;
    add(new JLabel("Eye X rotation"), String.format("0,%d,0,%d", row, row));
    eyeXRotationSs = new JSpinnerSlider(-10, 10, 2000, 0.0);
    add(eyeXRotationSs, String.format("2, %d, 2, %d", row, row));

    add(new JLabel("Eye Y rotation"), String.format("4,%d,4,%d", row, row));
    eyeYRotationSs = new JSpinnerSlider(-5, 5, 1000, 0.0);
    add(eyeYRotationSs, String.format("6, %d, 6, %d", row, row));

    row += 2;
    add(new JLabel("Left Eyelid"), String.format("0,%d,0,%d", row, row));
    leftEyelidSs = new JSpinnerSlider(0, 1, 1000, 0.0);
    add(leftEyelidSs, String.format("2, %d, 2, %d", row, row));

    add(new JLabel("Right Eyelid"), String.format("4,%d,4,%d", row, row));
    rightEyelidSs = new JSpinnerSlider(0, 1, 1000, 0.0);
    add(rightEyelidSs, String.format("6, %d, 6, %d", row, row));

    row += 2;
    add(new JSeparator(), String.format("0, %d, 10, %d", row, row));

    row += 2;
    add(new JLabel("Mouth"), String.format("0,%d,0,%d", row, row));
    mouthSs = new JSpinnerSlider(0, 1, 1000, 0.0);
    add(mouthSs, String.format("2, %d, 2, %d", row, row));

    row += 2;
    add(new JSeparator(), String.format("0, %d, 10, %d", row, row));

    row += 2;
    add(new JLabel("Body Y Rotation"), String.format("0,%d,0,%d", row, row));
    bodyYRotationSs = new JSpinnerSlider(-10, 10, 2000, 0.0);
    add(bodyYRotationSs, String.format("2, %d, 2, %d", row, row));

    add(new JLabel("Body Z Rotation"), String.format("4,%d,4,%d", row, row));
    bodyZRotationSs = new JSpinnerSlider(-10, 10, 2000, 0.0);
    add(bodyZRotationSs, String.format("6, %d, 6, %d", row, row));

    row += 2;
    add(new JSeparator(), String.format("0, %d, 10, %d", row, row));

    row += 2;

    add(new JLabel("Vertical Extent Multiplier"), String.format("0,%d,0,%d", row, row));
    extentMultiplierSs = new JSpinnerSlider(2.5, 3.5, 2000, 3.0);
    add(extentMultiplierSs, String.format("2, %d, 2, %d", row, row));

    add(new JLabel("Side Shift"), String.format("4,%d,4,%d", row, row));
    sideShiftSs = new JSpinnerSlider(-0.1, 0.1, 1000, 0.0);
    add(sideShiftSs, String.format("6, %d, 6, %d", row, row));

    bustZoomButton = new JButton(BUST_ZOOM_COMMAND);
    add(bustZoomButton, String.format("8, %d, 10, %d", row, row));
    bustZoomButton.addActionListener(actionListener);
  }

  private LayoutManager createLayout() {
    double[][] tableLayoutSizes =
      {
        {
          TableLayout.MINIMUM, 5,
          TableLayout.FILL, 5,
          TableLayout.MINIMUM, 5,
          TableLayout.FILL, 5,
          TableLayout.MINIMUM, 5,
          TableLayout.FILL
        },
        {
          TableLayout.MINIMUM, 5,
          TableLayout.MINIMUM, 5,
          TableLayout.MINIMUM, 5,
          TableLayout.MINIMUM, 5,
          TableLayout.MINIMUM, 5,
          TableLayout.MINIMUM, 5,
          TableLayout.MINIMUM, 5,
          TableLayout.MINIMUM, 5,
          TableLayout.MINIMUM, 5,
          TableLayout.MINIMUM,
        }
      };
    return new TableLayout(tableLayoutSizes);
  }
}
