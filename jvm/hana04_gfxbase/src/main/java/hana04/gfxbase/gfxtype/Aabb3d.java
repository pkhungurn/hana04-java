package hana04.gfxbase.gfxtype;

import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

public class Aabb3d {
  public final Vector3d pMin = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
  public final Vector3d pMax = new Vector3d(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);

  public Aabb3d() {
    // NO-OP
  }

  public Aabb3d(Tuple3d pMin, Tuple3d pMax) {
    this.expandBy(pMin);
    this.expandBy(pMax);
  }

  public void reset() {
    pMin.set(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
    pMax.set(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
  }

  public boolean isValid() {
    return pMin.x <= pMax.x && pMin.y <= pMax.y && pMin.z <= pMax.z;
  }

  public void expandBy(Tuple3d p) {
    pMin.x = Math.min(pMin.x, p.x);
    pMin.y = Math.min(pMin.y, p.y);
    pMin.z = Math.min(pMin.z, p.z);
    pMax.x = Math.max(pMax.x, p.x);
    pMax.y = Math.max(pMax.y, p.y);
    pMax.z = Math.max(pMax.z, p.z);
  }

  public void expandBy(double t) {
    pMin.x -= t;
    pMin.y -= t;
    pMin.z -= t;
    pMax.x += t;
    pMax.y += t;
    pMax.z += t;
  }

  public void expandBy(Aabb3d other) {
    pMin.x = Math.min(pMin.x, other.pMin.x);
    pMin.y = Math.min(pMin.y, other.pMin.y);
    pMin.z = Math.min(pMin.z, other.pMin.z);
    pMax.x = Math.max(pMax.x, other.pMax.x);
    pMax.y = Math.max(pMax.y, other.pMax.y);
    pMax.z = Math.max(pMax.z, other.pMax.z);
  }

  public boolean overlap(Aabb3d other) {
    if (pMin.x > other.pMax.x || pMax.x < other.pMin.x) {
      return false;
    } else if (pMin.y > other.pMax.y || pMax.y < other.pMin.y) {
      return false;
    } else if (pMin.z > other.pMax.z || pMax.z < other.pMin.z) {
      return false;
    } else {
      return true;
    }
  }

  public boolean overlap(Tuple3d p) {
    if (p.x < pMin.x || p.x > pMax.x) {
      return false;
    } else if (p.y < pMin.y || p.y > pMax.y) {
      return false;
    } else if (p.z < pMin.z || p.z > pMax.z) {
      return false;
    } else {
      return true;
    }
  }

  public boolean overlap(double x, double y, double z) {
    if (x < pMin.x || x > pMax.x) {
      return false;
    } else if (y < pMin.y || y > pMax.y) {
      return false;
    } else if (z < pMin.z || z > pMax.z) {
      return false;
    } else {
      return true;
    }
  }

  public void set(Aabb3d other) {
    pMin.set(other.pMin);
    pMax.set(other.pMax);
  }

  @Override
  public String toString() {
    return "Aabb[pMin = " + pMin.toString() + ", pMax = " + pMax.toString() + "]";
  }

  public Double getMaxExtent() {
    return (Double) Math.max(pMax.x - pMin.x, Math.max(pMax.y - pMin.y, pMin.z - pMin.z));
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Aabb3d) {
      Aabb3d other = (Aabb3d) obj;
      return other.pMin.equals(this.pMin) && other.pMax.equals(this.pMax);
    } else {
      return false;
    }
  }

  public void getCornerPoint(int cornerId, Tuple3d out) {
    out.x = ((cornerId & 1) == 0) ? pMin.x : pMax.x;
    out.y = ((cornerId & 2) == 0) ? pMin.y : pMax.y;
    out.z = ((cornerId & 4) == 0) ? pMin.z : pMax.z;
  }

  public double getExtent(int dim) {
    switch (dim) {
      case 0:
        return pMax.x - pMin.x;
      case 1:
        return pMax.y - pMin.y;
      case 2:
        return pMax.z - pMin.z;
      default:
        throw new RuntimeException("dim must be 0, 1, or 2");
    }
  }

  public double getSurfaceArea() {
    double xx = pMax.x - pMin.x;
    double yy = pMax.y - pMin.y;
    double zz = pMax.z - pMin.z;
    return 2 * (xx * yy + yy * zz + zz * xx);
  }

  public double getVolume() {
    double xx = pMax.x - pMin.x;
    double yy = pMax.y - pMin.y;
    double zz = pMax.z - pMin.z;
    return xx * yy * zz;
  }

  public int getLargestAxis() {
    double xx = pMax.x - pMin.x;
    double yy = pMax.y - pMin.y;
    double zz = pMax.z - pMin.z;

    if (xx >= yy && xx >= zz)
      return 0;
    else if (yy >= xx && yy >= zz)
      return 1;
    else
      return 2;
  }

  /**
   * Check if the ray intersect the bounding box.
   *
   * @param ray the ray
   * @return whether the ray intersect the bounding box
   */
  public boolean rayIntersect(Ray ray) {
    double nearT = Double.NEGATIVE_INFINITY;
    double farT = Double.POSITIVE_INFINITY;

    for (int i = 0; i < 3; i++) {
      double origin, minVal, maxVal, d;

      if (i == 0) {
        origin = ray.o.x;
        minVal = pMin.x;
        maxVal = pMax.x;
        d = ray.d.x;
      } else if (i == 1) {
        origin = ray.o.y;
        minVal = pMin.y;
        maxVal = pMax.y;
        d = ray.d.y;
      } else {
        origin = ray.o.z;
        minVal = pMin.z;
        maxVal = pMax.z;
        d = ray.d.z;
      }

      if (d == 0) {
        if (origin < minVal || origin > maxVal)
          return false;
      } else {
        double t1, t2;
        double recip = TupleUtil.getComponent(ray.d, i);
        if (recip > 0) {
          t1 = (minVal - origin) * recip;
          t2 = (maxVal - origin) * recip;
        } else {
          t2 = (minVal - origin) * recip;
          t1 = (maxVal - origin) * recip;
        }

        if (nearT < t1) nearT = t1;
        if (farT > t2) farT = t2;

        if (nearT > farT)
          return false;
      }
    }

    return ray.mint <= farT && nearT <= ray.maxt;
  }

  /**
   * Check if the ray intersect the bounding box.
   * This version is faster than the one without dRcp.
   *
   * @param ray  the ray
   * @param dRcp the component-wise reciprocal of the ray direction
   * @return whether the ray intersect the bounding box
   */
  public boolean rayIntersectFast(Ray ray, double[] dRcp) {
    double nearT = Double.NEGATIVE_INFINITY;
    double farT = Double.POSITIVE_INFINITY;

    for (int i = 0; i < 3; i++) {
      double origin, minVal, maxVal, d;

      if (i == 0) {
        origin = ray.o.x;
        minVal = pMin.x;
        maxVal = pMax.x;
        d = ray.d.x;
      } else if (i == 1) {
        origin = ray.o.y;
        minVal = pMin.y;
        maxVal = pMax.y;
        d = ray.d.y;
      } else {
        origin = ray.o.z;
        minVal = pMin.z;
        maxVal = pMax.z;
        d = ray.d.z;
      }

      if (d == 0) {
        if (origin < minVal || origin > maxVal)
          return false;
      } else {
        double t1, t2;
        double recip = dRcp[i];
        if (recip > 0) {
          t1 = (minVal - origin) * recip;
          t2 = (maxVal - origin) * recip;
        } else {
          t2 = (minVal - origin) * recip;
          t1 = (maxVal - origin) * recip;
        }

        if (nearT < t1) nearT = t1;
        if (farT > t2) farT = t2;

        if (nearT > farT)
          return false;
      }
    }

    return ray.mint <= farT && nearT <= ray.maxt;
  }

  /**
   * Compute the overlapping region of the bounding box and the unbounded ray.
   *
   * @param ray the ray
   * @param t   a 2-element array which receives the distance along the ray where it intersects the AABB.
   *            The intersection interval is [t[0], t[1]].
   * @return whether the unbounded ray intersects the bounding box
   */
  public boolean rayIntersect(Ray ray, double[] t) {
    double nearT = Double.NEGATIVE_INFINITY;
    double farT = Double.POSITIVE_INFINITY;

    for (int i = 0; i < 3; i++) {
      double origin, minVal, maxVal, d;

      if (i == 0) {
        origin = ray.o.x;
        minVal = pMin.x;
        maxVal = pMax.x;
        d = ray.d.x;
      } else if (i == 1) {
        origin = ray.o.y;
        minVal = pMin.y;
        maxVal = pMax.y;
        d = ray.d.y;
      } else {
        origin = ray.o.z;
        minVal = pMin.z;
        maxVal = pMax.z;
        d = ray.d.z;
      }

      if (d == 0) {
        if (origin < minVal || origin > maxVal)
          return false;
      } else {
        double t1, t2;
        double recip = 1.0 / TupleUtil.getComponent(ray.d, i);
        if (recip > 0) {
          t1 = (minVal - origin) * recip;
          t2 = (maxVal - origin) * recip;
        } else {
          t2 = (minVal - origin) * recip;
          t1 = (maxVal - origin) * recip;
        }

        if (nearT < t1) nearT = t1;
        if (farT > t2) farT = t2;

        t[0] = nearT;
        t[1] = farT;

        if (nearT > farT)
          return false;
      }
    }

    return ray.mint <= farT && nearT <= ray.maxt;
  }


  /**
   * Compute the overlapping region of the bounding box and the unbounded ray.
   *
   * @param ray the ray
   * @param t   a 2-element array which receives the distance along the ray where it intersects the AABB.
   *            The intersection interval is [t[0], t[1]].
   * @return whether the unbounded ray intersects the bounding box
   */
  public boolean rayIntersectFast(Ray ray, double[] dRcp, double[] t) {
    double nearT = Double.NEGATIVE_INFINITY;
    double farT = Double.POSITIVE_INFINITY;

    for (int i = 0; i < 3; i++) {
      double origin, minVal, maxVal, d;

      if (i == 0) {
        origin = ray.o.x;
        minVal = pMin.x;
        maxVal = pMax.x;
        d = ray.d.x;
      } else if (i == 1) {
        origin = ray.o.y;
        minVal = pMin.y;
        maxVal = pMax.y;
        d = ray.d.y;
      } else {
        origin = ray.o.z;
        minVal = pMin.z;
        maxVal = pMax.z;
        d = ray.d.z;
      }

      if (d == 0) {
        if (origin < minVal || origin > maxVal)
          return false;
      } else {
        double t1, t2;
        double recip = dRcp[i];
        if (recip > 0) {
          t1 = (minVal - origin) * recip;
          t2 = (maxVal - origin) * recip;
        } else {
          t2 = (minVal - origin) * recip;
          t1 = (maxVal - origin) * recip;
        }

        if (nearT < t1) nearT = t1;
        if (farT > t2) farT = t2;

        t[0] = nearT;
        t[1] = farT;

        if (nearT > farT)
          return false;
      }
    }

    return ray.mint <= farT && nearT <= ray.maxt;
  }

  public static double volumeOfIntersection(Aabb3d a, Aabb3d b) {
    double xMin = Math.max(a.pMin.x, b.pMin.x);
    double xMax = Math.min(a.pMax.x, b.pMax.x);
    if (xMin >= xMax) {
      return 0;
    }
    double yMin = Math.max(a.pMin.y, b.pMin.y);
    double yMax = Math.min(a.pMax.y, b.pMax.y);
    if (yMin >= yMax) {
      return 0;
    }
    double zMin = Math.max(a.pMin.z, b.pMin.z);
    double zMax = Math.min(a.pMax.z, b.pMax.z);
    if (zMin >= zMax) {
      return 0;
    }
    return (xMax - xMin) * (yMax - yMin) * (zMax - zMin);
  }
}
