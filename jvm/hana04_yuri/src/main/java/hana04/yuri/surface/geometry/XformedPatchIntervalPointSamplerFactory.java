package hana04.yuri.surface.geometry;

import hana04.base.changeprop.VersionedValue;
import hana04.gfxbase.gfxtype.Transform;
import hana04.yuri.surface.PatchIntervalPointSampler;

public interface XformedPatchIntervalPointSamplerFactory {
  PatchIntervalPointSampler create(int startIndex, int endIndex, Transform toWorld);

  interface Vv extends VersionedValue<XformedPatchIntervalPointSamplerFactory> {
    // NO-OP
  }
}
