package hana04.gfxbase.gfxtype;

import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;

public class Aabb2d {
  public final Vector2d pMin = new Vector2d(Double.MAX_VALUE, Double.MAX_VALUE);
  public final Vector2d pMax = new Vector2d(-Double.MAX_VALUE, -Double.MAX_VALUE);

  public Aabb2d() {
    // NO-OP
  }

  public Aabb2d(Tuple2d pMin, Tuple2d pMax) {
    this.expandBy(pMin);
    this.expandBy(pMax);
  }

  public void reset() {
    pMin.set(Double.MAX_VALUE, Double.MAX_VALUE);
    pMax.set(-Double.MAX_VALUE, -Double.MAX_VALUE);
  }

  public boolean isValid() {
    return pMin.x <= pMax.x && pMin.y <= pMax.y;
  }

  public void expandBy(Tuple2d p) {
    pMin.x = Math.min(pMin.x, p.x);
    pMin.y = Math.min(pMin.y, p.y);
    pMax.x = Math.max(pMax.x, p.x);
    pMax.y = Math.max(pMax.y, p.y);
  }

  public void expandBy(double t) {
    pMin.x -= t;
    pMin.y -= t;
    pMax.x += t;
    pMax.y += t;
  }

  public void expandBy(Aabb2d other) {
    pMin.x = Math.min(pMin.x, other.pMin.x);
    pMin.y = Math.min(pMin.y, other.pMin.y);
    pMax.x = Math.max(pMax.x, other.pMax.x);
    pMax.y = Math.max(pMax.y, other.pMax.y);
  }

  public boolean overlap(Aabb2d other) {
    if (pMin.x > other.pMax.x || pMax.x < other.pMin.x) {
      return false;
    } else if (pMin.y > other.pMax.y || pMax.y < other.pMin.y) {
      return false;
    } else {
      return true;
    }
  }

  public boolean overlap(Tuple2d p) {
    if (p.x < pMin.x || p.x > pMax.x) {
      return false;
    } else if (p.y < pMin.y || p.y > pMax.y) {
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
    } else {
      return true;
    }
  }

  public void set(Aabb2d other) {
    pMin.set(other.pMin);
    pMax.set(other.pMax);
  }

  @Override
  public String toString() {
    return "Aabb[pMin = " + pMin.toString() + ", pMax = " + pMax.toString() + "]";
  }

  public double getMaxExtent() {
    return Math.max(pMax.x - pMin.x, pMax.y - pMin.y);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Aabb2d) {
      Aabb2d other = (Aabb2d) obj;
      return other.pMin.equals(this.pMin) && other.pMax.equals(this.pMax);
    } else {
      return false;
    }
  }

  public void getCornerPoint(int cornerId, Tuple2d out) {
    out.x = ((cornerId & 1) == 0) ? pMin.x : pMax.x;
    out.y = ((cornerId & 2) == 0) ? pMin.y : pMax.y;
  }

  public double getExtent(int dim) {
    switch (dim) {
      case 0:
        return pMax.x - pMin.x;
      case 1:
        return pMax.y - pMin.y;
      default:
        throw new RuntimeException("dim must be 0 or 1");
    }
  }

  public double getArea() {
    double xx = pMax.x - pMin.x;
    double yy = pMax.y - pMin.y;
    return xx * yy;
  }

  public int getLargestAxis() {
    double xx = pMax.x - pMin.x;
    double yy = pMax.y - pMin.y;

    if (xx >= yy)
      return 0;
    else if (yy >= xx)
      return 1;
    else
      return 2;
  }

  public static double volumeOfIntersection(Aabb2d a, Aabb2d b) {
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
    return (xMax - xMin) * (yMax - yMin);
  }
}
