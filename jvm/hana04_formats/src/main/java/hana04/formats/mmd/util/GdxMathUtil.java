package hana04.formats.mmd.util;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import hana04.gfxbase.gfxtype.VecMathDUtil;
import hana04.gfxbase.gfxtype.VecMathFUtil;

import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;

public class GdxMathUtil {
  public static void gdxToMatrix4f(Matrix4 gdx, Matrix4f m) {
    float[] values = gdx.getValues();

    m.m00 = values[0];
    m.m10 = values[1];
    m.m20 = values[2];
    m.m30 = values[3];

    m.m01 = values[4];
    m.m11 = values[5];
    m.m21 = values[6];
    m.m31 = values[7];

    m.m02 = values[8];
    m.m12 = values[9];
    m.m22 = values[10];
    m.m32 = values[11];

    m.m03 = values[12];
    m.m13 = values[13];
    m.m23 = values[14];
    m.m33 = values[15];
  }

  public static void gdxToMatrix4f(Matrix4 gdx, Matrix4d m) {
    float[] values = gdx.getValues();

    m.m00 = values[0];
    m.m10 = values[1];
    m.m20 = values[2];
    m.m30 = values[3];

    m.m01 = values[4];
    m.m11 = values[5];
    m.m21 = values[6];
    m.m31 = values[7];

    m.m02 = values[8];
    m.m12 = values[9];
    m.m22 = values[10];
    m.m32 = values[11];

    m.m03 = values[12];
    m.m13 = values[13];
    m.m23 = values[14];
    m.m33 = values[15];
  }

  public static Matrix4f gdxToMatrix4f(Matrix4 gdx) {
    float[] values = gdx.getValues();
    var m = new Matrix4f();

    m.m00 = values[0];
    m.m10 = values[1];
    m.m20 = values[2];
    m.m30 = values[3];

    m.m01 = values[4];
    m.m11 = values[5];
    m.m21 = values[6];
    m.m31 = values[7];

    m.m02 = values[8];
    m.m12 = values[9];
    m.m22 = values[10];
    m.m32 = values[11];

    m.m03 = values[12];
    m.m13 = values[13];
    m.m23 = values[14];
    m.m33 = values[15];

    return m;
  }

  public static Matrix4d gdxToMatrix4d(Matrix4 gdx) {
    float[] values = gdx.getValues();
    var m = new Matrix4d();

    m.m00 = values[0];
    m.m10 = values[1];
    m.m20 = values[2];
    m.m30 = values[3];

    m.m01 = values[4];
    m.m11 = values[5];
    m.m21 = values[6];
    m.m31 = values[7];

    m.m02 = values[8];
    m.m12 = values[9];
    m.m22 = values[10];
    m.m32 = values[11];

    m.m03 = values[12];
    m.m13 = values[13];
    m.m23 = values[14];
    m.m33 = values[15];

    return m;
  }

  public static void matrix4ftoGdx(Matrix4f m, Matrix4 gdx) {
    float[] values = matrixToFloatArrayColumnMajor(m);
    gdx.set(values);
  }

  public static void matrix4dtoGdx(Matrix4d m, Matrix4 gdx) {
    float[] values = matrixToFloatArrayColumnMajor(m);
    gdx.set(values);
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

  public static Matrix4 convertToGdxMatrix(Matrix4f m) {
    Matrix4 result = new Matrix4();
    float[] mm = VecMathFUtil.matrixToFloatArrayColumnMajor(m);
    result.set(mm);
    return result;
  }

  public static Matrix4 convertToGdxMatrix(Matrix4d m) {
    Matrix4 result = new Matrix4();
    float[] mm = VecMathDUtil.matrixToFloatArrayColumnMajor(m);
    result.set(mm);
    return result;
  }

  public static Vector3 convertToGdxVector(Tuple3f v) {
    return new Vector3(v.x, v.y, v.z);
  }

  public static Vector3 convertToGdxVector(Tuple3d v) {
    return new Vector3((float) v.x, (float) v.y, (float) v.z);
  }
}
