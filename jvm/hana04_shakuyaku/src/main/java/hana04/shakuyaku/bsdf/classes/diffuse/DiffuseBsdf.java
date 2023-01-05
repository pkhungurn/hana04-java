package hana04.shakuyaku.bsdf.classes.diffuse;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.Variable;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.bsdf.Bsdf;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;

/**
 * Two-sided opaque diffuse material.
 */
@HanaDeclareObject(
  parent = Bsdf.class,
  typeId = TypeIds.TYPE_ID_DIFFUSE_BSDF,
  typeNames = {"shakuyaku.DiffuseBsdf", "DiffuseBsdf"})
public interface DiffuseBsdf extends Bsdf {
  @HanaProperty(1)
  Variable<Wrapped<TextureTwoDim>> reflectance();
}
