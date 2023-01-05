package hana04.botan.glasset.program;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(FileProgramAsset.class)
public class FileProgramAssetBuilder extends FileProgramAsset__Impl__Builder<FileProgramAssetBuilder> {
  @Inject
  public FileProgramAssetBuilder(FileProgramAsset__ImplFactory factory) {
    super(factory);
  }

  public static FileProgramAssetBuilder builder(Component component) {
    return component.uberFactory().create(FileProgramAssetBuilder.class);
  }
}
