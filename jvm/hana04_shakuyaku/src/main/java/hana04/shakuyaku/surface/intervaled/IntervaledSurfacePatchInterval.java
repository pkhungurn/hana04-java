package hana04.shakuyaku.surface.intervaled;

import hana04.apt.annotation.HanaDeclareExtensible;
import hana04.apt.annotation.HanaProperty;
import hana04.base.caching.Wrapped;
import hana04.shakuyaku.bsdf.Bsdf;
import hana04.shakuyaku.emitter.Emitter;
import hana04.shakuyaku.surface.PatchInterval;

import java.util.Optional;

@HanaDeclareExtensible(PatchInterval.class)
public interface IntervaledSurfacePatchInterval extends PatchInterval {
  @HanaProperty(1)
  Integer startPatchIndex();

  @HanaProperty(2)
  Integer endPatchIndex();

  @HanaProperty(3)
  Wrapped<Bsdf> bsdf();

  @HanaProperty(4)
  Optional<Wrapped<Emitter>> emitter();

  @HanaProperty(5)
  IntervaledSurface surface();
}
