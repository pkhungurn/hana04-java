package hana04.gfxbase.spectrum.scalar;

import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.SpectrumTransform;
import hana04.gfxbase.spectrum.rgb.Rgb;

public class Scalar implements Spectrum, SpectrumTransform<Scalar> {
  public static final String TYPE_NAME = "Scalar";

  private double value;

  public Scalar(double value) {
    this.value = value;
  }

  public double toDouble() {
    return value;
  }

  @Override
  public Rgb spectrumToRgb() {
    return new Rgb(value, value, value);
  }

  @Override
  public double spectrumAverage() {
    return value;
  }

  @Override
  public double spectrumMaxComponent() {
    return value;
  }

  @Override
  public double spectrumMinComponent() {
    return value;
  }

  @Override
  public boolean isZero() {
    return value == 0;
  }

  @Override
  public boolean isNaN() {
    return Double.isNaN(value);
  }
}
