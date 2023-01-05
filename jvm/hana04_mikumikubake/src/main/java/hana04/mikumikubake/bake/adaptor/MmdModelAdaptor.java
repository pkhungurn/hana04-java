package hana04.mikumikubake.bake.adaptor;

import hana04.formats.mmd.vmd.VmdMotion;

import javax.vecmath.Vector3f;

public interface MmdModelAdaptor<ModelType, PoseType> {
  MmdPoseAdaptor<PoseType> createPose();
  MmdAnimatedInstanceAdaptor<ModelType, PoseType> createAnimatedInstance();
  ModelType getModel();
  void populateBoneAndMorphNames(VmdMotion vmdMotion);
  String getJapaneseName();
  void getBoneDisplacement(String boneName, Vector3f displacement);
}
