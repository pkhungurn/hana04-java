package hana04.yuri.emitter.area;

import hana04.apt.annotation.HanaDeclareExtensible;
import hana04.apt.annotation.HanaProperty;
import hana04.shakuyaku.emitter.area.AreaEmitter;
import hana04.yuri.emitter.EmitterInScene;
import hana04.shakuyaku.scene.Scene;
import hana04.shakuyaku.surface.PatchInterval;

import java.util.Optional;

@HanaDeclareExtensible(EmitterInScene.class)
public interface AreaEmitterInScene extends EmitterInScene {
  @HanaProperty(1)
  AreaEmitter emitter();

  @HanaProperty(2)
  Scene scene();

  @HanaProperty(3)
  Optional<PatchInterval> patchInterval();
}