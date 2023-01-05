package hana04.yuri.emitter.area;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.Constant;
import hana04.shakuyaku.emitter.area.AreaEmitter;
import hana04.yuri.emitter.EmitterInSceneFactory;

import javax.inject.Provider;

public class AreaEmitters {
  public static class EmitterInSceneFactoryVv
      extends Constant<EmitterInSceneFactory>
      implements EmitterInSceneFactory.Vv {
    @HanaDeclareExtension(
        extensibleClass = AreaEmitter.class,
        extensionClass = EmitterInSceneFactory.Vv.class)
    EmitterInSceneFactoryVv(AreaEmitter emitter,
        Provider<AreaEmitterInScenes.AreaEmitterInSceneBuilder> emitterBuilder) {
      super((scene, patchInterval) -> emitterBuilder.get()
          .emitter(emitter)
          .scene(scene)
          .patchInterval(patchInterval)
          .build());
    }
  }
}
