package hana04.yuri.bsdf.shader;

import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.SpectrumSpace;
import hana04.gfxbase.spectrum.SpectrumTransform;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.spectrum.rgb.RgbSpace;
import hana04.yuri.texture.twodim.TextureTwoDimAlphaEvaluator;
import hana04.yuri.texture.twodim.TextureTwoDimEvaluator;
import hana04.yuri.texture.twodim.specspaces.TextureTwoDimEvaluatorRgb;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

public class ScaleAmbientLightShader<T extends Spectrum, V extends SpectrumTransform<T>>
  implements AmbientLightShader<T, V> {
  private final V radianceScale;
  private final double alphaScale;
  private final TextureTwoDimEvaluator<T> radianceTextureEvaluator;
  private final TextureTwoDimAlphaEvaluator alphaTextureEvaluator;
  private final SpectrumSpace<T, V> ss;

  public ScaleAmbientLightShader(TextureTwoDimEvaluator<T> radianceTextureEvaluator,
                                 T radianceScale,
                                 TextureTwoDimAlphaEvaluator alphaTextureEvaluator,
                                 double alphaScale,
                                 SpectrumSpace<T, V> ss) {
    this.radianceScale = ss.createTransform(radianceScale);
    this.alphaScale = alphaScale;
    this.radianceTextureEvaluator = radianceTextureEvaluator;
    this.alphaTextureEvaluator = alphaTextureEvaluator;
    this.ss = ss;
  }

  @Override
  public V shade(Vector3d wi, Vector2d uv) {
    V transform = ss.mul(radianceScale, ss.createTransform(radianceTextureEvaluator.eval(uv)));
    return ss.scale(alphaScale * alphaTextureEvaluator.eval(uv), transform);
  }

  public static class ForRgb extends ScaleAmbientLightShader<Rgb, Rgb> implements AmbientLightShader.ForRgb {
    public ForRgb(
      TextureTwoDimEvaluatorRgb radianceTextureEvaluator,
      Spectrum radianceScale,
      TextureTwoDimAlphaEvaluator alphaTextureEvaluator,
      double alphaScale) {
      super(radianceTextureEvaluator, RgbSpace.I.convert(radianceScale), alphaTextureEvaluator, alphaScale, RgbSpace.I);
    }
  }
}
