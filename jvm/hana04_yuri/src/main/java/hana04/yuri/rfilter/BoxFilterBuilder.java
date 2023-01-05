package hana04.yuri.rfilter;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(BoxFilter.class)
public class BoxFilterBuilder extends BoxFilter__Impl__Builder<BoxFilterBuilder> {
  @Inject
  public BoxFilterBuilder(BoxFilter__ImplFactory factory) {
    super(factory);
  }

  public static BoxFilterBuilder builder(Component component) {
      return component.uberFactory().create(BoxFilterBuilder.class);
  }
}
