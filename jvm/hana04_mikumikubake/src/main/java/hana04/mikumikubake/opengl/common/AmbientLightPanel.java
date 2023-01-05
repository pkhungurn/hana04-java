package hana04.mikumikubake.opengl.common;

import info.clearthought.layout.TableLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.vecmath.Vector3d;
import java.awt.Dimension;
import java.awt.LayoutManager;

public class AmbientLightPanel extends JPanel {
  public final JSpinner rSpinner;
  public final JSpinner gSpinner;
  public final JSpinner bSpinner;

  public AmbientLightPanel() {
    this.setLayout(createLayout());

    this.add(new JLabel("Ambient Light R:"), "0,0,0,0");
    this.add(new JLabel("Ambient Light G:"), "4,0,4,0");
    this.add(new JLabel("Ambient Light B:"), "8,0,8,0");

    SpinnerModel ambientLightRModel =
      new SpinnerNumberModel(
        /* value= */ 1.0,
        /* minimum= */ 0.0,
        /* maximum= */ 1.0,
        /* step= */ 0.01);
    rSpinner = new JSpinner(ambientLightRModel);
    rSpinner.setPreferredSize(new Dimension(100,10));
    this.add(rSpinner, "2,0,2,0");

    SpinnerModel ambientLightGModel =
      new SpinnerNumberModel(
        /* value= */ 1.0,
        /* minimum= */ 0.0,
        /* maximum= */ 1.0,
        /* step= */ 0.01);
    gSpinner = new JSpinner(ambientLightGModel);
    gSpinner.setPreferredSize(new Dimension(100,10));
    this.add(gSpinner, "6,0,6,0");

    SpinnerModel ambientLightBModel =
      new SpinnerNumberModel(
        /* value= */ 1.0,
        /* minimum= */ 0.0,
        /* maximum= */ 1.0,
        /* step= */ 0.01);
    bSpinner = new JSpinner(ambientLightBModel);
    bSpinner.setPreferredSize(new Dimension(100,10));
    this.add(bSpinner, "10,0,10,0");
  }

  private LayoutManager createLayout() {
    double[][] tableLayoutSizes =
      {
        {
          TableLayout.MINIMUM, 5,
          TableLayout.PREFERRED, 5,
          TableLayout.MINIMUM, 5,
          TableLayout.PREFERRED, 5,
          TableLayout.MINIMUM, 5,
          TableLayout.PREFERRED
        },
        {
          TableLayout.MINIMUM
        }
      };
    return new TableLayout(tableLayoutSizes);
  }

  public Vector3d getRadiance() {
    return new Vector3d(
      ((SpinnerNumberModel) rSpinner.getModel()).getNumber().doubleValue(),
      ((SpinnerNumberModel) gSpinner.getModel()).getNumber().doubleValue(),
      ((SpinnerNumberModel) bSpinner.getModel()).getNumber().doubleValue());
  }
}
