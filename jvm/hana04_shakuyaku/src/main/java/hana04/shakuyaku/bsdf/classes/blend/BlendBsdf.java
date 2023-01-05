package hana04.shakuyaku.bsdf.classes.blend;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.Variable;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.bsdf.Bsdf;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;

@HanaDeclareObject(
  parent = Bsdf.class,
  typeId = TypeIds.TYPE_ID_BLEND_BSDF,
  typeNames = {"shakuyaku.BlendBsdf", "BlendBsdf"})
public interface BlendBsdf extends Bsdf {
  @HanaProperty(1)
  Variable<Wrapped<TextureTwoDim>> alpha();

  // The BSDF that is multiplied by alphaScale.
  @HanaProperty(2)
  Variable<Wrapped<Bsdf>> first();

  // The BSDF that is multiplied by 1-alphaScale.
  @HanaProperty(3)
  Variable<Wrapped<Bsdf>> second();
}
