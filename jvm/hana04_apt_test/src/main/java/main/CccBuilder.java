package main;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(Ccc.class)
public class CccBuilder extends Ccc__Impl__Builder<CccBuilder> {
  @Inject
  public CccBuilder(Ccc__ImplFactory factory) {
    super(factory);
    intVar(0);
    floatValue(1.0f);
  }

  public static CccBuilder builder(Component component) {
    return component.uberFactory().create(CccBuilder.class);
  }
}
