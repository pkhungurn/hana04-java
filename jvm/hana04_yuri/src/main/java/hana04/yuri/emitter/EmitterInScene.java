package hana04.yuri.emitter;

import hana04.apt.annotation.HanaDeclareExtensibleInterface;
import hana04.base.extension.HanaExtensible;
import hana04.shakuyaku.emitter.Emitter;
import hana04.shakuyaku.scene.Scene;

/**
 * A coupling between an {@link Emitter}, a {@link Scene}, and the part of the scene that the emitter is attached to.
 * Has all the information that allows the emitter to be sampled and evaluated. As a result, it admits the
 * {@link EmitterSampler} extension.
 */
@HanaDeclareExtensibleInterface(HanaExtensible.class)
public interface EmitterInScene extends HanaExtensible {
  /**
   * The emitter.
   */
  Emitter emitter();

  /**
   * The scene where it is located in.
   */
  Scene scene();
}
