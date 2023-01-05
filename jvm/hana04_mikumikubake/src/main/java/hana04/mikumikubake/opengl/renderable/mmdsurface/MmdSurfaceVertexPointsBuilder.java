package hana04.mikumikubake.opengl.renderable.mmdsurface;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(MmdSurfaceVertexPoints.class)
public class MmdSurfaceVertexPointsBuilder
    extends MmdSurfaceVertexPoints__Impl__Builder<MmdSurfaceVertexPointsBuilder> {
  @Inject
  public MmdSurfaceVertexPointsBuilder(MmdSurfaceVertexPoints__ImplFactory factory) {
    super(factory);
  }

  public static MmdSurfaceVertexPointsBuilder builder(Component component) {
    return component.uberFactory().create(MmdSurfaceVertexPointsBuilder.class);
  }
}
