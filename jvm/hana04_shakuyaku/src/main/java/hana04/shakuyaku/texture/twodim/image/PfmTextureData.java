package hana04.shakuyaku.texture.twodim.image;

import hana04.gfxbase.pfm.Pfm;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.rgb.Rgb;

import java.nio.file.Path;

public class PfmTextureData implements ImageTextureData {
  private final Pfm pfm;

  public PfmTextureData(Path path) {
    pfm = Pfm.load(path);
  }

  @Override
  public int getWidth() {
    return pfm.width;
  }

  @Override
  public int getHeight() {
    return pfm.height;
  }

  @Override
  public Spectrum getSpectrum(int x, int y) {
    Rgb rgb = new Rgb();
    pfm.getColor(x, y, rgb);
    return rgb;
  }

  @Override
  public double getAlpha(int x, int y) {
    return 1.0;
  }
}
