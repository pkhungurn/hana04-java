package hana04.shakuyaku.surface;

import hana04.apt.annotation.HanaDeclareExtensibleInterface;
import hana04.base.caching.Wrapped;
import hana04.base.extension.HanaExtensible;
import hana04.shakuyaku.bsdf.Bsdf;
import hana04.shakuyaku.emitter.Emitter;

import java.util.Optional;

@HanaDeclareExtensibleInterface(HanaExtensible.class)
public interface PatchInterval extends HanaExtensible {
  /**
   * The index of the first patch in the interval.
   */
  Integer startPatchIndex();
  /**
   * The index of the patch after the last patch in the interval. The index is *exclusive*. The patch with this index
   * does not belong to the interval.
   */
  Integer endPatchIndex();
  /**
   * The BSDF of the interval.
   */
  Wrapped<Bsdf> bsdf();
  /**
   * The Emitter of the interval.
   */
  Optional<Wrapped<Emitter>> emitter();
  /**
   * The surface that this patch belongs to.
   */
  Surface surface();
}
