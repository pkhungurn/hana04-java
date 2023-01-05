package hana04.botan.glasset.program;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(ResourceProgramAsset.class)
public class ResourceProgramAssetBuilder extends ResourceProgramAsset__Impl__Builder<ResourceProgramAssetBuilder> {
  @Inject
  public ResourceProgramAssetBuilder(ResourceProgramAsset__ImplFactory factory) {
    super(factory);
  }

  public static ResourceProgramAssetBuilder builder(Component component) {
    return component.uberFactory().create(ResourceProgramAssetBuilder.class);
  }
}
