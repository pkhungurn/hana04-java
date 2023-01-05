package hana04.formats.mmd.generic.impl.pmx;

import com.google.common.collect.ImmutableList;
import hana04.formats.mmd.generic.api.physics.MmdJoint;
import hana04.formats.mmd.pmx.PmxJoint;

import javax.vecmath.Vector3f;

public class PmxJointAdaptor implements MmdJoint {
  private final PmxJoint joint;
  private final ImmutableList<Integer> rigidBodyIndices;
  private final ImmutableList<Float> springLinearStiffness;
  private final ImmutableList<Float> springAngularStiffness;

  public PmxJointAdaptor(PmxJoint joint) {
    this.joint = joint;
    rigidBodyIndices = ImmutableList.of(joint.rigidBodies[0], joint.rigidBodies[1]);
    springLinearStiffness = ImmutableList.of(
        joint.springLinearStiffness[0],
        joint.springLinearStiffness[1],
        joint.springLinearStiffness[2]);
    springAngularStiffness = ImmutableList.of(
        joint.springAngularStiffness[0],
        joint.springAngularStiffness[1],
        joint.springAngularStiffness[2]);
  }

  @Override
  public String japaneseName() {
    return joint.japaneseName;
  }

  @Override
  public String englishName() {
    return joint.englishName;
  }

  @Override
  public ImmutableList<Integer> rigidBodyIndices() {
    return rigidBodyIndices;
  }

  @Override
  public Vector3f position() {
    return joint.position;
  }

  @Override
  public Vector3f rotation() {
    return joint.rotation;
  }

  @Override
  public Vector3f linearLowerLimit() {
    return joint.linearLowerLimit;
  }

  @Override
  public Vector3f linearUpperLimit() {
    return joint.linearUpperLimit;
  }

  @Override
  public Vector3f angularLowerLimit() {
    return joint.angularLowerLimit;
  }

  @Override
  public Vector3f angularUpperLimit() {
    return joint.angularUpperLimit;
  }

  @Override
  public ImmutableList<Float> springLinearStiffness() {
    return springLinearStiffness;
  }

  @Override
  public ImmutableList<Float> springAngularStiffness() {
    return springAngularStiffness;
  }
}
