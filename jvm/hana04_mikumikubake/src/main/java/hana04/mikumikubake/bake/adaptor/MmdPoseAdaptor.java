package hana04.mikumikubake.bake.adaptor;

import hana04.formats.mmd.vmd.VmdMotion;

public interface MmdPoseAdaptor<PoseType> {
  PoseType getPose();

  void copyMotionFrame(VmdMotion vmdMotion, float time);

  void addKeyFrameToMotion(VmdMotion outputVmdMotion, int frameNumber);
}
