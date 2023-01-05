package hana04.yuri.film.simplergb;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(SimpleRgbFilm.class)
public class SimpleRgbFilmBuilder extends SimpleRgbFilm__Impl__Builder<SimpleRgbFilmBuilder> {
  @Inject
  public SimpleRgbFilmBuilder(SimpleRgbFilm__ImplFactory factory) {
    super(factory);
    width(512);
    height(512);
  }

  public static SimpleRgbFilmBuilder builder(Component component) {
    return component.uberFactory().create(SimpleRgbFilmBuilder.class);
  }
}
