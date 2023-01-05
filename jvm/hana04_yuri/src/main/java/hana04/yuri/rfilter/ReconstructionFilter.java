package hana04.yuri.rfilter;

import hana04.apt.annotation.HanaDeclareExtensibleInterface;
import hana04.base.extension.HanaObject;

/**
 * Generic bilaterally symmetric image reconstruction filter.
 */
@HanaDeclareExtensibleInterface(HanaObject.class)
public interface ReconstructionFilter extends HanaObject {
  // NO-OP
}
