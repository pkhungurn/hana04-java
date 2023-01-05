package hana04.shakuyaku.surface;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(ConcretePatchInterval.class)
public class ConcretePatchIntervalBuilder extends ConcretePatchInterval__Impl__Builder<ConcretePatchIntervalBuilder> {
  @Inject
  public ConcretePatchIntervalBuilder(ConcretePatchInterval__ImplFactory factory) {
    super(factory);
  }

  public static ConcretePatchIntervalBuilder builder(Component component) {
      return component.uberFactory().create(ConcretePatchIntervalBuilder.class);
  }
}
