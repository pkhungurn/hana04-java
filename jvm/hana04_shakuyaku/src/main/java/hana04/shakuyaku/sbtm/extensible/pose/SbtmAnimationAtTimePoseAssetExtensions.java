package hana04.shakuyaku.sbtm.extensible.pose;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.changeprop.util.VvWrappedToVvAdaptor;
import hana04.shakuyaku.sbtm.SbtmAnimation;
import hana04.shakuyaku.sbtm.extensible.animation.SbtmAnimationAsset;
import hana04.shakuyaku.sbtm.extensible.animation.SbtmAnimationVv;

public class SbtmAnimationAtTimePoseAssetExtensions {
  public static class SbtmAnimationVv_
    extends VvWrappedToVvAdaptor<SbtmAnimationAsset, SbtmAnimation>
    implements SbtmAnimationVv {

    @HanaDeclareExtension(
      extensibleClass = SbtmAnimationAtTimePoseAsset.class,
      extensionClass = SbtmAnimationVv.class)
    SbtmAnimationVv_(SbtmAnimationAtTimePoseAsset asset, HanaUnwrapper unwrapper) {
      super(
        asset.animationAsset(),
        unwrappedAsset -> unwrappedAsset.getExtension(SbtmAnimationVv.class),
        unwrapper);
    }
  }

  public static class SbtmPoseVv_ extends SbtmAnimationAtTimePoseVv {

    @HanaDeclareExtension(
      extensibleClass = SbtmAnimationAtTimePoseAsset.class,
      extensionClass = SbtmPoseVv.class)
    SbtmPoseVv_(SbtmAnimationAtTimePoseAsset asset) {
      super(asset.getExtension(SbtmAnimationVv.class), asset.time());
    }
  }
}
