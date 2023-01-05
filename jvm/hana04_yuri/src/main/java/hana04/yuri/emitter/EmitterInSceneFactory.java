package hana04.yuri.emitter;

import hana04.base.changeprop.VersionedValue;
import hana04.shakuyaku.scene.Scene;
import hana04.shakuyaku.surface.PatchInterval;

import java.util.Optional;

public interface EmitterInSceneFactory {
  EmitterInScene create(Scene scene, Optional<PatchInterval> patchInterval);

  interface Vv extends VersionedValue<EmitterInSceneFactory> {
    // NO-OP
  }
}
