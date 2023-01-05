package hana04.gfxbase.gfxtype;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point2d;
import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Point4i;
import javax.vecmath.Quat4d;
import javax.vecmath.Quat4f;
import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple2f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Tuple3i;
import javax.vecmath.Tuple4d;
import javax.vecmath.Tuple4f;
import javax.vecmath.Tuple4i;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4d;
import javax.vecmath.Vector4f;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class WireSerializationUtil {
  public static void readMatrix4d(DataInputStream stream, Matrix4d m) throws IOException {
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

  public static Matrix4d readMatrix4d(DataInputStream stream) throws IOException {
    Matrix4d m = new Matrix4d();
    readMatrix4d(stream, m);
    return m;
  }

  public static void writeMatrix4d(DataOutputStream stream, Matrix4d obj) throws IOException {
    stream.writeDouble(obj.m00);
    stream.writeDouble(obj.m01);
    stream.writeDouble(obj.m02);
    stream.writeDouble(obj.m03);

    stream.writeDouble(obj.m10);
    stream.writeDouble(obj.m11);
    stream.writeDouble(obj.m12);
    stream.writeDouble(obj.m13);

    stream.writeDouble(obj.m20);
    stream.writeDouble(obj.m21);
    stream.writeDouble(obj.m22);
    stream.writeDouble(obj.m23);

    stream.writeDouble(obj.m30);
    stream.writeDouble(obj.m31);
    stream.writeDouble(obj.m32);
    stream.writeDouble(obj.m33);
  }

  public static void writeTuple2f(DataOutputStream stream, Tuple2f v) throws IOException {
    stream.writeFloat(v.x);
    stream.writeFloat(v.y);
  }

  public static void readTuple2f(DataInputStream stream, Tuple2f v) throws IOException {
    v.x = stream.readFloat();
    v.y = stream.readFloat();
  }

  public static Point2f readPoint2f(DataInputStream stream) throws IOException {
    Point2f v = new Point2f();
    readTuple2f(stream, v);
    return v;
  }

  public static Vector2f readVector2f(DataInputStream stream) throws IOException {
    Vector2f v = new Vector2f();
    readTuple2f(stream, v);
    return v;
  }

  public static void writeTuple2d(DataOutputStream stream, Tuple2d v) throws IOException {
    stream.writeDouble(v.x);
    stream.writeDouble(v.y);
  }

  public static void writePoint2d(DataOutputStream stream, Point2d p) throws IOException {
    stream.writeDouble(p.x);
    stream.writeDouble(p.y);
  }

  public static void readTuple2d(DataInputStream stream, Tuple2d v) throws IOException {
    v.x = stream.readDouble();
    v.y = stream.readDouble();
  }

  public static Point2d readPoint2d(DataInputStream stream) throws IOException {
    Point2d v = new Point2d();
    readTuple2d(stream, v);
    return v;
  }

  public static Vector2d readVector2d(DataInputStream stream) throws IOException {
    Vector2d v = new Vector2d();
    readTuple2d(stream, v);
    return v;
  }

  public static void writeVector2d(DataOutputStream stream, Vector2d v) throws IOException {
    stream.writeDouble(v.x);
    stream.writeDouble(v.y);
  }

  public static void writeTuple3d(DataOutputStream stream, Tuple3d v) throws IOException {
    stream.writeDouble(v.x);
    stream.writeDouble(v.y);
    stream.writeDouble(v.z);
  }

  public static void readTuple3d(DataInputStream stream, Tuple3d v) throws IOException {
    v.x = stream.readDouble();
    v.y = stream.readDouble();
    v.z = stream.readDouble();
  }

  public static Point3d readPoint3d(DataInputStream stream) throws IOException {
    Point3d v = new Point3d();
    readTuple3d(stream, v);
    return v;
  }

  public static void writePoint3d(DataOutputStream stream, Point3d p) throws IOException {
    writeTuple3d(stream, p);
  }

  public static Vector3d readVector3d(DataInputStream stream) throws IOException {
    Vector3d v = new Vector3d();
    readTuple3d(stream, v);
    return v;
  }

  public static void writeVector3d(DataOutputStream stream, Vector3d v) throws IOException {
    writeTuple3d(stream, v);
  }

  public static void writeTuple3f(DataOutputStream stream, Tuple3f v) throws IOException {
    stream.writeFloat(v.x);
    stream.writeFloat(v.y);
    stream.writeFloat(v.z);
  }

  public static void readTuple3f(DataInputStream stream, Tuple3f v) throws IOException {
    v.x = stream.readFloat();
    v.y = stream.readFloat();
    v.z = stream.readFloat();
  }

  public static Point3f readPoint3f(DataInputStream stream) throws IOException {
    Point3f v = new Point3f();
    readTuple3f(stream, v);
    return v;
  }

  public static Vector3f readVector3f(DataInputStream stream) throws IOException {
    Vector3f v = new Vector3f();
    readTuple3f(stream, v);
    return v;
  }

  public static void writeTuple4d(DataOutputStream stream, Tuple4d v) throws IOException {
    stream.writeDouble(v.x);
    stream.writeDouble(v.y);
    stream.writeDouble(v.z);
    stream.writeDouble(v.w);
  }

  public static void writeVector4d(DataOutputStream stream, Vector4d v) throws IOException {
    writeTuple4d(stream, v);
  }

  public static void readTuple4d(DataInputStream stream, Tuple4d v) throws IOException {
    v.x = stream.readDouble();
    v.y = stream.readDouble();
    v.z = stream.readDouble();
    v.w = stream.readDouble();
  }

  public static Vector4d readVector4d(DataInputStream stream) throws IOException {
    Vector4d v = new Vector4d();
    readTuple4d(stream, v);
    return v;
  }

  public static Quat4d readQuat4d(DataInputStream stream) throws IOException {
    Quat4d v = new Quat4d();
    readTuple4d(stream, v);
    return v;
  }

  public static void writeQuat4d(DataOutputStream stream, Quat4d q) throws IOException {
    writeTuple4d(stream, q);
  }

  public static void writeTuple4f(DataOutputStream stream, Tuple4f v) throws IOException {
    stream.writeFloat(v.x);
    stream.writeFloat(v.y);
    stream.writeFloat(v.z);
    stream.writeFloat(v.w);
  }

  public static void readTuple4f(DataInputStream stream, Tuple4f v) throws IOException {
    v.x = stream.readFloat();
    v.y = stream.readFloat();
    v.z = stream.readFloat();
    v.w = stream.readFloat();
  }

  public static Vector4f readVector4f(DataInputStream stream) throws IOException {
    Vector4f v = new Vector4f();
    readTuple4f(stream, v);
    return v;
  }

  public static Quat4f readQuat4f(DataInputStream stream) throws IOException {
    Quat4f v = new Quat4f();
    readTuple4f(stream, v);
    return v;
  }

  public static void writeTuple3i(DataOutputStream stream, Tuple3i v) throws IOException {
    stream.writeInt(v.x);
    stream.writeInt(v.y);
    stream.writeInt(v.z);
  }

  public static void readTuple3i(DataInputStream stream, Tuple3i v) throws IOException {
    v.x = stream.readInt();
    v.y = stream.readInt();
    v.z = stream.readInt();
  }

  public static Point3i readPoint3i(DataInputStream stream) throws IOException {
    Point3i v = new Point3i();
    readTuple3i(stream, v);
    return v;
  }

  public static void writeTuple4i(DataOutputStream stream, Tuple4i v) throws IOException {
    stream.writeInt(v.x);
    stream.writeInt(v.y);
    stream.writeInt(v.z);
    stream.writeInt(v.w);
  }

  public static void readTuple4i(DataInputStream stream, Tuple4i v) throws IOException {
    v.x = stream.readInt();
    v.y = stream.readInt();
    v.z = stream.readInt();
    v.w = stream.readInt();
  }

  public static Point4i readPoint4i(DataInputStream stream) throws IOException {
    Point4i v = new Point4i();
    readTuple4i(stream, v);
    return v;
  }

  public static Transform readTransform(DataInputStream stream) throws IOException {
    Transform t = new Transform();
    readMatrix4d(stream, t.m);
    t.updateFromM();
    return t;
  }

  public static void writeTransform(DataOutputStream stream, Transform t) throws IOException {
    writeMatrix4d(stream, t.m);
  }
}
