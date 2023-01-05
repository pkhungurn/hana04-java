package hana04.yuri.bsdf.specspaces;

import hana04.base.changeprop.VersionedValue;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.yuri.bsdf.BsdfEvaluator;

public interface BsdfEvaluatorRgb extends BsdfEvaluator<Rgb, Rgb> {
  // NO-OP

  interface Vv extends VersionedValue<BsdfEvaluatorRgb> {
    // NO-OP
  }
}
