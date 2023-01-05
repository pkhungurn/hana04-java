package hana04.yuri.integrand.directillum;

import com.google.common.base.Preconditions;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(DirectIlluminationIntegrandRgb.class)
public class DirectIlluminationIntegrandRgbBuilder
    extends DirectIlluminationIntegrandRgb__Impl__Builder<DirectIlluminationIntegrandRgbBuilder> {
  @Inject
  public DirectIlluminationIntegrandRgbBuilder(DirectIlluminationIntegrandRgb__ImplFactory factory) {
    super(factory);
    strategy("mis");
  }

  public DirectIlluminationIntegrandRgbBuilder strategy(String value) {
    Preconditions.checkArgument(value.toLowerCase().equals("emitter")
        || value.toLowerCase().equals("bsdf")
        || value.toLowerCase().equals("mis"));
    super.strategy(value);
    return this;
  }

  public static DirectIlluminationIntegrandRgbBuilder builder(Component component) {
    return component.uberFactory().create(DirectIlluminationIntegrandRgbBuilder.class);
  }
}
