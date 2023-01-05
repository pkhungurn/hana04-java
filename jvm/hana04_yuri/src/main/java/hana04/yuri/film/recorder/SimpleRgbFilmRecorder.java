package hana04.yuri.film.recorder;

import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.gfxtype.TupleUtil;

public class SimpleRgbFilmRecorder implements RgbFilmRecorder {
  final Rgb value = new Rgb(0,0,0);
  private final double x;
  private final double y;

  public SimpleRgbFilmRecorder(double x, double y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public double getX() {
    return x;
  }

  @Override
  public double getY() {
    return y;
  }

  @Override
  public void scale(double s) {
    value.scale(s);
  }

  @Override
  public void record(Rgb spectrum) {
    if (TupleUtil.isNaN(spectrum)) {
      throw new RuntimeException("spectrum is NaN!!!");
    }
    value.add(spectrum);
  }

  public Rgb getValue() {
    return value;
  }
}
