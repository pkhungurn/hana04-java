package hana04.yuri.surface;

/**
 * Information about the point a ray intersects with a patch.
 */
public class PatchIntersection {
  /**
   * The "time" of the intersection (i.e., the position along the ray of the intersection point,
   * measured in the unit of the length of the direction vector of the ray).
   */
  public double t;
  /**
   * The index of the part being intersected.
   */
  public int patchIndex;
}
