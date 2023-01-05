package hana04.yuri.bsdf;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

/**
 * Convenience value class used to pass arguments to BSDF sampling routine.
 */
public class BsdfSamplingInput {
  /**
   * The wIn direction.
   */
  public final Vector3d wIn = new Vector3d();
  /**
   * The texture coordinate.
   */
  public final Vector2d uv = new Vector2d();
  /**
   * Sampled direction.
   */
  public SampledDirection sampledDirection = SampledDirection.Wi;

  public BsdfSamplingInput(Vector3d wIn, Vector2d uv, SampledDirection sampledDirection) {
    this.wIn.set(wIn);
    this.uv.set(uv);
    this.sampledDirection = sampledDirection;
  }
}
