package hana04.shakuyaku.surface.mmd.util;

import hana04.base.changeprop.VersionedValue;

public interface MaterialAmbientUsage {
  boolean shouldMaterialUseAmbient(String name);

  interface Vv extends VersionedValue<MaterialAmbientUsage> {
    // NO-OP
  }
}
