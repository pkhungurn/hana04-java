package hana04.yuri.film.filteredrgb;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.yuri.TypeIds;
import hana04.yuri.film.Film;
import hana04.yuri.rfilter.ReconstructionFilter;

@HanaDeclareObject(
    parent = Film.class,
    typeId = TypeIds.TYPE_ID_FILTERED_RGB_FILM,
    typeNames = {"shakuyaku.FilteredRgbFilm", "FilteredRgbFilm"})
public interface FilteredRgbFilm extends Film {
  @HanaProperty(1)
  Variable<Integer> width();

  @HanaProperty(2)
  Variable<Integer> height();

  @HanaProperty(3)
  Variable<ReconstructionFilter> filter();
}