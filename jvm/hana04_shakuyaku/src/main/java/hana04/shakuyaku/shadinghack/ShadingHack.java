package hana04.shakuyaku.shadinghack;

import hana04.apt.annotation.HanaDeclareExtensibleInterface;
import hana04.base.extension.HanaObject;

/**
 * Any data that can be used to help the shading computation.
 */
@HanaDeclareExtensibleInterface(HanaObject.class)
public interface ShadingHack extends HanaObject {
  // NO-OP
}
