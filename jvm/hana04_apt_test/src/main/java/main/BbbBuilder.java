package main;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(Bbb.class)
public class BbbBuilder extends Bbb__Impl__Builder<BbbBuilder> {
  @Inject
  public BbbBuilder(Bbb__ImplFactory factory) {
    super(factory);
  }

  public static BbbBuilder builder(Component component) {
    return component.uberFactory().create(BbbBuilder.class);
  }
}