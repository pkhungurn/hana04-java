package hana04.yuri.sampler;

import hana04.base.changeprop.VersionedValue;

import javax.vecmath.Tuple2d;

/**
 * A per-pixel sampler is responsible for generating the random number stream
 * that will be passed an SensorIntegrand implementation as it computes the
 * value incident along a specified ray.
 * <p>
 * The most simple conceivable generator is just a wrapper around the
 * Mersenne-Twister random number generator and is implemented in
 * the PerPixelSampler_ of the IndependentSampler class (it is named this way
 * because it generates statistically independent random numbers).
 * <p>
 * Fancier samplers might use stratification or low-discrepancy sequences
 * (e.g. Halton, Hammersley, or Sobol point sets) for improved convergence.
 * Another use of this class is in producing intentionally correlated
 * random numbers, e.g. as part of a Metropolis-Hastings integration scheme.
 * <p>
 * The general interface between a sampler and a rendering algorithm is as
 * follows: Before beginning to render a pixel, the rendering algorithm calls
 * generate(). The first pixel sample can now be computed, after which
 * advance() needs to be invoked. This repeats until all pixel samples have
 * been exhausted.  While computing a pixel sample, the rendering
 * algorithm requests (pseudo-) random numbers using the next1D() and
 * next2D() functions.
 * <p>
 * Conceptually, the right way of thinking of this goes as follows:
 * For each sample in a pixel, a sample generator produces a (hypothetical)
 * point in an infinite dimensional random number hypercube. A rendering
 * algorithm can then request subsequent 1D or 2D components of this point
 * using the next1D() and next2D() functions. Fancy implementations
 * of this class make certain guarantees about the stratification of the
 * first n components with respect to the other points that are sampled
 * within a pixel.
 */
public interface PerPixelSampler extends RandomNumberGenerator {
  /**
   * Prepare to generate new batch of samples for a pixel. This function is called initially and every time the
   * integrand starts rendering a new pixel.
   */
  void generate();

  /**
   * Advance to the next sample in the batch.
   */
  void advance();

  /**
   * Retrieve the next component value from the current sample.
   */
  double next1D();

  /**
   * Retrieve the next two component values from the current sample.
   */
  void next2D(Tuple2d output);

  /**
   * Return the number of samples in the batch.
   */
  int getSampleCount();

  /**
   * Set the random seed of the sampler.
   */
  void setSeed(byte[] seed);

  /**
   * Create a copy the per-pixel sampler.
   */
  PerPixelSampler duplicate();

  interface Vv extends VersionedValue<PerPixelSampler> {
    // No-OP
  }
}
