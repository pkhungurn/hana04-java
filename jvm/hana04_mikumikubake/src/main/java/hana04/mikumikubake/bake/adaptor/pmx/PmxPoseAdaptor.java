package hana04.mikumikubake.bake.adaptor.pmx;

import hana04.formats.mmd.pmx.PmxBone;
import hana04.formats.mmd.pmx.PmxMorph;
import hana04.formats.mmd.pmx.PmxPose;
import hana04.formats.mmd.vmd.VmdBoneKeyframe;
import hana04.formats.mmd.vmd.VmdBoneMotion;
import hana04.formats.mmd.vmd.VmdMorphKeyframe;
import hana04.formats.mmd.vmd.VmdMorphMotion;
import hana04.formats.mmd.vmd.VmdMotion;
import hana04.gfxbase.gfxtype.TupleUtil;
import hana04.mikumikubake.bake.adaptor.MmdPoseAdaptor;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class PmxPoseAdaptor implements MmdPoseAdaptor<PmxPose> {
  private final PmxPose pmxPose;

  public PmxPoseAdaptor(PmxPose pmxPose) {
    this.pmxPose = pmxPose;
  }

  @Override
  public PmxPose getPose() {
    return pmxPose;
  }

  @Override
  public void copyMotionFrame(VmdMotion vmdMotion, float time) {
    vmdMotion.getPose(time, pmxPose);
  }

  @Override
  public void addKeyFrameToMotion(VmdMotion outputVmdMotion, int frameNumber) {
    Vector3f displacement = new Vector3f();
    Quat4f rotation = new Quat4f();

    for (int i = 0; i < pmxPose.getModel().getBoneCount(); i++) {
      PmxBone bone = pmxPose.getModel().getBone(i);
      pmxPose.getBoneDisplacement(bone.boneIndex, displacement);
      pmxPose.getBoneRotation(bone.boneIndex, rotation);
      if (TupleUtil.isNaN(displacement)) {
        System.out.println("frameNumber = " + frameNumber + ", boneName = " + bone.japaneseName + " [displacement]");
      }
      if (TupleUtil.isNaN(rotation)) {
        System.out.println("frameNumber = " + frameNumber + ", boneName = " + bone.japaneseName + " [rotation]");
      }
      VmdBoneKeyframe keyframe = new VmdBoneKeyframe();
      keyframe.displacement.set(displacement);
      keyframe.rotation.set(rotation);
      keyframe.frameNumber = frameNumber;
      VmdBoneMotion motion = outputVmdMotion.boneMotions.get(bone.japaneseName);
      if (motion != null) {
        motion.keyFrames.add(keyframe);
      }
    }

    for (int i = 0; i < pmxPose.getModel().getMorphCount(); i++) {
      PmxMorph morph = pmxPose.getModel().getMorph(i);
      float weight = pmxPose.getMorphWeight(i);
      VmdMorphKeyframe keyframe = new VmdMorphKeyframe();
      keyframe.frameNumber = frameNumber;
      keyframe.weight = weight;
      VmdMorphMotion motion = outputVmdMotion.morphMotions.get(morph.japaneseName);
      if (motion != null) {
        motion.keyFrames.add(keyframe);
      }
    }
  }
}
