package hana04.yuri.film.filteredrgb;

import hana04.base.util.TypeUtil;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.gfxtype.TupleUtil;
import hana04.gfxbase.gfxtype.WireSerializationUtil;
import hana04.yuri.film.FilmRecorder;
import hana04.yuri.film.interfaces.CanRecordRgbFilmBlock;
import hana04.yuri.film.recorder.RgbFilmRecorder;
import hana04.yuri.film.recorder.SimpleRgbFilmRecorder;
import hana04.yuri.rfilter.ReconstructionFilters;

import javax.vecmath.Point2d;
import javax.vecmath.Vector4d;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class FilteredRgbFilmBlock implements CanRecordRgbFilmBlock {
  private final int offsetX;
  private final int offsetY;
  private final int sizeX;
  private final int sizeY;
  final int borderSize;
  private final double filterRadius;
  private final ReconstructionFilters.Evaluator filterEvaluator;
  final Vector4d[][] data;

  FilteredRgbFilmBlock(int offsetX, int offsetY, int sizeX, int sizeY,
                       ReconstructionFilters.Evaluator filterEvaluator) {
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    this.filterEvaluator = filterEvaluator;
    this.filterRadius = filterEvaluator.getRadius();
    this.borderSize = (int) Math.ceil(filterRadius - 0.5);
    this.data = FilteredRgbFilmUtil.allocateStorage(sizeX, sizeY, borderSize);
  }

  @Override
  public RgbFilmRecorder getFilmRecorder(double x, double y) {
    return new SimpleRgbFilmRecorder(x, y);
  }

  @Override
  public void put(FilmRecorder recorder) {
    Rgb value = TypeUtil.cast(recorder, RgbFilmRecorder.class).getValue();
    double x = recorder.getX();
    double y = recorder.getY();

    if (TupleUtil.isNaN(value)) {
      throw new RuntimeException("Computed an invalid value: " + value.toString());
    }

    /* Convert to pixel coordinates within the image block */
    Point2d pos = new Point2d(x - 0.5 - (offsetX - borderSize),
      y - 0.5 - (offsetY - borderSize));

    /* Compute the rectangle of pixels that will need to be updated */
    int minX = Math.max((int) Math.ceil(pos.x - filterRadius), 0);
    int minY = Math.max((int) Math.ceil(pos.y - filterRadius), 0);
    int maxX = Math.min((int) Math.floor(pos.x + filterRadius), data[0].length - 1);
    int maxY = Math.min((int) Math.floor(pos.y + filterRadius), data.length - 1);

    for (int _y = minY; _y <= maxY; ++_y) {
      double weightY = filterEvaluator.eval(_y - pos.y);
      for (int _x = minX; _x <= maxX; ++_x) {
        double weightX = filterEvaluator.eval(_x - pos.x);
        data[_y][_x].x += value.x * weightX * weightY;
        data[_y][_x].y += value.y * weightX * weightY;
        data[_y][_x].z += value.z * weightX * weightY;
        data[_y][_x].w += weightX * weightY;
      }
    }
  }

  @Override
  public int getOffsetX() {
    return offsetX;
  }

  @Override
  public int getOffsetY() {
    return offsetY;
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
  public void serializeContent(DataOutputStream stream) {
    try {
      for (Vector4d[] datum : data) {
        for (Vector4d vector4d : datum) {
          WireSerializationUtil.writeTuple4d(stream, vector4d);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deserializeContent(DataInputStream stream) {
    try {
      for (Vector4d[] datum : data) {
        for (Vector4d vector4d : datum) {
          WireSerializationUtil.readTuple4d(stream, vector4d);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
