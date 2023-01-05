package hana04.yuri.film.zirr;

import com.google.common.base.Preconditions;
import hana04.gfxbase.pfm.Pfm;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.util.MathUtil;
import hana04.yuri.film.FilmBlock;
import hana04.yuri.film.interfaces.CanRecordRgbFilmBlock;
import hana04.yuri.film.interfaces.CanRecordRgbFilmStorage;

import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.List;

public class ZirrFireflyReweighingFilmStorage implements CanRecordRgbFilmStorage {
  private int cascadeCount;
  private int width;
  private int height;
  private double exponentBase;
  private int lowestExponent;
  private int highestExponent;
  private int sampleCount;
  private double outlierCount;
  private double minCount;
  private final List<CascadeBuffers> cascades = new ArrayList<>();
  private final FileSystem fileSystem;

  ZirrFireflyReweighingFilmStorage(ZirrFireflyReweightingFilm film, FileSystem fileSystem) {
    this.width = film.width().value();
    this.height = film.height().value();

    cascadeCount = film.highestExponent().value() - film.lowestExponent().value() + 1;
    for (int i = 0; i < cascadeCount; i++) {
      cascades.add(new CascadeBuffers(width, height));
    }

    this.exponentBase = film.exponentBase().value();
    this.lowestExponent = film.lowestExponent().value();
    this.highestExponent = film.highestExponent().value();
    this.sampleCount = film.sampleCount().value();
    this.outlierCount = film.outlierCount().value();
    this.minCount = film.minCount().value();
    this.fileSystem = fileSystem;
  }

  @Override
  public CanRecordRgbFilmBlock getFilmBlock(int offsetX, int offsetY, int sizeX, int sizeY) {
    Preconditions.checkArgument(sizeX > 0);
    Preconditions.checkArgument(sizeY > 0);
    return new ZirrFireflyReweightingFilmBlock(
      offsetX, offsetY, sizeX, sizeY,
      exponentBase,
      lowestExponent,
      highestExponent);
  }

  @Override
  public void put(FilmBlock filmBlock) {
    Preconditions.checkArgument(filmBlock instanceof ZirrFireflyReweightingFilmBlock);
    ZirrFireflyReweightingFilmBlock block = (ZirrFireflyReweightingFilmBlock) filmBlock;
    for (int i = 0; i < cascadeCount; i++) {
      cascades.get(i).put(block.cascades.get(i), block.getOffsetX(), block.getOffsetY());
    }
  }

  @Override
  public int getSizeX() {
    return width;
  }

  @Override
  public int getSizeY() {
    return height;
  }

  private double getAverageCount(int cascadeIndex, int x, int y) {
    double sum = 0;
    double count = 0;
    for (int dy = -1; dy <= 1; dy++) {
      if (dy < 0 || dy >= height) {
        continue;
      }
      for (int dx = -1; dx <= 1; dx++) {
        if (dx < 0 || dx >= width) {
          continue;
        }
        count++;
        sum += cascades.get(cascadeIndex).getCount(x, y);
      }
    }
    return sum / count;
  }

  @Override
  public void save(String fileName) {
    Pfm pfm = new Pfm(width, height);

    Rgb sum = new Rgb(0, 0, 0);
    Rgb rgb = new Rgb(0, 0, 0);
    double lowerBound = 0;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        sum.set(0, 0, 0);
        for (int casecadeIndex = 0; casecadeIndex < cascadeCount; casecadeIndex++) {
          double count = getAverageCount(casecadeIndex, x, y);
          if (count <= minCount) {
            continue;
          }
          if (casecadeIndex == 0) {
            cascades.get(0).getRgb(x, y, sum);
            sum.scale(MathUtil.clamp((count - minCount) / outlierCount, 0, 1));
          } else {
            cascades.get(casecadeIndex).getRgb(x, y, rgb);
            double s1 = (count - minCount) / outlierCount;
            double s2 = sampleCount * lowerBound / outlierCount / rgb.getLuminance();
            double weight = MathUtil.clamp(Math.max(s1, s2), 0, 1);
            rgb.scale(weight);
            sum.add(rgb);
          }
          lowerBound = sum.getLuminance();
        }
        sum.scale(1.0 / sampleCount);
        pfm.setColor(x, y, sum);
      }
    }

    pfm.save(fileSystem.getPath(fileName));
  }
}
