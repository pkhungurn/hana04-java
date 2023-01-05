package hana04.yuri.scene.standard;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.VersionedValue;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.SpectrumTransform;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.shakuyaku.bsdf.Bsdf;
import hana04.shakuyaku.scene.standard.StandardScene;
import hana04.yuri.bsdf.BsdfEvaluator;
import hana04.yuri.bsdf.BsdfSampler;
import hana04.yuri.bsdf.specspaces.BsdfEvaluatorRgb;
import hana04.yuri.bsdf.specspaces.BsdfSamplerRgb;
import hana04.yuri.scene.SceneSurfaceShading;
import hana04.yuri.scene.specspaces.SceneSurfaceShadingRgb;
import hana04.shakuyaku.surface.PatchInterval;
import hana04.shakuyaku.surface.Surface;
import hana04.shakuyaku.surface.SurfacePatchIntervalInfo;

public abstract class StandardSceneSurfaceShading<T extends Spectrum, V extends SpectrumTransform<T>>
  implements SceneSurfaceShading<T, V> {

  StandardSceneSurfaceShading(StandardScene node,
                              Class<? extends VersionedValue<? extends BsdfEvaluator<T, V>>> bsdfEvaluatorClass,
                              Class<? extends VersionedValue<? extends BsdfSampler<T, V>>> bsdfSamplerClass,
                              HanaUnwrapper unwrapper) {
    for (Wrapped<Surface> wrappedSurface : node.surface().value()) {
      Surface surface = wrappedSurface.unwrap(unwrapper);
      SurfacePatchIntervalInfo info = surface.getExtension(SurfacePatchIntervalInfo.Vv.class).value();
      for (int i = 0; i < info.getPatchIntervalCount(); i++) {
        PatchInterval patchInterval = info.getPatchInterval(i);
        Bsdf bsdf = patchInterval.bsdf().unwrap(unwrapper);
        bsdf.prepareExtension(bsdfEvaluatorClass);
        bsdf.prepareExtension(bsdfSamplerClass);
      }
    }
  }

  public static class ForRgb extends StandardSceneSurfaceShading<Rgb, Rgb> implements SceneSurfaceShadingRgb {
    @HanaDeclareExtension(
      extensibleClass = StandardScene.class,
      extensionClass = SceneSurfaceShadingRgb.class)
    public ForRgb(StandardScene standardScene, HanaUnwrapper unwrapper) {
      super(standardScene, BsdfEvaluatorRgb.Vv.class, BsdfSamplerRgb.Vv.class, unwrapper);
    }
  }
}
