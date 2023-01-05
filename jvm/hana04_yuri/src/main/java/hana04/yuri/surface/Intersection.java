package hana04.yuri.surface;

import hana04.gfxbase.gfxtype.Frame;

import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

public class Intersection {
  /**
   * Position of the surface intersection point.
   */
  public final Point3d p = new Point3d();
  /**
   * UV coordinates, if any.
   */
  public final Vector2d uv = new Vector2d();
  /**
   * Shading frame (based on the shading normal)
   */
  public final Frame shFrame = new Frame();
  /**
   * Shading frame (based on the shading normal)
   */
  public final Frame geoFrame = new Frame();
  /**
   * The patch interaction information.
   */
  public PatchIntersection patchIntersection;

  /**
   * Unoccluded distance along the ray.
   */
  public double t() {
    return patchIntersection.t;
  }

  /**
   * The index of the patch intersected.
   */
  public int patchIndex() {
    return patchIntersection.patchIndex;
  }

  /**
   * Transform a direction vector into the local shading frame
   *
   * @param v      the vector in world coordinates
   * @param output the receiver of the vector in local shading frame
   */
  public void toLocal(Vector3d v, Vector3d output) {
    shFrame.toLocal(v, output);
  }

  /**
   * Transform a direction vector from local to world coordinates
   *
   * @param v      the vector in the shading frame coordinates
   * @param output the receiver of the vector in world coordinates
   */
  public void toWorld(Vector3d v, Vector3d output) {
    shFrame.toWorld(v, output);
  }
}
