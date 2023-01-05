package hana04.yuri.texture.twodim.image;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.shakuyaku.texture.twodim.image.ImageTexture;
import hana04.shakuyaku.texture.twodim.image.ImageTextureData;
import hana04.yuri.texture.twodim.specspaces.TextureTwoDimEvaluatorRgb;
import hana04.yuri.texture.twodim.TextureTwoDimAlphaEvaluator;

public class ImageTextures {
  public static class EvaluatorRgbVv
      extends DerivedVersionedValue<TextureTwoDimEvaluatorRgb>
      implements TextureTwoDimEvaluatorRgb.Vv {
    @HanaDeclareExtension(
        extensibleClass = ImageTexture.class,
        extensionClass = TextureTwoDimEvaluatorRgb.Vv.class)
    EvaluatorRgbVv(ImageTexture texture) {
      super(
          ImmutableList.of(), ChangePropUtil::largestBetweenIncSelfAndDeps,
          () -> new ImageTextureEvaluator.ForRgb(
              texture.getExtension(ImageTextureData.Vv.class).value(),
              texture.wrapS().value(),
              texture.wrapT().value()));
    }
  }

  public static class AlphaEvaluatorVv
      extends DerivedVersionedValue<TextureTwoDimAlphaEvaluator>
      implements TextureTwoDimAlphaEvaluator.Vv {
    @HanaDeclareExtension(
        extensibleClass = ImageTexture.class,
        extensionClass = TextureTwoDimAlphaEvaluator.Vv.class)
    public AlphaEvaluatorVv(ImageTexture texture) {
      super(ImmutableList.of(), ChangePropUtil::largestBetweenIncSelfAndDeps,
          () -> new ImageTextureAlphaEvaluator(
              texture.getExtension(ImageTextureData.Vv.class).value(),
              texture.wrapS().value(),
              texture.wrapT().value()));
    }
  }
}
