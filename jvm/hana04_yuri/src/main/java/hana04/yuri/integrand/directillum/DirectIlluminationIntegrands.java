package hana04.yuri.integrand.directillum;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.Constant;
import hana04.yuri.integrand.ScenePreparer;
import hana04.yuri.scene.SceneRayIntersector;
import hana04.yuri.scene.specspaces.SceneEmitterSamplerRgb;
import hana04.yuri.scene.specspaces.SceneSurfaceShadingRgb;

public class DirectIlluminationIntegrands {
  public static class ScenePreparerVv extends Constant<ScenePreparer> implements ScenePreparer.Vv {
    @HanaDeclareExtension(
      extensibleClass = DirectIlluminationIntegrand.class,
      extensionClass = ScenePreparer.Vv.class)
    ScenePreparerVv(DirectIlluminationIntegrand integrand) {
      super(scene -> {
        scene.prepareExtension(SceneRayIntersector.class);
        scene.prepareExtension(SceneSurfaceShadingRgb.class);
        scene.prepareExtension(SceneEmitterSamplerRgb.class);
      });
    }
  }
}
