package hana04.yuri.request.onepassrender;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;
import hana04.yuri.sampler.IndependentSamplerBuilder;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.UUID;

@HanaDeclareBuilder(OnePassRenderRequest.class)
public class OnePassRenderRequestBuilder extends OnePassRenderRequest__Impl__Builder<OnePassRenderRequestBuilder> {
  @Inject
  public OnePassRenderRequestBuilder(
      OnePassRenderRequest__ImplFactory factory,
      Provider<IndependentSamplerBuilder> independentSamplerBuilder) {
    super(factory);
    uuid(UUID.randomUUID());
    sampler(independentSamplerBuilder.get().build());
  }

  public static OnePassRenderRequestBuilder builder(Component component) {
    return component.uberFactory().create(OnePassRenderRequestBuilder.class);
  }
}
