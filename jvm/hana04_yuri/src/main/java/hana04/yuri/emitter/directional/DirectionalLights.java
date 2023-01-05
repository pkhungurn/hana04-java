package hana04.yuri.emitter.directional;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.Constant;
import hana04.shakuyaku.emitter.directional.DirectionalLight;
import hana04.yuri.emitter.EmitterInSceneFactory;

import javax.inject.Provider;

public class DirectionalLights {
  public static class EmitterInSceneFactoryVv
    extends Constant<EmitterInSceneFactory>
    implements EmitterInSceneFactory.Vv {
    @HanaDeclareExtension(
      extensibleClass = DirectionalLight.class,
      extensionClass = EmitterInSceneFactory.Vv.class)
    EmitterInSceneFactoryVv(DirectionalLight light, Provider<DirectionalLightInScenes.DirectionalLightInSceneBuilder> emitterBuilder) {
      super((scene, patchInterval) -> emitterBuilder.get().emitter(light).scene(scene).build());
    }
  }
}
