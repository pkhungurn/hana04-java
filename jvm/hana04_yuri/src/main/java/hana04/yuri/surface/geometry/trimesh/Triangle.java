package hana04.yuri.surface.geometry.trimesh;

import hana04.gfxbase.gfxtype.Ray;
import hana04.gfxbase.gfxtype.VecMathDUtil;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;
import java.util.OptionalDouble;

/**
 * Utility functions on triangles
 */
public class Triangle {
  /**
   * Intersect the triangle defined by points p0, p1, and p0 with the given ray.
   *
   * @param p0   the first point
   * @param p1   the second point
   * @param p2   the third point
   * @param ray  the ray
   * @param bary the receiver of the bary centric coordinate of the hit point, if the ray hits
   * @return whether the ray intersects the triangle
   */
  public static OptionalDouble rayIntersect(Point3d p0, Point3d p1, Point3d p2, Ray ray, Tuple3d bary) {
    Vector3d edge1 = VecMathDUtil.sub(p1, p0);
    Vector3d edge2 = VecMathDUtil.sub(p2, p0);

    // Begin calculating the determinant - also used to calculate the y-component of the barycentric coordinate.
    Vector3d h = new Vector3d();
    h.cross(ray.d, edge2);
    double det = edge1.dot(h);

    // If the determinant is near zero, ray lies in the plane of the triangle.
    if (det > -1e-25 && det < 1e-25) {
      return OptionalDouble.empty();
    }
    double invDet = 1.0 / det;

    // Calculate the distance from p0 to ray origin.
    Vector3d s = new Vector3d();
    s.sub(ray.o, p0);

    // Calculate the y-component and test bounds.
    double u = s.dot(h) * invDet;
    if (u < 0 || u > 1) {
      return OptionalDouble.empty();
    }

    // Prepare to test the z-component.
    Vector3d q = new Vector3d();
    q.cross(s, edge1);

    // Calculate the z-component and test bounds.
    double v = ray.d.dot(q) * invDet;
    if (v < 0 || u + v > 1) {
      return OptionalDouble.empty();
    }

    // Compute the "time" of the intersection.
    double t = edge2.dot(q) * invDet;

    if (t < ray.mint || t > ray.maxt) {
      return OptionalDouble.empty();
    }

    bary.set(1 - u - v, u, v);
    return OptionalDouble.of(t);
  }
}
