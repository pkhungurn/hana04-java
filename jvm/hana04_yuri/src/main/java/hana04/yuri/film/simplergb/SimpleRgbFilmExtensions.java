package hana04.yuri.film.simplergb;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.yuri.film.FilmStorage;

import java.nio.file.FileSystem;

public class SimpleRgbFilmExtensions {
  public static class StorageVv extends DerivedVersionedValue<FilmStorage> implements FilmStorage.Vv {
    @HanaDeclareExtension(
      extensibleClass = SimpleRgbFilm.class,
      extensionClass = FilmStorage.Vv.class)
    public StorageVv(SimpleRgbFilm film, FileSystem fileSystem) {
      super(
          ImmutableList.of(film.width(), film.height()),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> new SimpleRgbFilmStorage(film.width().value(), film.height().value(), fileSystem));
    }
  }
}
