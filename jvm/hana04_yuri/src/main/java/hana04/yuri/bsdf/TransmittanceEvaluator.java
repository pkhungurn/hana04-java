package hana04.yuri.bsdf;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.Constant;
import hana04.base.changeprop.VersionedValue;
import hana04.shakuyaku.bsdf.Bsdf;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

public interface TransmittanceEvaluator {
  double eval(Vector3d shadowRayDir, Vector2d uv);

  interface Vv extends VersionedValue<TransmittanceEvaluator> {
    // No-OP
  }

  class OpaqueVv extends Constant<TransmittanceEvaluator> implements TransmittanceEvaluator.Vv {
    @HanaDeclareExtension(
      extensibleClass = Bsdf.class,
      extensionClass = TransmittanceEvaluator.Vv.class)
    OpaqueVv(Bsdf bsdf) {
      super(((shadowRayDir, uv) -> 0.0));
    }
  }
}
