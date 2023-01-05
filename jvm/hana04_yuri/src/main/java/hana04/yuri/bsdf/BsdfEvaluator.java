package hana04.yuri.bsdf;

import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.SpectrumTransform;

import javax.vecmath.Tuple2d;
import javax.vecmath.Vector3d;

public interface BsdfEvaluator<T extends Spectrum, V extends SpectrumTransform<T>> {
  V eval(Vector3d wi, Vector3d wo, Tuple2d uv);

  class Proxy<T extends Spectrum, V extends SpectrumTransform<T>> implements BsdfEvaluator<T, V> {
    private BsdfEvaluator<T, V> inner;

    public Proxy(BsdfEvaluator<T, V> inner) {
      this.inner = inner;
    }

    @Override
    public V eval(Vector3d wi, Vector3d wo, Tuple2d uv) {
      return inner.eval(wi, wo, uv);
    }
  }
}
