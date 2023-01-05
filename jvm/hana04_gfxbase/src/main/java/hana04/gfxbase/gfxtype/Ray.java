package hana04.gfxbase.gfxtype;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

/**
 * Simple 3-dimensional ray segment data structure
 *
 * Along with the ray origin and direction, this data structure additionally
 * stores a ray segment [mint, maxt] (whose entries may include positive/negative
 * infinity), as well as the component-wise reciprocals of the ray direction.
 * That is just done for convenience, as these values are frequently required.
 *
 * Important: be careful when changing the ray direction. You must
 * call update() to compute the component-wise reciprocals as well, or Wakame's
 * ray-triangle intersection code will go haywire.
 */
public class Ray {
  public static final double EPSILON = 1e-6;

  /**
   * The origin.
   */
  public final Point3d o = new Point3d();
  /**
   * The direction.
   */
  public final Vector3d d = new Vector3d();
  /**
   * Minimum position on the ray segment.
   */
  public double mint;
  /**
   * Maximum position on the ray segment.
   */
  public double maxt;

  /**
   * Create a new ray.
   */
  public Ray() {
    this.o.set(0,0,0);
    this.d.set(0,0,1);
    this.mint = EPSILON;
    this.maxt = Double.POSITIVE_INFINITY;
  }

  /**
   * Create a new ray.
   * @param o
   * @param d
   */
  public Ray(Point3d o, Vector3d d) {
    this(o, d, EPSILON, Double.POSITIVE_INFINITY);
  }

  /**
   * Construct a new ray.
   * @param o
   * @param d
   * @param mint
   * @param maxt
   */
  public Ray(Point3d o, Vector3d d, double mint, double maxt) {
    this.o.set(o);
    this.d.set(d);
    this.mint = mint;
    this.maxt = maxt;
  }

  /**
   * Copy constructor
   * @param other
   */
  public Ray(Ray other) {
    copy(other);
  }

  /**
   * Copy all the states from the given ray.
   * @param other another ray
   */
  public void copy(Ray other) {
    this.o.set(other.o);
    this.d.set(other.d);
    this.mint = other.mint;
    this.maxt = other.maxt;
  }

  /**
   * Transform the ray with the given matrix.
   */
  public void transform(Matrix4d M) {
    M.transform(this.o);
    M.transform(this.d);
  }

  /**
   * Set this to the transformation of the given ray with the given matrix.
   * @param M the matrix
   * @param other another ray
   */
  public void transform(Matrix4d M, Ray other) {
    this.copy(other);
    this.transform(M);
  }

  /**
   * Get the position of a point along the ray at "time" t.
   * @param t the time
   * @param p the receiver of the postion.
   */
  public void project(double t, Tuple3d p) {
    p.scaleAdd(t, d, o);
  }

  /**
   * Set the output ray to the ray that goes in the opposition direction of the current instant.
   * @param output the output ray
   */
  public void reverse(Ray output) {
    output.o.set(this.o);
    output.d.negate(this.d);
    output.mint = mint;
    output.maxt = maxt;
  }

  public String toString() {
    return String.format(
      "Ray[\n" +
        "  o = %s,\n" +
        "  d = %s,\n" +
        "  mint = %f,\n" +
        "  maxt = %f\n" +
        "]",
      o.toString(), d.toString(), mint, maxt);
  }
}
