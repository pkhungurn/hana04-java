package hana04.yuri.film.zirr;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.extension.validator.Validator;
import hana04.yuri.film.FilmStorage;

import javax.inject.Inject;
import java.nio.file.FileSystem;

public class ZirrFireflyReweightingFilms {
  public static class StorageVv extends DerivedVersionedValue<FilmStorage> implements FilmStorage.Vv {
    @HanaDeclareExtension(
        extensibleClass = ZirrFireflyReweightingFilm.class,
        extensionClass = FilmStorage.Vv.class)
    public StorageVv(ZirrFireflyReweightingFilm film, FileSystem fileSystem) {
      super(
          ImmutableList.of(
              film.exponentBase(),
              film.lowestExponent(),
              film.highestExponent(),
              film.sampleCount(),
              film.outlierCount(),
              film.minCount(),
              film.width(),
              film.height()),
          ChangePropUtil::largestBetweenIncSelfAndDeps,
          () -> new ZirrFireflyReweighingFilmStorage(film, fileSystem));
    }
  }

  @HanaDeclareBuilder(ZirrFireflyReweightingFilm.class)
  public static class ZirrFireflyReweightingFilmBuilder
      extends ZirrFireflyReweightingFilm__Impl__Builder<ZirrFireflyReweightingFilmBuilder> {
    @Inject
    public ZirrFireflyReweightingFilmBuilder(ZirrFireflyReweightingFilm__ImplFactory factory) {
      super(factory);
      exponentBase(8.0);
      lowestExponent(-2);
      highestExponent(6);
      outlierCount(32.0);
      minCount(1.0);
      width(512);
      height(512);
    }

    public static ZirrFireflyReweightingFilmBuilder builder(Component component) {
      return component.uberFactory().create(ZirrFireflyReweightingFilmBuilder.class);
    }
  }

  public static class ZirrFireflyReweightingFilmValidator implements Validator {
    private final ZirrFireflyReweightingFilm instance;

    @HanaDeclareExtension(
        extensibleClass = ZirrFireflyReweightingFilm.class,
        extensionClass = Validator.class)
    public ZirrFireflyReweightingFilmValidator(ZirrFireflyReweightingFilm instance) {
      this.instance = instance;
    }

    @Override
    public void validate() {
      Preconditions.checkArgument(instance.exponentBase().value() > 1.0);
      Preconditions.checkArgument(instance.lowestExponent().value() <= 0);
      Preconditions.checkArgument(instance.highestExponent().value() > instance.lowestExponent().value());
      Preconditions.checkArgument(instance.minCount().value() >= 1.0);
      Preconditions.checkArgument(instance.outlierCount().value() > instance.minCount().value());
      Preconditions.checkArgument(instance.width().value() >= 1);
      Preconditions.checkArgument(instance.height().value() >= 1);
    }
  }
}
