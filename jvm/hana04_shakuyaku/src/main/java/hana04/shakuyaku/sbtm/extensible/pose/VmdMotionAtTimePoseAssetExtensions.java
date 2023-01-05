package hana04.shakuyaku.sbtm.extensible.pose;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.Constant;
import hana04.base.changeprop.VersionedValue;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.formats.mmd.vmd.VmdMotion;
import hana04.formats.mmd.vpd.VpdPose;
import hana04.shakuyaku.sbtm.SbtmPose;
import hana04.shakuyaku.sbtm.converter.VpdPoseToSbtmPoseConverter;

public class VmdMotionAtTimePoseAssetExtensions {
  public static class VmdMotionVv_ extends DerivedVersionedValue<VmdMotion> implements VmdMotionVv {

    @HanaDeclareExtension(
      extensibleClass = VmdMotionAtTimePoseAsset.class,
      extensionClass = VmdMotionVv.class)
    public VmdMotionVv_(VmdMotionAtTimePoseAsset asset) {
      super(
          ImmutableList.of(new Constant<>(asset.filePath())),
        ChangePropUtil::largestBetweenIncSelfAndDeps, () -> {
          String resolvePath = asset.filePath().storedPath;
          try {
            return VmdMotion.load(resolvePath);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
    }
  }

  public static class SbtmPoseVv_ extends DerivedVersionedValue<SbtmPose> implements SbtmPoseVv {

    @HanaDeclareExtension(
      extensibleClass = VmdMotionAtTimePoseAsset.class,
      extensionClass = SbtmPoseVv.class)
    public SbtmPoseVv_(VmdMotionAtTimePoseAsset asset) {
      super(
        ImmutableList.of(asset.getExtension(VmdMotionVv.class), asset.time()),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> {
          VersionedValue<VmdMotion> vmdMotionVv = asset.getExtension(VmdMotionVv.class);
          VmdMotion motion = vmdMotionVv.value();
          double time = asset.time().value();
          VpdPose pose = new VpdPose();
          motion.getPose((float) time, pose);
          return VpdPoseToSbtmPoseConverter.convert(pose);
        });
    }
  }
}
