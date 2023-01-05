package hana04.yuri.texture.twodim;

import hana04.gfxbase.spectrum.Spectrum;

import javax.vecmath.Tuple2d;

public interface TextureTwoDimEvaluator<T extends Spectrum> {
  T eval(Tuple2d uv);
}
