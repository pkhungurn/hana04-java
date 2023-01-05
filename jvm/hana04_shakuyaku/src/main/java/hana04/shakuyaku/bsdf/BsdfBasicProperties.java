package hana04.shakuyaku.bsdf;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.Constant;
import hana04.base.changeprop.VersionedValue;

/**
 * Basic properties of a BSDF.
 */
public interface BsdfBasicProperties {
  /**
   * Whether this BSDF contains a PassThroughBsdf in its composition
   * inside.
   */
  boolean containsPassThrough();

  /**
   * Whether this BSDF allows transmission of light through its surface.
   */
  boolean allowsTransmission();

  interface Vv extends VersionedValue<BsdfBasicProperties> {
    // NO-OP
  }

  class Default implements BsdfBasicProperties {
    @Override
    public boolean containsPassThrough() {
      return false;
    }

    @Override
    public boolean allowsTransmission() {
      return false;
    }
  }

  class DefaultVv extends Constant<BsdfBasicProperties> implements Vv {
    @HanaDeclareExtension(
      extensibleClass = Bsdf.class,
      extensionClass = Vv.class)
    DefaultVv(Bsdf bsdf) {
      super(new Default());
    }
  }
}
