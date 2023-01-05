package hana04.shakuyaku.sbtm.extensible.pose;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.sbtm.SbtmPose;

@HanaDeclareObject(
  parent = SbtmPoseAsset.class,
  typeId = TypeIds.TYPE_ID_DIRECT_SBTM_POSE_ASSET,
  typeNames = {"shakuyaku.DirectSbtmPoseAsset", "DirectSbtmPoseAsset"})
public interface DirectSbtmPoseAsset extends SbtmPoseAsset {
  @HanaProperty(1)
  Variable<SbtmPose> sbtmPose();

}
