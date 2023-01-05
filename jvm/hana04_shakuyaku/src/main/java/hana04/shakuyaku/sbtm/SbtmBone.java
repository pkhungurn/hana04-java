package hana04.shakuyaku.sbtm;

import javax.vecmath.Quat4d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

public interface SbtmBone {
  String name();
  int parentIndex();
  Vector3d translationToParent();
  void getTranslationToParent(Tuple3d output);
  Quat4d rotationToParent();
  void getRotationToParent(Quat4d output);
  String parentName();
}
