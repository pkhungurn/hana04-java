package hana04.gfxbase.gfxtype;

import org.msgpack.core.MessagePacker;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.Value;

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
import java.io.IOException;

public final class BinarySerializationUtil {

  public static void unpackMatrix4d(Value value, Matrix4d m) {
    ArrayValue arrayValue = value.asArrayValue();
    m.m00 = arrayValue.get(0).asFloatValue().toDouble();
    m.m01 = arrayValue.get(1).asFloatValue().toDouble();
    m.m02 = arrayValue.get(2).asFloatValue().toDouble();
    m.m03 = arrayValue.get(3).asFloatValue().toDouble();

    m.m10 = arrayValue.get(4).asFloatValue().toDouble();
    m.m11 = arrayValue.get(5).asFloatValue().toDouble();
    m.m12 = arrayValue.get(6).asFloatValue().toDouble();
    m.m13 = arrayValue.get(7).asFloatValue().toDouble();

    m.m20 = arrayValue.get(8).asFloatValue().toDouble();
    m.m21 = arrayValue.get(9).asFloatValue().toDouble();
    m.m22 = arrayValue.get(10).asFloatValue().toDouble();
    m.m23 = arrayValue.get(11).asFloatValue().toDouble();

    m.m30 = arrayValue.get(12).asFloatValue().toDouble();
    m.m31 = arrayValue.get(13).asFloatValue().toDouble();
    m.m32 = arrayValue.get(14).asFloatValue().toDouble();
    m.m33 = arrayValue.get(15).asFloatValue().toDouble();
  }

  public static Matrix4d unpackMatrix4d(Value value) {
    Matrix4d m = new Matrix4d();
    unpackMatrix4d(value, m);
    return m;
  }

  public static void packMatrix4d(MessagePacker packer, Matrix4d obj) throws IOException {
    packer.packArrayHeader(16);

    packer.packDouble(obj.m00);
    packer.packDouble(obj.m01);
    packer.packDouble(obj.m02);
    packer.packDouble(obj.m03);

    packer.packDouble(obj.m10);
    packer.packDouble(obj.m11);
    packer.packDouble(obj.m12);
    packer.packDouble(obj.m13);

    packer.packDouble(obj.m20);
    packer.packDouble(obj.m21);
    packer.packDouble(obj.m22);
    packer.packDouble(obj.m23);

    packer.packDouble(obj.m30);
    packer.packDouble(obj.m31);
    packer.packDouble(obj.m32);
    packer.packDouble(obj.m33);
  }

  public static void packTuple2f(MessagePacker packer, Tuple2f v) throws IOException {
    packer.packArrayHeader(2);
    packer.packFloat(v.x);
    packer.packFloat(v.y);
  }

  public static void unpackTuple2f(Value value, Tuple2f v) {
    ArrayValue arrayValue = value.asArrayValue();
    v.x = arrayValue.get(0).asFloatValue().toFloat();
    v.y = arrayValue.get(1).asFloatValue().toFloat();
  }

  public static Point2f unpackPoint2f(Value value) {
    Point2f v = new Point2f();
    unpackTuple2f(value, v);
    return v;
  }

  public static Vector2f unpackVector2f(Value value) {
    Vector2f v = new Vector2f();
    unpackTuple2f(value, v);
    return v;
  }

  public static void packTuple2d(MessagePacker packer, Tuple2d v) throws IOException {
    packer.packArrayHeader(2);
    packer.packDouble(v.x);
    packer.packDouble(v.y);
  }

  public static void packPoint2d(MessagePacker packer, Point2d p) throws IOException {
    packTuple2d(packer, p);
  }

  public static void unpackTuple2d(Value value, Tuple2d v) {
    ArrayValue arrayValue = value.asArrayValue();
    v.x = arrayValue.get(0).asFloatValue().toDouble();
    v.y = arrayValue.get(1).asFloatValue().toDouble();
  }

  public static Point2d unpackPoint2d(Value value) {
    Point2d v = new Point2d();
    unpackTuple2d(value, v);
    return v;
  }

  public static Vector2d unpackVector2d(Value value) {
    Vector2d v = new Vector2d();
    unpackTuple2d(value, v);
    return v;
  }

  public static void packVector2d(MessagePacker packer, Vector2d v) throws IOException {
    packTuple2d(packer, v);
  }

  public static void packTuple3d(MessagePacker packer, Tuple3d v) throws IOException {
    packer.packArrayHeader(3);
    packer.packDouble(v.x);
    packer.packDouble(v.y);
    packer.packDouble(v.z);
  }

  public static void unpackTuple3d(Value value, Tuple3d v) {
    ArrayValue arrayValue = value.asArrayValue();
    v.x = arrayValue.get(0).asFloatValue().toDouble();
    v.y = arrayValue.get(1).asFloatValue().toDouble();
    v.z = arrayValue.get(2).asFloatValue().toDouble();
  }

  public static Point3d unpackPoint3d(Value value) {
    Point3d v = new Point3d();
    unpackTuple3d(value, v);
    return v;
  }

  public static void packPoint3d(MessagePacker packer, Point3d p) throws IOException {
    packTuple3d(packer, p);
  }

  public static Vector3d unpackVector3d(Value value) {
    Vector3d v = new Vector3d();
    unpackTuple3d(value, v);
    return v;
  }

  public static void packVector3d(MessagePacker packer, Vector3d v) throws IOException {
    packTuple3d(packer, v);
  }

  public static void packTuple3f(MessagePacker packer, Tuple3f v) throws IOException {
    packer.packArrayHeader(3);
    packer.packFloat(v.x);
    packer.packFloat(v.y);
    packer.packFloat(v.z);
  }

  public static void unpackTuple3f(Value value, Tuple3f v) {
    ArrayValue arrayValue = value.asArrayValue();
    v.x = arrayValue.get(0).asFloatValue().toFloat();
    v.y = arrayValue.get(1).asFloatValue().toFloat();
    v.z = arrayValue.get(2).asFloatValue().toFloat();
  }

  public static Point3f unpackPoint3f(Value value) {
    Point3f v = new Point3f();
    unpackTuple3f(value, v);
    return v;
  }

  public static Vector3f unpackVector3f(Value value) {
    Vector3f v = new Vector3f();
    unpackTuple3f(value, v);
    return v;
  }

  public static void packTuple4d(MessagePacker packer, Tuple4d v) throws IOException {
    packer.packArrayHeader(4);
    packer.packDouble(v.x);
    packer.packDouble(v.y);
    packer.packDouble(v.z);
    packer.packDouble(v.w);
  }

  public static void unpackTuple4d(Value value, Tuple4d v) {
    ArrayValue arrayValue = value.asArrayValue();
    v.x = arrayValue.get(0).asFloatValue().toDouble();
    v.y = arrayValue.get(1).asFloatValue().toDouble();
    v.z = arrayValue.get(2).asFloatValue().toDouble();
    v.w = arrayValue.get(3).asFloatValue().toDouble();
  }

  public static void packVector4d(MessagePacker packer, Vector4d v) throws IOException {
    packTuple4d(packer, v);
  }

  public static Vector4d unpackVector4d(Value value) {
    Vector4d v = new Vector4d();
    unpackTuple4d(value, v);
    return v;
  }

  public static Quat4d unpackQuat4d(Value value) {
    Quat4d v = new Quat4d();
    unpackTuple4d(value, v);
    return v;
  }

  public static void packQuat4d(MessagePacker packer, Quat4d q) throws IOException {
    packTuple4d(packer, q);
  }

  public static void packTuple4f(MessagePacker packer, Tuple4f v) throws IOException {
    packer.packFloat(v.x);
    packer.packFloat(v.y);
    packer.packFloat(v.z);
    packer.packFloat(v.w);
  }

  public static void unpackTuple4f(Value value, Tuple4f v) {
    ArrayValue arrayValue = value.asArrayValue();
    v.x = arrayValue.get(0).asFloatValue().toFloat();
    v.y = arrayValue.get(1).asFloatValue().toFloat();
    v.z = arrayValue.get(2).asFloatValue().toFloat();
    v.w = arrayValue.get(3).asFloatValue().toFloat();
  }

  public static Vector4f unpackVector4f(Value value) {
    Vector4f v = new Vector4f();
    unpackTuple4f(value, v);
    return v;
  }

  public static Quat4f unpackQuat4f(Value value) {
    Quat4f v = new Quat4f();
    unpackTuple4f(value, v);
    return v;
  }

  public static void packTuple3i(MessagePacker packer, Tuple3i v) throws IOException {
    packer.packArrayHeader(3);
    packer.packInt(v.x);
    packer.packInt(v.y);
    packer.packInt(v.z);
  }

  public static void unpackTuple3i(Value value, Tuple3i v) {
    ArrayValue arrayValue = value.asArrayValue();
    v.x = arrayValue.get(0).asIntegerValue().asInt();
    v.y = arrayValue.get(1).asIntegerValue().asInt();
    v.z = arrayValue.get(2).asIntegerValue().asInt();
  }

  public static Point3i unpackPoint3i(Value value) {
    Point3i v = new Point3i();
    unpackTuple3i(value, v);
    return v;
  }

  public static void packPoint3i(MessagePacker packer, Point3i v) throws IOException {
    packTuple3i(packer, v);
  }

  public static void packTuple4i(MessagePacker packer, Tuple4i v) throws IOException {
    packer.packArrayHeader(4);
    packer.packInt(v.x);
    packer.packInt(v.y);
    packer.packInt(v.z);
    packer.packInt(v.w);
  }

  public static void unpackTuple4i(Value value, Tuple4i v) {
    ArrayValue arrayValue = value.asArrayValue();
    v.x = arrayValue.get(0).asIntegerValue().asInt();
    v.y = arrayValue.get(1).asIntegerValue().asInt();
    v.z = arrayValue.get(2).asIntegerValue().asInt();
    v.w = arrayValue.get(3).asIntegerValue().asInt();
  }

  public static Point4i unpackPoint4i(Value value) {
    Point4i v = new Point4i();
    unpackTuple4i(value, v);
    return v;
  }

  public static Transform unpackTransform(Value value) {
    Transform t = new Transform();
    unpackMatrix4d(value, t.m);
    t.updateFromM();
    return t;
  }

  public static void packTransform(MessagePacker packer, Transform t) throws IOException {
    packMatrix4d(packer, t.m);
  }

  public static void packAabb3d(MessagePacker packer, Aabb3d aabb3d) throws IOException {
    packer.packArrayHeader(6);
    packer.packDouble(aabb3d.pMin.x);
    packer.packDouble(aabb3d.pMin.y);
    packer.packDouble(aabb3d.pMin.z);
    packer.packDouble(aabb3d.pMax.x);
    packer.packDouble(aabb3d.pMax.y);
    packer.packDouble(aabb3d.pMax.z);
  }

  public static Aabb3d unpackAabb3d(Value value) {
    ArrayValue arrayValue = value.asArrayValue();
    Aabb3d aabb3d = new Aabb3d();
    aabb3d.pMin.x = arrayValue.get(0).asFloatValue().toDouble();
    aabb3d.pMin.y = arrayValue.get(1).asFloatValue().toDouble();
    aabb3d.pMin.z = arrayValue.get(2).asFloatValue().toDouble();
    aabb3d.pMax.x = arrayValue.get(3).asFloatValue().toDouble();
    aabb3d.pMax.y = arrayValue.get(4).asFloatValue().toDouble();
    aabb3d.pMax.z = arrayValue.get(5).asFloatValue().toDouble();
    return aabb3d;
  }

  public static void packAabb2d(MessagePacker packer, Aabb2d aabb2d) throws IOException {
    packer.packArrayHeader(4);
    packer.packDouble(aabb2d.pMin.x);
    packer.packDouble(aabb2d.pMin.y);
    packer.packDouble(aabb2d.pMax.x);
    packer.packDouble(aabb2d.pMax.y);
  }

  public static Aabb2d unpackAabb2d(Value value) {
    ArrayValue arrayValue = value.asArrayValue();
    Aabb2d aabb2d = new Aabb2d();
    aabb2d.pMin.x = arrayValue.get(0).asFloatValue().toDouble();
    aabb2d.pMin.y = arrayValue.get(1).asFloatValue().toDouble();
    aabb2d.pMax.x = arrayValue.get(3).asFloatValue().toDouble();
    aabb2d.pMax.y = arrayValue.get(4).asFloatValue().toDouble();
    return aabb2d;
  }
}
