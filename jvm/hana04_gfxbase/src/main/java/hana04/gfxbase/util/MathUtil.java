package hana04.gfxbase.util;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.special.Erf;

import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;

public class MathUtil {
  public static int getClosestPowerOfTwo(int x) {
    if (x < 0)
      return 1;
    else {
      int y = 1;
      while (y < x) {
        y = y * 2;
      }
      if (Math.abs(y - x) < Math.abs(y / 2 - x)) {
        return y;
      } else {
        return y / 2;
      }
    }
  }

  public static int getCeilingPowerOfTwo(int x) {
    if (x < 0) {
      return 1;
    } else {
      int y = 1;
      while (y < x) {
        y *= 2;
      }
      return y;
    }
  }

  public static boolean isPowerOfTwo(int x) {
    x = Math.abs(x);
    if (x == 0)
      return false;
    else if (x == 1)
      return true;
    else {
      while (x > 1) {
        if ((x & 1) == 1)
          return false;
        else
          x = (x >> 1);
      }
      return x == 1;
    }
  }

  public static boolean between(float x, float lower, float upper) {
    return (x >= lower) && (x <= upper);
  }

  public static boolean between(double v, double lower, double upper) {
    return (v >= lower) && (v <= upper);
  }

  /**
   * Clamp x to the given lower and upper bound.
   *
   * @param x     the value
   * @param lower the lower bound
   * @param upper the upper bound
   * @return x clamped to the interval [lower, upper].
   */
  public static double clamp(double x, double lower, double upper) {
    return Math.min(upper, Math.max(x, lower));
  }

  /**
   * Clamp x to the given lower and upper bound.
   *
   * @param v     the value
   * @param lower the lower bound
   * @param upper the upper bound
   * @return x clamped to the interval [lower, upper].
   */
  public static float clamp(float v, float lower, float upper) {
    return Math.min(upper, Math.max(v, lower));
  }

  public static void clamp(Tuple3f v, Tuple3f lower, Tuple3f upper) {
    v.x = clamp(v.x, lower.x, upper.x);
    v.y = clamp(v.y, lower.y, upper.y);
    v.z = clamp(v.z, lower.z, upper.z);
  }

  public static void clamp(Tuple3d v, Tuple3d lower, Tuple3d upper) {
    v.x = clamp(v.x, lower.x, upper.x);
    v.y = clamp(v.y, lower.y, upper.y);
    v.z = clamp(v.z, lower.z, upper.z);
  }

  /**
   * Clamp x to the given lower and upper bound.
   *
   * @param x     the value
   * @param lower the lower bound
   * @param upper the upper bound
   * @return x clamped to the interval [lower, upper].
   */
  public static int clamp(int x, int lower, int upper) {
    return Math.min(upper, Math.max(x, lower));
  }

  public static boolean solveQuadratic(double a, double b, double c, double[] x) {
    /* Linear case */
    if (a == 0) {
      if (b != 0) {
        x[0] = x[1] = -c / b;
        return true;
      }
      return false;
    }

    double discrim = b * b - 4.0f * a * c;

    /* Leave if there is no solution */
    if (discrim < 0)
      return false;

    double temp, sqrtDiscrim = Math.sqrt(discrim);

    /* Numerically stable version of (-b (+/-) sqrtDiscrim) / (2 * a)
     *
     * Based on the observation that one solution is always
     * accurate while the other is not. Finds the solution of
     * greater magnitude which does not suffer from loss of
     * precision and then uses the identity x1 * x2 = c / a
     */
    if (b < 0)
      temp = -0.5 * (b - sqrtDiscrim);
    else
      temp = -0.5 * (b + sqrtDiscrim);

    x[0] = temp / a;
    x[1] = c / temp;

    /* Return the results so that x0 < x1 */
    if (x[0] > x[1]) {
      temp = x[0];
      x[0] = x[1];
      x[1] = temp;
    }

    return true;
  }

  public static double gaussian(double x, double mu, double sigma) {
    return Math.exp(-(x - mu) * (x - mu) / (2 * sigma * sigma)) / (Math.sqrt(2 * Math.PI) * Math.abs(sigma));
  }

  public static double gaussianDerivSigma(double x, double mu, double sigma) {
    return (((x - mu) * (x - mu) - sigma * sigma) /
        (Math.sqrt(2 * Math.PI) * sigma * sigma * sigma * Math.abs(sigma)))
        * Math.exp(-(x - mu) * (x - mu) / (2 * sigma * sigma));
  }

  public static double gaussianCdf(double x, double mu, double sigma) {
    return (1 + Erf.erf((x - mu) / (sigma * Math.sqrt(2.0)))) * 0.5;
  }

  public static double radToDeg(double rad) {
    return rad * 180 / Math.PI;
  }

  public static double degToRad(double deg) {
    return deg * Math.PI / 180;
  }

  public static double normalizeRad(double rad) {
    double r = rad % (2 * Math.PI);
    if (rad < 0) {
      return r + 2 * Math.PI;
    }
    return r;
  }

  public static int getSpatialCellIndex(double x, double lower, double upper, int cellCount) {
    return clamp((int) Math.floor((x - lower) / (upper - lower) * cellCount), 0, cellCount - 1);
  }

  public static Pair<Integer, Double> getSpacialCellIndexAndFraction(double x, double lower, double upper,
                                                                     int cellCount) {
    x = (x - lower) / (upper - lower) * cellCount;
    int index = clamp((int) x, 0, cellCount - 1);
    double fraction = x - index;
    return new ImmutablePair<>(index, fraction);
  }

  public static boolean approxEqual(double a, double b, double tolerance) {
    return Math.abs(a - b) <= tolerance;
  }

  public static boolean approxEqual(double a, double b) {
    return approxEqual(a, b, 1e-7);
  }

  public static double erf(double x) {
    // constants
    double a1 = 0.254829592;
    double a2 = -0.284496736;
    double a3 = 1.421413741;
    double a4 = -1.453152027;
    double a5 = 1.061405429;
    double p = 0.3275911;

    // Save the sign of x
    int sign = 1;
    if (x < 0)
      sign = -1;
    x = Math.abs(x);

    // A&S formula 7.1.26
    double t = 1.0 / (1.0 + p * x);
    double y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * Math.exp(-x * x);

    return sign * y;
  }

  public static int mod(int x, int base) {
    x = x % base;
    if (x < 0) {
      x += base;
    }
    return x;
  }

  public static double interp(double v0, double v1, double alpha) {
    return (1 - alpha) * v0 + alpha * v1;
  }

  public static int decile(int seen, int total) {
    double frac = seen * 10.0 / total;
    return (int) Math.floor(frac);
  }
}
