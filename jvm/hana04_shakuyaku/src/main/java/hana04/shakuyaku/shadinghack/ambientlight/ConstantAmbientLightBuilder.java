package hana04.shakuyaku.shadinghack.ambientlight;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(ConstantAmbientLight.class)
public class ConstantAmbientLightBuilder extends ConstantAmbientLight__Impl__Builder<ConstantAmbientLightBuilder> {
  @Inject
  public ConstantAmbientLightBuilder(ConstantAmbientLight__ImplFactory factory) {
    super(factory);
  }

  public static ConstantAmbientLightBuilder builder(Component component) {
    return component.uberFactory().create(ConstantAmbientLightBuilder.class);
  }
}
