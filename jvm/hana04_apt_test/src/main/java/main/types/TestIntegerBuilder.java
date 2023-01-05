package main.types;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(TestInteger.class)
public class TestIntegerBuilder extends TestInteger__Impl__Builder<TestIntegerBuilder> {
  @Inject
  public TestIntegerBuilder(TestInteger__ImplFactory factory) {
    super(factory);
    intConst(10);
  }

  public static TestIntegerBuilder builder(Component component) {
    return component.uberFactory().create(TestIntegerBuilder.class);
  }
}