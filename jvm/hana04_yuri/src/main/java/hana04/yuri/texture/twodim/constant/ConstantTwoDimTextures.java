package hana04.yuri.texture.twodim.constant;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.gfxbase.spectrum.rgb.RgbSpace;
import hana04.gfxbase.spectrum.scalar.ScalarSpace;
import hana04.shakuyaku.texture.twodim.constant.ConstantTwoDimTexture;
import hana04.yuri.texture.twodim.TextureTwoDimAlphaEvaluator;
import hana04.yuri.texture.twodim.specspaces.TextureTwoDimEvaluatorRgb;
import hana04.yuri.texture.twodim.specspaces.TextureTwoDimEvaluatorScalar;

public class ConstantTwoDimTextures {
  public static class EvaluatorRgbVv
    extends DerivedVersionedValue<TextureTwoDimEvaluatorRgb>
    implements TextureTwoDimEvaluatorRgb.Vv {
    @HanaDeclareExtension(
      extensibleClass = ConstantTwoDimTexture.class,
      extensionClass = TextureTwoDimEvaluatorRgb.Vv.class)
    EvaluatorRgbVv(ConstantTwoDimTexture texture) {
      super(
        ImmutableList.of(texture.spectrum()),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> uv -> RgbSpace.I.convert(texture.spectrum().value()));
    }
  }

  public static class EvaluatorScalarVv
    extends DerivedVersionedValue<TextureTwoDimEvaluatorScalar>
    implements TextureTwoDimEvaluatorScalar.Vv {
    @HanaDeclareExtension(
      extensibleClass = ConstantTwoDimTexture.class,
      extensionClass = TextureTwoDimEvaluatorScalar.Vv.class)
    EvaluatorScalarVv(ConstantTwoDimTexture texture) {
      super(
        ImmutableList.of(texture.spectrum()),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> uv -> ScalarSpace.I.convert(texture.spectrum().value()));
    }
  }

  public static class AlphaEvaluator extends DerivedVersionedValue<TextureTwoDimAlphaEvaluator>
    implements TextureTwoDimAlphaEvaluator.Vv {
    @HanaDeclareExtension(
      extensibleClass = ConstantTwoDimTexture.class,
      extensionClass = TextureTwoDimAlphaEvaluator.Vv.class)
    AlphaEvaluator(ConstantTwoDimTexture texture) {
      super(
        ImmutableList.of(texture.alpha()),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> uv -> texture.alpha().value());
    }
  }

}
