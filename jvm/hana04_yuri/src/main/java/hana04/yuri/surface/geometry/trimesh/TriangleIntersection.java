package hana04.yuri.surface.geometry.trimesh;

import hana04.yuri.surface.PatchIntersection;

import javax.vecmath.Vector3d;

public class TriangleIntersection extends PatchIntersection {
  /**
   * The barycentric coordinate of the intersection point.
   * Let us say the triangle points are p0, p1, and p2.
   * Then, the intersection point is given by bary.x*p0 + bary.y*p1 + bary.z*p2.
   */
  public Vector3d bary = new Vector3d();
}
