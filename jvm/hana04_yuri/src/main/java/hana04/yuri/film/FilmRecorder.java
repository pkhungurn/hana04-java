package hana04.yuri.film;

/**
 * Can be used to record information about to a {@link FilmBlock} at a particular point.
 */
public interface FilmRecorder {
  /**
   * The x-coordinate of the point to record information.
   */
  double getX();

  /**
   * The y-coordinate of the point to record information.
   */
  double getY();

  /**
   * Scale the relevant part of the recorded contribution by the given scalar factor.
   */
  void scale(double s);
}
