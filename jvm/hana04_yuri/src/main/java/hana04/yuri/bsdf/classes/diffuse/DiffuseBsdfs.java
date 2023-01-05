package hana04.yuri.bsdf.classes.diffuse;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.VersionedValue;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.changeprop.util.VvTransform;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.SpectrumSpace;
import hana04.gfxbase.spectrum.SpectrumTransform;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.spectrum.rgb.RgbSpace;
import hana04.gfxbase.gfxtype.Frame;
import hana04.gfxbase.gfxtype.Measure;
import hana04.shakuyaku.bsdf.classes.diffuse.DiffuseBsdf;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;
import hana04.yuri.bsdf.BsdfEvaluator;
import hana04.yuri.bsdf.BsdfSampler;
import hana04.yuri.bsdf.BsdfSamplingInput;
import hana04.yuri.bsdf.BsdfSamplingOutput;
import hana04.yuri.bsdf.specspaces.BsdfEvaluatorRgb;
import hana04.yuri.bsdf.specspaces.BsdfSamplerRgb;
import hana04.yuri.bsdf.util.TwoSidedUtil;
import hana04.yuri.sampler.RandomNumberGenerator;
import hana04.yuri.texture.twodim.TextureTwoDimEvaluator;
import hana04.yuri.texture.twodim.specspaces.TextureTwoDimEvaluatorRgb;
import hana04.yuri.util.SamplingUtil;

import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import java.util.Optional;

public class DiffuseBsdfs {
  public interface TextureEvaluatorVv<T extends Spectrum, U extends TextureTwoDimEvaluator<T>>
    extends VersionedValue<Optional<U>> {
    // NO-OP
  }

  public static class TextureEvaluatorRgbVv extends VvTransform<TextureTwoDim, TextureTwoDimEvaluatorRgb> {
    @HanaDeclareExtension(
      extensibleClass = DiffuseBsdf.class,
      extensionClass = TextureEvaluatorRgbVv.class)
    TextureEvaluatorRgbVv(DiffuseBsdf bsdf) {
      super(
        bsdf.getExtension(hana04.shakuyaku.bsdf.classes.diffuse.DiffuseBsdfs.UnwrappedReflectanceTextureVv.class),
        texture -> texture.getExtension(TextureTwoDimEvaluatorRgb.Vv.class));
    }
  }

  public abstract static class Evaluator<
    T extends Spectrum,
    V extends SpectrumTransform<T>>
    implements BsdfEvaluator<T, V> {
    private final TextureTwoDimEvaluator<T> textureEvaluator;
    private final SpectrumSpace<T, V> ss;

    Evaluator(TextureTwoDimEvaluator<T> textureEvaluator,
              SpectrumSpace<T, V> ss) {
      this.ss = ss;
      this.textureEvaluator = textureEvaluator;
    }

    private V evalOneSide(Vector3d wi, Vector3d wo, Tuple2d uv) {
      if (Frame.cosTheta(wi) <= 0 || Frame.cosTheta(wo) <= 0) {
        return ss.createZeroTransform();
      }
      return ss.createTransform(ss.scale(1.0 / Math.PI, textureEvaluator.eval(uv)));
    }

    @Override
    public V eval(Vector3d wi, Vector3d wo, Tuple2d uv) {
      return TwoSidedUtil.eval(wi, wo, uv, ss, this::evalOneSide, this::evalOneSide);
    }

    static class ForRgb extends Evaluator<Rgb, Rgb> implements BsdfEvaluatorRgb {
      ForRgb(TextureTwoDimEvaluatorRgb textureEvaluator) {
        super(textureEvaluator, RgbSpace.I);
      }
    }
  }

  public static class EvaluatorRgbVv
    extends DerivedVersionedValue<BsdfEvaluatorRgb>
    implements BsdfEvaluatorRgb.Vv {
    @HanaDeclareExtension(
      extensibleClass = DiffuseBsdf.class,
      extensionClass = BsdfEvaluatorRgb.Vv.class
    )
    EvaluatorRgbVv(DiffuseBsdf bsdf) {
      super(
        ImmutableList.of(
          bsdf.reflectance(),
          bsdf.getExtension(TextureEvaluatorRgbVv.class)),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> new Evaluator.ForRgb(
          bsdf.getExtension(TextureEvaluatorRgbVv.class).value()));
    }
  }

  public abstract static class Sampler<
    T extends Spectrum,
    V extends SpectrumTransform<T>>
    implements BsdfSampler<T, V> {
    private final TextureTwoDimEvaluator<T> textureEvaluator;
    private final SpectrumSpace<T, V> ss;

    Sampler(TextureTwoDimEvaluator<T> textureEvaluator, SpectrumSpace<T, V> ss) {
      this.ss = ss;
      this.textureEvaluator = textureEvaluator;
    }

    private V getTransform(Tuple2d uv) {
      return ss.createTransform(textureEvaluator.eval(uv));
    }

    private BsdfSamplingOutput<T, V> sampleOneSide(BsdfSamplingInput samplingRecord, RandomNumberGenerator rng) {
      Vector2d xi = new Vector2d();
      rng.next2D(xi);
      Vector3d wOut = new Vector3d();
      SamplingUtil.squareToCosineHemisphere(xi, wOut);

      BsdfSamplingOutput<T, V> result = new BsdfSamplingOutput<>(getTransform(samplingRecord.uv));
      result.measure = Measure.SolidAngle;
      result.sampledDirection = samplingRecord.sampledDirection;
      result.wOut.set(wOut);
      result.eta = 1.0;

      return result;
    }

    @Override
    public BsdfSamplingOutput<T, V> sample(BsdfSamplingInput samplingRecord, RandomNumberGenerator rng) {
      return TwoSidedUtil.sample(samplingRecord, rng, this::sampleOneSide, this::sampleOneSide);
    }

    private double pdfOneSide(BsdfSamplingInput samplingRecord, Vector3d wOut, Measure measure) {
      if (measure != Measure.SolidAngle) {
        return 0;
      }
      if (Frame.cosTheta(samplingRecord.wIn) <= 0 || Frame.cosTheta(wOut) <= 0)
        return 0;
      return Frame.cosTheta(wOut) / Math.PI;
    }

    @Override
    public double pdf(BsdfSamplingInput samplingRecord, Vector3d wOut, Measure measure) {
      return TwoSidedUtil.pdf(samplingRecord, wOut, measure, this::pdfOneSide, this::pdfOneSide);
    }

    static class ForRgb extends Sampler<Rgb, Rgb> implements BsdfSamplerRgb {
      ForRgb(TextureTwoDimEvaluatorRgb textureEvaluator) {
        super(textureEvaluator, RgbSpace.I);
      }
    }
  }

  public static class SamplerRgbVv
    extends DerivedVersionedValue<BsdfSamplerRgb>
    implements BsdfSamplerRgb.Vv {
    @HanaDeclareExtension(
      extensibleClass = DiffuseBsdf.class,
      extensionClass = BsdfSamplerRgb.Vv.class)
    SamplerRgbVv(DiffuseBsdf bsdf) {
      super(
        ImmutableList.of(
          bsdf.reflectance(),
          bsdf.getExtension(TextureEvaluatorRgbVv.class)),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> new Sampler.ForRgb(
          bsdf.getExtension(TextureEvaluatorRgbVv.class).value()));
    }
  }
}
