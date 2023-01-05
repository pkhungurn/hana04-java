package hana04.yuri.film.zirr;

import com.google.common.base.Preconditions;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.yuri.film.FilmRecorder;
import hana04.yuri.film.interfaces.CanRecordRgbFilmBlock;
import hana04.yuri.film.recorder.RgbFilmRecorder;
import hana04.yuri.film.recorder.SimpleRgbFilmRecorder;
import hana04.yuri.film.recorder.SpectrumRecorder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ZirrFireflyReweightingFilmBlock implements CanRecordRgbFilmBlock {
  private int sizeX;
  private int sizeY;
  private int offsetX;
  private int offsetY;
  private double exponentBase;
  private int lowestExponent;
  private int highestExponent;
  private int cascadeCount;
  List<CascadeBuffers> cascades = new ArrayList<>();
  private double highestLuminance;
  private double logExponentBase;


  public ZirrFireflyReweightingFilmBlock(int offsetX, int offsetY, int sizeX, int sizeY,
                                         double exponentBase,
                                         int lowestExponent, int highestExponent) {
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    Preconditions.checkArgument(this.sizeX > 0);
    Preconditions.checkArgument(this.sizeY > 0);
    this.exponentBase = exponentBase;
    this.lowestExponent = lowestExponent;
    this.highestExponent = highestExponent;
    this.cascadeCount = highestExponent - lowestExponent + 1;

    for (int i = 0; i < cascadeCount; i++) {
      cascades.add(new CascadeBuffers(sizeX, sizeY));
    }

    logExponentBase = Math.log(exponentBase);
    highestLuminance = Math.pow(exponentBase, highestExponent);
  }

  @Override
  public SpectrumRecorder<Rgb> getFilmRecorder(double x, double y) {
    return new SimpleRgbFilmRecorder(x, y);
  }

  @Override
  public void put(FilmRecorder recorder) {
    int x = (int) Math.floor(recorder.getX());
    int y = (int) Math.floor(recorder.getY());
    Preconditions.checkState(x >= offsetX && x < offsetX + sizeX && y >= offsetY && y < offsetY + sizeY);
    x -= offsetX;
    y -= offsetY;

    Preconditions.checkArgument(recorder instanceof RgbFilmRecorder);
    RgbFilmRecorder rgbFilmRecorder = (RgbFilmRecorder) recorder;
    Rgb rgb = rgbFilmRecorder.getValue();

    double luminance = rgb.getLuminance();
    double cascadeIndex = Math.log(luminance) / logExponentBase;
    if (cascadeIndex < lowestExponent) {
      cascades.get(0).addWeightedRgb(x, y, rgb, 1.0);
    } else if (cascadeIndex > highestExponent) {
      cascades.get(cascadeCount - 1).addWeightedRgb(x, y, rgb, highestLuminance / rgb.getLuminance());
    } else {
      int j = (int) Math.floor(cascadeIndex);
      double lowerLuminance = Math.pow(exponentBase, j);
      double lowerWeight = (lowerLuminance / luminance - 1.0 / exponentBase) / (1 - 1.0 / exponentBase);
      assert lowerLuminance < j && lowerLuminance * exponentBase > j;
      cascades.get(j - lowestExponent).addWeightedRgb(x, y, rgb, lowerWeight);
      cascades.get(j + 1 - lowestExponent).addWeightedRgb(x, y, rgb, 1.0 - lowerWeight);
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
    for (int i = 0; i < cascadeCount; i++) {
      cascades.get(i).serialize(stream);
    }
  }

  @Override
  public void deserializeContent(DataInputStream stream) {
    for (int i = 0; i < cascadeCount; i++) {
      cascades.get(i).deserialize(stream);
    }
  }
}
