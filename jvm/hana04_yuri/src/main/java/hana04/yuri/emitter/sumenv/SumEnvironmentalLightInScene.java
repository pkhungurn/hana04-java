package hana04.yuri.emitter.sumenv;

import hana04.apt.annotation.HanaDeclareExtensible;
import hana04.apt.annotation.HanaProperty;
import hana04.shakuyaku.emitter.sumenv.SumEnvironmentalLight;
import hana04.yuri.emitter.EmitterInScene;
import hana04.shakuyaku.scene.Scene;

@HanaDeclareExtensible(EmitterInScene.class)
public interface SumEnvironmentalLightInScene extends EmitterInScene {
  @HanaProperty(1)
  SumEnvironmentalLight emitter();

  @HanaProperty(2)
  Scene scene();
}
