package hana04.shakuyaku.sbtm.extensible.animation;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(FileSbtmAnimationAsset.class)
public class FileSbtmAnimationAssetBuilder
    extends FileSbtmAnimationAsset__Impl__Builder<FileSbtmAnimationAssetBuilder> {
  @Inject
  public FileSbtmAnimationAssetBuilder(FileSbtmAnimationAsset__ImplFactory factory) {
    super(factory);
  }

  public static FileSbtmAnimationAssetBuilder builder(Component component) {
    return component.uberFactory().create(FileSbtmAnimationAssetBuilder.class);
  }
}
