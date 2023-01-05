package hana04.shakuyaku.bsdf.classes.smoothdielectric;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.changeprop.Constant;
import hana04.shakuyaku.bsdf.BsdfBasicProperties;

import javax.inject.Inject;

public class SmoothDielectricBsdfs {
  public static class BasicPropertiesVv
    extends Constant<BsdfBasicProperties>
    implements BsdfBasicProperties.Vv {
    @HanaDeclareExtension(
      extensibleClass = SmoothDielectricBsdf.class,
      extensionClass = BsdfBasicProperties.Vv.class)
    public BasicPropertiesVv(SmoothDielectricBsdf bsdf) {
      super(new BsdfBasicProperties() {
        @Override
        public boolean containsPassThrough() {
          return false;
        }

        @Override
        public boolean allowsTransmission() {
          return true;
        }
      });
    }
  }

  @HanaDeclareBuilder(SmoothDielectricBsdf.class)
  public static class SmoothDielectricBsdfBuilder extends SmoothDielectricBsdf__Impl__Builder<SmoothDielectricBsdfBuilder> {
    @Inject
    public SmoothDielectricBsdfBuilder(SmoothDielectricBsdf__ImplFactory factory) {
      super(factory);
      intIor(1.5046);
      extIor(1.000277);
    }

    public static SmoothDielectricBsdfBuilder builder(Component component) {
      return component.uberFactory().create(SmoothDielectricBsdfBuilder.class);
    }
  }
}
