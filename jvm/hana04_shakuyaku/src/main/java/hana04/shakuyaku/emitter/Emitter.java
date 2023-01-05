package hana04.shakuyaku.emitter;

import hana04.apt.annotation.HanaDeclareExtensibleInterface;
import hana04.base.changeprop.Variable;
import hana04.base.extension.HanaObject;

/**
 * Something that can emit light energy. This must be physical, so an ambient light is not an Emitter.
 */
@HanaDeclareExtensibleInterface(HanaObject.class)
public interface Emitter extends HanaObject {
  /**
   * The unnormalized probability that this light source is chosen when sampling a random light source.
   * In other words, if the samplingWeight of this light source is w_i, then the normalized probability is
   * w_i / (w_1 + w_2 + ... + w_n) where the sum ranges over all light sources.
   */
  Variable<Double> samplingWeight();
}
