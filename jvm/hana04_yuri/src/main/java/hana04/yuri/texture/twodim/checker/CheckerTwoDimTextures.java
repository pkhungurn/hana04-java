package hana04.yuri.texture.twodim.checker;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.extension.validator.Validator;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.SpectrumSpace;
import hana04.gfxbase.spectrum.SpectrumTransform;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.spectrum.rgb.RgbSpace;
import hana04.yuri.texture.twodim.TextureTwoDimEvaluator;
import hana04.yuri.texture.twodim.specspaces.TextureTwoDimEvaluatorRgb;

import javax.inject.Inject;
import javax.vecmath.Tuple2d;

public class CheckerTwoDimTextures {
  @HanaDeclareBuilder(CheckerTwoDimTexture.class)
  public static class CheckerTwoDimTextureBuilder
      extends CheckerTwoDimTexture__Impl__Builder<CheckerTwoDimTextureBuilder> {
    @Inject
    public CheckerTwoDimTextureBuilder(CheckerTwoDimTexture__ImplFactory factory) {
      super(factory);
      evenSpectrum(new Rgb(0, 0, 0));
      oddSpectrum(new Rgb(1, 1, 1));
      uCellSize(1.0);
      vCellSize(1.0);
    }

    public static CheckerTwoDimTextureBuilder builder(Component component) {
      return component.uberFactory().create(CheckerTwoDimTextureBuilder.class);
    }

    public abstract static class Evaluator<T extends Spectrum, V extends SpectrumTransform<T>>
        implements TextureTwoDimEvaluator<T> {
      private final SpectrumSpace<T, V> ss;
      private T evenSpectrum;
      private T oddSpectrum;
      private double uCellSize;
      private double vCellSize;

      Evaluator(CheckerTwoDimTexture texture, SpectrumSpace<T, V> ss) {
        this.ss = ss;
        this.evenSpectrum = ss.convert(texture.evenSpectrum().value());
        this.oddSpectrum = ss.convert(texture.oddSpectrum().value());
        this.uCellSize = texture.uCellSize().value();
        this.vCellSize = texture.vCellSize().value();
      }

      @Override
      public T eval(Tuple2d uv) {
        long sumCoord =
            (long) (Math.floor(uv.x / uCellSize) + Math.floor(uv.y / vCellSize));
        if (sumCoord % 2 == 0) {
          return ss.copy(evenSpectrum);
        } else {
          return ss.copy(oddSpectrum);
        }
      }

      static class ForRgb extends Evaluator<Rgb, Rgb> implements TextureTwoDimEvaluatorRgb {
        ForRgb(CheckerTwoDimTexture texture) {
          super(texture, RgbSpace.I);
        }
      }
    }

    public static class EvaluatorRgbVv
        extends DerivedVersionedValue<TextureTwoDimEvaluatorRgb>
        implements TextureTwoDimEvaluatorRgb.Vv {
      @HanaDeclareExtension(
          extensibleClass = CheckerTwoDimTexture.class,
          extensionClass = TextureTwoDimEvaluatorRgb.Vv.class)
      EvaluatorRgbVv(CheckerTwoDimTexture texture) {
        super(
            ImmutableList.of(
                texture.evenSpectrum(),
                texture.oddSpectrum(),
                texture.uCellSize(),
                texture.vCellSize()),
            ChangePropUtil::largestBetweenIncSelfAndDeps,
            () -> new Evaluator.ForRgb(texture));
      }
    }
  }

  public static class CheckerTwoDimTextureValidator implements Validator {
    private final CheckerTwoDimTexture instance;

    @HanaDeclareExtension(
        extensibleClass = CheckerTwoDimTexture.class,
        extensionClass = Validator.class)
    public CheckerTwoDimTextureValidator(CheckerTwoDimTexture instance) {
      this.instance = instance;
    }

    @Override
    public void validate() {
      Preconditions.checkState(instance.uCellSize().value() > 0);
      Preconditions.checkState(instance.vCellSize().value() > 0);
    }
  }
}
