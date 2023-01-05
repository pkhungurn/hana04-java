package hana04.yuri.film.filteredrgb;

import com.google.common.base.Preconditions;
import hana04.gfxbase.pfm.Pfm;
import hana04.yuri.film.FilmBlock;
import hana04.yuri.film.interfaces.CanRecordRgbFilmStorage;
import hana04.yuri.rfilter.ReconstructionFilters;

import javax.vecmath.Vector4d;
import java.nio.file.FileSystem;

class FilteredRgbFilmStorage implements CanRecordRgbFilmStorage {
  private final FileSystem fileSystem;
  private int sizeX;
  private int sizeY;
  private ReconstructionFilters.Evaluator filterEvaluator;
  private int borderSize;
  private Vector4d[][] data;

  FilteredRgbFilmStorage(int sizeX, int sizeY, ReconstructionFilters.Evaluator filterEvaluator, FileSystem fileSystem) {
    this.fileSystem = fileSystem;
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    this.filterEvaluator = filterEvaluator;
    double filterRadius = filterEvaluator.getRadius();
    borderSize = (int) Math.ceil(filterRadius - 0.5);
    data = FilteredRgbFilmUtil.allocateStorage(sizeX, sizeY, borderSize);
  }

  @Override
  public FilteredRgbFilmBlock getFilmBlock(int offsetX, int offsetY, int sizeX, int sizeY) {
    return new FilteredRgbFilmBlock(offsetX, offsetY, sizeX, sizeY, filterEvaluator);
  }

  @Override
  public void put(FilmBlock block) {
    Preconditions.checkArgument(block instanceof FilteredRgbFilmBlock, "block is not an instance of " +
      "FilteredRgbFilmBlock");
    FilteredRgbFilmBlock b = (FilteredRgbFilmBlock) block;

    int offsetX = b.getOffsetX();
    int offsetY = b.getOffsetY();
    int sizeX = b.getSizeX();
    int sizeY = b.getSizeY();
    int borderSize_ = borderSize;
    Vector4d[][] data_ = data;
    int dataSizeY = data_.length;
    int dataSizeX = data_[0].length;

    synchronized (this) {
      for (int y = 0; y < sizeY + 2 * b.borderSize; y++) {
        for (int x = 0; x < sizeX + 2 * b.borderSize; x++) {
          int yy = offsetY + borderSize_ - b.borderSize + y;
          int xx = offsetX + borderSize_ - b.borderSize + x;
          if (yy >= 0 && yy < dataSizeY && xx >= 0 && xx < dataSizeX)
            data_[yy][xx].add(b.data[y][x]);
        }
      }
    }
  }

  @Override
  public int getSizeX() {
    return sizeX;
  }

  @Override
  public int getSizeY() {
    return sizeY;
  }

  @Override
  public void save(String fileName) {
    Pfm pfm;
    synchronized (this) {
      int sizeX_ = sizeX;
      int sizeY_ = sizeY;
      int borderSize_ = borderSize;
      Vector4d[][] data_ = data;
      pfm = new Pfm(sizeX_, sizeY_);
      javax.vecmath.Vector3d color = new javax.vecmath.Vector3d();
      for (int y = 0; y < sizeY_; y++) {
        for (int x = 0; x < sizeX_; x++) {
          Vector4d d = data_[y + borderSize_][x + borderSize_];
          if (d.w != 0) {
            color.x = d.x / d.w;
            color.y = d.y / d.w;
            color.z = d.z / d.w;
          } else {
            color.x = color.y = color.z = 0;
          }
          pfm.setColor(x, y, color);
        }
      }
    }
    try {
      pfm.save(fileSystem.getPath(fileName));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
