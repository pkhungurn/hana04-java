package hana04.yuri.bsdf.classes.twosided;

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
import hana04.gfxbase.gfxtype.Measure;
import hana04.shakuyaku.bsdf.Bsdf;
import hana04.shakuyaku.bsdf.classes.twosided.TwoSidedBsdf;
import hana04.yuri.bsdf.BsdfEvaluator;
import hana04.yuri.bsdf.BsdfSampler;
import hana04.yuri.bsdf.BsdfSamplingInput;
import hana04.yuri.bsdf.BsdfSamplingOutput;
import hana04.yuri.bsdf.specspaces.BsdfEvaluatorRgb;
import hana04.yuri.bsdf.specspaces.BsdfSamplerRgb;
import hana04.yuri.sampler.RandomNumberGenerator;

import javax.vecmath.Tuple2d;
import javax.vecmath.Vector3d;

public class TwoSidedBsdfs {
  public static class FrontEvaluatorRgb extends VvTransform<Bsdf, BsdfEvaluatorRgb> {
    @HanaDeclareExtension(
        extensibleClass = TwoSidedBsdf.class,
        extensionClass = FrontEvaluatorRgb.class)
    FrontEvaluatorRgb(TwoSidedBsdf bsdf) {
      super(
          bsdf.getExtension(hana04.shakuyaku.bsdf.classes.twosided.TwoSidedBsdfs.UnwrappedFrontBsdf.class),
          bsdf_ -> bsdf_.getExtension(BsdfEvaluatorRgb.Vv.class));
    }
  }

  public static class BackEvaluatorRgb extends VvTransform<Bsdf, BsdfEvaluatorRgb> {
    @HanaDeclareExtension(
        extensibleClass = TwoSidedBsdf.class,
        extensionClass = BackEvaluatorRgb.class)
    BackEvaluatorRgb(TwoSidedBsdf bsdf) {
      super(
          bsdf.getExtension(hana04.shakuyaku.bsdf.classes.twosided.TwoSidedBsdfs.UnwrappedBackBsdf.class),
          bsdf_ -> bsdf_.getExtension(BsdfEvaluatorRgb.Vv.class));
    }
  }

  public abstract static class TwoSidedBsdfEvaluator<T extends Spectrum, V extends SpectrumTransform<T>>
      implements BsdfEvaluator<T, V> {
    private final SpectrumSpace<T, V> ss;
    private final BsdfEvaluator<T, V> frontSideEvaluator;
    private final BsdfEvaluator<T, V> backSideEvaluator;

    TwoSidedBsdfEvaluator(
        BsdfEvaluator<T, V> frontSideEvaluator,
        BsdfEvaluator<T, V> backSideEvaluator,
        SpectrumSpace<T, V> ss) {
      this.ss = ss;
      this.frontSideEvaluator = frontSideEvaluator;
      this.backSideEvaluator = backSideEvaluator;
    }

    @Override
    public V eval(Vector3d wi, Vector3d wo, Tuple2d uv) {
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
  }

  public static class EvaluatorRgb extends TwoSidedBsdfEvaluator<Rgb, Rgb> implements BsdfEvaluatorRgb {
    EvaluatorRgb(BsdfEvaluatorRgb frontSideEvaluator, BsdfEvaluatorRgb backSideEvaluator) {
      super(frontSideEvaluator, backSideEvaluator, RgbSpace.I);
    }
  }

  public static class EvaluatorRgbVv
      extends DerivedVersionedValue<BsdfEvaluatorRgb>
      implements BsdfEvaluatorRgb.Vv {
    @HanaDeclareExtension(
        extensibleClass = TwoSidedBsdf.class,
        extensionClass = BsdfEvaluatorRgb.Vv.class)
    EvaluatorRgbVv(TwoSidedBsdf bsdf) {
      super(
          ImmutableList.of(
              bsdf.getExtension(FrontEvaluatorRgb.class),
              bsdf.getExtension(BackEvaluatorRgb.class)),
          ChangePropUtil::largestBetweenIncSelfAndDeps,
          () -> new EvaluatorRgb(
              bsdf.getExtension(FrontEvaluatorRgb.class).value(),
              bsdf.getExtension(BackEvaluatorRgb.class).value()));
    }
  }

  public static class FrontSamplerRgb extends VvTransform<Bsdf, BsdfSamplerRgb> {
    @HanaDeclareExtension(
        extensibleClass = TwoSidedBsdf.class,
        extensionClass = FrontSamplerRgb.class)
    FrontSamplerRgb(TwoSidedBsdf bsdf) {
      super(
          bsdf.getExtension(hana04.shakuyaku.bsdf.classes.twosided.TwoSidedBsdfs.UnwrappedFrontBsdf.class),
          bsdf_ -> bsdf_.getExtension(BsdfSamplerRgb.Vv.class));
    }
  }

  public static class BackSamplerRgb extends VvTransform<Bsdf, BsdfSamplerRgb> {
    @HanaDeclareExtension(
        extensibleClass = TwoSidedBsdf.class,
        extensionClass = BackSamplerRgb.class)
    BackSamplerRgb(TwoSidedBsdf bsdf) {
      super(
          bsdf.getExtension(hana04.shakuyaku.bsdf.classes.twosided.TwoSidedBsdfs.UnwrappedBackBsdf.class),
          bsdf_ -> bsdf_.getExtension(BsdfSamplerRgb.Vv.class));
    }
  }

  public abstract static class TwoSidedBsdfSampler<T extends Spectrum, V extends SpectrumTransform<T>>
      implements BsdfSampler<T, V> {
    private final BsdfSampler<T, V> frontSideSampler;
    private final BsdfSampler<T, V> backSideSampler;


    TwoSidedBsdfSampler(BsdfSampler<T, V> frontSideSampler, BsdfSampler<T, V> backSideSampler) {
      this.frontSideSampler = frontSideSampler;
      this.backSideSampler = backSideSampler;
    }

    @Override
    public BsdfSamplingOutput<T, V> sample(BsdfSamplingInput samplingRecord, RandomNumberGenerator rng) {
      if (samplingRecord.wIn.z > 0) {
        return frontSideSampler.sample(samplingRecord, rng);
      } else {
        BsdfSamplingOutput<T, V> output = backSideSampler.sample(mirror(samplingRecord), rng);
        output.wOut.z *= -1;
        return output;
      }
    }

    @Override
    public double pdf(BsdfSamplingInput samplingRecord, Vector3d wOut, Measure measure) {
      if (samplingRecord.wIn.z * wOut.z < 0) {
        return 0;
      }
      if (samplingRecord.wIn.z > 0) {
        return frontSideSampler.pdf(samplingRecord, wOut, measure);
      } else {
        return backSideSampler.pdf(mirror(samplingRecord), mirror(wOut), measure);
      }
    }

    private BsdfSamplingInput mirror(BsdfSamplingInput input) {
      return new BsdfSamplingInput(
          new Vector3d(input.wIn.x, input.wIn.y, -input.wIn.z),
          input.uv,
          input.sampledDirection);
    }

    private Vector3d mirror(Vector3d v) {
      return new Vector3d(v.x, v.y, -v.z);
    }
  }

  public static class SamplerRgb extends TwoSidedBsdfSampler<Rgb, Rgb> implements BsdfSamplerRgb {
    SamplerRgb(BsdfSamplerRgb frontSideSampler, BsdfSamplerRgb backSideSampler) {
      super(frontSideSampler, backSideSampler);
    }
  }

  public static class SamplerRgbVv
      extends DerivedVersionedValue<BsdfSamplerRgb>
      implements BsdfSamplerRgb.Vv {
    @HanaDeclareExtension(
        extensibleClass = TwoSidedBsdf.class,
        extensionClass = BsdfSamplerRgb.Vv.class)
    SamplerRgbVv(TwoSidedBsdf bsdf) {
      super(
          ImmutableList.of(
              bsdf.getExtension(FrontSamplerRgb.class),
              bsdf.getExtension(BackSamplerRgb.class)),
          ChangePropUtil::largestBetweenIncSelfAndDeps,
          () -> new SamplerRgb(
              bsdf.getExtension(FrontSamplerRgb.class).value(),
              bsdf.getExtension(BackSamplerRgb.class).value()));
    }
  }
}
