package hana04.yuri.trial.t01;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;
import hana04.yuri.sampler.IndependentSamplerBuilder;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.UUID;

@HanaDeclareBuilder(RenderFractalImageToFilmRequest.class)
public class RenderFractalImageToFilmRequestBuilder
    extends RenderFractalImageToFilmRequest__Impl__Builder<RenderFractalImageToFilmRequestBuilder> {
  @Inject
  public RenderFractalImageToFilmRequestBuilder(
      RenderFractalImageToFilmRequest__ImplFactory factory,
      Provider<IndependentSamplerBuilder> independentSamplerBuilder) {
    super(factory);
    uuid(UUID.randomUUID());
    centerX(0.0);
    centerY(0.0);
    scale(1.0);
    sampler(independentSamplerBuilder.get().build());
  }

  public static RenderFractalImageToFilmRequestBuilder builder(Component component) {
    return component.uberFactory().create(RenderFractalImageToFilmRequestBuilder.class);
  }
}
