package hana04.yuri.texture.twodim.arithemetic;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.changeprop.util.VvTransform;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.SpectrumSpace;
import hana04.gfxbase.spectrum.SpectrumTransform;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.spectrum.rgb.RgbSpace;
import hana04.gfxbase.spectrum.scalar.Scalar;
import hana04.gfxbase.spectrum.scalar.ScalarSpace;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;
import hana04.shakuyaku.texture.twodim.arithmetic.ScaleTwoDimTexture;
import hana04.yuri.texture.twodim.TextureTwoDimAlphaEvaluator;
import hana04.yuri.texture.twodim.TextureTwoDimEvaluator;
import hana04.yuri.texture.twodim.specspaces.TextureTwoDimEvaluatorRgb;
import hana04.yuri.texture.twodim.specspaces.TextureTwoDimEvaluatorScalar;

import javax.vecmath.Tuple2d;

public class ScaleTwoDimTextures {
  public static class BaseSpectrumEvaluatorRgbVv extends VvTransform<TextureTwoDim, TextureTwoDimEvaluatorRgb> {
    @HanaDeclareExtension(
      extensibleClass = ScaleTwoDimTexture.class,
      extensionClass = BaseSpectrumEvaluatorRgbVv.class)
    BaseSpectrumEvaluatorRgbVv(ScaleTwoDimTexture node) {
      super(
        node.getExtension(hana04.shakuyaku.texture.twodim.arithmetic.ScaleTwoDimTextures.UnwrappedBaseTexture.class),
        texture -> texture.getExtension(TextureTwoDimEvaluatorRgb.Vv.class));
    }
  }

  public static class BaseSpectrumEvaluatorScalarVv extends VvTransform<TextureTwoDim, TextureTwoDimEvaluatorScalar> {
    @HanaDeclareExtension(
      extensibleClass = ScaleTwoDimTexture.class,
      extensionClass = BaseSpectrumEvaluatorScalarVv.class)
    BaseSpectrumEvaluatorScalarVv(ScaleTwoDimTexture node) {
      super(
        node.getExtension(hana04.shakuyaku.texture.twodim.arithmetic.ScaleTwoDimTextures.UnwrappedBaseTexture.class),
        texture -> texture.getExtension(TextureTwoDimEvaluatorScalar.Vv.class));
    }
  }

  public static class BaseAlphaEvaluatorVv
    extends VvTransform<TextureTwoDim, TextureTwoDimAlphaEvaluator> {
    @HanaDeclareExtension(
      extensibleClass = ScaleTwoDimTexture.class,
      extensionClass = BaseAlphaEvaluatorVv.class)
    BaseAlphaEvaluatorVv(ScaleTwoDimTexture node) {
      super(
        node.getExtension(hana04.shakuyaku.texture.twodim.arithmetic.ScaleTwoDimTextures.UnwrappedBaseTexture.class),
        texture -> texture.getExtension(TextureTwoDimAlphaEvaluator.Vv.class));
    }
  }

  public static class SpectrumEvaluator<T extends Spectrum, V extends SpectrumTransform<T>>
    implements TextureTwoDimEvaluator<T> {
    private final TextureTwoDimEvaluator<T> baseEvaluator;
    private final V scale;
    private final SpectrumSpace<T, V> ss;

    public SpectrumEvaluator(TextureTwoDimEvaluator<T> baseEvaluator, V scale, SpectrumSpace<T, V> ss) {
      this.baseEvaluator = baseEvaluator;
      this.scale = scale;
      this.ss = ss;
    }

    @Override
    public T eval(Tuple2d uv) {
      return ss.transform(scale, baseEvaluator.eval(uv));
    }

    static class ForRgb extends SpectrumEvaluator<Rgb, Rgb> implements TextureTwoDimEvaluatorRgb {
      ForRgb(TextureTwoDimEvaluatorRgb baseEvaluator, Spectrum scale) {
        super(baseEvaluator, RgbSpace.I.createTransform(RgbSpace.I.convert(scale)), RgbSpace.I);
      }
    }

    static class ForScalar extends SpectrumEvaluator<Scalar, Scalar> implements TextureTwoDimEvaluatorScalar {
      ForScalar(TextureTwoDimEvaluatorScalar baseEvaluator, Spectrum scale) {
        super(baseEvaluator, ScalarSpace.I.createTransform(ScalarSpace.I.convert(scale)), ScalarSpace.I);
      }
    }
  }

  public static class SpectrumEvaluatorRgbVv
    extends DerivedVersionedValue<TextureTwoDimEvaluatorRgb>
    implements TextureTwoDimEvaluatorRgb.Vv {
    @HanaDeclareExtension(
      extensibleClass = ScaleTwoDimTexture.class,
      extensionClass = TextureTwoDimEvaluatorRgb.Vv.class)
    SpectrumEvaluatorRgbVv(ScaleTwoDimTexture node) {
      super(
        ImmutableList.of(
          node.getExtension(BaseSpectrumEvaluatorRgbVv.class),
          node.spectrumScale()),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> new SpectrumEvaluator.ForRgb(
          node.getExtension(BaseSpectrumEvaluatorRgbVv.class).value(),
          node.spectrumScale().value()));
    }
  }

  public static class SpectrumEvaluatorScalarVv
    extends DerivedVersionedValue<TextureTwoDimEvaluatorScalar>
    implements TextureTwoDimEvaluatorScalar.Vv {
    @HanaDeclareExtension(
      extensibleClass = ScaleTwoDimTexture.class,
      extensionClass = TextureTwoDimEvaluatorScalar.Vv.class)
    SpectrumEvaluatorScalarVv(ScaleTwoDimTexture node) {
      super(
        ImmutableList.of(
          node.getExtension(BaseSpectrumEvaluatorScalarVv.class),
          node.spectrumScale()),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> new SpectrumEvaluator.ForScalar(
          node.getExtension(BaseSpectrumEvaluatorScalarVv.class).value(),
          node.spectrumScale().value()));
    }
  }

  public static class AlphaEvaluator implements TextureTwoDimAlphaEvaluator {
    private final TextureTwoDimAlphaEvaluator alphaEvaluator;
    private final double alphaScale;

    AlphaEvaluator(TextureTwoDimAlphaEvaluator alphaEvaluator, double alphaScale) {
      this.alphaEvaluator = alphaEvaluator;
      this.alphaScale = alphaScale;
    }

    @Override
    public double eval(Tuple2d uv) {
      return alphaScale * alphaEvaluator.eval(uv);
    }
  }

  public static class AlphaEvaluatorVv
    extends DerivedVersionedValue<TextureTwoDimAlphaEvaluator>
    implements TextureTwoDimAlphaEvaluator.Vv {
    @HanaDeclareExtension(
      extensibleClass = ScaleTwoDimTexture.class,
      extensionClass = TextureTwoDimAlphaEvaluator.Vv.class)
    AlphaEvaluatorVv(ScaleTwoDimTexture texture) {
      super(
        ImmutableList.of(
          texture.getExtension(BaseAlphaEvaluatorVv.class),
          texture.alphaScale()),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> new AlphaEvaluator(texture.getExtension(BaseAlphaEvaluatorVv.class).value(),
          texture.alphaScale().value()));
    }
  }
}
