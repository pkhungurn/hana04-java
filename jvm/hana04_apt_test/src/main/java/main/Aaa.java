package main;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.base.extension.HanaObject;
import hana04.gfxbase.gfxtype.Aabb2d;
import hana04.gfxbase.gfxtype.Aabb3d;
import hana04.gfxbase.gfxtype.Transform;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Point3i;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.util.List;

@HanaDeclareObject(
  parent = HanaObject.class,
  typeId = TypeIds.TYPE_ID_AAA,
  typeNames = {"base.Aaa", "Aaa"})
public interface Aaa extends HanaObject {
  @HanaProperty(1)
  Integer intConst();

  @HanaProperty(2)
  Variable<Integer> intVar();

  @HanaProperty(3)
  Long longConst();

  @HanaProperty(4)
  Variable<Long> longVar();

  @HanaProperty(5)
  Float floatConst();

  @HanaProperty(6)
  Variable<Float> floatVar();

  @HanaProperty(7)
  Double doubleConst();

  @HanaProperty(8)
  Variable<Double> doubleVar();

  @HanaProperty(9)
  Boolean booleanConst();

  @HanaProperty(10)
  Variable<Boolean> booleanVar();

  @HanaProperty(11)
  String stringConst();

  @HanaProperty(12)
  Variable<String> stringVar();

  @HanaProperty(13)
  Point2d point2dConst();

  @HanaProperty(14)
  Variable<Point2d> point2dVar();

  @HanaProperty(15)
  Vector2d vector2dConst();

  @HanaProperty(16)
  Variable<Vector2d> vector2dVar();

  @HanaProperty(17)
  Point3d point3dConst();

  @HanaProperty(18)
  Variable<Point3d> point3dVar();

  @HanaProperty(19)
  Vector3d vector3dConst();

  @HanaProperty(20)
  Variable<Vector3d> vector3dVar();

  @HanaProperty(21)
  Vector4d vector4dConst();

  @HanaProperty(22)
  Variable<Vector4d> vector4dVar();

  @HanaProperty(23)
  Quat4d quat4dConst();

  @HanaProperty(24)
  Variable<Quat4d> quat4dVar();

  @HanaProperty(25)
  Matrix4d matrix4dConst();

  @HanaProperty(26)
  Variable<Matrix4d> matrix4dVar();

  @HanaProperty(27)
  Transform xformConst();

  @HanaProperty(28)
  Variable<Transform> xformVar();

  @HanaProperty(29)
  Aabb2d aabb2dConst();

  @HanaProperty(30)
  Variable<Aabb2d> aabbb2dVar();

  @HanaProperty(31)
  Aabb3d aabb3dConst();

  @HanaProperty(32)
  Variable<Aabb3d> aabbb3dVar();

  @HanaProperty(33)
  List<Integer> intListConst();

  @HanaProperty(34)
  Variable<List<Integer>> intListVar();

  @HanaProperty(35)
  Variable<List<Float>> floatListVar();

  @HanaProperty(36)
  Variable<List<Double>> doubleListVar();

  @HanaProperty(37)
  Variable<List<Long>> longListVar();

  @HanaProperty(38)
  Variable<List<Boolean>> booleanListVar();

  @HanaProperty(39)
  Variable<List<String>> stringListVar();

  @HanaProperty(40)
  Variable<List<Point2d>> point2dListVar();

  @HanaProperty(41)
  Point3i point3iConst();

  @HanaProperty(42)
  Variable<Point3i> point3iVar();

  @HanaProperty(43)
  Variable<List<Point3i>> point3iListVar();
}
