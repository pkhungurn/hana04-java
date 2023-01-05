package hana04.yuri.emitter.envmap;

import hana04.apt.annotation.HanaDeclareExtensible;
import hana04.apt.annotation.HanaProperty;
import hana04.shakuyaku.emitter.envmap.EnvironmentMapLight;
import hana04.yuri.emitter.EmitterInScene;
import hana04.shakuyaku.scene.Scene;

@HanaDeclareExtensible(EmitterInScene.class)
public interface EnvironmentMapLightInScene extends EmitterInScene {
  @HanaProperty(1)
  EnvironmentMapLight emitter();

  @HanaProperty(2)
  Scene scene();
}