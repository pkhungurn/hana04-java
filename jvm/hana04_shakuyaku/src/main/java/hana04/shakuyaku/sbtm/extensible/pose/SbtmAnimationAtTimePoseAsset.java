package hana04.shakuyaku.sbtm.extensible.pose;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.Variable;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.sbtm.extensible.animation.SbtmAnimationAsset;

@HanaDeclareObject(
    parent = SbtmPoseAsset.class,
    typeId = TypeIds.TYPE_ID_SBTM_ANIMATION_AT_TIME_POSE_ASSET,
    typeNames = {"shakuyaku.SbtmAnimationAtTimePoseAsset", "SbtmAnimationAtTimePoseAsset"})
public interface SbtmAnimationAtTimePoseAsset extends SbtmPoseAsset {
  @HanaProperty(1)
  Variable<Wrapped<SbtmAnimationAsset>> animationAsset();

  @HanaProperty(2)
  Variable<Double> time();
}
