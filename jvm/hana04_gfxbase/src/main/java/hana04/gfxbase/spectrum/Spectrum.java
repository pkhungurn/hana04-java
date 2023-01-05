package hana04.gfxbase.spectrum;

import hana04.gfxbase.spectrum.rgb.Rgb;

public interface Spectrum {
  Rgb spectrumToRgb();

  double spectrumAverage();

  double spectrumMaxComponent();

  double spectrumMinComponent();

  boolean isZero();

  boolean isNaN();
}
