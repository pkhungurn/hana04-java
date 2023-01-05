package hana04.yuri.emitter.distantdisk;

import hana04.apt.annotation.HanaDeclareExtensible;
import hana04.apt.annotation.HanaProperty;
import hana04.shakuyaku.emitter.distantdisk.DistantDiskLight;
import hana04.yuri.emitter.EmitterInScene;
import hana04.shakuyaku.scene.Scene;

@HanaDeclareExtensible(EmitterInScene.class)
public interface DistantDiskLightInScene extends EmitterInScene {
  @HanaProperty(1)
  DistantDiskLight emitter();

  @HanaProperty(2)
  Scene scene();
}