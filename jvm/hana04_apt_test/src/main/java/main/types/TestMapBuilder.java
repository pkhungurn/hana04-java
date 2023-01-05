package main.types;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(TestMap.class)
public class TestMapBuilder extends TestMap__Impl__Builder<TestMapBuilder> {
  @Inject
  public TestMapBuilder(TestMap__ImplFactory factory) {
    super(factory);
  }

  public static TestMapBuilder builder(Component component) {
    return component.uberFactory().create(TestMapBuilder.class);
  }
}