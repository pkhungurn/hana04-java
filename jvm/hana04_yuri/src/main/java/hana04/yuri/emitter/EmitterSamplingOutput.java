package hana04.yuri.emitter;

import hana04.gfxbase.spectrum.Spectrum;

public class EmitterSamplingOutput<T extends Spectrum> extends EmitterPdfInput {
  /**
   * The radiance value divided by the inverse of the PDF of the sample.
   */
  public T value;
}
