package hana04.gfxbase.gfxtype;

import hana04.gfxbase.util.MathUtil;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class PointMathUtil {
  public static Point3d add(Point3d a, Point3d b) {
    return new Point3d(a.x + b.x, a.y + b.y, a.z + b.z);
  }

  public static Point3d add(Point3d a, Vector3d b) {
    return new Point3d(a.x + b.x, a.y + b.y, a.z + b.z);
  }

  public static Point3d scale(Point3d a, double c) {
    return new Point3d(a.x * c, a.y * c, a.z * c);
  }

  public static Point3d sub(Point3d a, Point3d b) {
    return new Point3d(a.x - b.x, a.y - b.y, a.z - b.z);
  }

  public static Point2d add(Point2d a, Point2d b) {
    return new Point2d(a.x + b.x, a.y + b.y);
  }

  public static Point2d sub(Point2d a, Point2d b) {
    return new Point2d(a.x - b.x, a.y - b.y);
  }

  public static double getDirectionAngle(Point2d from, Point2d to) {
    double dx = to.x - from.x;
    double dy = to.y - from.y;
    return MathUtil.normalizeRad(Math.atan2(dy, dx));
  }

  public static Point2d interp(Point2d p0, Point2d p1, double alpha) {
    double x = (1 - alpha) * p0.x + alpha * p1.x;
    double y = (1 - alpha) * p0.y + alpha * p1.y;
    return new Point2d(x, y);
  }
}
