package hana04.yuri.bsdf.util;

import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.SpectrumSpace;
import hana04.gfxbase.spectrum.SpectrumTransform;
import hana04.gfxbase.gfxtype.Measure;
import hana04.yuri.bsdf.BsdfSamplingInput;
import hana04.yuri.bsdf.BsdfSamplingOutput;
import hana04.yuri.sampler.RandomNumberGenerator;

import javax.vecmath.Tuple2d;
import javax.vecmath.Vector3d;

public final class TwoSidedUtil {
  private TwoSidedUtil() {
    // NO-OP
  }

  public interface BsdfEvaluatorFunc<T extends Spectrum, V extends SpectrumTransform<T>> {
    V eval(Vector3d wi, Vector3d wo, Tuple2d uv);
  }

  public static <T extends Spectrum, V extends SpectrumTransform<T>>
  V eval(Vector3d wi, Vector3d wo, Tuple2d uv, SpectrumSpace<T, V> ss,
         BsdfEvaluatorFunc<T, V> frontSideEvaluator,
         BsdfEvaluatorFunc<T, V> backSideEvaluator) {
    if (wi.z * wo.z < 0) {
      return ss.createZeroTransform();
    }
    if (wi.z > 0) {
      return frontSideEvaluator.eval(wi, wo, uv);
    } else {
      return backSideEvaluator.eval(
        new Vector3d(wi.x, wi.y, -wi.z),
        new Vector3d(wo.x, wo.y, -wo.z),
        uv);
    }
  }

  public interface BsdfSamplerFunc<T extends Spectrum, V extends SpectrumTransform<T>> {
    BsdfSamplingOutput<T, V> sample(BsdfSamplingInput samplingRecord, RandomNumberGenerator rng);
  }

  public static <T extends Spectrum, V extends SpectrumTransform<T>>
  BsdfSamplingOutput<T, V> sample(BsdfSamplingInput samplingRecord, RandomNumberGenerator rng,
                                  BsdfSamplerFunc<T, V> frontSideSampler,
                                  BsdfSamplerFunc<T, V> backSideSampler) {
    if (samplingRecord.wIn.z > 0) {
      return frontSideSampler.sample(samplingRecord, rng);
    } else {
      BsdfSamplingOutput<T, V> output = backSideSampler.sample(mirror(samplingRecord), rng);
      output.wOut.z *= -1;
      return output;
    }
  }

  public interface BsdfPdfFunc {
    double pdf(BsdfSamplingInput samplingRecord, Vector3d wOut, Measure measure);
  }


  public static <T extends Spectrum, V extends SpectrumTransform<T>>
  double pdf(BsdfSamplingInput samplingRecord, Vector3d wOut, Measure measure,
             BsdfPdfFunc frontSideSampler, BsdfPdfFunc backSideSampler) {
    if (samplingRecord.wIn.z * wOut.z < 0) {
      return 0;
    }
    if (samplingRecord.wIn.z > 0) {
      return frontSideSampler.pdf(samplingRecord, wOut, measure);
    } else {
      return backSideSampler.pdf(mirror(samplingRecord), mirror(wOut), measure);
    }
  }

  private static BsdfSamplingInput mirror(BsdfSamplingInput input) {
    return new BsdfSamplingInput(
      new Vector3d(input.wIn.x, input.wIn.y, -input.wIn.z),
      input.uv,
      input.sampledDirection);
  }

  private static Vector3d mirror(Vector3d v) {
    return new Vector3d(v.x, v.y, -v.z);
  }
}
