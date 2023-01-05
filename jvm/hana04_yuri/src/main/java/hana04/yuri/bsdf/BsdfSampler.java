package hana04.yuri.bsdf;

import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.SpectrumTransform;
import hana04.gfxbase.gfxtype.Measure;
import hana04.yuri.sampler.RandomNumberGenerator;

import javax.vecmath.Vector3d;

public interface BsdfSampler<T extends Spectrum, V extends SpectrumTransform<T>> {
  BsdfSamplingOutput<T, V> sample(BsdfSamplingInput samplingRecord, RandomNumberGenerator rng);

  double pdf(BsdfSamplingInput samplingRecord, Vector3d wOut, Measure measure);

  class Proxy<T extends Spectrum, V extends SpectrumTransform<T>> implements BsdfSampler<T, V> {
    private BsdfSampler<T, V> inner;

    public Proxy(BsdfSampler<T, V> inner) {
      this.inner = inner;
    }

    @Override
    public BsdfSamplingOutput<T, V> sample(BsdfSamplingInput samplingRecord, RandomNumberGenerator rng) {
      return inner.sample(samplingRecord, rng);
    }

    @Override
    public double pdf(BsdfSamplingInput samplingRecord, Vector3d wOut, Measure measure) {
      return inner.pdf(samplingRecord, wOut, measure);
    }
  }
}
