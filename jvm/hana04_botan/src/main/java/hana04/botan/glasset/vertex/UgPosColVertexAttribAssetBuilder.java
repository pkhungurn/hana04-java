package hana04.botan.glasset.vertex;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(UgPosColVertexAttribAsset.class)
public class UgPosColVertexAttribAssetBuilder
    extends UgPosColVertexAttribAsset__Impl__Builder<UgPosColVertexAttribAssetBuilder> {
  @Inject
  public UgPosColVertexAttribAssetBuilder(UgPosColVertexAttribAsset__ImplFactory factory) {
    super(factory);
  }

  public static UgPosColVertexAttribAssetBuilder builder(Component component) {
    return component.uberFactory().create(UgPosColVertexAttribAssetBuilder.class);
  }
}
