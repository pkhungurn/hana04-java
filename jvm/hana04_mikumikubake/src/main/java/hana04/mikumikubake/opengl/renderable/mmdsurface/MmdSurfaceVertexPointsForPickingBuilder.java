package hana04.mikumikubake.opengl.renderable.mmdsurface;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(MmdSurfaceVertexPointsForPicking.class)
public class MmdSurfaceVertexPointsForPickingBuilder
    extends MmdSurfaceVertexPointsForPicking__Impl__Builder<MmdSurfaceVertexPointsForPickingBuilder> {
  @Inject
  public MmdSurfaceVertexPointsForPickingBuilder(MmdSurfaceVertexPointsForPicking__ImplFactory factory) {
    super(factory);
  }

  public static MmdSurfaceVertexPointsForPickingBuilder builder(Component component) {
    return component.uberFactory().create(MmdSurfaceVertexPointsForPickingBuilder.class);
  }
}
