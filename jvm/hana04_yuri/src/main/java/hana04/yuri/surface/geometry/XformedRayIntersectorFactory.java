package hana04.yuri.surface.geometry;

import hana04.base.changeprop.VersionedValue;
import hana04.gfxbase.gfxtype.Transform;
import hana04.yuri.surface.RayIntersector;

public interface XformedRayIntersectorFactory {
  RayIntersector create(Transform toWorld);

  interface Vv extends VersionedValue<XformedRayIntersectorFactory> {
    // NO-OP
  }
}
