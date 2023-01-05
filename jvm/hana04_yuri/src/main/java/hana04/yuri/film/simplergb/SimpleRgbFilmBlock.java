package hana04.yuri.film.simplergb;

import com.google.common.base.Preconditions;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.gfxtype.WireSerializationUtil;
import hana04.yuri.film.FilmRecorder;
import hana04.yuri.film.interfaces.CanRecordRgbFilmBlock;
import hana04.yuri.film.recorder.SimpleRgbFilmRecorder;
import hana04.yuri.film.recorder.SpectrumRecorder;

import javax.vecmath.Vector4d;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class SimpleRgbFilmBlock implements CanRecordRgbFilmBlock {
  private final int offsetX;
  private final int offsetY;
  private final int sizeX;
  private final int sizeY;
  Vector4d[][] data;

  public SimpleRgbFilmBlock(int offsetX, int offsetY, int sizeX, int sizeY) {
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    data = new Vector4d[sizeY][sizeX];
    for (int y = 0; y < sizeY; y++) {
      for (int x = 0; x < sizeX; x++) {
        data[y][x] = new Vector4d();
      }
    }
  }

  @Override
  public SpectrumRecorder<Rgb> getFilmRecorder(double x, double y) {
    return new SimpleRgbFilmRecorder(x, y);
  }

  @Override
  public synchronized void put(FilmRecorder recorder) {
    int x = (int)Math.floor(recorder.getX());
    int y = (int)Math.floor(recorder.getY());
    Preconditions.checkState(x >= offsetX && x < offsetX + sizeX && y >= offsetY && y < offsetY + sizeY);
    Vector4d toModify = data[y - offsetY][x - offsetX];
    SpectrumRecorder<Rgb> rgbRecorder = (SpectrumRecorder<Rgb>)recorder;
    Rgb rgb =  rgbRecorder.getValue();
    toModify.x += rgb.x;
    toModify.y += rgb.y;
    toModify.z += rgb.z;
    toModify.w += 1;
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
