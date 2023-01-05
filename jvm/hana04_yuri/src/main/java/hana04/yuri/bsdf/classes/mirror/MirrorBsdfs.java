package hana04.yuri.bsdf.classes.mirror;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.Constant;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.SpectrumSpace;
import hana04.gfxbase.spectrum.SpectrumTransform;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.spectrum.rgb.RgbSpace;
import hana04.gfxbase.gfxtype.Frame;
import hana04.gfxbase.gfxtype.Measure;
import hana04.shakuyaku.bsdf.classes.mirror.MirrorBsdf;
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

public class MirrorBsdfs {
  public abstract static class Evaluator<T extends Spectrum, V extends SpectrumTransform<T>>
    implements BsdfEvaluator<T, V> {
    private final SpectrumSpace<T, V> ss;

    Evaluator(SpectrumSpace<T, V> ss) {
      this.ss = ss;
    }

    @Override
    public V eval(Vector3d wi, Vector3d wo, Tuple2d uv) {
      return ss.createZeroTransform();
    }
  }

  public static class EvaluatorRgb extends Evaluator<Rgb, Rgb> implements BsdfEvaluatorRgb {
    public EvaluatorRgb() {
      super(RgbSpace.I);
    }
  }

  public static class EvaluatorRgbVv extends Constant<BsdfEvaluatorRgb> implements EvaluatorRgb.Vv {
    @HanaDeclareExtension(
      extensibleClass = MirrorBsdf.class,
      extensionClass = EvaluatorRgb.Vv.class)
    EvaluatorRgbVv(MirrorBsdf mirrorBsdf) {
      super(new EvaluatorRgb());
    }
  }

  public abstract static class Sampler<T extends Spectrum, V extends SpectrumTransform<T>>
    implements BsdfSampler<T, V> {
    private final SpectrumSpace<T, V> ss;

    Sampler(SpectrumSpace<T, V> ss) {
      this.ss = ss;
    }

    @Override
    public BsdfSamplingOutput<T, V> sample(BsdfSamplingInput samplingRecord, RandomNumberGenerator rng) {
      double cosThetaI = Frame.cosTheta(samplingRecord.wIn);
      BsdfSamplingOutput<T, V> samplingOutput = createSamplingOutput();
      samplingOutput.measure = Measure.Discrete;
      samplingOutput.isPassThrough = false;
      samplingOutput.sampledDirection = samplingRecord.sampledDirection;
      samplingOutput.eta = 1.0;
      if (cosThetaI < 0) {
        samplingOutput.wOut.set(0, 0, 0);
        samplingOutput.value = ss.createTransformFromScalar(0);
      } else {
        // Reflection in local coordinates
        samplingOutput.wOut.set(-samplingRecord.wIn.x, -samplingRecord.wIn.y, samplingRecord.wIn.z);
        // Relative index of refraction: no change
        samplingOutput.eta = 1;
        samplingOutput.value = ss.createIdentityTransform();
      }
      return samplingOutput;
    }

    abstract BsdfSamplingOutput<T, V> createSamplingOutput();

    @Override
    public double pdf(BsdfSamplingInput samplingRecord, Vector3d wOut, Measure meature) {
      return 0;
    }
  }

  public static class SamplerRgb extends Sampler<Rgb, Rgb> implements BsdfSamplerRgb {

    public SamplerRgb() {
      super(RgbSpace.I);
    }

    @Override
    BsdfSamplingOutput<Rgb, Rgb> createSamplingOutput() {
      return new BsdfSamplingOutputRgb();
    }
  }

  public static class SamplerRgbVv extends Constant<BsdfSamplerRgb> implements BsdfSamplerRgb.Vv {
    @HanaDeclareExtension(
      extensibleClass = MirrorBsdf.class,
      extensionClass = BsdfSamplerRgb.Vv.class)
    SamplerRgbVv(MirrorBsdf mirrorBsdf) {
      super(new SamplerRgb());
    }
  }
}
