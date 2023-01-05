package hana04.yuri.film.recorder;

import hana04.gfxbase.spectrum.Spectrum;
import hana04.yuri.film.FilmRecorder;

public interface SpectrumRecorder<T extends Spectrum> extends FilmRecorder {
  void record(T spectrum);
  T getValue();
}
