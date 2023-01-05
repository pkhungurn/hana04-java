package hana04.shakuyaku.sbtm;

import javax.vecmath.Quat4d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

public class SbtmBoneImpl implements SbtmBone {
  String name = "";
  int parentIndex = -1;
  final Vector3d translationToParent = new Vector3d();
  final Quat4d rotationToParent = new Quat4d();
  String parentName = "";

  @Override
  public String name() {
    return name;
  }

  @Override
  public int parentIndex() {
    return parentIndex;
  }

  @Override
  public Vector3d translationToParent() {
    return new Vector3d(translationToParent);
  }

  @Override
  public void getTranslationToParent(Tuple3d output) {
    output.set(translationToParent);
  }

  @Override
  public Quat4d rotationToParent() {
    return new Quat4d(rotationToParent);
  }

  @Override
  public void getRotationToParent(Quat4d output) {
    output.set(rotationToParent);
  }

  @Override
  public String parentName() {
    return parentName;
  }
}
