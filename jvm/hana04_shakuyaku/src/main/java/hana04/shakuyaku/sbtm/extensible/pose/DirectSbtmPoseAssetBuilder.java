package hana04.shakuyaku.sbtm.extensible.pose;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;
import hana04.shakuyaku.sbtm.SbtmPose;

import javax.inject.Inject;

@HanaDeclareBuilder(DirectSbtmPoseAsset.class)
public class DirectSbtmPoseAssetBuilder extends DirectSbtmPoseAsset__Impl__Builder<DirectSbtmPoseAssetBuilder> {
  @Inject
  public DirectSbtmPoseAssetBuilder(DirectSbtmPoseAsset__ImplFactory factory) {
    super(factory);
    sbtmPose(new SbtmPose());
  }

  public static DirectSbtmPoseAssetBuilder builder(Component component) {
    return component.uberFactory().create(DirectSbtmPoseAssetBuilder.class);
  }
}
