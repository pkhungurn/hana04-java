package hana04.mikumikubake.surface;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(DirectPmxBaseGeometry.class)
public class DirectPmxBaseGeometryBuilder extends DirectPmxBaseGeometry__Impl__Builder<DirectPmxBaseGeometryBuilder> {
  @Inject
  public DirectPmxBaseGeometryBuilder(DirectPmxBaseGeometry__ImplFactory factory) {
    super(factory);
  }

  public static DirectPmxBaseGeometryBuilder builder(Component component) {
    return component.uberFactory().create(DirectPmxBaseGeometryBuilder.class);
  }
}
