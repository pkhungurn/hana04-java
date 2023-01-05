package hana04.formats.mmd.generic.api.physics;

import com.google.common.collect.ImmutableList;

import javax.vecmath.Vector3f;

public interface MmdJoint {
  String japaneseName();

  String englishName();

  ImmutableList<Integer> rigidBodyIndices();

  Vector3f position();

  Vector3f rotation();

  Vector3f linearLowerLimit();

  Vector3f linearUpperLimit();

  Vector3f angularLowerLimit();

  Vector3f angularUpperLimit();

  ImmutableList<Float> springLinearStiffness();

  ImmutableList<Float> springAngularStiffness();

}
