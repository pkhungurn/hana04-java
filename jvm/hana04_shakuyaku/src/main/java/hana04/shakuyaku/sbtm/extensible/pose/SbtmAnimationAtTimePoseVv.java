package hana04.shakuyaku.sbtm.extensible.pose;

import com.google.common.collect.ImmutableList;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.VersionedValue;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.shakuyaku.sbtm.SbtmAnimation;
import hana04.shakuyaku.sbtm.SbtmPose;

public class SbtmAnimationAtTimePoseVv
  extends DerivedVersionedValue<SbtmPose>
  implements SbtmPoseVv {

  public SbtmAnimationAtTimePoseVv(VersionedValue<SbtmAnimation> animation, VersionedValue<Double> time) {
    super(
      ImmutableList.of(animation, time),
      ChangePropUtil::largestBetweenIncSelfAndDeps,
      () -> {
        SbtmPose pose = new SbtmPose();
        animation.value().getPose(time.value(), pose);
        return pose;
      }
    );
  }
}
