package hana04.yuri.texture.twodim.image;

import hana04.gfxbase.util.MathUtil;
import hana04.shakuyaku.texture.twodim.image.ImageTextureData;
import hana04.yuri.texture.twodim.TextureTwoDimAlphaEvaluator;
import hana04.shakuyaku.texture.twodim.WrapMode;

import javax.vecmath.Tuple2d;

public class ImageTextureAlphaEvaluator implements TextureTwoDimAlphaEvaluator {
  private final ImageTextureData textureData;
  private final WrapMode wrapS;
  private final WrapMode wrapT;

  public ImageTextureAlphaEvaluator(ImageTextureData textureData, WrapMode wrapS, WrapMode wrapT) {
    this.textureData = textureData;
    this.wrapS = wrapS;
    this.wrapT = wrapT;
  }

  @Override
  public double eval(Tuple2d uv) {
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

    double v00 = getPixel(ix, iy, textureData);
    double v01 = getPixel(ix, iy + 1, textureData);
    double v10 = getPixel(ix + 1, iy, textureData);
    double v11 = getPixel(ix + 1, iy + 1, textureData);

    return (1 - dx) * (1 - dy) * v00 + (dx) * (1 - dy) * v10 + (1 - dx) * (dy) * v01 + (dx) * (dy) * v11;
  }

  private double getPixel(int x, int y, ImageTextureData textureData) {
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
    return textureData.getAlpha(x, y);
  }
}
