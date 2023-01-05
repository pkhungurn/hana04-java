package hana04.yuri.film.interfaces;

import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.yuri.film.FilmBlock;
import hana04.yuri.film.recorder.SpectrumRecorder;

public interface CanRecordRgbFilmBlock extends FilmBlock {
  SpectrumRecorder<Rgb> getFilmRecorder(double x, double y);
}
