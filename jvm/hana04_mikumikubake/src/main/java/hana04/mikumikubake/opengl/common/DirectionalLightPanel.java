package hana04.mikumikubake.opengl.common;

import hana04.gfxbase.util.MathUtil;
import info.clearthought.layout.TableLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.vecmath.Vector3d;
import java.awt.Dimension;
import java.awt.LayoutManager;

public class DirectionalLightPanel extends JPanel {
  public final JSpinner rSpinner;
  public final JSpinner gSpinner;
  public final JSpinner bSpinner;
  public final JSpinner thetaSpinner;
  public final JSpinner phiSpinner;

  public DirectionalLightPanel() {
    this.setLayout(createLayout());

    this.add(new JLabel("Light R:"), "0,0,0,0");
    this.add(new JLabel("Light G:"), "4,0,4,0");
    this.add(new JLabel("Light B:"), "8,0,8,0");

    SpinnerModel lightRModel =
      new SpinnerNumberModel(
        /* value= */ 0.5,
        /* minimum= */ 0.0,
        /* maximum= */ 1.0,
        /* step= */ 0.01);
    rSpinner = new JSpinner(lightRModel);
    rSpinner.setPreferredSize(new Dimension(100,10));
    this.add(rSpinner, "2,0,2,0");

    SpinnerModel lightGModel =
      new SpinnerNumberModel(
        /* value= */ 0.5,
        /* minimum= */ 0.0,
        /* maximum= */ 1.0,
        /* step= */ 0.01);
    gSpinner = new JSpinner(lightGModel);
    gSpinner.setPreferredSize(new Dimension(100,10));
    this.add(gSpinner, "6,0,6,0");


    SpinnerModel lightBModel =
      new SpinnerNumberModel(
        /* value= */ 0.5,
        /* minimum= */ 0.0,
        /* maximum= */ 1.0,
        /* step= */ 0.01);
    bSpinner = new JSpinner(lightBModel);
    bSpinner.setPreferredSize(new Dimension(100,10));
    this.add(bSpinner, "10,0,10,0");

    this.add(new JLabel("Light Dir Theta:"), "0,2,0,2");
    this.add(new JLabel("Light Dir Phi:"), "4,2,4,2");

    SpinnerModel lightDirThetaModel =
      new SpinnerNumberModel(
        /* value= */ 90.0,
        /* minimum= */ 0.0,
        /* maximum= */ 180.0,
        /* step= */ 1.0);
    thetaSpinner = new JSpinner(lightDirThetaModel);
    thetaSpinner.setPreferredSize(new Dimension(100,10));
    this.add(thetaSpinner, "2,2,2,2");

    SpinnerModel lightDirPhiModel =
      new SpinnerNumberModel(
        /* value= */ 0.0,
        /* minimum= */ -360.0,
        /* maximum= */ 360.0,
        /* step= */ 1.0);
    phiSpinner = new JSpinner(lightDirPhiModel);
    phiSpinner.setPreferredSize(new Dimension(100,10));
    this.add(phiSpinner, "6,2,6,2");
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
          TableLayout.MINIMUM, 5,
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

  public Vector3d getDirection() {
    double theta = MathUtil.degToRad(
      ((SpinnerNumberModel) thetaSpinner.getModel()).getNumber().doubleValue());
    double phi = MathUtil.degToRad(
      ((SpinnerNumberModel) phiSpinner.getModel()).getNumber().doubleValue());
    return new Vector3d(
      Math.sin(theta) * Math.sin(phi),
      Math.cos(theta),
      Math.sin(theta) * Math.cos(phi));
  }
}
