package hana04.yuri.emitter.envmap;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.Constant;
import hana04.shakuyaku.emitter.envmap.EnvironmentMapLight;
import hana04.yuri.emitter.EmitterInSceneFactory;

import javax.inject.Provider;

public class EnvironmentMapLights {
  public static class EmitterInSceneFactoryVv
      extends Constant<EmitterInSceneFactory>
      implements EmitterInSceneFactory.Vv {
    @HanaDeclareExtension(
        extensibleClass = EnvironmentMapLight.class,
        extensionClass = EmitterInSceneFactory.Vv.class)
    EmitterInSceneFactoryVv(EnvironmentMapLight light, Provider<EnvironmentMapLightInScenes.EnvironmentMapLightInSceneBuilder> emitterBuilder) {
      super((scene, patchInterval) -> emitterBuilder.get().emitter(light).scene(scene).build());
    }
  }
}
