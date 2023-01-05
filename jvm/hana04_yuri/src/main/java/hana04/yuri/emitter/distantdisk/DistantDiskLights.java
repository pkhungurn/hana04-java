package hana04.yuri.emitter.distantdisk;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.Constant;
import hana04.shakuyaku.emitter.distantdisk.DistantDiskLight;
import hana04.yuri.emitter.EmitterInSceneFactory;

import javax.inject.Provider;

public class DistantDiskLights {
  public static class EmitterInSceneFactoryVv
      extends Constant<EmitterInSceneFactory>
      implements EmitterInSceneFactory.Vv {
    @HanaDeclareExtension(
        extensibleClass = DistantDiskLight.class,
        extensionClass = EmitterInSceneFactory.Vv.class)
    EmitterInSceneFactoryVv(DistantDiskLight light,
        Provider<DistantDiskLightInScenes.DistantDiskLightInSceneBuilder> emitterBuilder) {
      super((scene, patchInterval) -> emitterBuilder.get().emitter(light).scene(scene).build());
    }
  }

}
