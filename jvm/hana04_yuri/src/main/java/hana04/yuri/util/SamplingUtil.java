package hana04.yuri.util;

import hana04.gfxbase.random.MersenneTwister;
import hana04.yuri.sampler.RandomNumberGenerator;

import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

/**
 * A collection of useful warping functions for importance sampling
 */
public class SamplingUtil {
  /**
   * Uniformly sample a vector on the unit hemisphere with respect to solid angles (naive implementation)
   *
   * @param sampler   the sampler
   * @param northPole the position of the north pole of the hemisphere
   * @param output    the receiver of the sampled unit vector
   */
  public static void sampleUniformHemisphere(RandomNumberGenerator sampler, Vector3d northPole, Tuple3d output) {
    // Naive implementation using rejection sampling
    Vector3d v = new Vector3d();
    do {
      v.x = 1 - 2 * sampler.next1D();
      v.y = 1 - 2 * sampler.next1D();
      v.z = 1 - 2 * sampler.next1D();
    } while (v.lengthSquared() > 1);

    if (v.dot(northPole) < 0)
      v.negate();
    v.normalize();
    output.set(v);
  }

  /**
   * Dummy warping function: takes uniformly distributed points in a square and just returns them
   *
   * @param sample a 2D point in [0,1]^2
   * @param output the same point as the given sample
   */
  public static void squareToUniformSquare(Tuple2d sample, Tuple2d output) {
    output.set(sample);
  }

  /**
   * Probability density of squareToUniformSquare()
   *
   * @param p the sampled point
   * @return the probability density of squareToUniformSquare() to sample p
   */
  public static double squareToUniformSquarePdf(Tuple2d p) {
    if (p.x < 0 || p.y < 0 || p.x > 1 || p.y > 1)
      return 0;
    else
      return 1;
  }

  /**
   * Uniformly sample a vector on a 2D disk with radius 1, centered around the origin
   *
   * @param sample a 2D point in [0,1]^2
   * @param output the receiver of the sampled vector
   */
  public static void squareToUniformDisk(Tuple2d sample, Tuple2d output) {
    double r = Math.sqrt(sample.x);
    double theta = 2 * Math.PI * sample.y;
    double cosTheta = Math.cos(theta);
    double sinTheta = Math.sin(theta);
    output.x = cosTheta * r;
    output.y = sinTheta * r;
  }

  /**
   * Probability density of squareToUniformDisk()
   *
   * @param p the sampled point
   * @return the probability density of squareToUniformDisk() to sample p
   */
  public static double squareToUniformDiskPdf(Tuple2d p) {
    double l2 = p.x * p.x + p.y * p.y;
    if (l2 > 1 + 1e-6) {
      return 0;
    } else {
      return 1.0 / Math.PI;
    }
  }

  /**
   * Uniformly sample a vector on the unit sphere with respect to solid angles
   *
   * @param sample a point uniformly sampled from [0,1]^2
   * @param output the receiver of the sampled vector
   */
  public static void squareToUniformSphere(Tuple2d sample, Tuple3d output) {
    double cosTheta = 1 - 2 * sample.x;
    double sinTheta = Math.sqrt(1 - cosTheta * cosTheta);
    double phi = 2 * Math.PI * sample.y;
    output.x = Math.cos(phi) * sinTheta;
    output.y = Math.sin(phi) * sinTheta;
    output.z = cosTheta;
  }

  /**
   * Probability density of squareToUniformSphere()
   *
   * @param v the sampled vector
   * @return the probability density of squareToUniformSphere() to sample v
   */
  public static double squareToUniformSpherePdf(Tuple3d v) {
    double l2 = v.x * v.x + v.y * v.y + v.z * v.z;
    if (Math.abs(l2 - 1) > 1e-6)
      return 0;
    else
      return 0.25 / Math.PI;
  }

  /**
   * Uniformly sample a vector on a spherical cap around (0, 0, 1)
   * <p>
   * A spherical cap is the subset of a unit sphere whose normals
   * make an angle of less than 'theta' with the north pole. This function
   * expects the cosine of 'theta' as a parameter.
   *
   * @param sample      a point uniformly sampled from [0,1]^2
   * @param cosThetaMax the maximum value of the cosine of the longitudinal angle between the sample vector and the
   *                    vector
   *                    (0, 0, 1)
   * @param output      the receiver of the sampled vector
   */
  public static void squareToUniformSphereCap(Tuple2d sample, double cosThetaMax, Tuple3d output) {
    double cosTheta = 1 - sample.x * (1 - cosThetaMax);
    double sinTheta = Math.sqrt(1 - cosTheta * cosTheta);
    double phi = 2 * Math.PI * sample.y;
    output.x = Math.cos(phi) * sinTheta;
    output.y = Math.sin(phi) * sinTheta;
    output.z = cosTheta;
  }

  /**
   * Probability density of squareToUniformSphereCap()
   *
   * @param v           the sampled vector
   * @param cosThetaMax the cosine value of the maximum longitudinal angle between the sample vector
   *                    and the vector (0, 0, 1)
   * @return the probability density of squareToUniformSphereCap() to sample v
   */
  public static double squareToUniformSphereCapPdf(Tuple3d v, double cosThetaMax) {
    double l2 = v.x * v.x + v.y * v.y + v.z * v.z;
    if (Math.abs(l2 - 1) > 1e-6)
      return 0;
    else {
      if (v.z < cosThetaMax) {
        return 0;
      } else {
        return 1.0 / (2 * Math.PI * (1 - cosThetaMax));
      }
    }
  }

  /**
   * Uniformly sample a vector on the unit hemisphere around the pole (0,0,1) with respect to solid angles
   * (fast implementation)
   *
   * @param sample a point uniformly sampled from [0,1]^2
   * @param output the receiver of the sampled vector
   */
  public static void squareToUniformHemisphere(Tuple2d sample, Tuple3d output) {
    squareToUniformSphereCap(sample, 0, output);
  }

  /**
   * Probability density of squareToUniformHemisphere()
   *
   * @param v the sampled vector
   * @return the probability density of squareToUniformSphere() to sample v
   */
  public static double squareToUniformHemispherePdf(Tuple3d v) {
    double l2 = v.x * v.x + v.y * v.y + v.z * v.z;
    if (Math.abs(l2 - 1) > 1e-6 || v.z < 0)
      return 0;
    else {
      return 1 / (2 * Math.PI);
    }
  }

  /**
   * Uniformly sample a vector on the unit hemisphere around the pole (0,0,1) with respect to projected solid angles
   *
   * @param sample a point uniformly sampled from in [0,1]^2
   * @param output the receiver of the sampled vector
   */
  public static void squareToCosineHemisphere(Tuple2d sample, Tuple3d output) {
    double cosTheta = Math.sqrt(1 - sample.x);
    double sinTheta = Math.sqrt(sample.x);
    double phi = 2 * Math.PI * sample.y;
    output.x = Math.cos(phi) * sinTheta;
    output.y = Math.sin(phi) * sinTheta;
    output.z = cosTheta;
  }

  /**
   * Probability density of squareToCosineHemisphere()
   *
   * @param v the sampled vector
   * @return the probability density of squareToCosineHemisphere() to sample v
   */
  public static double squareToCosineHemispherePdf(Tuple3d v) {
    double l2 = v.x * v.x + v.y * v.y + v.z * v.z;
    if (Math.abs(l2 - 1) > 1e-6 || v.z < 0)
      return 0;
    else {
      return v.z / Math.PI;
    }
  }

  /**
   * Warp a uniformly distributed square sample to a Beckmann distribution * cosine for the given 'alphaScale' parameter
   *
   * @param sample a point uniformly sampled from [0,1]^2
   * @param alpha  the alphaScale parameter of the Beckmann distribution
   * @param output the receiver of the sampled vector
   */
  public static void squareToBeckmann(Tuple2d sample, double alpha, Tuple3d output) {
    double tanTheta2 = -alpha * alpha * Math.log(1 - sample.x);
    double phi = 2 * Math.PI * sample.y;
    double cosTheta = 1 / Math.sqrt(1 + tanTheta2);
    double sinTheta = Math.sqrt(1 - cosTheta * cosTheta);
    output.x = Math.cos(phi) * sinTheta;
    output.y = Math.sin(phi) * sinTheta;
    output.z = cosTheta;
  }

  /**
   * Probability density of squareToBeckmann()
   *
   * @param v     the sampled vector
   * @param alpha the alphaScale parameter of the Beckmann distribution
   * @return the probability density of squareToBeckmann() to sample m
   */
  public static double squareToBeckmannPdf(Tuple3d v, double alpha) {
    double l2 = v.x * v.x + v.y * v.y + v.z * v.z;
    if (Math.abs(l2 - 1) > 1e-6 || v.z < 1e-9)
      return 0;
    else {
      double cosTheta = v.z;
      double cosTheta2 = cosTheta * cosTheta;
      double sinTheta2 = 1 - cosTheta2;
      double tanTheta2 = sinTheta2 / cosTheta2;
      double pdf = v.z * Math.exp(-tanTheta2 / alpha / alpha) / (Math.PI * alpha * alpha * cosTheta2 * cosTheta2);
      return pdf;
    }
  }

  /**
   * Warp a uniformly distributed square sample to a 2D Gaussian distribution with mean (0,0) and
   * unit variance.  In other words, perform the Box-Muller transform.
   *
   * @param sample a point uniformly sampled from [0,1]^2
   * @param output the receiver of the warped sample
   */
  public static void squareToUnitVarianceGaussian2D(Tuple2d sample, Tuple2d output) {
    double r = Math.sqrt(-2 * Math.log(1 - sample.x));
    double theta = 2 * Math.PI * sample.y;
    output.x = r * Math.cos(theta);
    output.y = r * Math.sin(theta);
  }

  static class MersenneTwisterRngAdaptor implements RandomNumberGenerator {
    private final MersenneTwister mersenneTwister;

    MersenneTwisterRngAdaptor(MersenneTwister mersenneTwister) {
      this.mersenneTwister = mersenneTwister;
    }

    @Override
    public double next1D() {
      return mersenneTwister.nextDouble();
    }

    @Override
    public void next2D(Tuple2d output) {
      output.x = mersenneTwister.nextDouble();
      output.y = mersenneTwister.nextDouble();
    }
  }

  public static RandomNumberGenerator mersenneTwisterToRgb(MersenneTwister mersenneTwister) {
    return new MersenneTwisterRngAdaptor(mersenneTwister);
  }
}
