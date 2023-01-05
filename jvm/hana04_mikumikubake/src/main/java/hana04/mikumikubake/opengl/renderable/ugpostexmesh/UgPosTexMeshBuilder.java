package hana04.mikumikubake.opengl.renderable.ugpostexmesh;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(UgPosTexMesh.class)
public class UgPosTexMeshBuilder extends UgPosTexMesh__Impl__Builder<UgPosTexMeshBuilder> {
  @Inject
  public UgPosTexMeshBuilder(UgPosTexMesh__ImplFactory factory) {
    super(factory);
  }

  public static UgPosTexMeshBuilder builder(Component component) {
    return component.uberFactory().create(UgPosTexMeshBuilder.class);
  }
}
