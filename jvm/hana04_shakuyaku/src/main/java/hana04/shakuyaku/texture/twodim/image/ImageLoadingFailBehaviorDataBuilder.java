package hana04.shakuyaku.texture.twodim.image;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(ImageLoadingFailBehaviorData.class)
public class ImageLoadingFailBehaviorDataBuilder
    extends ImageLoadingFailBehaviorData__Impl__Builder<ImageLoadingFailBehaviorDataBuilder> {
  @Inject
  public ImageLoadingFailBehaviorDataBuilder(ImageLoadingFailBehaviorData__ImplFactory factory) {
    super(factory);
    defaultToBlackImageIfLoadingFail(true);
  }

  public static ImageLoadingFailBehaviorDataBuilder builder(Component component) {
    return component.uberFactory().create(ImageLoadingFailBehaviorDataBuilder.class);
  }
}
