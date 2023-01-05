package hana04.yuri.film;

import hana04.distrib.request.workblock.WorkBlock2D;

/**
 * Represents a 2D subblock of a {@link Film}
 */
public interface FilmBlock extends WorkBlock2D {
  /**
   * Get the {@link FilmRecorder} for the given (x,y)-position.
   */
  FilmRecorder getFilmRecorder(double x, double y);

  /**
   * Incorporate the information in the given {@code recorder} to the block.
   */
  void put(FilmRecorder recorder);

  /**
   * Get the offset x-coordinate of the film block.
   */
  int getOffsetX();

  /**
   * Get the offset y-coordinate of the film block.
   */
  int getOffsetY();

  /**
   * Get the horizontal size of the film block.
   */
  int getSizeX();

  /**
   * Get the vertical size of the film block.
   */
  int getSizeY();
}
