package hana04.mikumikubake.opengl.renderable.ugposcolmesh;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.base.Component;

import javax.inject.Inject;

@HanaDeclareBuilder(UgPosColMesh.class)
public class UgPosColMeshBuilder extends UgPosColMesh__Impl__Builder<UgPosColMeshBuilder> {
  @Inject
  public UgPosColMeshBuilder(UgPosColMesh__ImplFactory factory) {
    super(factory);
  }

  public static UgPosColMeshBuilder builder(Component component) {
    return component.uberFactory().create(UgPosColMeshBuilder.class);
  }
}
