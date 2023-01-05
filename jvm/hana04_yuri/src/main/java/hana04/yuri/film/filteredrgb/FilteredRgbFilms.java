package hana04.yuri.film.filteredrgb;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.changeprop.util.VvTransform;
import hana04.base.extension.validator.Validator;
import hana04.yuri.film.FilmStorage;
import hana04.yuri.rfilter.GaussianFilters;
import hana04.yuri.rfilter.ReconstructionFilter;
import hana04.yuri.rfilter.ReconstructionFilters;

import javax.inject.Inject;
import javax.inject.Provider;
import java.nio.file.FileSystem;

public class FilteredRgbFilms {
  public static class FilterEvaluatorVv
    extends VvTransform<ReconstructionFilter, ReconstructionFilters.Evaluator> {
    @HanaDeclareExtension(
      extensibleClass = FilteredRgbFilm.class,
      extensionClass = FilterEvaluatorVv.class)
    public FilterEvaluatorVv(FilteredRgbFilm film) {
      super(film.filter(), filter -> filter.getExtension(ReconstructionFilters.Evaluator.Vv.class));
    }
  }

  public static class StorageVv extends DerivedVersionedValue<FilmStorage> implements FilmStorage.Vv {
    @HanaDeclareExtension(
      extensibleClass = FilteredRgbFilm.class,
      extensionClass = FilmStorage.Vv.class)
    public StorageVv(FilteredRgbFilm film, FileSystem fileSystem) {
      super(
        ImmutableList.of(film.width(), film.height(), film.getExtension(FilterEvaluatorVv.class)),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> new FilteredRgbFilmStorage(
          film.width().value(), film.height().value(),
          film.getExtension(FilterEvaluatorVv.class).value(), fileSystem
        ));
    }
  }

  @HanaDeclareBuilder(FilteredRgbFilm.class)
  public static class FilteredRgbFilmBuilder extends FilteredRgbFilm__Impl__Builder<FilteredRgbFilmBuilder> {
    @Inject
    public FilteredRgbFilmBuilder(FilteredRgbFilm__ImplFactory factory,
        Provider<GaussianFilters.GaussianFilterBuilder> gaussianFilterBuilder) {
      super(factory);
      width(512);
      height(512);
      filter(gaussianFilterBuilder.get().build());
    }

    public static FilteredRgbFilmBuilder builder(Component component) {
      return component.uberFactory().create(FilteredRgbFilmBuilder.class);
    }
  }

  public static class FilteredRgbFilmValidator implements Validator {
      private final FilteredRgbFilm instance;

      @HanaDeclareExtension(
          extensibleClass = FilteredRgbFilm.class,
          extensionClass = Validator.class)
      public FilteredRgbFilmValidator(FilteredRgbFilm instance) {
          this.instance = instance;
      }

      @Override
      public void validate() {
        Preconditions.checkArgument(instance.width().value() > 0);
        Preconditions.checkArgument(instance.height().value() > 0);
      }
  }
}
