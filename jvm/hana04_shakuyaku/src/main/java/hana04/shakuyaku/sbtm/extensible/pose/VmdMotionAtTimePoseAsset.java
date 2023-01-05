package hana04.shakuyaku.sbtm.extensible.pose;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.base.filesystem.FilePath;
import hana04.shakuyaku.TypeIds;

@HanaDeclareObject(
  parent = SbtmPoseAsset.class,
  typeId = TypeIds.TYPE_ID_VMD_MOTION_AT_TIME_POSE_ASSET,
  typeNames = {"shakuyaku.VmdMotionAtTimePoseAsset", "VmdMotionAtTimePoseAsset"})
public interface VmdMotionAtTimePoseAsset extends SbtmPoseAsset {
  @HanaProperty(1)
  FilePath filePath();

  @HanaProperty(2)
  Variable<Double> time();

}
