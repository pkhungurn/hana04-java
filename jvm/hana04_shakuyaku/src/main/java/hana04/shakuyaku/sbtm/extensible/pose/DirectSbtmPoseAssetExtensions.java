package hana04.shakuyaku.sbtm.extensible.pose;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.util.VvProxy;
import hana04.shakuyaku.sbtm.SbtmPose;

public class DirectSbtmPoseAssetExtensions {
  public static class SbtmPoseVv_ extends VvProxy<SbtmPose> implements SbtmPoseVv {
    @HanaDeclareExtension(
      extensibleClass = DirectSbtmPoseAsset.class,
      extensionClass = SbtmPoseVv.class)
    SbtmPoseVv_(DirectSbtmPoseAsset asset) {
      super(asset.sbtmPose());
    }
  }
}
