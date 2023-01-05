package hana04.yuri.texture.twodim.specspaces;

import hana04.base.changeprop.VersionedValue;
import hana04.gfxbase.spectrum.scalar.Scalar;
import hana04.yuri.texture.twodim.TextureTwoDimEvaluator;

public interface TextureTwoDimEvaluatorScalar extends TextureTwoDimEvaluator<Scalar> {
  // NO-OP

  interface Vv extends VersionedValue<TextureTwoDimEvaluatorScalar> {
    // NO-OP
  }
}
