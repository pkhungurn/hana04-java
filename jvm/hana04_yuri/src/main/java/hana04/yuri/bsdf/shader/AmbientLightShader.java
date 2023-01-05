package hana04.yuri.bsdf.shader;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.Constant;
import hana04.base.changeprop.VersionedValue;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.SpectrumTransform;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.shakuyaku.bsdf.Bsdf;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

public interface AmbientLightShader<
  T extends Spectrum, V extends SpectrumTransform<T>> {
  V shade(Vector3d wi, Vector2d uv);

  interface ForRgb extends AmbientLightShader<Rgb, Rgb> {
    // NO-OP

    interface Vv extends VersionedValue<ForRgb> {
      // NO-OP
    }
  }

  class ZeroShaderForRgb implements ForRgb {
    @Override
    public Rgb shade(Vector3d wi, Vector2d uv) {
      return new Rgb(0, 0, 0);
    }
  }

  class ZeroShaderForRgbVv extends Constant<ForRgb> implements ForRgb.Vv {
    @HanaDeclareExtension(
      extensibleClass = Bsdf.class,
      extensionClass = ForRgb.Vv.class)
    ZeroShaderForRgbVv(Bsdf bsdf) {
      super(new ZeroShaderForRgb());
    }
  }
}
