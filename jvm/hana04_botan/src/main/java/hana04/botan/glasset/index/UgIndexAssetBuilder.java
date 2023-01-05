package hana04.botan.glasset.index;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(UgIndexAsset.class)
public class UgIndexAssetBuilder extends UgIndexAsset__Impl__Builder<UgIndexAssetBuilder> {
  @Inject
  public UgIndexAssetBuilder(UgIndexAsset__ImplFactory factory) {
    super(factory);
  }

  public static UgIndexAssetBuilder builder(Component component) {
    return component.uberFactory().create(UgIndexAssetBuilder.class);
  }
}
