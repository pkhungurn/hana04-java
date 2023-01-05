package hana04.shakuyaku.surface.geometry;

import hana04.base.changeprop.VersionedValue;
import hana04.gfxbase.gfxtype.Transform;
import hana04.shakuyaku.surface.SurfacePatchInfo;

/**
 * A factory that creates a {@link SurfacePatchInfo} object that deals with a geometry transformed to world space.
 */
public interface XformedPatchInfoFactory {
  SurfacePatchInfo create(Transform xform);

  interface Vv extends VersionedValue<XformedPatchInfoFactory> {
    // NO-OP
  }
}
