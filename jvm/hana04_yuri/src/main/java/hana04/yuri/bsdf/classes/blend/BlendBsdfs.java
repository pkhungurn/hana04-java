package hana04.yuri.bsdf.classes.blend;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.changeprop.util.VvTransform;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.gfxtype.Measure;
import hana04.shakuyaku.bsdf.Bsdf;
import hana04.shakuyaku.bsdf.classes.blend.BlendBsdf;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;
import hana04.yuri.bsdf.BsdfSamplingInput;
import hana04.yuri.bsdf.BsdfSamplingOutput;
import hana04.yuri.bsdf.TransmittanceEvaluator;
import hana04.yuri.bsdf.specspaces.BsdfEvaluatorRgb;
import hana04.yuri.bsdf.specspaces.BsdfSamplerRgb;
import hana04.yuri.sampler.RandomNumberGenerator;
import hana04.yuri.texture.twodim.TextureTwoDimAlphaEvaluator;

import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

public class BlendBsdfs {
  public static class FirstEvaluatorRgbVv extends VvTransform<Bsdf, BsdfEvaluatorRgb> {
    @HanaDeclareExtension(
      extensibleClass = BlendBsdf.class,
      extensionClass = FirstEvaluatorRgbVv.class)
    FirstEvaluatorRgbVv(BlendBsdf bsdf) {
      super(bsdf.getExtension(hana04.shakuyaku.bsdf.classes.blend.BlendBsdfs.UnwrappedFirstBsdf.class),
        bsdf_ -> bsdf_.getExtension(BsdfEvaluatorRgb.Vv.class));
    }
  }

  public static class SecondEvaluatorRgbVv extends VvTransform<Bsdf, BsdfEvaluatorRgb> {
    @HanaDeclareExtension(
      extensibleClass = BlendBsdf.class,
      extensionClass = SecondEvaluatorRgbVv.class)
    SecondEvaluatorRgbVv(BlendBsdf bsdf) {
      super(bsdf.getExtension(hana04.shakuyaku.bsdf.classes.blend.BlendBsdfs.UnwrappedSecondBsdf.class),
        bsdf_ -> bsdf_.getExtension(BsdfEvaluatorRgb.Vv.class));
    }
  }

  public static class AlphaEvaluatorVv extends VvTransform<TextureTwoDim, TextureTwoDimAlphaEvaluator> {
    @HanaDeclareExtension(
      extensibleClass = BlendBsdf.class,
      extensionClass = AlphaEvaluatorVv.class)
    AlphaEvaluatorVv(BlendBsdf bsdf) {
      super(
        bsdf.getExtension(hana04.shakuyaku.bsdf.classes.blend.BlendBsdfs.UnwrappedAlphaTexture.class),
        texture -> texture.getExtension(TextureTwoDimAlphaEvaluator.Vv.class));
    }
  }

  public static class EvaluatorRgb implements BsdfEvaluatorRgb {
    private final BsdfEvaluatorRgb firstEvaluator;
    private final BsdfEvaluatorRgb secondEvaluator;
    private final TextureTwoDimAlphaEvaluator alphaEvaluator;

    EvaluatorRgb(BsdfEvaluatorRgb firstEvaluator,
                 BsdfEvaluatorRgb secondEvaluator,
                 TextureTwoDimAlphaEvaluator alphaEvaluator) {
      this.firstEvaluator = firstEvaluator;
      this.secondEvaluator = secondEvaluator;
      this.alphaEvaluator = alphaEvaluator;
    }

    @Override
    public Rgb eval(Vector3d wi, Vector3d wo, Tuple2d uv) {
      double alpha = alphaEvaluator.eval(uv);
      Rgb firstOutput = firstEvaluator.eval(wi, wo, uv);
      Rgb secondOutput = secondEvaluator.eval(wi, wo, uv);
      Rgb output = new Rgb(0, 0, 0);
      output.scaleAdd(alpha, firstOutput, output);
      output.scaleAdd(1 - alpha, secondOutput, output);
      return output;
    }
  }

  public static class EvaluatorRgbVv
    extends DerivedVersionedValue<BsdfEvaluatorRgb>
    implements BsdfEvaluatorRgb.Vv {
    @HanaDeclareExtension(
      extensibleClass = BlendBsdf.class,
      extensionClass = BsdfEvaluatorRgb.Vv.class)
    EvaluatorRgbVv(BlendBsdf bsdf) {
      super(
        ImmutableList.of(
          bsdf.getExtension(FirstEvaluatorRgbVv.class),
          bsdf.getExtension(SecondEvaluatorRgbVv.class),
          bsdf.getExtension(AlphaEvaluatorVv.class)),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> new EvaluatorRgb(
          bsdf.getExtension(FirstEvaluatorRgbVv.class).value(),
          bsdf.getExtension(SecondEvaluatorRgbVv.class).value(),
          bsdf.getExtension(AlphaEvaluatorVv.class).value()));
    }
  }

  public static class FirstSamplerRgbVv extends VvTransform<Bsdf, BsdfSamplerRgb> {
    @HanaDeclareExtension(
      extensibleClass = BlendBsdf.class,
      extensionClass = FirstSamplerRgbVv.class)
    FirstSamplerRgbVv(BlendBsdf bsdf) {
      super(bsdf.getExtension(hana04.shakuyaku.bsdf.classes.blend.BlendBsdfs.UnwrappedFirstBsdf.class),
        bsdf_ -> bsdf_.getExtension(BsdfSamplerRgb.Vv.class));
    }
  }

  public static class SecondSamplerRgbVv extends VvTransform<Bsdf, BsdfSamplerRgb> {
    @HanaDeclareExtension(
      extensibleClass = BlendBsdf.class,
      extensionClass = SecondSamplerRgbVv.class)
    SecondSamplerRgbVv(BlendBsdf bsdf) {
      super(bsdf.getExtension(hana04.shakuyaku.bsdf.classes.blend.BlendBsdfs.UnwrappedSecondBsdf.class),
        bsdf_ -> bsdf_.getExtension(BsdfSamplerRgb.Vv.class));
    }
  }

  public static class SamplerRgb implements BsdfSamplerRgb {
    private final BsdfSamplerRgb firstSampler;
    private final BsdfSamplerRgb secondSampler;
    private final TextureTwoDimAlphaEvaluator alphaEvaluator;

    SamplerRgb(BsdfSamplerRgb firstSampler, BsdfSamplerRgb secondSampler,
               TextureTwoDimAlphaEvaluator alphaEvaluator) {
      this.firstSampler = firstSampler;
      this.secondSampler = secondSampler;
      this.alphaEvaluator = alphaEvaluator;
    }

    @Override
    public BsdfSamplingOutput<Rgb, Rgb> sample(BsdfSamplingInput samplingRecord, RandomNumberGenerator rng) {
      double alpha = alphaEvaluator.eval(samplingRecord.uv);
      double xi = rng.next1D();
      if (xi <= alpha) {
        return firstSampler.sample(samplingRecord, rng);
      } else {
        return secondSampler.sample(samplingRecord, rng);
      }
    }

    @Override
    public double pdf(BsdfSamplingInput samplingRecord, Vector3d wOut, Measure measure) {
      double alpha = alphaEvaluator.eval(samplingRecord.uv);
      double firstPdf = firstSampler.pdf(samplingRecord, wOut, measure);
      double secondPdf = secondSampler.pdf(samplingRecord, wOut, measure);
      return alpha * firstPdf + (1 - alpha) * secondPdf;
    }
  }

  public static class SamplerRgbVv
    extends DerivedVersionedValue<BsdfSamplerRgb>
    implements BsdfSamplerRgb.Vv {
    @HanaDeclareExtension(
      extensibleClass = BlendBsdf.class,
      extensionClass = BsdfSamplerRgb.Vv.class)
    SamplerRgbVv(BlendBsdf bsdf) {
      super(
        ImmutableList.of(
          bsdf.getExtension(FirstSamplerRgbVv.class),
          bsdf.getExtension(SecondSamplerRgbVv.class),
          bsdf.getExtension(AlphaEvaluatorVv.class)),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> new SamplerRgb(
          bsdf.getExtension(FirstSamplerRgbVv.class).value(),
          bsdf.getExtension(SecondSamplerRgbVv.class).value(),
          bsdf.getExtension(AlphaEvaluatorVv.class).value()));
    }
  }

  public static class FirstTransmittanceEvaluatorVv extends VvTransform<Bsdf, TransmittanceEvaluator> {
    @HanaDeclareExtension(
      extensibleClass = BlendBsdf.class,
      extensionClass = FirstTransmittanceEvaluatorVv.class)
    public FirstTransmittanceEvaluatorVv(BlendBsdf bsdf) {
      super(
        bsdf.getExtension(hana04.shakuyaku.bsdf.classes.blend.BlendBsdfs.UnwrappedFirstBsdf.class),
        bsdf_ -> bsdf_.getExtension(TransmittanceEvaluator.Vv.class));
    }
  }

  public static class SecondTransmittanceEvaluatorVv extends VvTransform<Bsdf, TransmittanceEvaluator> {
    @HanaDeclareExtension(
      extensibleClass = BlendBsdf.class,
      extensionClass = SecondTransmittanceEvaluatorVv.class)
    public SecondTransmittanceEvaluatorVv(BlendBsdf bsdf) {
      super(
        bsdf.getExtension(hana04.shakuyaku.bsdf.classes.blend.BlendBsdfs.UnwrappedSecondBsdf.class),
        bsdf_ -> bsdf_.getExtension(TransmittanceEvaluator.Vv.class));
    }
  }

  public static class TransmittanceEvaluator_ implements TransmittanceEvaluator {
    private final TransmittanceEvaluator firstEvaluator;
    private final TransmittanceEvaluator secondEvaluator;
    private final TextureTwoDimAlphaEvaluator alphaEvaluator;

    TransmittanceEvaluator_(TransmittanceEvaluator firstEvaluator,
                            TransmittanceEvaluator secondEvaluator,
                            TextureTwoDimAlphaEvaluator alphaEvaluator) {
      this.firstEvaluator = firstEvaluator;
      this.secondEvaluator = secondEvaluator;
      this.alphaEvaluator = alphaEvaluator;
    }

    @Override
    public double eval(Vector3d shadowRayDir, Vector2d uv) {
      double firstTransmittance = this.firstEvaluator.eval(shadowRayDir, uv);
      double secondTransmittance = this.secondEvaluator.eval(shadowRayDir, uv);
      double alpha = this.alphaEvaluator.eval(uv);
      return alpha * firstTransmittance + (1 - alpha) * secondTransmittance;
    }
  }

  public static class TransmittanceEvaluatorVv
    extends DerivedVersionedValue<TransmittanceEvaluator>
    implements TransmittanceEvaluator.Vv {
    @HanaDeclareExtension(
      extensibleClass = BlendBsdf.class,
      extensionClass = TransmittanceEvaluator.Vv.class)
    TransmittanceEvaluatorVv(BlendBsdf bsdf) {
      super(
        ImmutableList.of(
          bsdf.getExtension(FirstTransmittanceEvaluatorVv.class),
          bsdf.getExtension(SecondTransmittanceEvaluatorVv.class),
          bsdf.getExtension(AlphaEvaluatorVv.class)),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> new TransmittanceEvaluator_(
          bsdf.getExtension(FirstTransmittanceEvaluatorVv.class).value(),
          bsdf.getExtension(SecondTransmittanceEvaluatorVv.class).value(),
          bsdf.getExtension(AlphaEvaluatorVv.class).value()));
    }
  }
}
