package hana04.mikumikubake.bake.adaptor;

import hana04.formats.mmd.vpd.VpdPose;

public interface MmdAnimatedInstanceAdaptor<ModelType, PoseType> {
  void setGravity(float x, float y, float z);

  void enablePhysics(boolean enabled);

  void resetPhysics(MmdPoseAdaptor<PoseType> modelPose);

  void getModelPose(MmdPoseAdaptor<PoseType> modelPose);

  void setModelPose(MmdPoseAdaptor<PoseType> modelPose);

  void setVpdPose(VpdPose vpdPose);

  void update(float elapsedTime);

  void dispose();
}
