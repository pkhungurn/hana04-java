package hana04.yuri.texture.twodim;

import hana04.base.changeprop.VersionedValue;

import javax.vecmath.Tuple2d;

public interface TextureTwoDimAlphaEvaluator {
  double eval(Tuple2d uv);

  interface Vv extends VersionedValue<TextureTwoDimAlphaEvaluator> {
    // NO-OP
  }
}
