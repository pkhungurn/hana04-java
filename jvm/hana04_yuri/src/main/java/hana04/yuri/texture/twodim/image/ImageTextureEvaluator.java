package hana04.yuri.texture.twodim.image;

import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.SpectrumSpace;
import hana04.gfxbase.spectrum.SpectrumTransform;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.spectrum.rgb.RgbSpace;
import hana04.gfxbase.util.MathUtil;
import hana04.shakuyaku.texture.twodim.image.ImageTextureData;
import hana04.yuri.texture.twodim.TextureTwoDimEvaluator;
import hana04.shakuyaku.texture.twodim.WrapMode;
import hana04.yuri.texture.twodim.specspaces.TextureTwoDimEvaluatorRgb;

import javax.vecmath.Tuple2d;

public abstract class ImageTextureEvaluator<T extends Spectrum, V extends SpectrumTransform<T>>
  implements TextureTwoDimEvaluator<T> {
  private final SpectrumSpace<T, V> ss;
  private final WrapMode wrapS;
  private final WrapMode wrapT;
  private final ImageTextureData textureData;

  ImageTextureEvaluator(ImageTextureData textureData, WrapMode wrapS, WrapMode wrapT, SpectrumSpace<T, V> ss) {
    this.ss = ss;
    this.textureData = textureData;
    this.wrapS = wrapS;
    this.wrapT = wrapT;
  }

  @Override
  public T eval(Tuple2d uv) {
    double xx = uv.x * textureData.getWidth();
    double yy = uv.y * textureData.getHeight();
    double dx = xx - Math.floor(xx);
    double dy = yy - Math.floor(yy);
    int ix = (int) Math.floor(xx);
    int iy = (int) Math.floor(yy);
    ix = (dx < 0.5) ? ix - 1 : ix;
    iy = (dy < 0.5) ? iy - 1 : iy;
    dx = (dx < 0.5) ? dx + 0.5 : dx - 0.5;
    dy = (dy < 0.5) ? dy + 0.5 : dy - 0.5;

    T v00 = ss.convert(getPixel(ix, iy, textureData));
    T v01 = ss.convert(getPixel(ix, iy + 1, textureData));
    T v10 = ss.convert(getPixel(ix + 1, iy, textureData));
    T v11 = ss.convert(getPixel(ix + 1, iy + 1, textureData));

    return ss.add(
      ss.add(ss.scale((1 - dx) * (1 - dy), v00), ss.scale((dx) * (1 - dy), v10)),
      ss.add(ss.scale((1 - dx) * (dy), v01), ss.scale((dx) * (dy), v11)));
  }

  private Spectrum getPixel(int x, int y, ImageTextureData textureData) {
    if (wrapS == WrapMode.CLAMP) {
      x = MathUtil.clamp(x, 0, textureData.getWidth() - 1);
    } else {
      x = MathUtil.mod(x, textureData.getWidth());
    }
    if (wrapT == WrapMode.CLAMP) {
      y = MathUtil.clamp(y, 0, textureData.getHeight() - 1);
    } else {
      y = MathUtil.mod(y, textureData.getHeight());
    }
    return textureData.getSpectrum(x, y);
  }

  public static class ForRgb extends ImageTextureEvaluator<Rgb, Rgb> implements TextureTwoDimEvaluatorRgb {
    public ForRgb(ImageTextureData textureData, WrapMode wrapS, WrapMode wrapT) {
      super(textureData, wrapS, wrapT, RgbSpace.I);
    }
  }
}
