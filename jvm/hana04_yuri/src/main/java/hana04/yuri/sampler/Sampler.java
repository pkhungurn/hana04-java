package hana04.yuri.sampler;

import hana04.apt.annotation.HanaDeclareExtensibleInterface;
import hana04.base.extension.HanaObject;

/**
 * Generator of randon number.
 */
@HanaDeclareExtensibleInterface(HanaObject.class)
public interface Sampler extends HanaObject {
  // NO-OP
}
