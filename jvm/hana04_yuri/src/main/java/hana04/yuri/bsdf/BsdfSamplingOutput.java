package hana04.yuri.bsdf;

import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.SpectrumTransform;
import hana04.gfxbase.gfxtype.Measure;

import javax.vecmath.Vector3d;

public class BsdfSamplingOutput<T extends Spectrum, V extends SpectrumTransform<T>> {
  /**
   * The output direction.
   */
  public final Vector3d wOut = new Vector3d();

  /**
   * Sampled direction.
   */
  public SampledDirection sampledDirection = SampledDirection.Wi;

  /**
   * The measure associated with the sampled direction.
   */
  public Measure measure = Measure.UnknownMeasure;

  /**
   * The ratio between
   * (1) the index of refraction of the medium where the output direction points to
   * (2) the index of refraction of the medium where the input direction points to
   */
  public double eta = 1.0;

  /**
   * The BSDF value scaled by the product of
   * (1) the cosine of angle the output direction makes with the surface normal, and
   * (2) the inverse of the sampling probability.
   */
  public V value;

  /**
   * Whether the sampled direction is the result of copying the input direction by the PassThroughBsdf.
   */
  public boolean isPassThrough = false;

  public BsdfSamplingOutput(V value) {
    this.value = value;
  }
}
