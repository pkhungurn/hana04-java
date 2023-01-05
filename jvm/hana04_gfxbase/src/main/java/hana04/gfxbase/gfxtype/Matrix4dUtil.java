package hana04.gfxbase.gfxtype;

import hana04.gfxbase.util.MathUtil;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Utility for manipulating Matrix4d/Matrix4f type.
 */
public class Matrix4dUtil {
  public static final Matrix4d IDENTITY_MATRIX = new Matrix4d(
    1, 0, 0, 0,
    0, 1, 0, 0,
    0, 0, 1, 0,
    0, 0, 0, 1);

  public static Matrix4d add(Matrix4d A, Matrix4d B) {
    Matrix4d result = new Matrix4d();
    result.add(A, B);
    return result;
  }

  public static Matrix4d sub(Matrix4d A, Matrix4d B) {
    Matrix4d result = new Matrix4d();
    result.sub(A, B);
    return result;
  }

  public static Matrix4d mul(Matrix4d A, Matrix4d B) {
    Matrix4d result = new Matrix4d();
    result.mul(A, B);
    return result;
  }

  public static Matrix4d Mul(Matrix4d... Ms) {
    Matrix4d result = new Matrix4d();
    result.setIdentity();
    for (int i = 0; i < Ms.length; i++) {
      result.mul(Ms[i]);
    }
    return result;
  }

  public static Matrix4d transpose(Matrix4d A) {
    Matrix4d result = new Matrix4d();
    result.transpose(A);
    return result;
  }

  public static Matrix4d inverse(Matrix4d A) {
    Matrix4d result = new Matrix4d();
    result.invert(A);
    return result;
  }

  public static Matrix4d createIdentity() {
    Matrix4d result = new Matrix4d();
    result.setIdentity();
    return result;
  }

  public static void makeTranslation(double x, double y, double z, Matrix4d output) {
    output.setIdentity();
    output.setTranslation(new Vector3d(x, y, z));
  }

  public static Matrix4d createTranslation(double x, double y, double z) {
    Matrix4d result = new Matrix4d();
    makeTranslation(x, y, z, result);
    return result;
  }

  public static void makeScaling(double x, double y, double z, Matrix4d output) {
    output.setIdentity();
    output.m00 = x;
    output.m11 = y;
    output.m22 = z;
  }

  public static Matrix4d createScaling(double x, double y, double z) {
    Matrix4d result = new Matrix4d();
    makeScaling(x, y, z, result);
    return result;
  }

  public static void makeRotation(double deg, double x, double y, double z, Matrix4d output) {
    output.setIdentity();
    output.setRotation(new AxisAngle4d(x, y, z, MathUtil.degToRad(deg)));
  }

  public static Matrix4d createRotation(double deg, double x, double y, double z) {
    Matrix4d result = new Matrix4d();
    makeRotation(deg, x, y, z, result);
    return result;
  }

  public static void serialize(Matrix4d m, DataOutputStream stream) throws IOException {
    stream.writeDouble(m.m00);
    stream.writeDouble(m.m01);
    stream.writeDouble(m.m02);
    stream.writeDouble(m.m03);

    stream.writeDouble(m.m10);
    stream.writeDouble(m.m11);
    stream.writeDouble(m.m12);
    stream.writeDouble(m.m13);

    stream.writeDouble(m.m20);
    stream.writeDouble(m.m21);
    stream.writeDouble(m.m22);
    stream.writeDouble(m.m23);

    stream.writeDouble(m.m30);
    stream.writeDouble(m.m31);
    stream.writeDouble(m.m32);
    stream.writeDouble(m.m33);
  }

  public static void deserialize(DataInputStream stream, Matrix4d m) throws IOException {
    m.m00 = stream.readDouble();
    m.m01 = stream.readDouble();
    m.m02 = stream.readDouble();
    m.m03 = stream.readDouble();

    m.m10 = stream.readDouble();
    m.m11 = stream.readDouble();
    m.m12 = stream.readDouble();
    m.m13 = stream.readDouble();

    m.m20 = stream.readDouble();
    m.m21 = stream.readDouble();
    m.m22 = stream.readDouble();
    m.m23 = stream.readDouble();

    m.m30 = stream.readDouble();
    m.m31 = stream.readDouble();
    m.m32 = stream.readDouble();
    m.m33 = stream.readDouble();
  }

  public static Matrix4d createLookAtMatrix(double eyeX, double eyeY, double eyeZ,
                                            double atX, double atY, double atZ,
                                            double upX, double upY, double upZ) {
    Matrix4d M = new Matrix4d();
    makeLookAtMatrix(M, eyeX, eyeY, eyeZ, atX, atY, atZ, upX, upY, upZ);
    return M;
  }

  public static void makeLookAtMatrix(Matrix4d M,
                                      double eyeX, double eyeY, double eyeZ,
                                      double atX, double atY, double atZ,
                                      double upX, double upY, double upZ) {
    Point3d origin = new Point3d(eyeX, eyeY, eyeZ);
    Point3d target = new Point3d(atX, atY, atZ);
    Vector3d up = new Vector3d(upX, upY, upZ);

    Vector3d dir = new Vector3d();
    dir.sub(origin, target);
    dir.normalize();

    up.normalize();
    Vector3d right = new Vector3d();
    right.cross(up, dir);
    right.normalize();

    Vector3d newUp = new Vector3d();
    newUp.cross(dir, right);
    newUp.normalize();

    M.setIdentity();

    M.m00 = right.x;
    M.m10 = right.y;
    M.m20 = right.z;

    M.m01 = newUp.x;
    M.m11 = newUp.y;
    M.m21 = newUp.z;

    M.m02 = dir.x;
    M.m12 = dir.y;
    M.m22 = dir.z;

    M.m03 = origin.x;
    M.m13 = origin.y;
    M.m23 = origin.z;
  }


  public static Matrix4d scale(Matrix4d M, double c) {
    Matrix4d output = new Matrix4d(M);
    output.mul(c);
    return output;
  }

  public static boolean isZero(Matrix4d M) {
    return (M.m00 == 0) && (M.m01 == 0) && (M.m02 == 0) && (M.m03 == 0) &&
      (M.m10 == 0) && (M.m11 == 0) && (M.m12 == 0) && (M.m13 == 0) &&
      (M.m20 == 0) && (M.m21 == 0) && (M.m22 == 0) && (M.m23 == 0) &&
      (M.m30 == 0) && (M.m31 == 0) && (M.m32 == 0) && (M.m33 == 0);
  }

  public static boolean isNaN(Matrix4d M) {
    return Double.isNaN(M.m00) || Double.isNaN(M.m01) || Double.isNaN(M.m02) || Double.isNaN(M.m03) ||
      Double.isNaN(M.m10) || Double.isNaN(M.m11) || Double.isNaN(M.m12) || Double.isNaN(M.m13) ||
      Double.isNaN(M.m20) || Double.isNaN(M.m21) || Double.isNaN(M.m22) || Double.isNaN(M.m23) ||
      Double.isNaN(M.m30) || Double.isNaN(M.m31) || Double.isNaN(M.m32) || Double.isNaN(M.m33);
  }

  public static void setAllElements(Matrix4d M, double v) {
    M.m00 = v;
    M.m01 = v;
    M.m02 = v;
    M.m03 = v;

    M.m10 = v;
    M.m11 = v;
    M.m12 = v;
    M.m13 = v;

    M.m20 = v;
    M.m21 = v;
    M.m22 = v;
    M.m23 = v;

    M.m30 = v;
    M.m31 = v;
    M.m32 = v;
    M.m33 = v;
  }

  public static Point3d transform(Matrix4d M, Point3d p) {
    Point3d output = new Point3d();
    M.transform(p, output);
    return output;
  }

  public static Vector3d transform(Matrix4d M, Vector3d v) {
    Vector3d output = new Vector3d();
    M.transform(v, output);
    return output;
  }

  // Source: Mike Day's algorithm from https://marc-b-reynolds.github.io/quaternions/2017/08/08/QuatRotMatrix.html
  public static Quat4d rotationPartToQuaternion(Matrix4d M) {
    double m00 = M.m00, m01 = M.m01, m02 = M.m02;
    double m10 = M.m10, m11 = M.m11, m12 = M.m12;
    double m20 = M.m20, m21 = M.m21, m22 = M.m22;
    double e0;

    Quat4d q = new Quat4d();
    if (m22 >= 0) {
      double a = m00 + m11;
      double b = m10 - m01;
      double c = 1.f + m22;

      if (a >= 0) {
        e0 = c + a;
        q.set(m21 - m12, m02 - m20, b, e0); // w
      } else {
        e0 = c - a;
        q.set(m02 + m20, m21 + m12, e0, b); // z
      }
    } else {
      double a = m00 - m11;
      double b = m10 + m01;
      double c = 1.f - m22;

      if (a >= 0) {
        e0 = c + a;
        q.set(e0, b, m02 + m20, m21 - m12); // x
      } else {
        e0 = c - a;
        q.set(b, e0, m21 + m12, m02 - m20); // y)
      }
    }

    q.scale(0.5 / Math.sqrt(e0));
    return q;
  }
}
