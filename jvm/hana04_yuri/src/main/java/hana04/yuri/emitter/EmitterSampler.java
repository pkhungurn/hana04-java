package hana04.yuri.emitter;

import hana04.gfxbase.spectrum.Spectrum;
import hana04.shakuyaku.emitter.Emitter;
import hana04.yuri.sampler.RandomNumberGenerator;

import javax.vecmath.Point3d;

/**
 * This is meant to be an extension of {@link EmitterInScene}, not {@link Emitter}.
 */
public interface EmitterSampler<T extends Spectrum> {
  T eval(EmitterEvalInput record);

  double pdf(EmitterPdfInput record);

  EmitterSamplingOutput<T> sample(Point3d p, RandomNumberGenerator rng);
}
