package hana04.shakuyaku.sbtm.extensible.pose;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(SbtmAnimationAtTimePoseAsset.class)
public class SbtmAnimationAtTimePoseAssetBuilder
    extends SbtmAnimationAtTimePoseAsset__Impl__Builder<SbtmAnimationAtTimePoseAssetBuilder> {
  @Inject
  public SbtmAnimationAtTimePoseAssetBuilder(SbtmAnimationAtTimePoseAsset__ImplFactory factory) {
    super(factory);
    time(0.0);
  }

  public static SbtmAnimationAtTimePoseAssetBuilder builder(Component component) {
    return component.uberFactory().create(SbtmAnimationAtTimePoseAssetBuilder.class);
  }
}
