package hana04.mikumikubake.surface;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(DirectPmdBaseGeometry.class)
public class DirectPmdBaseGeometryBuilder extends DirectPmdBaseGeometry__Impl__Builder<DirectPmdBaseGeometryBuilder> {
  @Inject
  public DirectPmdBaseGeometryBuilder(DirectPmdBaseGeometry__ImplFactory factory) {
    super(factory);
  }

  public static DirectPmdBaseGeometryBuilder builder(Component component) {
    return component.uberFactory().create(DirectPmdBaseGeometryBuilder.class);
  }
}
