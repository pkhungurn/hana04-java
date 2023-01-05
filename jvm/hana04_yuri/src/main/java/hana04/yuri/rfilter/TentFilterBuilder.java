package hana04.yuri.rfilter;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(TentFilter.class)
public class TentFilterBuilder extends TentFilter__Impl__Builder<TentFilterBuilder> {
  @Inject
  public TentFilterBuilder(TentFilter__ImplFactory factory) {
    super(factory);
  }

  public static TentFilterBuilder builder(Component component) {
    return component.uberFactory().create(TentFilterBuilder.class);
  }
}
