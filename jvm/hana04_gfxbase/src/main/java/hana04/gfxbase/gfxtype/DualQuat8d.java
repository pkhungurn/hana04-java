package hana04.gfxbase.gfxtype;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple4d;
import javax.vecmath.Vector3d;

public class DualQuat8d {
  public final UnnormalizedQuat4d q0 = new UnnormalizedQuat4d();
  public final UnnormalizedQuat4d qe = new UnnormalizedQuat4d();

  public DualQuat8d() {
    // NO-OP
  }

  public DualQuat8d(Tuple4d q0, Tuple4d qe) {
    this.q0.set(q0);
    this.qe.set(qe);
  }

  public DualQuat8d(UnnormalizedQuat4d q0, UnnormalizedQuat4d qe) {
    this.q0.set(q0);
    this.qe.set(qe);
  }

  public DualQuat8d(double x0, double y0, double z0, double w0, double xe, double ye, double ze, double we) {
    q0.set(x0, y0, z0, w0);
    qe.set(xe, ye, ze, we);
  }

  public static DualQuat8d fromPoint(double x, double y, double z) {
    return new DualQuat8d(0, 0, 0, 1, x, y, z, 0);
  }

  public static DualQuat8d fromPoint(Tuple3d p) {
    return new DualQuat8d(0, 0, 0, 1, p.x, p.y, p.z, 0);
  }

  public static DualQuat8d fromQuaternion(Tuple4d q) {
    return new DualQuat8d(q.x, q.y, q.z, q.w, 0, 0, 0, 0);
  }

  public static DualQuat8d fromQuaternion(UnnormalizedQuat4d q) {
    return new DualQuat8d(q.x, q.y, q.z, q.w, 0, 0, 0, 0);
  }

  public static DualQuat8d fromTranslation(double x, double y, double z) {
    return new DualQuat8d(0, 0, 0, 1, x / 2, y / 2, z / 2, 0);
  }

  public static DualQuat8d fromTranslation(Tuple3d t) {
    return new DualQuat8d(0, 0, 0, 1, t.x / 2, t.y / 2, t.z / 2, 0);
  }

  public static DualQuat8d fromDualNumber(double r, double e) {
    return new DualQuat8d(0, 0, 0, r, 0, 0, 0, e);
  }

  public static DualQuat8d fromDualNumber(DualNumber2d d) {
    return fromDualNumber(d.r, d.e);
  }

  public void mulI(DualQuat8d a, DualQuat8d b) {
    UnnormalizedQuat4d newQ0 = a.q0.mul(b.q0);
    UnnormalizedQuat4d newQe = a.q0.mul(b.qe).add(a.qe.mul(b.q0));
    q0.set(newQ0);
    qe.set(newQe);
  }

  public void mulI(DualQuat8d other) {
    mulI(this, other);
  }

  public DualQuat8d mul(DualQuat8d other) {
    return new DualQuat8d(this.q0.mul(other.q0), this.q0.mul(other.qe).add(this.qe.mul(other.q0)));
  }

  public void addI(DualQuat8d other) {
    this.q0.addI(other.q0);
    this.qe.addI(other.qe);
  }

  public DualQuat8d add(DualQuat8d other) {
    return new DualQuat8d(q0.add(other.q0), qe.add(other.qe));
  }

  public void scaleI(double c) {
    this.q0.scaleI(c);
    this.qe.scaleI(c);
  }

  public DualQuat8d scale(double c) {
    return new DualQuat8d(q0.scale(c), qe.scale(c));
  }

  @Override
  public String toString() {
    return String.format("(%f, %f, %f, %f; %f, %f, %f, %f)", q0.x, q0.y, q0.z, q0.w, qe.x, qe.y, qe.z, qe.w);
  }

  public DualQuat8d conjugate() {
    return new DualQuat8d(q0.conjugate(), qe.conjugate());
  }

  public DualQuat8d doubleConjugate() {
    return new DualQuat8d(-q0.x, -q0.y, -q0.z, q0.w, qe.x, qe.y, qe.z, -qe.w);
  }

  public void applyI(Tuple3d input, Tuple3d output) {
    DualQuat8d p = DualQuat8d.fromPoint(input);
    DualQuat8d result = this.mul(p).mul(this.doubleConjugate());
    output.set(result.qe.x, result.qe.y, result.qe.z);
  }

  public static DualQuat8d fromRotationAndTranslation(Tuple3d disp, Tuple4d quat) {
    return DualQuat8d.fromTranslation(disp).mul(DualQuat8d.fromQuaternion(quat));
  }

  public static DualQuat8d fromMatrix(Matrix4d M) {
    Quat4d q = Matrix4dUtil.rotationPartToQuaternion(M);
    return fromRotationAndTranslation(new Vector3d(M.m03, M.m13, M.m23), q);
  }

  public DualNumber2d normSquared() {
    return new DualNumber2d(q0.normSquared(), 2 * q0.dot(qe));
  }

  public DualNumber2d norm() {
    double q0Norm = q0.norm();
    return new DualNumber2d(q0Norm, q0.dot(qe) / q0Norm);
  }

  public Matrix4d toMatrix() {
    Matrix4d M = new Matrix4d();
    M.set(new Quat4d(q0.x, q0.y, q0.z, q0.w));
    UnnormalizedQuat4d tt = qe.mul(q0.conjugate()).scale(2);
    M.setTranslation(new Vector3d(tt.x, tt.y, tt.z));
    return M;
  }

  public static void main(String[] args) {
    Point3d p = new Point3d(2, 3, 4);
    DualQuat8d T = DualQuat8d.fromTranslation(1, 1, 1);
    Point3d q = new Point3d();
    T.applyI(p, q);
    System.out.println(q);

    AxisAngle4d axisAngle = new AxisAngle4d(new Vector3d(0, 0, 1), Math.PI / 2);
    Quat4d rot = new Quat4d();
    rot.set(axisAngle);
    System.out.println(rot);
    DualQuat8d R = DualQuat8d.fromQuaternion(rot);

    System.out.println(R.q0.mul(new UnnormalizedQuat4d(p.x, p.y, p.z, 0)).mul(R.q0.conjugate()));

    R.applyI(p, q);
    System.out.println(q);
  }
}
