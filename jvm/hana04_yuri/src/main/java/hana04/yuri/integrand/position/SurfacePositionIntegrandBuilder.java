package hana04.yuri.integrand.position;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(SurfacePositionIntegrand.class)
public class SurfacePositionIntegrandBuilder
    extends SurfacePositionIntegrand__Impl__Builder<SurfacePositionIntegrandBuilder> {
  @Inject
  public SurfacePositionIntegrandBuilder(SurfacePositionIntegrand__ImplFactory factory) {
    super(factory);
  }

  public static SurfacePositionIntegrandBuilder builder(Component component) {
    return component.uberFactory().create(SurfacePositionIntegrandBuilder.class);
  }
}
