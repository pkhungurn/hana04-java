package hana04.yuri.film.interfaces;

import hana04.yuri.film.FilmStorage;

public interface CanRecordRgbFilmStorage extends FilmStorage {
  CanRecordRgbFilmBlock getFilmBlock(int offsetX, int offsetY, int sizeX, int sizeY);
}
