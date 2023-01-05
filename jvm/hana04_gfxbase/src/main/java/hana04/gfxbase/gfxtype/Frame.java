package hana04.gfxbase.gfxtype;

import hana04.gfxbase.util.MathUtil;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Quat4d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

/**
 * Stores a three-dimensional orthonormal coordinate frame
 *
 * This class is mostly used to quickly convert between different
 * cartesian coordinate systems and to efficiently compute certain
 * quantities (e.g. cosTheta, tanTheta, ..).
 */
public class Frame {
  /**
   * The first tangent.
   */
  public final Vector3d s = new Vector3d();
  /**
   * The second tangent.
   */
  public final Vector3d t = new Vector3d();
  /**
   * The normal.
   */
  public final Vector3d n = new Vector3d();

  /**
   * Construct the xyz-frame.
   */
  public Frame() {
    s.set(1,0,0);
    t.set(0,1,0);
    n.set(0,0,1);
  }

  /**
   * Construct a frame from the given basis vectors.
   * @param s the first tangent
   * @param t the second tangent
   * @param n the normal
   */
  public Frame(Vector3d s, Vector3d t, Vector3d n) {
    this.s.set(s);
    this.t.set(t);
    this.n.set(n);
  }

  /**
   * Construct a frame from the given normal.
   * @param n the normal
   */
  public Frame(Vector3d n) {
    this.n.set(n);
    VecMathDUtil.coordinateSystem(this.n, s, t);
  }

  /**
   * Copy from another Frame.
   * @param other the other frame
   */
  public void set(Frame other) {
    this.s.set(other.s);
    this.t.set(other.t);
    this.n.set(other.n);
  }

  /**
   * Change the state of this instance to that it becomes a frame with the given normal vector.
   * @param n the normal vector
   */
  public void setFromNormal(Vector3d n) {
    this.n.set(n);
    VecMathDUtil.coordinateSystem(this.n, s, t);
  }

  /**
   * Convert from world coordinates to local coordinates.
   * @param v a vector in world coordinate
   * @param output the receiver of the local coordinates
   */
  public void toLocal(Tuple3d v, Tuple3d output) {
    Vector3d vv = new Vector3d(v);
    double xx = vv.dot(s);
    double yy = vv.dot(t);
    double zz = vv.dot(n);
    output.set(xx, yy, zz);
  }

  /**
   * Convert from local coordinates to world coordinates.
   * @param v a vector in local coordinates
   * @param output the receiver of the vector in world coordinates
   */
  public void toWorld(Tuple3d v, Tuple3d output) {
    Vector3d temp = new Vector3d();
    temp.set(0,0,0);
    temp.scaleAdd(v.x, s, temp);
    temp.scaleAdd(v.y, t, temp);
    temp.scaleAdd(v.z, n, temp);
    output.set(temp);
  }

  /**
   * Assuming that the given direction is in the local coordinate
   * system, return the cosine of the angle between the normal and v
   * @param v a vector in local coordinates
   * @return the cosine of the angle between the normal and v
   */
  public static double cosTheta(Vector3d v) {
    return v.z;
  }

  /**
   * Assuming that the given direction is in the local coordinate
   * system, return the sine of the angle between the normal and v
   */
  public static double sinTheta(Vector3d v) {
    double temp = sinTheta2(v);
    if (temp <= 0.0)
      return 0.0;
    return Math.sqrt(temp);
  }

  /**
   * Assuming that the given direction is in the local coordinate
   * system, return the tangent of the angle between the normal and v
   */
  public static double tanTheta(Vector3d v) {
    double temp = 1 - v.z*v.z;
    if (temp <= 0.0)
      return 0.0;
    return Math.sqrt(temp) / v.z;
  }

  /**
   * Assuming that the given direction is in the local coordinate
   * system, return the squared sine of the angle between the normal and v
   */
  public static double sinTheta2(Vector3d v) {
    return 1.0 - v.z * v.z;
  }

  /**
   *  Assuming that the given direction is in the local coordinate
   * system, return the sine of the phi parameter in spherical coordinates
   */
  public static double sinPhi(Vector3d v) {
    double sinTheta = Frame.sinTheta(v);
    if (sinTheta == 0.0)
      return 1.0f;
    return MathUtil.clamp(v.y / sinTheta, -1.0, 1.0);
  }

  /**
   * Assuming that the given direction is in the local coordinate
   * system, return the cosine of the phi parameter in spherical coordinates
   */
  public static double cosPhi(Vector3d v) {
    double sinTheta = Frame.sinTheta(v);
    if (sinTheta == 0.0)
      return 1.0;
    return MathUtil.clamp(v.x / sinTheta, -1.0, 1.0);
  }

  /**
   * Assuming that the given direction is in the local coordinate
   * system, return the squared sine of the phi parameter in  spherical
   * coordinates
   */
  public static double sinPhi2(Vector3d v) {
    return MathUtil.clamp(v.y * v.y / sinTheta2(v), 0.0, 1.0);
  }

  /**
   * Assuming that the given direction is in the local coordinate
   * system, return the squared cosine of the phi parameter in  spherical
   * coordinates
   */
  public static double cosPhi2(Vector3d v) {
    return MathUtil.clamp(v.x * v.x / sinTheta2(v), 0.0, 1.0);
  }

  /**
   * Equality testing
   * @param obj the object to test equality with
   * @return whether the given statictrimesh is equal to this
   */
  public boolean equals(Object obj) {
    if (obj instanceof Frame) {
      Frame other = (Frame)obj;
      return s.equals(other.s) && t.equals(other.t) && n.equals(other.n);
    } else {
      return false;
    }
  }

  public void getToWorldMatrix3d(Matrix3d m) {
    m.m00 = s.x;
    m.m10 = s.y;
    m.m20 = s.z;

    m.m01 = t.x;
    m.m11 = t.y;
    m.m21 = t.z;

    m.m02 = n.x;
    m.m12 = n.y;
    m.m22 = n.z;
  }

  public String toString() {
    return String.format("[s=%s, t=%s, n=%s]", s.toString(), t.toString(), n.toString());
  }

  public static void interpolate(Frame f1, Frame f2, double alpha, Frame output) {
    Matrix3d m = new Matrix3d();
    f1.getToWorldMatrix3d(m);
    Quat4d q1 = new Quat4d();
    q1.set(m);

    f2.getToWorldMatrix3d(m);
    Quat4d q2 = new Quat4d();
    q2.set(m);

    Quat4d q = new Quat4d();
    q.interpolate(q1, q2, alpha);
    Matrix3d mm = new Matrix3d();
    mm.set(q);

    output.s.set(mm.m00, mm.m10, mm.m20);
    output.t.set(mm.m01, mm.m11, mm.m21);
    output.n.set(mm.m02, mm.m12, mm.m22);
  }

  public void toLocal(Frame input, Frame output) {
    Vector3d n = new Vector3d();
    toLocal(input.n, n);
    Vector3d s = new Vector3d();
    toLocal(input.s, s);
    Vector3d t = new Vector3d();
    toLocal(input.t, t);
    output.n.set(n);
    output.s.set(s);
    output.t.set(t);
  }

  public void toWorld(Frame input, Frame output) {
    Vector3d n = new Vector3d();
    toWorld(input.n, n);
    Vector3d s = new Vector3d();
    toWorld(input.s, s);
    Vector3d t = new Vector3d();
    toWorld(input.t, t);
    output.n.set(n);
    output.s.set(s);
    output.t.set(t);
  }

  public Matrix4d getToWorldMatrix4d() {
    Matrix4d M = new Matrix4d();
    M.setIdentity();

    M.m00 = s.x;
    M.m10 = s.y;
    M.m20 = s.z;

    M.m01 = t.x;
    M.m11 = t.y;
    M.m21 = t.z;

    M.m02 = n.x;
    M.m12 = n.y;
    M.m22 = n.z;

    return M;
  }

  public Matrix4d getToLocalMatrix4d() {
    Matrix4d M = new Matrix4d();
    M.setIdentity();

    M.m00 = s.x;
    M.m01 = s.y;
    M.m02 = s.z;

    M.m10 = t.x;
    M.m11 = t.y;
    M.m12 = t.z;

    M.m20 = n.x;
    M.m21 = n.y;
    M.m22 = n.z;

    return M;
  }
}
