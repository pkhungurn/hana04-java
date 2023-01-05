package hana04.gfxbase.gfxtype;

import javax.vecmath.Tuple3i;

public class VecMathIUtil {
  public static int getCompenent(Tuple3i p, int dim) {
    if (dim == 0) {
      return p.x;
    } else if (dim == 1) {
      return p.y;
    } else if (dim == 2) {
      return p.z;
    } else {
      throw new RuntimeException("dim was neither 0, 1, or 2");
    }
  }
}
