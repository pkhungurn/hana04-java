package hana04.gfxbase.gfxtype;

import hana04.gfxbase.util.MathUtil;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Matrix4fUtil {
  public static final Matrix4f IDENTITY_MATRIX = new Matrix4f(
      1, 0, 0, 0,
      0, 1, 0, 0,
      0, 0, 1, 0,
      0, 0, 0, 1);

  public static Matrix4f add(Matrix4f A, Matrix4f B) {
    Matrix4f result = new Matrix4f();
    result.add(A, B);
    return result;
  }

  public static Matrix4f sub(Matrix4f A, Matrix4f B) {
    Matrix4f result = new Matrix4f();
    result.sub(A, B);
    return result;
  }

  public static Matrix4f mul(Matrix4f A, Matrix4f B) {
    Matrix4f result = new Matrix4f();
    result.mul(A, B);
    return result;
  }

  public static Matrix4f Mul(Matrix4f... Ms) {
    Matrix4f result = new Matrix4f();
    result.setIdentity();
    for (Matrix4f m : Ms) {
      result.mul(m);
    }
    return result;
  }

  public static Matrix4f transpose(Matrix4f A) {
    Matrix4f result = new Matrix4f();
    result.transpose(A);
    return result;
  }

  public static Matrix4f inverse(Matrix4f A) {
    Matrix4f result = new Matrix4f();
    result.invert(A);
    return result;
  }

  public static Matrix4f createIdentity() {
    Matrix4f result = new Matrix4f();
    result.setIdentity();
    return result;
  }

  public static void makeTranslation(float x, float y, float z, Matrix4f output) {
    output.setIdentity();
    output.setTranslation(new Vector3f(x, y, z));
  }

  public static Matrix4f createTranslation(float x, float y, float z) {
    Matrix4f result = new Matrix4f();
    makeTranslation(x, y, z, result);
    return result;
  }

  public static void makeScaling(float x, float y, float z, Matrix4f output) {
    output.setIdentity();
    output.m00 = x;
    output.m11 = y;
    output.m22 = z;
  }

  public static Matrix4f createScaling(float x, float y, float z) {
    Matrix4f result = new Matrix4f();
    makeScaling(x, y, z, result);
    return result;
  }

  public static void makeRotation(float deg, float x, float y, float z, Matrix4f output) {
    output.setIdentity();
    output.setRotation(new AxisAngle4f(x, y, z, (float) MathUtil.degToRad(deg)));
  }
  
  public static void makeRotation(Quat4f q, Matrix4f output) {
    output.setIdentity();
    output.setRotation(q);
  }

  public static Matrix4f createRotation(float deg, float x, float y, float z) {
    Matrix4f result = new Matrix4f();
    makeRotation(deg, x, y, z, result);
    return result;
  }

  public static Matrix4f createRotation(Quat4f q) {
    Matrix4f result = new Matrix4f();
    makeRotation(q, result);
    return result;
  } 

  public static void serialize(Matrix4f m, DataOutputStream stream) throws IOException {
    stream.writeFloat(m.m00);
    stream.writeFloat(m.m01);
    stream.writeFloat(m.m02);
    stream.writeFloat(m.m03);

    stream.writeFloat(m.m10);
    stream.writeFloat(m.m11);
    stream.writeFloat(m.m12);
    stream.writeFloat(m.m13);

    stream.writeFloat(m.m20);
    stream.writeFloat(m.m21);
    stream.writeFloat(m.m22);
    stream.writeFloat(m.m23);

    stream.writeFloat(m.m30);
    stream.writeFloat(m.m31);
    stream.writeFloat(m.m32);
    stream.writeFloat(m.m33);
  }

  public static void deserialize(DataInputStream stream, Matrix4f m) throws IOException {
    m.m00 = stream.readFloat();
    m.m01 = stream.readFloat();
    m.m02 = stream.readFloat();
    m.m03 = stream.readFloat();

    m.m10 = stream.readFloat();
    m.m11 = stream.readFloat();
    m.m12 = stream.readFloat();
    m.m13 = stream.readFloat();

    m.m20 = stream.readFloat();
    m.m21 = stream.readFloat();
    m.m22 = stream.readFloat();
    m.m23 = stream.readFloat();

    m.m30 = stream.readFloat();
    m.m31 = stream.readFloat();
    m.m32 = stream.readFloat();
    m.m33 = stream.readFloat();
  }

  public static Matrix4f createLookAtMatrix(float eyeX, float eyeY, float eyeZ,
      float atX, float atY, float atZ,
      float upX, float upY, float upZ) {
    Matrix4f M = new Matrix4f();
    makeLookAtMatrix(M, eyeX, eyeY, eyeZ, atX, atY, atZ, upX, upY, upZ);
    return M;
  }

  public static void makeLookAtMatrix(Matrix4f M,
      float eyeX, float eyeY, float eyeZ,
      float atX, float atY, float atZ,
      float upX, float upY, float upZ) {
    Point3f origin = new Point3f(eyeX, eyeY, eyeZ);
    Point3f target = new Point3f(atX, atY, atZ);
    Vector3f up = new Vector3f(upX, upY, upZ);

    Vector3f dir = new Vector3f();
    dir.sub(origin, target);
    dir.normalize();

    up.normalize();
    Vector3f right = new Vector3f();
    right.cross(up, dir);
    right.normalize();

    Vector3f newUp = new Vector3f();
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


  public static Matrix4f scale(Matrix4f M, float c) {
    Matrix4f output = new Matrix4f(M);
    output.mul(c);
    return output;
  }

  public static boolean isZero(Matrix4f M) {
    return (M.m00 == 0) && (M.m01 == 0) && (M.m02 == 0) && (M.m03 == 0) &&
        (M.m10 == 0) && (M.m11 == 0) && (M.m12 == 0) && (M.m13 == 0) &&
        (M.m20 == 0) && (M.m21 == 0) && (M.m22 == 0) && (M.m23 == 0) &&
        (M.m30 == 0) && (M.m31 == 0) && (M.m32 == 0) && (M.m33 == 0);
  }

  public static boolean isNaN(Matrix4f M) {
    return Float.isNaN(M.m00) || Float.isNaN(M.m01) || Float.isNaN(M.m02) || Float.isNaN(M.m03) ||
        Float.isNaN(M.m10) || Float.isNaN(M.m11) || Float.isNaN(M.m12) || Float.isNaN(M.m13) ||
        Float.isNaN(M.m20) || Float.isNaN(M.m21) || Float.isNaN(M.m22) || Float.isNaN(M.m23) ||
        Float.isNaN(M.m30) || Float.isNaN(M.m31) || Float.isNaN(M.m32) || Float.isNaN(M.m33);
  }

  public static void setAllElements(Matrix4f M, float v) {
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

  public static Point3f transform(Matrix4f M, Point3f p) {
    Point3f output = new Point3f();
    M.transform(p, output);
    return output;
  }

  public static Vector3f transform(Matrix4f M, Vector3f v) {
    Vector3f output = new Vector3f();
    M.transform(v, output);
    return output;
  }

  // Source: Mike Day's algorithm from https://marc-b-reynolds.github.io/quaternions/2017/08/08/QuatRotMatrix.html
  public static Quat4d rotationPartToQuaternion(Matrix4f M) {
    float m00 = M.m00, m01 = M.m01, m02 = M.m02;
    float m10 = M.m10, m11 = M.m11, m12 = M.m12;
    float m20 = M.m20, m21 = M.m21, m22 = M.m22;
    float e0;

    Quat4d q = new Quat4d();
    if (m22 >= 0) {
      float a = m00 + m11;
      float b = m10 - m01;
      float c = 1.f + m22;

      if (a >= 0) {
        e0 = c + a;
        q.set(m21 - m12, m02 - m20, b, e0); // w
      } else {
        e0 = c - a;
        q.set(m02 + m20, m21 + m12, e0, b); // z
      }
    } else {
      float a = m00 - m11;
      float b = m10 + m01;
      float c = 1.f - m22;

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


  /**
   * Create a projection matrix as used by OpenGL
   *
   * @param fovy   field of view in the Y direction in degrees
   * @param aspect aspect ratio
   * @param near   near z cutoff
   * @param far    far z cutoff
   */
  public static javax.vecmath.Matrix4f createPerspectiveMatrix(float fovy, float aspect, float near, float far) {
    javax.vecmath.Matrix4f M = new javax.vecmath.Matrix4f();
    makePerspectiveMatrix(M, fovy, aspect, near, far);
    return M;
  }

  public static void makePerspectiveMatrix(javax.vecmath.Matrix4f M, float fovy, float aspect, float near, float far) {
    float f = (float) (1.0 / Math.tan(Math.toRadians(fovy / 2.0)));
    M.setZero();
    M.m00 = f / aspect;
    M.m11 = f;
    M.m22 = (far + near) / (near - far);
    M.m23 = (2 * far * near) / (near - far);
    M.m32 = -1;
  }

  public static Matrix4f createPerspectiveMatrix(float left, float right,
      float bottom, float top,
      float near, float far) {
    Matrix4f M = new Matrix4f();
    makePerspectiveMatrix(M, left, right, bottom, top, near, far);
    return M;
  }

  public static void makePerspectiveMatrix(Matrix4f M,
      float left, float right,
      float bottom, float top,
      float near, float far) {
    M.setZero();
    M.m00 = 2 * near / (right - left);
    M.m02 = (right + left) / (right - left);
    M.m11 = 2 * near / (top - bottom);
    M.m12 = (top + bottom) / (top - bottom);
    M.m22 = (far + near) / (near - far);
    M.m23 = (2 * far * near) / (near - far);
    M.m32 = -1;
  }

  public static void makeOrthographicMatrix(Matrix4f M, float height, float aspect, float near,
      float far) {
    M.setZero();
    M.m00 = 2 / height / aspect;
    M.m11 = 2 / height;
    M.m22 = 2 / (near - far);
    M.m23 = (near + far) / (near - far);
    M.m33 = 1;
  }

  public static Matrix4f createOrthographicMatrix(float height, float aspect, float near, float far) {
    Matrix4f M = new Matrix4f();
    makeOrthographicMatrix(M, height, aspect, near, far);
    return M;
  }

  public static void makeOrthographicMatrix(Matrix4f M,
      float left, float right,
      float bottom, float top,
      float near, float far) {
    M.setZero();
    M.m00 = 2.0f / (right - left);
    M.m03 = -(right + left) / (right - left);
    M.m11 = 2.0f / (top - bottom);
    M.m13 = -(top + bottom) / (top - bottom);
    M.m22 = -2.0f / (far - near);
    M.m23 = -(far + near) / (far - near);
    M.m33 = 1;
  }

  public static Matrix4f createOrthographicMatrix(float left, float right,
      float bottom, float top,
      float near, float far) {
    Matrix4f M = new Matrix4f();
    makeOrthographicMatrix(M, left, right, bottom, top, near, far);
    return M;
  }

  /*
   * Create a viewport matrix that matches the OpenGL viewport transformation
   * after the calls glViewport(left, bottom, width, height);
   * glDepthRange(minDepth, maxDepth);  (normally 0, 1)
   */
  public static void makeViewportMatrix(
    Matrix4f M, int left, int bottom, int width, int height,
    float minDepth, float maxDepth) {
    M.setIdentity();
    M.m00 = width / 2.0f;
    M.m03 = left + width / 2.0f;
    M.m11 = height / 2.0f;
    M.m13 = bottom + height / 2.0f;
    M.m22 = (maxDepth - minDepth) / 2;
    M.m23 = (maxDepth + minDepth) / 2;
  }
}
