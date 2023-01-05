package hana04.yuri.sampler;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(IndependentSampler.class)
public class IndependentSamplerBuilder extends IndependentSampler__Impl__Builder<IndependentSamplerBuilder> {
  @Inject
  public IndependentSamplerBuilder(IndependentSampler__ImplFactory factory) {
    super(factory);
    sampleCount(512);
  }

  public static IndependentSamplerBuilder builder(Component component) {
    return component.uberFactory().create(IndependentSamplerBuilder.class);
  }

  public static IndependentSampler create(int sampleCount, Component component) {
    return builder(component).sampleCount(sampleCount).build();
  }
}
