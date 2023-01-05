package hana04.yuri.bsdf.classes.simpmicrofacet;

import com.google.common.base.Preconditions;
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
import hana04.gfxbase.gfxtype.Frame;
import hana04.gfxbase.gfxtype.Measure;
import hana04.shakuyaku.bsdf.classes.simpmicrofacet.SimpleMicrofacetBsdf;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;
import hana04.yuri.bsdf.BsdfEvaluator;
import hana04.yuri.bsdf.BsdfSampler;
import hana04.yuri.bsdf.BsdfSamplingInput;
import hana04.yuri.bsdf.BsdfSamplingOutput;
import hana04.yuri.bsdf.SampledDirection;
import hana04.yuri.bsdf.specspaces.BsdfEvaluatorRgb;
import hana04.yuri.bsdf.specspaces.BsdfSamplerRgb;
import hana04.yuri.sampler.RandomNumberGenerator;
import hana04.yuri.texture.twodim.TextureTwoDimEvaluator;
import hana04.yuri.texture.twodim.specspaces.TextureTwoDimEvaluatorRgb;
import hana04.yuri.texture.twodim.specspaces.TextureTwoDimEvaluatorScalar;
import hana04.yuri.util.SamplingUtil;

import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

public class SimpleMicrofacetBsdfs {
  public abstract static class Evaluator<T extends Spectrum, V extends SpectrumTransform<T>>
    implements BsdfEvaluator<T, V> {
    private final TextureTwoDimEvaluator<T> diffuseEvaluator;
    private final TextureTwoDimEvaluatorScalar alphaEvaluator;
    private final double extIor;
    private final double intIor;
    private final SpectrumSpace<T, V> spectrumSpace;

    Evaluator(
      TextureTwoDimEvaluator<T> diffuseEvaluator,
      TextureTwoDimEvaluatorScalar alphaEvaluator,
      double extIor,
      double intIor,
      SpectrumSpace<T, V> spectrumSpace) {
      this.diffuseEvaluator = diffuseEvaluator;
      this.alphaEvaluator = alphaEvaluator;
      this.spectrumSpace = spectrumSpace;
      this.extIor = extIor;
      this.intIor = intIor;
    }

    @Override
    public V eval(Vector3d wi, Vector3d wo, Tuple2d uv) {
      if (Frame.cosTheta(wi) < 0 || Frame.cosTheta(wo) < 0) {
        return spectrumSpace.createZeroTransform();
      }

      double alpha = alphaEvaluator.eval(uv).toDouble();

      // Compute the half vector.
      Vector3d H = new Vector3d();
      H.add(wi, wo);
      H.normalize();

      // Evaluate the normal distribution
      double D = evalBeckmann(H, alpha);

      // Fresnel factor
      double F = fresnel(wi.dot(H));

      // Shadow mask term
      double G = shadowMask(wi, H, alpha) * shadowMask(wo, H, alpha);

      V value = spectrumSpace.createZeroTransform();
      T kd = diffuseEvaluator.eval(uv);
      double kdMax = kd.spectrumMaxComponent();
      Preconditions.checkArgument(kdMax >= 0 && kdMax <= 1);
      double ks = 1 - kd.spectrumMaxComponent();
      value = spectrumSpace.add(
        value,
        spectrumSpace.scale(1 / Math.PI, spectrumSpace.createTransform(kd)));
      double specTerm = D * F * G / (4 * Frame.cosTheta(wi) * Frame.cosTheta(wo)) * ks;
      return spectrumSpace.add(value, spectrumSpace.createTransformFromScalar(specTerm));
    }

    private double evalBeckmann(Vector3d m, double alpha) {
      double temp = Frame.tanTheta(m) / alpha;
      double ct = Frame.cosTheta(m);
      double ct2 = ct * ct;

      return Math.exp(-temp * temp) / (Math.PI * alpha * alpha * ct2 * ct2);
    }

    private double fresnel(double c) {
      double g = Math.sqrt((intIor * intIor / extIor / extIor) - 1 + c * c);
      return 0.5 * ((g - c) * (g - c) / (g + c) / (g + c)) *
        (1 + ((c * (g + c) - 1) * (c * (g + c) - 1) / (c * (g - c) + 1) / (c * (g - c) + 1)));
    }

    private double shadowMask(Vector3d v, Vector3d m, double alpha) {
      double a = 1.0 / (alpha * Frame.tanTheta(v));

      if (v.dot(m) * Frame.cosTheta(v) <= 0) {
        return 0;
      }

      if (a < 1.6) {
        return (3.535 * a + 2.181 * a * a) / (1 + 2.276 * a + 2.577 * a * a);
      } else {
        return 1;
      }
    }

    public static class ForRgb extends Evaluator<Rgb, Rgb> implements BsdfEvaluatorRgb {
      public ForRgb(
        TextureTwoDimEvaluatorRgb diffuseEvaluator,
        TextureTwoDimEvaluatorScalar alphaEvaluator,
        double extIor,
        double intIor) {
        super(diffuseEvaluator, alphaEvaluator, extIor, intIor, RgbSpace.I);
      }
    }
  }

  public static class EvaluatorRgbVv
    extends DerivedVersionedValue<BsdfEvaluatorRgb>
    implements BsdfEvaluatorRgb.Vv {
    @HanaDeclareExtension(
      extensibleClass = SimpleMicrofacetBsdf.class,
      extensionClass = BsdfEvaluatorRgb.Vv.class)
    EvaluatorRgbVv(SimpleMicrofacetBsdf bsdf) {
      super(
        ImmutableList.of(
          bsdf.getExtension(DiffuseTextureEvaluatorRgbVv.class),
          bsdf.getExtension(RoughnessTextureEvaluatorVv.class),
          bsdf.extIor(),
          bsdf.intIor()),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> new Evaluator.ForRgb(
          bsdf.getExtension(DiffuseTextureEvaluatorRgbVv.class).value(),
          bsdf.getExtension(RoughnessTextureEvaluatorVv.class).value(),
          bsdf.extIor().value(),
          bsdf.intIor().value())
      );
    }
  }

  public abstract static class Sampler<T extends Spectrum, V extends SpectrumTransform<T>>
    implements BsdfSampler<T, V> {
    private final TextureTwoDimEvaluator<T> diffuseEvaluator;
    private final TextureTwoDimEvaluatorScalar alphaEvaluator;
    private final BsdfEvaluator<T, V> bsdfEvaluator;
    private final SpectrumSpace<T, V> ss;

    Sampler(
      TextureTwoDimEvaluator<T> diffuseEvaluator,
      TextureTwoDimEvaluatorScalar alphaEvaluator,
      BsdfEvaluator<T, V> bsdfEvaluator,
      SpectrumSpace<T, V> ss) {
      this.diffuseEvaluator = diffuseEvaluator;
      this.alphaEvaluator = alphaEvaluator;
      this.bsdfEvaluator = bsdfEvaluator;
      this.ss = ss;
    }

    @Override
    public BsdfSamplingOutput<T, V> sample(BsdfSamplingInput samplingRecord, RandomNumberGenerator rng) {
      BsdfSamplingOutput<T, V> result = new BsdfSamplingOutput<>(ss.createZeroTransform());
      result.measure = Measure.SolidAngle;
      result.eta = 1.0;
      result.sampledDirection = samplingRecord.sampledDirection;

      Vector2d sample = new Vector2d();
      rng.next2D(sample);

      if (Frame.cosTheta(samplingRecord.wIn) < 0) {
        result.wOut.set(0, 0, 0);
        return result;
      }

      T kd = diffuseEvaluator.eval(samplingRecord.uv);
      double kdMax = kd.spectrumMaxComponent();
      Preconditions.checkArgument(kdMax >= 0 && kdMax <= 1);
      double ks = 1 - kdMax;
      double pSpecular = ks / (ks + kdMax);
      double alpha = alphaEvaluator.eval(samplingRecord.uv).toDouble();

      Vector3d wOut = new Vector3d();
      if (sample.x < pSpecular) {
        sample.x /= pSpecular;
        SamplingUtil.squareToBeckmann(sample, alpha, wOut);
        wOut.scale(2 * samplingRecord.wIn.dot(wOut));
        wOut.sub(samplingRecord.wIn);
        wOut.normalize();
      } else {
        sample.x = (sample.x - pSpecular) / (1 - pSpecular);
        SamplingUtil.squareToCosineHemisphere(sample, wOut);
      }
      result.wOut.set(wOut);

      double pdfVal = pdf(samplingRecord, wOut, Measure.SolidAngle);
      if (pdfVal <= 0) {
        return result;
      }

      V value;
      if (samplingRecord.sampledDirection == SampledDirection.Wi) {
        value = bsdfEvaluator.eval(wOut, samplingRecord.wIn, samplingRecord.uv);
      } else {
        value = bsdfEvaluator.eval(samplingRecord.wIn, wOut, samplingRecord.uv);
      }
      result.value = ss.scale(Frame.cosTheta(wOut) / pdfVal, value);
      return result;
    }

    @Override
    public double pdf(BsdfSamplingInput bRec, Vector3d wOut, Measure measure) {
      if (measure != Measure.SolidAngle || Frame.cosTheta(bRec.wIn) < 0 || Frame.cosTheta(wOut) < 0) {
        return 0;
      }
      // Compute the half vector.
      Vector3d H = new Vector3d();
      H.add(bRec.wIn, wOut);
      H.normalize();

      double alpha = alphaEvaluator.eval(bRec.uv).toDouble();
      double ps = SamplingUtil.squareToBeckmannPdf(H, alpha) / (4 * wOut.dot(H));
      double pd;
      if (bRec.sampledDirection == SampledDirection.Wi) {
        pd = Frame.cosTheta(bRec.wIn) / Math.PI;
      } else {
        pd = Frame.cosTheta(wOut) / Math.PI;
      }

      T kd = diffuseEvaluator.eval(bRec.uv);
      double kdMax = kd.spectrumMaxComponent();
      Preconditions.checkArgument(kdMax >= 0 && kdMax <= 1);
      double ks = 1 - kdMax;
      return (ps * ks + pd * kdMax) / (ks + kdMax);
    }

    public static class ForRgb extends Sampler<Rgb, Rgb> implements BsdfSamplerRgb {
      public ForRgb(
        TextureTwoDimEvaluatorRgb diffuseEvaluator,
        TextureTwoDimEvaluatorScalar alphaEvaluator,
        BsdfEvaluatorRgb bsdfEvaluator) {
        super(
          diffuseEvaluator,
          alphaEvaluator,
          bsdfEvaluator,
          RgbSpace.I);
      }
    }
  }

  public static class SamplerRgbVv
    extends DerivedVersionedValue<BsdfSamplerRgb>
    implements BsdfSamplerRgb.Vv {
    @HanaDeclareExtension(
      extensibleClass = SimpleMicrofacetBsdf.class,
      extensionClass = BsdfSamplerRgb.Vv.class)
    SamplerRgbVv(SimpleMicrofacetBsdf bsdf) {
      super(
        ImmutableList.of(
          bsdf.getExtension(DiffuseTextureEvaluatorRgbVv.class),
          bsdf.getExtension(RoughnessTextureEvaluatorVv.class),
          bsdf.getExtension(BsdfEvaluatorRgb.Vv.class)),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> new Sampler.ForRgb(
          bsdf.getExtension(DiffuseTextureEvaluatorRgbVv.class).value(),
          bsdf.getExtension(RoughnessTextureEvaluatorVv.class).value(),
          bsdf.getExtension(BsdfEvaluatorRgb.Vv.class).value()));
    }
  }

  public static class DiffuseTextureEvaluatorRgbVv
    extends VvTransform<TextureTwoDim, TextureTwoDimEvaluatorRgb> {
    @HanaDeclareExtension(
      extensibleClass = SimpleMicrofacetBsdf.class,
      extensionClass = DiffuseTextureEvaluatorRgbVv.class)
    DiffuseTextureEvaluatorRgbVv(SimpleMicrofacetBsdf bsdf) {
      super(
        bsdf.getExtension(hana04.shakuyaku.bsdf.classes.simpmicrofacet.SimpleMicrofacetBsdfs.UnwrappedDiffuseTextureVv.class),
        texture -> texture.getExtension(TextureTwoDimEvaluatorRgb.Vv.class));
    }
  }

  public static class RoughnessTextureEvaluatorVv
    extends VvTransform<TextureTwoDim, TextureTwoDimEvaluatorScalar> {
    @HanaDeclareExtension(
      extensibleClass = SimpleMicrofacetBsdf.class,
      extensionClass = RoughnessTextureEvaluatorVv.class)
    RoughnessTextureEvaluatorVv(SimpleMicrofacetBsdf bsdf) {
      super(
        bsdf.getExtension(hana04.shakuyaku.bsdf.classes.simpmicrofacet.SimpleMicrofacetBsdfs.UnwrappedRoughnessTextureVv.class),
        texture -> texture.getExtension(TextureTwoDimEvaluatorScalar.Vv.class));
    }
  }
}
