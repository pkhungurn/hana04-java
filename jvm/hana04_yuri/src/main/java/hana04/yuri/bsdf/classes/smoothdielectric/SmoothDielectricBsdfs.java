package hana04.yuri.bsdf.classes.smoothdielectric;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.Constant;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.SpectrumSpace;
import hana04.gfxbase.spectrum.SpectrumTransform;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.spectrum.rgb.RgbSpace;
import hana04.gfxbase.gfxtype.Frame;
import hana04.gfxbase.gfxtype.Measure;
import hana04.shakuyaku.bsdf.classes.smoothdielectric.SmoothDielectricBsdf;
import hana04.yuri.bsdf.BsdfEvaluator;
import hana04.yuri.bsdf.BsdfSampler;
import hana04.yuri.bsdf.BsdfSamplingInput;
import hana04.yuri.bsdf.BsdfSamplingOutput;
import hana04.yuri.bsdf.specspaces.BsdfEvaluatorRgb;
import hana04.yuri.bsdf.specspaces.BsdfSamplerRgb;
import hana04.yuri.bsdf.specspaces.BsdfSamplingOutputRgb;
import hana04.yuri.sampler.RandomNumberGenerator;

import javax.vecmath.Tuple2d;
import javax.vecmath.Vector3d;

public class SmoothDielectricBsdfs {
  public static class Evaluator<T extends Spectrum, V extends SpectrumTransform<T>>
    implements BsdfEvaluator<T, V> {
    private final SpectrumSpace<T, V> ss;

    Evaluator(SpectrumSpace<T, V> ss) {
      this.ss = ss;
    }

    @Override
    public V eval(Vector3d wi, Vector3d wo, Tuple2d uv) {
      return ss.createZeroTransform();
    }

    public static class ForRgb extends Evaluator<Rgb, Rgb> implements BsdfEvaluatorRgb {
      public ForRgb() {
        super(RgbSpace.I);
      }
    }
  }

  public static class EvaluatorRgbVv
    extends Constant<BsdfEvaluatorRgb>
    implements BsdfEvaluatorRgb.Vv {
    @HanaDeclareExtension(
      extensibleClass = SmoothDielectricBsdf.class,
      extensionClass = BsdfEvaluatorRgb.Vv.class)
    EvaluatorRgbVv(SmoothDielectricBsdf bsdf) {
      super(new Evaluator.ForRgb());
    }
  }

  public abstract static class Sampler<T extends Spectrum, V extends SpectrumTransform<T>>
    implements BsdfSampler<T, V> {
    private final SpectrumSpace<T, V> ss;
    private final double extIor;
    private final double intIor;

    Sampler(double extIor, double intIor, SpectrumSpace<T, V> ss) {
      this.intIor = intIor;
      this.extIor = extIor;
      this.ss = ss;
    }

    @Override
    public BsdfSamplingOutput<T, V> sample(BsdfSamplingInput samplingRecord, RandomNumberGenerator rng) {
      double cosThetaI = Frame.cosTheta(samplingRecord.wIn);
      double etaI, etaT;
      // Check whether the ray enters or leaving the surface.
      boolean entering = Frame.cosTheta(samplingRecord.wIn) > 0;
      if (entering) {
        etaI = extIor;
        etaT = intIor;
      } else {
        etaI = intIor;
        etaT = extIor;
      }

      // Using Snell's law, calculate the squared sine of the angle between the normal and the transmitted ray.
      double invEta = etaI / etaT;
      double invEta2 = invEta * invEta;
      double sinThetaT2 = invEta2 * Frame.sinTheta2(samplingRecord.wIn);

      double Fr;
      double cosThetaT = 0;
      if (sinThetaT2 > 1.0) {
        // Total internal reflection.
        Fr = 1;
      } else {
        cosThetaT = Math.sqrt(1 - sinThetaT2);
        cosThetaI = Math.abs(cosThetaI);

        double Rs = (etaI * cosThetaI - etaT * cosThetaT)
          / (etaI * cosThetaI + etaT * cosThetaT);
        double Rp = (etaT * cosThetaI - etaI * cosThetaT)
          / (etaT * cosThetaI + etaI * cosThetaT);

        Fr = (Rs * Rs + Rp * Rp) / 2.0f;

        if (entering) {
          cosThetaT = -cosThetaT;
        }
      }

      BsdfSamplingOutput<T, V> samplingOutput = createSamplingOutput();
      samplingOutput.sampledDirection = samplingRecord.sampledDirection;
      samplingOutput.measure = Measure.Discrete;
      if (rng.next1D() <= Fr) {
        // Reflection in local coordinates
        samplingOutput.wOut.set(-samplingRecord.wIn.x, -samplingRecord.wIn.y, samplingRecord.wIn.z);
        // Relative index of refraction: no change
        samplingOutput.eta = 1;
        samplingOutput.value = ss.createIdentityTransform();
      } else {
        // Given cos(theta_t), compute the transmitted direction
        samplingOutput.wOut.set(-invEta * samplingRecord.wIn.x, -invEta * samplingRecord.wIn.y, cosThetaT);
        samplingOutput.wOut.normalize();
        // Also return the relative refractive index change
        samplingOutput.eta = 1.0f / invEta;
        // Account for the solid angle change at boundaries with different indices of refraction.
        samplingOutput.value = ss.createTransformFromScalar(invEta2);
      }
      return samplingOutput;
    }

    abstract BsdfSamplingOutput<T, V> createSamplingOutput();

    @Override
    public double pdf(BsdfSamplingInput samplingRecord, Vector3d wOut, Measure meature) {
      return 0;
    }

    public static class ForRgb extends Sampler<Rgb, Rgb> implements BsdfSamplerRgb {
      public ForRgb(double extIor, double intIor) {
        super(extIor, intIor, new RgbSpace());
      }

      @Override
      BsdfSamplingOutput<Rgb, Rgb> createSamplingOutput() {
        return new BsdfSamplingOutputRgb();
      }
    }
  }

  public static class SamplerRgbVv
    extends DerivedVersionedValue<BsdfSamplerRgb>
    implements BsdfSamplerRgb.Vv {
    @HanaDeclareExtension(
      extensibleClass = SmoothDielectricBsdf.class,
      extensionClass = BsdfSamplerRgb.Vv.class)
    SamplerRgbVv(SmoothDielectricBsdf bsdf) {
      super(
        ImmutableList.of(bsdf.extIor(), bsdf.intIor()),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> new Sampler.ForRgb(bsdf.extIor().value(), bsdf.intIor().value()));
    }
  }
}
