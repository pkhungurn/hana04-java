package hana04.gfxbase.gfxtype;

import hana04.gfxbase.util.MathUtil;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Point4d;
import javax.vecmath.Tuple2f;
import javax.vecmath.Tuple2i;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Tuple3i;
import javax.vecmath.Tuple4d;
import javax.vecmath.Tuple4f;
import javax.vecmath.Tuple4i;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

public class TupleUtil {
  public static int getComponent(Tuple4i v, int i) {
    switch (i) {
      case 0:
        return v.x;
      case 1:
        return v.y;
      case 2:
        return v.z;
      case 3:
        return v.w;
      default:
        throw new RuntimeException("invalid index");
    }
  }

  public static void setComponent(Tuple4i v, int i, int x) {
    switch (i) {
      case 0:
        v.x = x;
        break;
      case 1:
        v.y = x;
        break;
      case 2:
        v.z = x;
        break;
      case 3:
        v.w = x;
        break;
      default:
        throw new RuntimeException("invalid index");
    }
  }

  public static float getComponent(Tuple4f v, int i) {
    switch (i) {
      case 0:
        return v.x;
      case 1:
        return v.y;
      case 2:
        return v.z;
      case 3:
        return v.w;
      default:
        throw new RuntimeException("invalid index");
    }
  }

  public static double getComponent(Tuple4d v, int i) {
    switch (i) {
      case 0:
        return v.x;
      case 1:
        return v.y;
      case 2:
        return v.z;
      case 3:
        return v.w;
      default:
        throw new RuntimeException("invalid index");
    }
  }

  public static void setComponent(Tuple4f v, int i, float x) {
    switch (i) {
      case 0:
        v.x = x;
        break;
      case 1:
        v.y = x;
        break;
      case 2:
        v.z = x;
        break;
      case 3:
        v.w = x;
        break;
      default:
        throw new RuntimeException("invalid index");
    }
  }

  public static int getComponent(Tuple3i v, int i) {
    switch (i) {
      case 0:
        return v.x;
      case 1:
        return v.y;
      case 2:
        return v.z;
      default:
        throw new RuntimeException("invalid index");
    }
  }

  public static void setComponent(Tuple3i v, int i, int x) {
    switch (i) {
      case 0:
        v.x = x;
        break;
      case 1:
        v.y = x;
        break;
      case 2:
        v.z = x;
        break;
      default:
        throw new RuntimeException("invalid index");
    }
  }

  public static float getComponent(Tuple3f v, int i) {
    switch (i) {
      case 0:
        return v.x;
      case 1:
        return v.y;
      case 2:
        return v.z;
      default:
        throw new RuntimeException("invalid index");
    }
  }

  public static void setComponent(Tuple3f v, int i, float x) {
    switch (i) {
      case 0:
        v.x = x;
        break;
      case 1:
        v.y = x;
        break;
      case 2:
        v.z = x;
        break;
      default:
        throw new RuntimeException("invalid index");
    }
  }

  public static double getComponent(Tuple3d v, int i) {
    switch (i) {
      case 0:
        return v.x;
      case 1:
        return v.y;
      case 2:
        return v.z;
      default:
        throw new RuntimeException("invalid index");
    }
  }

  public static void setComponent(Tuple3d v, int i, double x) {
    switch (i) {
      case 0:
        v.x = x;
        break;
      case 1:
        v.y = x;
        break;
      case 2:
        v.z = x;
        break;
      default:
        throw new RuntimeException("invalid index");
    }
  }

  public static void setComponent(Tuple4d v, int i, double x) {
    switch (i) {
      case 0:
        v.x = x;
        break;
      case 1:
        v.y = x;
        break;
      case 2:
        v.z = x;
        break;
      case 3:
        v.w = x;
        break;
      default:
        throw new RuntimeException("invalid index");
    }
  }

  public static boolean isNaN(Tuple4f t) {
    return Float.isNaN(t.x) || Float.isNaN(t.y) || Float.isNaN(t.z) || Float.isNaN(t.w);
  }

  public static boolean isNaN(Tuple3f t) {
    return Float.isNaN(t.x) || Float.isNaN(t.y) || Float.isNaN(t.z);
  }

  public static boolean isNaN(Tuple3d t) {
    return Double.isNaN(t.x) || Double.isNaN(t.y) || Double.isNaN(t.z);
  }

  public static boolean isZero(Tuple3d t) {
    return t.x == 0 && t.y == 0 && t.z == 0;
  }

  public static boolean isZero(Tuple3d t, double epsilon) {
    return Math.abs(t.x) < epsilon && Math.abs(t.y) < epsilon && Math.abs(t.z) < epsilon;
  }

  public static double maxComponent(Tuple3d v) {
    return Math.max(v.x, Math.max(v.y, v.z));
  }

  public static double minComponent(Tuple3d v) {
    return Math.max(v.x, Math.max(v.y, v.z));
  }

  public static int minComponent(Tuple2i v) {
    return Math.min(v.x, v.y);
  }

  public static boolean isFinite(Tuple4d t) {
    return Double.isFinite(t.x)
      && Double.isFinite(t.y)
      && Double.isFinite(t.z)
      && Double.isFinite(t.w);
  }


  public static boolean approxEquals(Tuple3d a, Tuple3d b, double tolerance) {
    return MathUtil.approxEqual(a.x, b.x, tolerance)
      && MathUtil.approxEqual(a.y, b.y, tolerance)
      && MathUtil.approxEqual(a.z, b.z, tolerance);
  }

  public static Point3d toPoint3d(Tuple3f input) {
    return new Point3d(input.x, input.y, input.z);
  }

  public static Vector3d toVector3d(Tuple3f input) {
    return new Vector3d(input.x, input.y, input.z);
  }

  public static Point4d toPoint4d(Tuple4f input) {
    return new Point4d(input.x, input.y, input.z, input.w);
  }

  public static Vector4d toVector4d(Tuple4f input) {
    return new Vector4d(input.x, input.y, input.z, input.w);
  }

  public static Point2d toPoint2d(Tuple2f input) {
    return new Point2d(input.x, input.y);
  }

  public static Vector2d toVector2d(Tuple2f input) {
    return new Vector2d(input.x, input.y);
  }

  public static Vector3d toVector3d(Tuple4d input) {
    return new Vector3d(input.x, input.y, input.z);
  }
}
