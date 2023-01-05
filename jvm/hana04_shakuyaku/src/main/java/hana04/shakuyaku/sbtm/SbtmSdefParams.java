package hana04.shakuyaku.sbtm;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;

public class SbtmSdefParams {
  public final Point3d C = new Point3d();
  public final Point3d R0 = new Point3d();
  public final Point3d R1 = new Point3d();

  public SbtmSdefParams() {
    // NO-OP
  }

  public SbtmSdefParams(Tuple3d C, Tuple3d R0, Tuple3d R1) {
    this.C.set(C);
    this.R0.set(R0);
    this.R1.set(R1);
  }

  public SbtmSdefParams(Tuple3f C, Tuple3f R0, Tuple3f R1) {
    this.C.set(C);
    this.R0.set(R0);
    this.R1.set(R1);
  }

  /**
   * The copy constructor.
   */
  public SbtmSdefParams(SbtmSdefParams other) {
    C.set(other.C);
    R0.set(other.R0);
    R1.set(other.R1);
  }
}
