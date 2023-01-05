package hana04.yuri.emitter;

import hana04.shakuyaku.surface.PatchInterval;

import java.util.Optional;

public class EmitterInScenes {
  private EmitterInScenes() {
    // NO-OP
  }

  public interface PatchIntervalProvider {
    Optional<PatchInterval> getPatchInterval();
  }
}
