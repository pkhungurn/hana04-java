package hana04.shakuyaku.surface.intervaled;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.caching.Wrapped;
import hana04.base.extension.HanaObject;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.bsdf.Bsdf;
import hana04.shakuyaku.emitter.Emitter;

import java.util.Optional;

@HanaDeclareObject(
    parent = HanaObject.class,
    typeId = TypeIds.TYPE_ID_INTERVALED_SURFACE_PATCH_INTERVAL_SPEC,
    typeNames = {"shakuyaku.IntervaledSurfacePatchIntervalSpec", "IntervaledSurfacePatchIntervalSpec"})
public interface IntervaledSurfacePatchIntervalSpec extends HanaObject {
  /**
   * The index of the first patch in the interval. Default = 0.
   */
  @HanaProperty(1)
  Integer startPatchIndex();

  /**
   * The BSDF of the interval.
   */
  @HanaProperty(2)
  Wrapped<Bsdf> bsdf();

  /**
   * The Emitter of the interval.
   */
  @HanaProperty(3)
  Optional<Wrapped<Emitter>> emitter();
}
