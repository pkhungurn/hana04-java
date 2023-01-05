package hana04.yuri.film.simplergb;

import com.google.common.base.Preconditions;
import hana04.gfxbase.pfm.Pfm;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.yuri.film.FilmBlock;
import hana04.yuri.film.interfaces.CanRecordRgbFilmStorage;

import javax.vecmath.Vector4d;
import java.nio.file.FileSystem;
import java.nio.file.Path;

public class SimpleRgbFilmStorage implements CanRecordRgbFilmStorage {
  private int width;
  private int height;
  private FileSystem fileSystem;
  private Vector4d[][] data;

  public SimpleRgbFilmStorage(int width, int height, FileSystem fileSystem) {
    this.fileSystem = fileSystem;

    this.width = width;
    this.height = height;
    this.data = new Vector4d[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        data[y][x] = new Vector4d(0, 0, 0, 0);
      }
    }
  }

  @Override
  public void save(String fileName) {
    Pfm pfm = new Pfm(width, height);
    Rgb rgb = new Rgb();
    Vector4d[][] dataValue = data;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        Vector4d dataItem = dataValue[y][x];
        if (dataItem.w != 0) {
          rgb.set(dataItem.x / dataItem.w, dataItem.y / dataItem.w, dataItem.z / dataItem.w);
        } else {
          rgb.set(0, 0, 0);
        }
        pfm.setColor(x, y, rgb);
      }
    }
    Path path = fileSystem.getPath(fileName);
    pfm.save(path);
  }

  @Override
  public SimpleRgbFilmBlock getFilmBlock(int offsetX, int offsetY, int sizeX, int sizeY) {
    return new SimpleRgbFilmBlock(offsetX, offsetY, sizeX, sizeY);
  }

  @Override
  public void put(FilmBlock block) {
    Preconditions.checkState(block instanceof SimpleRgbFilmBlock);
    SimpleRgbFilmBlock simpleRgbFilmBlock = (SimpleRgbFilmBlock) block;
    int offsetX = block.getOffsetX();
    int offsetY = block.getOffsetY();
    int sizeX = block.getSizeX();
    int sizeY = block.getSizeY();
    Preconditions.checkState(inRangeX(offsetX) && inRangeX(offsetX + sizeX - 1) && sizeX > 0);
    Preconditions.checkState(inRangeY(offsetY) && inRangeY(offsetY + sizeY - 1) && sizeY > 0);
    Vector4d[][] dataValue = data;
    for (int dy = 0; dy < sizeY; dy++) {
      for (int dx = 0; dx < sizeX; dx++) {
        dataValue[offsetY + dy][offsetX + dx].add(simpleRgbFilmBlock.data[dy][dx]);
      }
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

  private boolean inRangeX(int x) {
    return x >= 0 && x < width;
  }

  private boolean inRangeY(int y) {
    return y >= 0 && y < height;
  }
}
