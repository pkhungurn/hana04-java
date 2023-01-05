package hana04.yuri.bsdf.classes.passthrough;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.Constant;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.gfxtype.Measure;
import hana04.shakuyaku.bsdf.BsdfBasicProperties;
import hana04.shakuyaku.bsdf.classes.passthrough.PassThroughBsdf;
import hana04.yuri.bsdf.BsdfSamplingInput;
import hana04.yuri.bsdf.BsdfSamplingOutput;
import hana04.yuri.bsdf.TransmittanceEvaluator;
import hana04.yuri.bsdf.specspaces.BsdfEvaluatorRgb;
import hana04.yuri.bsdf.specspaces.BsdfSamplerRgb;
import hana04.yuri.bsdf.specspaces.BsdfSamplingOutputRgb;
import hana04.yuri.sampler.RandomNumberGenerator;

import javax.vecmath.Vector3d;

public class PassThroughBsdfs {
  public static class BasicPropertiesVv
    extends Constant<BsdfBasicProperties>
    implements BsdfBasicProperties.Vv {
    @HanaDeclareExtension(
      extensibleClass = PassThroughBsdf.class,
      extensionClass = BsdfBasicProperties.Vv.class)
    BasicPropertiesVv(PassThroughBsdf bsdf) {
      super(new hana04.shakuyaku.bsdf.classes.passthrough.PassThroughBsdfs.BasicProperties());
    }
  }

  public static class EvaluatorRgbVv
    extends Constant<BsdfEvaluatorRgb>
    implements BsdfEvaluatorRgb.Vv {
    @HanaDeclareExtension(
      extensibleClass = PassThroughBsdf.class,
      extensionClass = BsdfEvaluatorRgb.Vv.class)
    EvaluatorRgbVv(PassThroughBsdf bsdf) {
      super((wi, wo, uv) -> new Rgb(0, 0, 0));
    }
  }

  public static class SamplerRgb implements BsdfSamplerRgb {
    @Override
    public BsdfSamplingOutput<Rgb, Rgb> sample(BsdfSamplingInput samplingRecord, RandomNumberGenerator rng) {
      BsdfSamplingOutputRgb result = new BsdfSamplingOutputRgb();
      result.value = new Rgb(1, 1, 1);
      result.measure = Measure.Discrete;
      result.eta = 1.0;
      result.sampledDirection = samplingRecord.sampledDirection;
      result.wOut.set(samplingRecord.wIn);
      result.wOut.negate();
      result.isPassThrough = true;
      return result;
    }

    @Override
    public double pdf(BsdfSamplingInput samplingRecord, Vector3d wOut, Measure meature) {
      return 0;
    }
  }

  public static class SamplerRgbVv
    extends Constant<BsdfSamplerRgb>
    implements BsdfSamplerRgb.Vv {
    @HanaDeclareExtension(
      extensibleClass = PassThroughBsdf.class,
      extensionClass = BsdfSamplerRgb.Vv.class)
    SamplerRgbVv(PassThroughBsdf bsdf) {
      super(new SamplerRgb());
    }
  }

  public static class TransmisstanceEvaluatorVv
    extends Constant<TransmittanceEvaluator>
    implements TransmittanceEvaluator.Vv {
    @HanaDeclareExtension(
      extensibleClass = PassThroughBsdf.class,
      extensionClass = TransmittanceEvaluator.Vv.class)
    TransmisstanceEvaluatorVv(PassThroughBsdf bsdf) {
      super((shadowRayDir, uv) -> 1.0);
    }
  }
}
