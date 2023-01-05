package hana04.shakuyaku.texture.twodim.image;

import hana04.base.util.FileIo;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.spectrum.util.SrgbUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageIoTextureData implements ImageTextureData {
  private final BufferedImage bufferedImage;

  public ImageIoTextureData(Path path) {
    InputStream stream = null;
    if (!Files.exists(path)) {
      throw new RuntimeException("File '" + path + "' does not exist!");
    }
    try {
      stream = FileIo.loadFileAndExposeAsInputStream(path);
      bufferedImage = ImageIO.read(stream);
      stream.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    if (bufferedImage == null) {
      throw new RuntimeException("Something is really wrong here!");
    }
  }

  @Override
  public int getWidth() {
    return bufferedImage.getWidth();
  }

  @Override
  public int getHeight() {
    return bufferedImage.getHeight();
  }

  @Override
  public Spectrum getSpectrum(int x, int y) {
    int[] rgbaArray = new int[4];
    int rgba = bufferedImage.getRGB(x, bufferedImage.getHeight() - y - 1);
    SrgbUtil.unpackRgba(rgba, rgbaArray);
    return new Rgb(SrgbUtil.srgbToLinear(rgbaArray[0] / 255.0),
      SrgbUtil.srgbToLinear(rgbaArray[1] / 255.0),
      SrgbUtil.srgbToLinear(rgbaArray[2] / 255.0));
  }

  @Override
  public double getAlpha(int x, int y) {
    int[] rgbaArray = new int[4];
    int rgba = bufferedImage.getRGB(x, bufferedImage.getHeight() - y - 1);
    SrgbUtil.unpackRgba(rgba, rgbaArray);
    return rgbaArray[3] / 255.0;
  }
}
