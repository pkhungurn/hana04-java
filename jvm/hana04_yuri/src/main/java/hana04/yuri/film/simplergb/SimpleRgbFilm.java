package hana04.yuri.film.simplergb;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.yuri.TypeIds;
import hana04.yuri.film.Film;

/**
 * A film that stores one RGB spectrum per pixel and integrates samples
 */
@HanaDeclareObject(
  parent = Film.class,
  typeId = TypeIds.TYPE_ID_SIMPLE_RGB_FILM,
  typeNames = {"shakuyaku.SimpleRgbFilm", "SimpleRgbFilm"})
public interface SimpleRgbFilm extends Film {
  @HanaProperty(1)
  Variable<Integer> width();

  @HanaProperty(2)
  Variable<Integer> height();

}
