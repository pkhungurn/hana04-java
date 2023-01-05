package hana04.formats.mmd.generic.impl.pmd;

import com.google.common.collect.ImmutableList;
import hana04.formats.mmd.generic.api.physics.MmdJoint;
import hana04.formats.mmd.pmd.PmdJoint;

import javax.vecmath.Vector3f;

public class PmdJointAdaptor implements MmdJoint {
  private final PmdJoint pmdJoint;
  private final ImmutableList<Integer> rigidBodyIndices;
  private final ImmutableList<Float> springLinearStiffness;
  private final ImmutableList<Float> springAngularStiffness;

  public PmdJointAdaptor(PmdJoint pmdJoint) {
    this.pmdJoint = pmdJoint;
    rigidBodyIndices = ImmutableList.of(pmdJoint.rigidBodies[0], pmdJoint.rigidBodies[1]);
    springLinearStiffness = ImmutableList.of(
        pmdJoint.springLinearStiffness[0],
        pmdJoint.springLinearStiffness[1],
        pmdJoint.springLinearStiffness[2]);
    springAngularStiffness = ImmutableList.of(
        pmdJoint.springAngularStiffness[0],
        pmdJoint.springAngularStiffness[1],
        pmdJoint.springAngularStiffness[2]);
  }

  @Override
  public String japaneseName() {
    return pmdJoint.name;
  }

  @Override
  public String englishName() {
    return "";
  }

  @Override
  public ImmutableList<Integer> rigidBodyIndices() {
    return rigidBodyIndices;
  }

  @Override
  public Vector3f position() {
    return pmdJoint.position;
  }

  @Override
  public Vector3f rotation() {
    return pmdJoint.rotation;
  }

  @Override
  public Vector3f linearLowerLimit() {
    return pmdJoint.linearLowerLimit;
  }

  @Override
  public Vector3f linearUpperLimit() {
    return pmdJoint.linearUpperLimit;
  }

  @Override
  public Vector3f angularLowerLimit() {
    return pmdJoint.angularLowerLimit;
  }

  @Override
  public Vector3f angularUpperLimit() {
    return pmdJoint.angularUpperLimit;
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
