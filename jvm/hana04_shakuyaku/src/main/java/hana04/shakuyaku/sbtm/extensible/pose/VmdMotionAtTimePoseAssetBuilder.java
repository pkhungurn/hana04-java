package hana04.shakuyaku.sbtm.extensible.pose;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(VmdMotionAtTimePoseAsset.class)
public class VmdMotionAtTimePoseAssetBuilder
    extends VmdMotionAtTimePoseAsset__Impl__Builder<VmdMotionAtTimePoseAssetBuilder> {
  @Inject
  public VmdMotionAtTimePoseAssetBuilder(VmdMotionAtTimePoseAsset__ImplFactory factory) {
    super(factory);
  }

  public static VmdMotionAtTimePoseAssetBuilder builder(Component component) {
    return component.uberFactory().create(VmdMotionAtTimePoseAssetBuilder.class);
  }
}
