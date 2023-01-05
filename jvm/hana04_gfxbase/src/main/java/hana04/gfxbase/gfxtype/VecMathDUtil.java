package hana04.gfxbase.gfxtype;

import hana04.gfxbase.util.MathUtil;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple4d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

/**
 * Some part of this file comes from MikuMikuFlex library.
 */
public class VecMathDUtil {
  public static Vector2d vec2(double x, double y) {
    return new Vector2d(x, y);
  }

  public static Vector2d add(Vector2d a, Vector2d b) {
    return vec2(a.x + b.x, a.y + b.y);
  }

  public static Vector2d sub(Vector2d a, Vector2d b) {
    return vec2(a.x - b.x, a.y - b.y);
  }

  public static Vector2d sub(Point2d a, Point2d b) {
    return vec2(a.x - b.x, a.y - b.y);
  }

  public static Vector2d mul(Vector2d a, double c) {
    return vec2(a.x * c, a.y * c);
  }

  public static Vector2d mul(double c, Vector2d a) {
    return vec2(a.x * c, a.y * c);
  }

  public static Vector2d mul(Vector2d a, Vector2d b) {
    return vec2(a.x * b.x, a.y * b.y);
  }

  public static Vector2d div(Vector2d a, Vector2d b) {
    return vec2(a.x / b.x, a.y / b.y);
  }

  public static Vector2d div(Vector2d a, double c) {
    return vec2(a.x / c, a.y / c);
  }

  public static Vector3d vec3(double x, double y, double z) {
    return new Vector3d(x, y, z);
  }

  public static Vector3d add(Vector3d a, Vector3d b) {
    return vec3(a.x + b.x, a.y + b.y, a.z + b.z);
  }

  public static Vector3d sub(Vector3d a, Vector3d b) {
    return vec3(a.x - b.x, a.y - b.y, a.z - b.z);
  }

  public static Vector3d scale(Vector3d a, double c) {
    return new Vector3d(a.x * c, a.y * c, a.z * c);
  }

  public static Vector3d scale(double c, Vector3d a) {
    return new Vector3d(a.x * c, a.y * c, a.z * c);
  }

  public static Point3d scale(double c, Point3d a) {
    return new Point3d(a.x * c, a.y * c, a.z * c);
  }

  public static Vector3d negate(Vector3d v) {
    return new Vector3d(-v.x, -v.y, -v.z);
  }

  public static Vector3d cross(Vector3d a, Vector3d b) {
    Vector3d c = new Vector3d();
    c.cross(a, b);
    return c;
  }

  public static Vector3d normalize(Vector3d a) {
    Vector3d b = new Vector3d(a);
    b.normalize();
    return b;
  }

  public static Vector3d toVector3d(Tuple4d v) {
    return new Vector3d(v.x, v.y, v.z);
  }

  /**
   * Complete the set {a} to an orthonormal base
   *
   * @param a a vector
   * @param b the receiver of one basis vector
   * @param c the receiver of one basis vector
   */
  public static void coordinateSystem(Vector3d a, Vector3d b, Vector3d c) {
    a.normalize();
    if (Math.abs(a.x) > Math.abs(a.y)) {
      double invLen = 1.0 / Math.sqrt(a.x * a.x + a.z * a.z);
      c.set(a.z * invLen, 0.0f, -a.x * invLen);
    } else {
      double invLen = 1.0f / Math.sqrt(a.y * a.y + a.z * a.z);
      c.set(0.0f, a.z * invLen, -a.y * invLen);
    }
    b.cross(c, a);
  }

  /**
   * Compute a direction for the given coordinates in spherical coordinates.
   *
   * @param theta  the elevation angle, in radian
   * @param phi    the azimuthal angle, in radian
   * @param output the receiver of the direction
   */
  public static void sphericalDirection(double theta, double phi, Tuple3d output) {
    double sinTheta = Math.sin(theta);
    double cosTheta = Math.cos(theta);
    double sinPhi = Math.sin(phi);
    double cosPhi = Math.cos(phi);

    output.set(
        sinTheta * cosPhi,
        sinTheta * sinPhi,
        cosTheta
    );
  }

  public static Vector3d sub(Point3d a, Point3d b) {
    return vec3(a.x - b.x, a.y - b.y, a.z - b.z);
  }

  public static Quat4d toQuat4d(AxisAngle4d axisAngle4d) {
    Quat4d result = new Quat4d();
    result.set(axisAngle4d);
    return result;
  }

  public static class DirectionFromSubject {
    private final Point3d p;

    DirectionFromSubject(Point3d p) {
      this.p = new Point3d(p);
    }

    public Vector3d to(Point3d q) {
      return normalize(sub(q, p));
    }
  }

  public static DirectionFromSubject directionFrom(Point3d p) {
    return new DirectionFromSubject(p);
  }

  public static Quat4d mul(Quat4d a, Quat4d b) {
    Quat4d c = new Quat4d();
    c.mul(a, b);
    return c;
  }

  public static Quat4d conjugate(Quat4d a) {
    return new Quat4d(-a.x, -a.y, -a.z, a.w);
  }

  /**
   * Rotate the point p with the given quaternion q. q must be a unit quaternion.
   */
  public static Point3d rotate(Quat4d q, Point3d p) {
    /*
    Quat4d pp = new Quat4d(p.x, p.y, p.z, 0);
    Quat4d result = mul(q, mul(pp, conjugate(q)));
    return new Point3d(result.x, result.y, result.z);
    */
    Matrix4d M = new Matrix4d();
    M.set(q);
    return Matrix4dUtil.transform(M, p);
  }

  public static double length(Quat4d q) {
    return Math.sqrt(q.x * q.x + q.y * q.y + q.z * q.z + q.w * q.w);
  }

  public static Quat4d restrictToAxis(Quat4d q, Vector3d axis) {
    double w = q.w;
    double dotProd = q.x * axis.x + q.y * axis.y + q.z * axis.z;
    double theta = 2 * Math.atan2(dotProd, w);
    return createQuat(axis, MathUtil.radToDeg(theta));
  }

  public static Quat4d createQuat(Vector3d axis, double deg) {
    double rad = MathUtil.degToRad(deg / 2);
    double c = Math.cos(rad);
    double s = Math.sin(rad);
    return new Quat4d(axis.x * s, axis.y * s, axis.z * s, c);
  }

  public static Quat4d interpolate(Quat4d q0, Quat4d q1, double alpha) {
    Quat4d result = new Quat4d();
    result.interpolate(q0, q1, alpha);
    return result;
  }

  public static float[] matrixToFloatArrayColumnMajor(Matrix4d m) {
    float[] result = new float[16];

    result[0] = (float) m.m00;
    result[1] = (float) m.m10;
    result[2] = (float) m.m20;
    result[3] = (float) m.m30;

    result[4] = (float) m.m01;
    result[5] = (float) m.m11;
    result[6] = (float) m.m21;
    result[7] = (float) m.m31;

    result[8] = (float) m.m02;
    result[9] = (float) m.m12;
    result[10] = (float) m.m22;
    result[11] = (float) m.m32;

    result[12] = (float) m.m03;
    result[13] = (float) m.m13;
    result[14] = (float) m.m23;
    result[15] = (float) m.m33;

    return result;
  }

  public static void normalizeEulerAngle(Tuple3d source) {
    while (!MathUtil.between(source.x, -Math.PI, Math.PI)) {
      if (source.x > Math.PI) {
        source.x -= Math.PI * 2;
      } else {
        source.x += Math.PI * 2;
      }
    }
    while (!MathUtil.between(source.y, -Math.PI, Math.PI)) {
      if (source.y > Math.PI) {
        source.y -= Math.PI * 2;
      } else {
        source.y += Math.PI * 2;
      }
    }
    while (!MathUtil.between(source.z, -Math.PI, Math.PI)) {
      if (source.z > Math.PI) {
        source.z -= Math.PI * 2;
      } else {
        source.z += Math.PI * 2;
      }
    }
  }

  public static boolean factorQuaternionZXY(Quat4d input, Tuple3d out) {
    Quat4d inputQ = new Quat4d();
    inputQ.set(input.x, input.y, input.z, input.w);
    inputQ.normalize();

    Matrix4f rot = new Matrix4f();
    rot.setIdentity();
    rot.setRotation(inputQ);
    rot.transpose();

    //ヨー(X軸周りの回転)を取得
    if (rot.m21 > 1 - 1.0e-4 || rot.m21 < -1 + 1.0e-4) {//ジンバルロック判定
      out.x = (rot.m21 < 0 ? Math.PI / 2 : -Math.PI / 2);
      out.z = 0;
      out.y = Math.atan2(-rot.m02, rot.m00);
      return false;
    }
    out.x = -Math.asin(rot.m21);
    //ロールを取得
    out.z = Math.asin(rot.m01 / Math.cos(out.x));
    if (Double.isNaN(out.z)) {//漏れ対策
      out.x = (rot.m21 < 0 ? Math.PI / 2 : -Math.PI / 2);
      out.z = 0;
      out.y = Math.atan2(-rot.m02, rot.m00);
      return false;
    }
    if (rot.m11 < 0) {
      out.z = (Math.PI - out.z);
    }
    //ピッチを取得
    out.y = Math.atan2(rot.m20, rot.m22);
    return true;
  }

  public static boolean factorQuaternionXYZ(Quat4d input, Tuple3d out) {
    Quat4d inputQ = new Quat4d();
    inputQ.set(input.x, input.y, input.z, input.w);
    inputQ.normalize();

    Matrix4f rot = new Matrix4f();
    rot.setIdentity();
    rot.setRotation(inputQ);
    rot.transpose();

    //Y軸回りの回転を取得
    if (rot.m02 > 1 - 1.0e-4 || rot.m02 < -1 + 1.0e-4) {//ジンバルロック判定
      out.x = 0;
      out.y = (rot.m02 < 0 ? Math.PI / 2 : -Math.PI / 2);
      out.z = -Math.atan2(-rot.m10, rot.m11);
      return false;
    }
    out.y = -Math.asin(rot.m02);
    //X軸回りの回転を取得
    out.x = Math.asin(rot.m12 / Math.cos(out.y));
    if (Double.isNaN(out.x)) {//ジンバルロック判定(漏れ対策)
      out.x = 0;
      out.y = (rot.m02 < 0 ? Math.PI / 2 : -Math.PI / 2);
      out.z = -Math.atan2(-rot.m10, rot.m11);
      return false;
    }
    if (rot.m22 < 0) {
      out.x = (Math.PI - out.x);
    }
    //Z軸回りの回転を取得
    out.z = Math.atan2(rot.m01, rot.m00);
    return true;
  }

  public static boolean factorQuaternionYZX(Quat4d input, Tuple3d out) {
    Quat4d inputQ = new Quat4d();
    inputQ.set(input.x, input.y, input.z, input.w);
    inputQ.normalize();

    Matrix4f rot = new Matrix4f();
    rot.setIdentity();
    rot.setRotation(inputQ);
    rot.transpose();

    //Z軸回りの回転を取得
    if (rot.m10 > 1 - 1.0e-4 || rot.m10 < -1 + 1.0e-4) {//ジンバルロック判定
      out.y = 0;
      out.z = (rot.m10 < 0 ? Math.PI / 2 : -Math.PI / 2);
      out.x = -Math.atan2(-rot.m21, rot.m22);
      return false;
    }
    out.z = -Math.asin(rot.m10);
    //Y軸回りの回転を取得
    out.y = Math.asin(rot.m20 / Math.cos(out.z));
    if (Double.isNaN(out.y)) {//ジンバルロック判定(漏れ対策)
      out.y = 0;
      out.z = (rot.m10 < 0 ? Math.PI / 2 : -Math.PI / 2);
      out.x = -Math.atan2(-rot.m21, rot.m22);
      return false;
    }
    if (rot.m00 < 0) {
      out.y = (Math.PI - out.y);
    }
    //X軸回りの回転を取得
    out.x = Math.atan2(rot.m12, rot.m11);
    return true;
  }

  public static void eulerXYZToQuaternion(Vector3d euler, Quat4d q) {
    Quat4d qx = new Quat4d();
    Quat4d qy = new Quat4d();
    Quat4d qz = new Quat4d();
    AxisAngle4d aax = new AxisAngle4d();
    AxisAngle4d aay = new AxisAngle4d();
    AxisAngle4d aaz = new AxisAngle4d();

    aax.set(1, 0, 0, euler.x);
    aay.set(0, 1, 0, euler.y);
    aaz.set(0, 0, 1, euler.z);
    qx.set(aax);
    qy.set(aay);
    qz.set(aaz);

    q.set(qz);
    q.mul(qy);
    q.mul(qx);
  }

  public static void eulerYZXToQuaternion(Vector3d euler, Quat4d q) {
    Quat4d qx = new Quat4d();
    Quat4d qy = new Quat4d();
    Quat4d qz = new Quat4d();
    AxisAngle4d aax = new AxisAngle4d();
    AxisAngle4d aay = new AxisAngle4d();
    AxisAngle4d aaz = new AxisAngle4d();

    aax.set(1, 0, 0, euler.x);
    aay.set(0, 1, 0, euler.y);
    aaz.set(0, 0, 1, euler.z);
    qx.set(aax);
    qy.set(aay);
    qz.set(aaz);

    q.set(qx);
    q.mul(qz);
    q.mul(qy);
  }

  public static void eulerZXYToQuaternion(Vector3d euler, Quat4d q) {
    Quat4d qx = new Quat4d();
    Quat4d qy = new Quat4d();
    Quat4d qz = new Quat4d();
    AxisAngle4d aax = new AxisAngle4d();
    AxisAngle4d aay = new AxisAngle4d();
    AxisAngle4d aaz = new AxisAngle4d();

    aax.set(1, 0, 0, euler.x);
    aay.set(0, 1, 0, euler.y);
    aaz.set(0, 0, 1, euler.z);
    qx.set(aax);
    qy.set(aay);
    qz.set(aaz);

    q.set(qy);
    q.mul(qx);
    q.mul(qz);
  }

  public static void yawPitchRollToQuaternion(double yawRad, double pitchRad, double rollRad, Quat4d quaternion) {
    quaternion.x =
        ((Math.cos((yawRad * 0.5)) * Math.sin(pitchRad * 0.5)) * Math.cos((rollRad * 0.5)))
            + ((Math.sin(yawRad * 0.5) * Math.cos(pitchRad * 0.5)) * Math.sin(rollRad * 0.5));
    quaternion.y =
        ((Math.sin((yawRad * 0.5)) * Math.cos(pitchRad * 0.5)) * Math.cos((rollRad * 0.5)))
            - ((Math.cos(yawRad * 0.5) * Math.sin(pitchRad * 0.5)) * Math.sin(rollRad * 0.5));
    quaternion.z = ((Math.cos((yawRad * 0.5)) * Math.cos(pitchRad * 0.5)) * Math.sin((rollRad * 0.5)))
        - ((Math.sin(yawRad * 0.5) * Math.sin((pitchRad * 0.5))) * Math.cos((rollRad * 0.5)));
    quaternion.w = ((Math.cos((yawRad * 0.5)) * Math.cos((pitchRad * 0.5))) * Math.cos((rollRad * 0.5)))
        + ((Math.sin((yawRad * 0.5)) * Math.sin((pitchRad * 0.5))) * Math.sin((rollRad * 0.5)));
  }

  public static Vector3d min(Vector3d a, Vector3d b) {
    return new Vector3d(Math.min(a.x,b.x), Math.min(a.y, b.y), Math.min(a.z, b.z));
  }

  public static Vector3d max(Vector3d a, Vector3d b) {
    return new Vector3d(Math.max(a.x,b.x), Math.max(a.y, b.y), Math.max(a.z, b.z));
  }
}
