package hana04.yuri.emitter.directional;

import hana04.apt.annotation.HanaDeclareExtensible;
import hana04.apt.annotation.HanaProperty;
import hana04.shakuyaku.emitter.directional.DirectionalLight;
import hana04.yuri.emitter.EmitterInScene;
import hana04.shakuyaku.scene.Scene;

@HanaDeclareExtensible(EmitterInScene.class)
public interface DirectionalLightInScene extends EmitterInScene {
  @HanaProperty(1)
  DirectionalLight emitter();

  @HanaProperty(2)
  Scene scene();
}
