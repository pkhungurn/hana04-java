package hana04.shakuyaku.surface.mmd.mmd;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.Variable;
import hana04.gfxbase.gfxtype.Transform;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.surface.Surface;
import hana04.shakuyaku.surface.mmd.geometry.MmdGeometry;

@HanaDeclareObject(
  parent = Surface.class,
  typeId = TypeIds.TYPE_ID_MMD_SURFACE,
  typeNames = {"shakuyaku.MmdSurface", "MmdSurface"})
public interface MmdSurface extends Surface {
  @HanaProperty(1)
  Variable<Transform> toWorld();

  @HanaProperty(2)
  Wrapped<MmdGeometry> geometry();
}
