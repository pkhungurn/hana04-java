package hana04.yuri.sampler;

import javax.vecmath.Tuple2d;

public interface RandomNumberGenerator {
  /**
   * Sample a floating point number uniformly from interval [0,1).
   */
  double next1D();

  /**
   * Sample two floating point numbers independently and uniformly from interval [0,1).
   */
  void next2D(Tuple2d output);
}
