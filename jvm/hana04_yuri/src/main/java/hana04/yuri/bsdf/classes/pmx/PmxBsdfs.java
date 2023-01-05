package hana04.yuri.bsdf.classes.pmx;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.changeprop.util.VvProxy;
import hana04.base.changeprop.util.VvTransform;
import hana04.shakuyaku.bsdf.classes.pmx.PmxBsdf;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;
import hana04.yuri.bsdf.shader.AmbientLightShader;
import hana04.yuri.bsdf.shader.ScaleAmbientLightShader;
import hana04.yuri.bsdf.specspaces.BsdfEvaluatorRgb;
import hana04.yuri.bsdf.specspaces.BsdfSamplerRgb;
import hana04.yuri.texture.twodim.TextureTwoDimAlphaEvaluator;
import hana04.yuri.texture.twodim.specspaces.TextureTwoDimEvaluatorRgb;

public class PmxBsdfs {
  public static class EvaluatorRgbVv extends VvProxy<BsdfEvaluatorRgb> implements BsdfEvaluatorRgb.Vv {
    @HanaDeclareExtension(
      extensibleClass = PmxBsdf.class,
      extensionClass = BsdfEvaluatorRgb.Vv.class)
    EvaluatorRgbVv(PmxBsdf node) {
      super(node.getExtension(hana04.shakuyaku.bsdf.classes.pmx.PmxBsdfs.InternalBsdfVv.class).value().getExtension(BsdfEvaluatorRgb.Vv.class));
    }
  }

  public static class SamplerRgbVv extends VvProxy<BsdfSamplerRgb> implements BsdfSamplerRgb.Vv {
    @HanaDeclareExtension(
      extensibleClass = PmxBsdf.class,
      extensionClass = BsdfSamplerRgb.Vv.class)
    SamplerRgbVv(PmxBsdf node) {
      super(node.getExtension(hana04.shakuyaku.bsdf.classes.pmx.PmxBsdfs.InternalBsdfVv.class).value().getExtension(BsdfSamplerRgb.Vv.class));
    }
  }

  public static class BaseTextureEvaluatorRgbVv extends VvTransform<TextureTwoDim, TextureTwoDimEvaluatorRgb> {
    @HanaDeclareExtension(
      extensibleClass = PmxBsdf.class,
      extensionClass = BaseTextureEvaluatorRgbVv.class)
    BaseTextureEvaluatorRgbVv(PmxBsdf node) {
      super(
        node.getExtension(hana04.shakuyaku.bsdf.classes.pmx.PmxBsdfs.UnwrappedTextureVv.class),
        texture -> texture.getExtension(TextureTwoDimEvaluatorRgb.Vv.class));
    }
  }

  public static class BaseTextureAlphaEvaluatorVv extends VvTransform<TextureTwoDim, TextureTwoDimAlphaEvaluator> {
    @HanaDeclareExtension(
      extensibleClass = PmxBsdf.class,
      extensionClass = BaseTextureAlphaEvaluatorVv.class)
    BaseTextureAlphaEvaluatorVv(PmxBsdf node) {
      super(
        node.getExtension(hana04.shakuyaku.bsdf.classes.pmx.PmxBsdfs.UnwrappedTextureVv.class),
        texture -> texture.getExtension(TextureTwoDimAlphaEvaluator.Vv.class));
    }
  }

  public static class AmbientLightShaderRgbVv
    extends DerivedVersionedValue<AmbientLightShader.ForRgb>
    implements AmbientLightShader.ForRgb.Vv {
    @HanaDeclareExtension(
      extensibleClass = PmxBsdf.class,
      extensionClass = AmbientLightShader.ForRgb.Vv.class)
    AmbientLightShaderRgbVv(PmxBsdf node) {
      super(
        ImmutableList.of(),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> new ScaleAmbientLightShader.ForRgb(
          node.getExtension(BaseTextureEvaluatorRgbVv.class).value(),
          node.ambientReflectance().value(),
          node.getExtension(BaseTextureAlphaEvaluatorVv.class).value(),
          node.alpha().value())
      );
    }
  }
}
