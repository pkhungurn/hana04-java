package hana04.shakuyaku.scene.shadinghack;

import hana04.shakuyaku.shadinghack.ambientlight.ConstantAmbientLight;

import java.util.Optional;

public interface ConstantAmbientLightInScene {
  Optional<ConstantAmbientLight> get();
}
