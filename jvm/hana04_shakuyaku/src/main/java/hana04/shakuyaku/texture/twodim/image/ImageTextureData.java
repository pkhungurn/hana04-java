package hana04.shakuyaku.texture.twodim.image;

import hana04.base.changeprop.VersionedValue;
import hana04.base.filesystem.FilePath;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.rgb.Rgb;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.FileSystem;
import java.nio.file.Path;

public interface ImageTextureData {
  int getWidth();

  int getHeight();

  Spectrum getSpectrum(int x, int y);

  double getAlpha(int x, int y);

  static ImageTextureData createImageTextureData(
    FilePath filePath,
    FileSystem fileSystem) {
    return createImageTextureData(filePath, fileSystem, true);
  }

  static ImageTextureData createImageTextureData(
    FilePath filePath,
    FileSystem fileSystem,
    boolean defaultToBlacImage) {
    String fileName = filePath.storedPath.trim();
    String extension = FilenameUtils.getExtension(fileName).toLowerCase();
    Path path = fileSystem.getPath(fileName);
    if (extension.equals("pfm")) {
      return new PfmTextureData(path);
    }
    try {
      return new ImageIoTextureData(path);
    } catch (Exception e) {
      if (defaultToBlacImage) {
        return new BlackImage();
      } else {
        throw e;
      }
    }
  }

  interface Vv extends VersionedValue<ImageTextureData> {
    // NO-OP
  }

  class BlackImage implements ImageTextureData {

    @Override
    public int getWidth() {
      return 2;
    }

    @Override
    public int getHeight() {
      return 2;
    }

    @Override
    public Spectrum getSpectrum(int x, int y) {
      return new Rgb(0, 0, 0);
    }

    @Override
    public double getAlpha(int x, int y) {
      return 1;
    }
  }
}
