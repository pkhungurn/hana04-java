package hana04.yuri.texture.twodim.specspaces;

import hana04.base.changeprop.VersionedValue;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.yuri.texture.twodim.TextureTwoDimEvaluator;

public interface TextureTwoDimEvaluatorRgb extends TextureTwoDimEvaluator<Rgb> {
  // NO-OP

  interface Vv extends VersionedValue<TextureTwoDimEvaluatorRgb> {
    // NO-OP
  }
}
