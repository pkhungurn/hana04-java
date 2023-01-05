package hana04.shakuyaku.surface.intervaled;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.Variable;
import hana04.gfxbase.gfxtype.Transform;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.surface.Surface;
import hana04.shakuyaku.surface.geometry.SurfaceGeometry;

import java.util.List;

@HanaDeclareObject(
  parent = Surface.class,
  typeId = TypeIds.TYPE_ID_INTERVALED_SURFACE,
  typeNames = {"shakuyaku.IntervaledSurface", "IntervaledSurface"})
public interface IntervaledSurface extends Surface {
  @HanaProperty(1)
  Variable<Transform> toWorld();

  @HanaProperty(2)
  Wrapped<SurfaceGeometry> geometry();

  @HanaProperty(3)
  Variable<List<IntervaledSurfacePatchIntervalSpec>> interval();
}