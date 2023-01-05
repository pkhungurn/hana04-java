package hana04.yuri.surface;

import hana04.gfxbase.gfxtype.Measure;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class PatchIntervalPointSamplingOutput {
  /**
   * The measure associated with the sample.
   */
  public Measure measure = Measure.UnknownMeasure;
  /**
   * The sampled point.
   */
  public Point3d q = new Point3d();
  /**
   * The *geometric* normal vector at the sampled point.
   */
  public Vector3d nq = new Vector3d();
}
