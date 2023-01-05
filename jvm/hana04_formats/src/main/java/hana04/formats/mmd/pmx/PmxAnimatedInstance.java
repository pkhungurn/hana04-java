package hana04.formats.mmd.pmx;

import hana04.formats.mmd.vpd.VpdPose;

public class PmxAnimatedInstance {
  public final PmxModel model;
  public final PmxPose inPose;
  public final PmxPose outPose;
  public boolean physicsEnabled;
  public boolean useLegIkBias;
  public PmxModelPhysics physics;
  boolean disposed = false;

  public PmxAnimatedInstance(PmxModel model) {
    this.model = model;
    this.inPose = new PmxPose(model);
    this.outPose = new PmxPose(model);
    physicsEnabled = false;
    useLegIkBias = true;
    physics = new PmxModelPhysics(model);
  }

  public void setPmxPose(PmxPose inPose) {
    this.inPose.copy(inPose);
  }

  public void setVpdPose(VpdPose inPose) {
    this.inPose.copy(inPose);
  }

  private void copyInPoseToOutPose() {
    /*
    outPose.clear();

    Vector3f boneDisp = new Vector3f();
    Quat4f boneRot = new Quat4f();
    for (int i = 0; i < model.getBoneCount(); i++) {
      PmxBone bone = model.getBone(i);
      if (!bone.assignedByPhysics) {
        inPose.getStoredBonePose(i, boneDisp, boneRot);
        outPose.setBoneDisplacement(i, boneDisp);
        outPose.setBoneRotation(i, boneRot);
      }
      if (bone.physicsAssignedRotationOnly) {
        inPose.getStoredBonePose(i, boneDisp, boneRot);
        outPose.setBoneDisplacement(i, boneDisp);
      }
    }
    for (int i = 0; i < model.getMorphCount(); i++) {
      outPose.setMorphWeight(i, inPose.getMorphWeight(i));
    }
    */
    outPose.copy(inPose);
  }

  public void update(float elaspedTimeInSeconds) {
    copyInPoseToOutPose();

    for (int i = 0; i < model.getBoneCount(); i++) {
      PmxBone bone = model.getBoneByOrder(i);
      if (bone.transformAfterPhysics())
        continue;
      if (bone.isIk()) {
        PmxIkSolver.solve(outPose, bone.boneIndex, useLegIkBias);
      }
    }

    if (physicsEnabled) {
      physics.update(outPose, elaspedTimeInSeconds);
    } else {
      physics.updateWithNoPhysics(outPose);
    }

    for (int i = 0; i < model.getBoneCount(); i++) {
      PmxBone bone = model.getBoneByOrder(i);
      if (!bone.transformAfterPhysics())
        continue;
      if (bone.isIk()) {
        PmxIkSolver.solve(outPose, bone.boneIndex, useLegIkBias);
      }
    }
  }

  public void getPmxPose(PmxPose pose) {
    pose.copy(this.outPose);
  }

  public void enablePhysics(boolean enabled) {
    this.physicsEnabled = enabled;
  }

  public boolean isPhysicsEnabled() {
    return physicsEnabled;
  }

  public PmxModel getModel() {
    return model;
  }

  public void dispose() {
    if (!disposed) {
      physics.dispose();
      physics = null;
      disposed = true;
    }
  }

  public void resetPhysics(PmxPose inPose) {
    outPose.copy(inPose);
    for (int i = 0; i < model.getBoneCount(); i++) {
      PmxBone bone = model.getBoneByOrder(i);
      if (bone.isIk()) {
        PmxIkSolver.solve(outPose, bone.boneIndex, useLegIkBias);
      }
    }
    physics.resetPhysics(outPose);
    /*
    ikSolver.solve(this.outPose, useLegIkBias);
    physics.resetPhysicsWithPose(outPose);
    physics.update(outPose, 1);
    physics.resetPhysicsWithPose(outPose);
    physics.update(outPose, 1);
    physics.resetPhysicsWithPose(outPose);
    physics.update(outPose, 1);
    */
  }

  public PmxModelPhysics getPhysics() {
    return physics;
  }
}
