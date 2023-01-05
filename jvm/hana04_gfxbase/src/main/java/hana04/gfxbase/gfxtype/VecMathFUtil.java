package hana04.gfxbase.gfxtype;

import hana04.gfxbase.util.MathUtil;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Tuple4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

public class VecMathFUtil {
  public static final Point3f ORIGIN = new Point3f(0, 0, 0);
  public static final Vector3f ZERO_VECTOR = new Vector3f(0, 0, 0);
  public static final Vector3f X_AXIS = new Vector3f(1, 0, 0);
  public static final Vector3f Y_AXIS = new Vector3f(0, 1, 0);
  public static final Vector3f Z_AXIS = new Vector3f(0, 0, 1);
  public static final Quat4f QUAT_IDENTITY = new Quat4f(0, 0, 0, 1);

  public static Vector2f vec2(float x, float y) {
    return new Vector2f(x, y);
  }

  public static Vector2f add(Vector2f a, Vector2f b) {
    return vec2(a.x + b.x, a.y + b.y);
  }

  public static Vector2f sub(Vector2f a, Vector2f b) {
    return vec2(a.x - b.x, a.y - b.y);
  }

  public static Vector2f sub(Point2f a, Point2f b) {
    return vec2(a.x - b.x, a.y - b.y);
  }

  public static Vector2f mul(Vector2f a, float c) {
    return vec2(a.x * c, a.y * c);
  }

  public static Vector2f mul(float c, Vector2f a) {
    return vec2(a.x * c, a.y * c);
  }

  public static Vector2f mul(Vector2f a, Vector2f b) {
    return vec2(a.x * b.x, a.y * b.y);
  }

  public static Vector2f div(Vector2f a, Vector2f b) {
    return vec2(a.x / b.x, a.y / b.y);
  }

  public static Vector2f div(Vector2f a, float c) {
    return vec2(a.x / c, a.y / c);
  }

  public static Vector3f vec3(float x, float y, float z) {
    return new Vector3f(x, y, z);
  }

  public static Point3f add(Point3f a, Vector3f b) {
    return new Point3f(a.x + b.x, a.y + b.y, a.z + b.z);
  }

  public static Point3f add(Vector3f a, Point3f b) {
    return new Point3f(a.x + b.x, a.y + b.y, a.z + b.z);
  }

  public static Vector3f add(Vector3f a, Vector3f b) {
    return vec3(a.x + b.x, a.y + b.y, a.z + b.z);
  }

  public static Vector3f sub(Tuple3f a, Tuple3f b) {
    return vec3(a.x - b.x, a.y - b.y, a.z - b.z);
  }

  public static Vector3f scale(Vector3f a, float c) {
    return new Vector3f(a.x * c, a.y * c, a.z * c);
  }

  public static Vector3f scale(float c, Vector3f a) {
    return new Vector3f(a.x * c, a.y * c, a.z * c);
  }

  public static Point3d scale(float c, Point3d a) {
    return new Point3d(a.x * c, a.y * c, a.z * c);
  }

  public static Vector3f negate(Vector3f v) {
    return new Vector3f(-v.x, -v.y, -v.z);
  }

  public static Vector3f interpolate(Vector3f a0, Vector3f a1, float alpha) {
    Vector3f result = new Vector3f();
    result.scaleAdd(1.0f - alpha, a0);
    result.scaleAdd(alpha, a1);
    return result;
  }

  public static Vector3f cross(Vector3f a, Vector3f b) {
    Vector3f c = new Vector3f();
    c.cross(a, b);
    return c;
  }

  public static Vector3f normalize(Vector3f a) {
    Vector3f b = new Vector3f(a);
    b.normalize();
    return b;
  }

  public static Vector3f toVector3f(Tuple4f v) {
    return new Vector3f(v.x, v.y, v.z);
  }

  public static Quat4f inverse(Quat4f q) {
    Quat4f result = new Quat4f();
    result.set(q);
    result.inverse();
    return result;
  }

  public static Quat4f mul(Quat4f q0, Quat4f q1) {
    Quat4f result = new Quat4f();
    result.mul(q0, q1);
    return result;
  }

  public static Quat4f interpolate(Quat4f q0, Quat4f q1, float alpha) {
    Quat4f result = new Quat4f();
    result.interpolate(q0, q1, alpha);
    return result;
  }

  public static Quat4f normalize(Quat4f q) {
    Quat4f result = new Quat4f(q);
    result.normalize();
    return result;
  }

  public static Vector3f rotate(Vector3f v, Quat4f q) {
    q = normalize(q);
    Quat4f vv = new Quat4f(v.x, v.y, v.z, 0.0f);
    Quat4f r = new Quat4f(0, 0, 0, 1);
    r.mul(q);
    r.mul(vv);
    r.mul(inverse(q));
    return new Vector3f(r.x, r.y, r.z);
  }

  public static Quat4f create(Vector3f axis, float deg) {
    double rad = MathUtil.degToRad(deg / 2);
    float c = (float) Math.cos(rad);
    float s = (float) Math.sin(rad);
    return new Quat4f(axis.x * s, axis.y * s, axis.z * s, c);
  }

  public static void quaternionToEuler(Quat4f q, Vector3f result) {
    float x2 = q.x + q.x;
    float y2 = q.y + q.y;
    float z2 = q.z + q.z;
    float xz2 = q.x * z2;
    float wy2 = q.w * y2;
    float temp = -(xz2 - wy2);

    if (temp >= 1) {
      temp = 1;
    } else if (temp <= -1) {
      temp = -1;
    }

    float yRadian = (float) Math.asin(temp);

    float xx2 = q.x * x2;
    float xy2 = q.x * y2;
    float zz2 = q.z * z2;
    float wz2 = q.w * z2;

    if (yRadian < 3.1415926f * 0.5f) {
      if (yRadian > -3.1415926f * 0.5f) {
        float yz2 = q.y * z2;
        float wx2 = q.w * x2;
        float yy2 = q.y * y2;
        result.x = (float) Math.atan2((yz2 + wx2), (1 - (xx2 + yy2)));
        result.y = yRadian;
        result.z = (float) Math.atan2((xy2 + wz2), (1 - (yy2 + zz2)));
      } else {
        result.x = -(float) Math.atan2((xy2 - wz2), (1 - (xx2 + zz2)));
        result.y = yRadian;
        result.z = 0;
      }
    } else {
      result.x = (float) Math.atan2((xy2 - wz2), (1 - (xx2 + zz2)));
      result.y = yRadian;
      result.z = 0;
    }
  }

  public static void eulerToQuaternion(Vector3f euler, Quat4f result) {
    float xRadian = euler.x * 0.5f;
    float yRadian = euler.y * 0.5f;
    float zRadian = euler.z * 0.5f;
    float sinX = (float) Math.sin(xRadian);
    float cosX = (float) Math.cos(xRadian);
    float sinY = (float) Math.sin(yRadian);
    float cosY = (float) Math.cos(yRadian);
    float sinZ = (float) Math.sin(zRadian);
    float cosZ = (float) Math.cos(zRadian);

    result.x = sinX * cosY * cosZ - cosX * sinY * sinZ;
    result.y = cosX * sinY * cosZ + sinX * cosY * sinZ;
    result.z = cosX * cosY * sinZ - sinX * sinY * cosZ;
    result.w = cosX * cosY * cosZ + sinX * sinY * sinZ;
    result.normalize();
  }


  public static void yawPitchRollToQuaternion(float yawRad, float pitchRad, float rollRad, Quat4f quaternion) {
    quaternion.x =
        (((float) Math.cos((yawRad * 0.5f)) * (float) Math.sin(pitchRad * 0.5f))
             * (float) Math.cos((rollRad * 0.5f))) + (((float) Math.sin(yawRad * 0.5f)
                                                           * (float) Math.cos(pitchRad * 0.5f))
                                                          * (float) Math.sin(rollRad * 0.5f));
    quaternion.y =
        (((float) Math.sin((yawRad * 0.5f)) * (float) Math.cos(pitchRad * 0.5f))
             * (float) Math.cos((rollRad * 0.5f))) - (((float) Math.cos(yawRad * 0.5f)
                                                           * (float) Math.sin(pitchRad * 0.5f))
                                                          * (float) Math.sin(rollRad * 0.5f));
    quaternion.z =
        (((float) Math.cos((yawRad * 0.5f)) * (float) Math.cos(pitchRad * 0.5f))
             * (float) Math.sin((rollRad * 0.5f))) - (((float) Math.sin(yawRad * 0.5f)
                                                           * (float) Math.sin((pitchRad * 0.5f)))
                                                          * (float) Math.cos((rollRad * 0.5f)));
    quaternion.w =
        (((float) Math.cos((yawRad * 0.5f)) * (float) Math.cos((pitchRad * 0.5f)))
             * (float) Math.cos((rollRad * 0.5f))) + (((float) Math.sin((yawRad * 0.5f))
                                                           * (float) Math.sin((pitchRad * 0.5f)))
                                                          * (float) Math.sin((rollRad * 0.5f)));
  }

  public static Quat4f yawPitchRollToQuaternion(float yawRad, float pitchRad, float rollRad) {
    Quat4f output = new Quat4f();
    yawPitchRollToQuaternion(yawRad, pitchRad, rollRad, output);
    return output;
  }

  public static Matrix4f matrixFromFrame(Tuple3f x, Tuple3f y, Tuple3f z) {
    Matrix4f M = new Matrix4f();
    M.setIdentity();

    M.m00 = x.x;
    M.m10 = x.y;
    M.m20 = x.z;

    M.m01 = y.x;
    M.m11 = y.y;
    M.m21 = y.z;

    M.m02 = z.x;
    M.m12 = z.y;
    M.m22 = z.z;

    return M;
  }

  public static float[] matrixToFloatArrayColumnMajor(Matrix4f m) {
    float[] result = new float[16];

    result[0] = m.m00;
    result[1] = m.m10;
    result[2] = m.m20;
    result[3] = m.m30;

    result[4] = m.m01;
    result[5] = m.m11;
    result[6] = m.m21;
    result[7] = m.m31;

    result[8] = m.m02;
    result[9] = m.m12;
    result[10] = m.m22;
    result[11] = m.m32;

    result[12] = m.m03;
    result[13] = m.m13;
    result[14] = m.m23;
    result[15] = m.m33;

    return result;
  }

  public static float getCompenent(Tuple3f p, int dim) {
    if (dim == 0) {
      return p.x;
    } else if (dim == 1) {
      return p.y;
    } else if (dim == 2) {
      return p.z;
    } else {
      throw new RuntimeException("dim was neither 0, 1, or 2");
    }
  }

  public static boolean factorQuaternionZXY(Quat4f input, Tuple3f out) {
    Quat4f inputQ = new Quat4f();
    inputQ.set(input.x, input.y, input.z, input.w);
    inputQ.normalize();

    Matrix4f rot = new Matrix4f();
    rot.setIdentity();
    rot.setRotation(inputQ);
    rot.transpose();

    //ヨー(X軸周りの回転)を取得
    if (rot.m21 > 1 - 1.0e-4 || rot.m21 < -1 + 1.0e-4) {//ジンバルロック判定
      out.x = (float) (rot.m21 < 0 ? Math.PI / 2 : -Math.PI / 2);
      out.z = 0;
      out.y = (float) Math.atan2(-rot.m02, rot.m00);
      return false;
    }
    out.x = -(float) Math.asin(rot.m21);
    //ロールを取得
    out.z = (float) Math.asin(rot.m01 / Math.cos(out.x));
    if (Float.isNaN(out.z)) {//漏れ対策
      out.x = (float) (rot.m21 < 0 ? Math.PI / 2 : -Math.PI / 2);
      out.z = 0;
      out.y = (float) Math.atan2(-rot.m02, rot.m00);
      return false;
    }
    if (rot.m11 < 0) {
      out.z = (float) (Math.PI - out.z);
    }
    //ピッチを取得
    out.y = (float) Math.atan2(rot.m20, rot.m22);
    return true;
  }

  public static boolean factorQuaternionXYZ(Quat4f input, Tuple3f out) {
    Quat4f inputQ = new Quat4f();
    inputQ.set(input.x, input.y, input.z, input.w);
    inputQ.normalize();

    Matrix4f rot = new Matrix4f();
    rot.setIdentity();
    rot.setRotation(inputQ);
    rot.transpose();

    //Y軸回りの回転を取得
    if (rot.m02 > 1 - 1.0e-4 || rot.m02 < -1 + 1.0e-4) {//ジンバルロック判定
      out.x = 0;
      out.y = (float) (rot.m02 < 0 ? Math.PI / 2 : -Math.PI / 2);
      out.z = -(float) Math.atan2(-rot.m10, rot.m11);
      return false;
    }
    out.y = -(float) Math.asin(rot.m02);
    //X軸回りの回転を取得
    out.x = (float) Math.asin(rot.m12 / Math.cos(out.y));
    if (Float.isNaN(out.x)) {//ジンバルロック判定(漏れ対策)
      out.x = 0;
      out.y = (float) (rot.m02 < 0 ? Math.PI / 2 : -Math.PI / 2);
      out.z = -(float) Math.atan2(-rot.m10, rot.m11);
      return false;
    }
    if (rot.m22 < 0) {
      out.x = (float) (Math.PI - out.x);
    }
    //Z軸回りの回転を取得
    out.z = (float) Math.atan2(rot.m01, rot.m00);
    return true;
  }

  public static boolean factorQuaternionYZX(Quat4f input, Tuple3f out) {
    Quat4f inputQ = new Quat4f();
    inputQ.set(input.x, input.y, input.z, input.w);
    inputQ.normalize();

    Matrix4f rot = new Matrix4f();
    rot.setIdentity();
    rot.setRotation(inputQ);
    rot.transpose();

    //Z軸回りの回転を取得
    if (rot.m10 > 1 - 1.0e-4 || rot.m10 < -1 + 1.0e-4) {//ジンバルロック判定
      out.y = 0;
      out.z = (float) (rot.m10 < 0 ? Math.PI / 2 : -Math.PI / 2);
      out.x = -(float) Math.atan2(-rot.m21, rot.m22);
      return false;
    }
    out.z = -(float) Math.asin(rot.m10);
    //Y軸回りの回転を取得
    out.y = (float) Math.asin(rot.m20 / Math.cos(out.z));
    if (Float.isNaN(out.y)) {//ジンバルロック判定(漏れ対策)
      out.y = 0;
      out.z = (float) (rot.m10 < 0 ? Math.PI / 2 : -Math.PI / 2);
      out.x = -(float) Math.atan2(-rot.m21, rot.m22);
      return false;
    }
    if (rot.m00 < 0) {
      out.y = (float) (Math.PI - out.y);
    }
    //X軸回りの回転を取得
    out.x = (float) Math.atan2(rot.m12, rot.m11);
    return true;
  }

  public static void eulerXYZToQuaternion(Vector3f euler, Quat4f q) {
    Quat4f qx = new Quat4f();
    Quat4f qy = new Quat4f();
    Quat4f qz = new Quat4f();
    AxisAngle4f aax = new AxisAngle4f();
    AxisAngle4f aay = new AxisAngle4f();
    AxisAngle4f aaz = new AxisAngle4f();

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

  public static void eulerYZXToQuaternion(Vector3f euler, Quat4f q) {
    Quat4f qx = new Quat4f();
    Quat4f qy = new Quat4f();
    Quat4f qz = new Quat4f();
    AxisAngle4f aax = new AxisAngle4f();
    AxisAngle4f aay = new AxisAngle4f();
    AxisAngle4f aaz = new AxisAngle4f();

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

  public static void eulerZXYToQuaternion(Vector3f euler, Quat4f q) {
    Quat4f qx = new Quat4f();
    Quat4f qy = new Quat4f();
    Quat4f qz = new Quat4f();
    AxisAngle4f aax = new AxisAngle4f();
    AxisAngle4f aay = new AxisAngle4f();
    AxisAngle4f aaz = new AxisAngle4f();

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

  public static void normalizeEulerAngle(Tuple3f source) {
    while (!MathUtil.between(source.x, (float) -Math.PI, (float) Math.PI)) {
      if (source.x > Math.PI) {
        source.x -= (float) Math.PI * 2;
      } else {
        source.x += (float) Math.PI * 2;
      }
    }
    while (!MathUtil.between(source.y, (float) -Math.PI, (float) Math.PI)) {
      if (source.y > Math.PI) {
        source.y -= (float) Math.PI * 2;
      } else {
        source.y += (float) Math.PI * 2;
      }
    }
    while (!MathUtil.between(source.z, (float) -Math.PI, (float) Math.PI)) {
      if (source.z > Math.PI) {
        source.z -= (float) Math.PI * 2;
      } else {
        source.z += (float) Math.PI * 2;
      }
    }
  }

  public static void coordinateSystem(Vector3f normal, Vector3f tangent, Vector3f binormal) {
    normal.normalize();

    float nx = Math.abs(normal.x);
    float ny = Math.abs(normal.y);
    float nz = Math.abs(normal.z);

    if (nx > ny && nx > nz) {
      tangent.set(-normal.y, normal.x, 0);
    } else if (ny > nx && ny > nz) {
      tangent.set(0, -normal.z, normal.y);
    } else {
      tangent.set(normal.z, 0, -normal.x);
    }
    tangent.normalize();

    binormal.cross(normal, tangent);
    binormal.normalize();
  }

  public static void coordinateSystemGivenZandY(Vector3f x, Vector3f y, Vector3f z) {
    z.normalize();
    x.cross(y, z);
    x.normalize();
    y.cross(z, x);
    y.normalize();
  }

  /**
   * Rotates the tuple (vector or point) by a quaternion.
   * <p>
   * Just does `quat * tuple * inverse(quat)`.
   *
   * @param quat  The quaternion to rotate by.
   * @param tuple The tuple to rotate. The rotation is done in-place; on
   *              output, `tuple` has been rotated by `quat`.
   */
  public static void rotateTuple(Quat4f quat, Tuple3f tuple) {
    if (tuple.x == 0.0f && tuple.y == 0.0f && tuple.z == 0.0f) {
      return;
    }

    /* Quat4f.mul() implicitly normalizes the result, so remember the length. */
    float length = (float) Math.sqrt(tuple.x * tuple.x + tuple.y * tuple.y + tuple.z * tuple.z);
    tuple.scale(1.0f / length);

    /* quat * tuple * inverse(quat) */
    Quat4f temp = new Quat4f(quat);
    temp.mul(new Quat4f(tuple.x, tuple.y, tuple.z, 0.0f));
    temp.mulInverse(quat);

    tuple.x = temp.x * length;
    tuple.y = temp.y * length;
    tuple.z = temp.z * length;
  }

  public static void set3x3Part(Matrix3f A, Matrix4f B) {
    A.m00 = B.m00;
    A.m01 = B.m01;
    A.m02 = B.m02;
    A.m10 = B.m10;
    A.m11 = B.m11;
    A.m12 = B.m12;
    A.m20 = B.m20;
    A.m21 = B.m21;
    A.m22 = B.m22;
  }

    /*
  public static Quat4f restrictToAxis(Quat4f q, Vector3f axis) {
    axis = normalize(axis);
    Vector3f rotatedAxis = rotate(axis, q);
    rotatedAxis = normalize(rotatedAxis);
    Vector3f cancelAxis = cross(rotatedAxis, axis);
    if (cancelAxis.lengthSquared() < 1e-10) {
      if (rotatedAxis.dot(axis) > 0) {
        return new Quat4f(q);
      } else {
        coordinateSystem(axis, cancelAxis, new Vector3f());
        Quat4f cancelQ = create(cancelAxis, 180);
        return mul(cancelQ, q);
      }
    } else {
      cancelAxis.normalize();
      float cosTheta = axis.dot(rotatedAxis);
      float cosThetaBy2 = (float) Math.sqrt((cosTheta + 1) / 2);
      float sinThetaBy2 = (float) Math.sqrt(Math.max(0.0f, 1.0f - cosThetaBy2 * cosThetaBy2));
      Quat4f cancelQ =
          new Quat4f(sinThetaBy2 * cancelAxis.x, sinThetaBy2 * cancelAxis.y, sinThetaBy2 * cancelAxis.z, cosThetaBy2);
      return mul(cancelQ, q);
    }
  }
    */

  public static void setComponent(Tuple3f v, int index, float value) {
    switch (index) {
      case 0:
        v.x = value;
        break;
      case 1:
        v.y = value;
        break;
      case 2:
        v.z = value;
        break;
      default:
        throw new RuntimeException("Invalid index: " + index);
    }
  }

  public static Quat4f restrictToAxis(Quat4f q, Vector3f axis) {
    float w = q.w;
    float dotProd = q.x * axis.x + q.y * axis.y + q.z * axis.z;
    double theta = 2 * Math.atan2(dotProd, w);
    return create(axis, (float) MathUtil.radToDeg(theta));
  }

  public static void main(String[] args) {
    /*
    Quat4f q0 = create(new Vector3f(0, 0, 1), 90);
    Quat4f q1 = create(normalize(new Vector3f(1, 0, 0)), 180);
    Quat4f q2 = mul(q1, q0);
    Quat4f result = restrictToAxis(q2, new Vector3f(0, 0, 1));
    System.out.println(result);
     */

    Quat4f q = yawPitchRollToQuaternion((float) Math.PI / 2, (float) Math.PI / 3, (float) Math.PI / 6);
    System.out.println(q);

    Quat4f qx = VecMathFUtil.create(new Vector3f(1, 0, 0), (float) Math.toDegrees(Math.PI / 3));
    Quat4f qy = VecMathFUtil.create(new Vector3f(0, 1, 0), (float) Math.toDegrees(Math.PI / 2));
    Quat4f qz = VecMathFUtil.create(new Vector3f(0, 0, 1), (float) Math.toDegrees(Math.PI / 6));
    Quat4f qq = VecMathFUtil.mul(qy, VecMathFUtil.mul(qx, qz));
    System.out.println(qq);
  }
}
