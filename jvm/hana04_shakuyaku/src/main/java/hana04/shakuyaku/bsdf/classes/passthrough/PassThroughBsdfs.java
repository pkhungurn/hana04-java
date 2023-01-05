package hana04.shakuyaku.bsdf.classes.passthrough;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;
import hana04.shakuyaku.bsdf.BsdfBasicProperties;

import javax.inject.Inject;

public class PassThroughBsdfs {
  public static class BasicProperties implements BsdfBasicProperties {
    @Override
    public boolean containsPassThrough() {
      return true;
    }

    @Override
    public boolean allowsTransmission() {
      return true;
    }
  }

  @HanaDeclareBuilder(PassThroughBsdf.class)
  public static class PassThroughBsdfBuilder extends PassThroughBsdf__Impl__Builder<PassThroughBsdfBuilder> {
    @Inject
    public PassThroughBsdfBuilder(PassThroughBsdf__ImplFactory factory) {
      super(factory);
    }

    public static PassThroughBsdfBuilder builder(Component component) {
      return component.uberFactory().create(PassThroughBsdfBuilder.class);
    }
  }
}
