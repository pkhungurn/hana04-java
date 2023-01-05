package hana04.yuri.trial.t00;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import dagger.Binds;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.extension.validator.Validator;
import hana04.distrib.request.Request;
import hana04.distrib.request.params.RequestParametersBuilder;
import hana04.yuri.film.simplergb.SimpleRgbFilmBuilder;
import hana04.yuri.request.params.BlockRendererParameters;
import hana04.yuri.sampler.IndependentSamplerBuilder;

import javax.inject.Inject;
import javax.inject.Provider;
import java.nio.file.FileSystem;
import java.util.UUID;

public class FractalImageRequests {
  private FractalImageRequests() {
    // NO-OP
  }

  public static class RequestRunnerVv
      extends DerivedVersionedValue<Request.Runner> implements Request.Runner.Vv {
    @HanaDeclareExtension(
        extensibleClass = FractalImageRequest.class,
        extensionClass = Request.Runner.Vv.class)
    public RequestRunnerVv(FractalImageRequest request,
        FileSystem fileSystem,
        Provider<SimpleRgbFilmBuilder> simpleRgbFilmBuilderProvider,
        Provider<IndependentSamplerBuilder> independentSamplerBuilderProvider) {
      super(
          ImmutableList.of(
              request.imageWidth(),
              request.imageHeight(),
              request.centerX(),
              request.centerY(),
              request.scale(),
              request.sampleCount()),
          ChangePropUtil::largestBetweenIncSelfAndDeps,
          () -> new FractalImageRequestRunner(
              request, fileSystem, simpleRgbFilmBuilderProvider,
              independentSamplerBuilderProvider));
    }
  }

  @HanaDeclareBuilder(FractalImageRequest.class)
  public static class FractalImageRequestBuilder
      extends FractalImageRequest__Impl__Builder<FractalImageRequestBuilder> {
    @Inject
    FractalImageRequestBuilder(FractalImageRequest__ImplFactory factory___) {
      super(factory___);
      uuid(UUID.randomUUID());
      imageWidth(512);
      imageHeight(512);
      centerX(0.0);
      centerY(0.0);
      scale(1.0);
      sampleCount(512);
    }

    public static FractalImageRequestBuilder builder(Component component) {
      return component.uberFactory().create(FractalImageRequestBuilder.class);
    }
  }

  public static class FractalImageRequestValidator implements Validator {
    private final FractalImageRequest instance;

    @HanaDeclareExtension(
        extensibleClass = FractalImageRequest.class,
        extensionClass = Validator.class)
    public FractalImageRequestValidator(FractalImageRequest instance) {
      this.instance = instance;
    }

    @Override
    public void validate() {
      Preconditions.checkState(instance.imageWidth().value() > 0);
      Preconditions.checkState(instance.imageHeight().value() > 0);
      Preconditions.checkState(instance.sampleCount().value() > 0);
    }
  }

  @dagger.Module
  public abstract static class Module {
    @Binds
    @IntoMap
    @StringKey("FractalImageRequest")
    public abstract RequestParametersBuilder provideFractalImageRequestPasrameterBuilder(
        BlockRendererParameters.Builder builder);
  }
}
