package hana04.yuri.integrand;

import hana04.base.changeprop.VersionedValue;
import hana04.shakuyaku.scene.Scene;

public interface ScenePreparer {
  void prepare(Scene scene);

  interface Vv extends VersionedValue<ScenePreparer> {
    // NO-OP
  }
}
