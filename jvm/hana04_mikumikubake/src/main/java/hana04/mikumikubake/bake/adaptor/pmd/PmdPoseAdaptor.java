package hana04.mikumikubake.bake.adaptor.pmd;

import hana04.formats.mmd.pmd.PmdBone;
import hana04.formats.mmd.pmd.PmdMorph;
import hana04.formats.mmd.pmd.PmdPose;
import hana04.formats.mmd.vmd.VmdBoneKeyframe;
import hana04.formats.mmd.vmd.VmdBoneMotion;
import hana04.formats.mmd.vmd.VmdMorphKeyframe;
import hana04.formats.mmd.vmd.VmdMorphMotion;
import hana04.formats.mmd.vmd.VmdMotion;
import hana04.mikumikubake.bake.adaptor.MmdPoseAdaptor;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class PmdPoseAdaptor implements MmdPoseAdaptor<PmdPose> {
  private final PmdPose pmdPose;

  public PmdPoseAdaptor(PmdPose pmdPose) {
    this.pmdPose = pmdPose;
  }

  @Override
  public PmdPose getPose() {
    return pmdPose;
  }

  @Override
  public void copyMotionFrame(VmdMotion vmdMotion, float time) {
    vmdMotion.getPose(time, pmdPose);
  }

  @Override
  public void addKeyFrameToMotion(VmdMotion outputVmdMotion, int frameNumber) {
    Vector3f displacement = new Vector3f();
    Quat4f rotation = new Quat4f();

    for (int boneIndex = 0; boneIndex < pmdPose.boneCount(); boneIndex++) {
      PmdBone bone = pmdPose.getModel().bones.get(boneIndex);
      pmdPose.getBoneDisplacement(boneIndex, displacement);
      pmdPose.getBoneRotation(boneIndex, rotation);
      VmdBoneKeyframe keyframe = new VmdBoneKeyframe();
      keyframe.displacement.set(displacement);
      keyframe.rotation.set(rotation);
      keyframe.frameNumber = frameNumber;
      VmdBoneMotion motion = outputVmdMotion.boneMotions.get(bone.japaneseName);
      if (motion != null) {
        motion.keyFrames.add(keyframe);
      }
    }

    for (int morphIndex = 0; morphIndex < pmdPose.morphCount(); morphIndex++) {
      PmdMorph morph = pmdPose.getModel().morphs.get(morphIndex);
      float weight = pmdPose.getMorphWeight(morphIndex);
      VmdMorphKeyframe keyframe = new VmdMorphKeyframe();
      keyframe.frameNumber = frameNumber;
      keyframe.weight = weight;
      VmdMorphMotion motion = outputVmdMotion.morphMotions.get(morph.japaneseName);
      if (motion != null)
        motion.keyFrames.add(keyframe);
    }
  }
}
