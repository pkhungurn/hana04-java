package hana04.yuri.film;

import hana04.base.changeprop.VersionedValue;

/**
 * Abstracts the storage of the content of a {@link Film}.
 */
public interface FilmStorage {
  /**
   * Save the content of the film to a file.
   */
  void save(String fileName);

  /**
   * Create a {@link FilmBlock} to the given offset coordinates and 2D size.
   */
  FilmBlock getFilmBlock(int offsetX, int offsetY, int sizeX, int sizeY);

  /**
   * Incorporate the information in the given film block into the film.
   */
  void put(FilmBlock block);

  /**
   * Return the horizontal size, in pixels, of the film.
   */
  int getSizeX();

  /**
   * Return the vertical size, in pixels, of the film.
   */
  int getSizeY();

  interface Vv extends VersionedValue<FilmStorage> {
    // NO-OP
  }
}
