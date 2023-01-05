package hana04.mikumikubake.opengl.renderable.mmdsurface;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(MmdSurfaceWireframe.class)
public class MmdSurfaceWireframeBuilder extends MmdSurfaceWireframe__Impl__Builder<MmdSurfaceWireframeBuilder> {
  @Inject
  public MmdSurfaceWireframeBuilder(MmdSurfaceWireframe__ImplFactory factory) {
    super(factory);
  }

  public static MmdSurfaceWireframeBuilder builder(Component component) {
    return component.uberFactory().create(MmdSurfaceWireframeBuilder.class);
  }
}
