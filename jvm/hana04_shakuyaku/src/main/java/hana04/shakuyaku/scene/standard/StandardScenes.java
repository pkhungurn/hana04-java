package hana04.shakuyaku.scene.standard;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.caching.Wrapped;
import hana04.shakuyaku.scene.shadinghack.ConstantAmbientLightInScene;
import hana04.shakuyaku.shadinghack.ShadingHack;
import hana04.shakuyaku.shadinghack.ambientlight.ConstantAmbientLight;

import javax.inject.Inject;
import java.util.Optional;

public class StandardScenes {
  @HanaDeclareBuilder(StandardScene.class)
  public static class StandardSceneBuilder extends StandardScene__Impl__Builder<StandardSceneBuilder> {
    @Inject
    public StandardSceneBuilder(StandardScene__ImplFactory factory) {
      super(factory);
    }

    public static StandardSceneBuilder builder(Component component) {
      return component.uberFactory().create(StandardSceneBuilder.class);
    }
  }

  public static class StandardSceneConstantAmbientLight implements ConstantAmbientLightInScene {
    private final StandardScene scene;
    private Optional<ConstantAmbientLight> constantAmbientLight = Optional.empty();

    @HanaDeclareExtension(
      extensibleClass = StandardScene.class,
      extensionClass = ConstantAmbientLightInScene.class)
    public StandardSceneConstantAmbientLight(StandardScene scene, HanaUnwrapper unwrapper) {
      this.scene = scene;
      for (Wrapped<ShadingHack> wrappedShadingHack : scene.shadingHack().value()) {
        ShadingHack shadingHack = wrappedShadingHack.unwrap(unwrapper);
        if (shadingHack instanceof ConstantAmbientLight) {
          constantAmbientLight = Optional.of((ConstantAmbientLight) shadingHack);
        }
      }
    }

    @Override
    public Optional<ConstantAmbientLight> get() {
      return constantAmbientLight;
    }
  }
}
