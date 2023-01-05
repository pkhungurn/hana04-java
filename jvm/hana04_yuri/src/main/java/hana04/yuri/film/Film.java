package hana04.yuri.film;

import hana04.apt.annotation.HanaDeclareExtensibleInterface;
import hana04.base.changeprop.Variable;
import hana04.base.extension.HanaObject;
import hana04.gfxbase.spectrum.Spectrum;

/**
 * A 2D array of {@link Spectrum}.
 */
@HanaDeclareExtensibleInterface(HanaObject.class)
public interface Film extends HanaObject {
  Variable<Integer> width();

  Variable<Integer> height();
}
