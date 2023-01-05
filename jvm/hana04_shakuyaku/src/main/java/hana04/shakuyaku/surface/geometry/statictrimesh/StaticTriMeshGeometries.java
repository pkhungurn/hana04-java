package hana04.shakuyaku.surface.geometry.statictrimesh;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.Constant;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.filesystem.FilePath;
import hana04.shakuyaku.surface.SurfacePatchInfo;
import hana04.shakuyaku.surface.geometry.XformedPatchInfoFactory;
import hana04.shakuyaku.surface.geometry.trimesh.FileTriangleMeshInfoVv;
import hana04.shakuyaku.surface.geometry.trimesh.TriangleMeshInfo;
import hana04.shakuyaku.surface.geometry.trimesh.XformedTriMeshPatchInfo;

import javax.inject.Inject;
import java.nio.file.FileSystem;

public class StaticTriMeshGeometries {
  public static class TriangleMeshInfoVv extends FileTriangleMeshInfoVv {
    @HanaDeclareExtension(
      extensibleClass = StaticTriMeshGeometry.class,
      extensionClass = TriangleMeshInfo.Vv.class)
    TriangleMeshInfoVv(StaticTriMeshGeometry geometry, FileSystem fileSystem) {
      super(new Constant<>(geometry.filePath()), fileSystem);
    }
  }

  public static class SurfacePatchInfoVv
    extends DerivedVersionedValue<SurfacePatchInfo>
    implements SurfacePatchInfo.Vv {
    @HanaDeclareExtension(
      extensibleClass = StaticTriMeshGeometry.class,
      extensionClass = SurfacePatchInfo.Vv.class)
    SurfacePatchInfoVv(StaticTriMeshGeometry geometry) {
      super(
        ImmutableList.of(geometry.getExtension(TriangleMeshInfo.Vv.class)),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> geometry.getExtension(TriangleMeshInfo.Vv.class).value());
    }
  }

  public static class PatchInfoFactoryVv
    extends DerivedVersionedValue<XformedPatchInfoFactory>
    implements XformedPatchInfoFactory.Vv {
    @HanaDeclareExtension(
      extensibleClass = StaticTriMeshGeometry.class,
      extensionClass = XformedPatchInfoFactory.Vv.class)
    PatchInfoFactoryVv(StaticTriMeshGeometry geometry) {
      super(
        ImmutableList.of(geometry.getExtension(TriangleMeshInfo.Vv.class)),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () ->
          toWorld ->
            new XformedTriMeshPatchInfo(geometry.getExtension(TriangleMeshInfo.Vv.class).value(), toWorld));
    }
  }

  @HanaDeclareBuilder(StaticTriMeshGeometry.class)
  public static
  class StaticTriMeshGeometryBuilder extends StaticTriMeshGeometry__Impl__Builder<StaticTriMeshGeometryBuilder> {
    @Inject
    public StaticTriMeshGeometryBuilder(StaticTriMeshGeometry__ImplFactory factory) {
      super(factory);
    }

    public static StaticTriMeshGeometryBuilder builder(Component component) {
      return component.uberFactory().create(StaticTriMeshGeometryBuilder.class);
    }

    public static StaticTriMeshGeometry create(String fileName, Component component) {
      return builder(component)
        .filePath(FilePath.relative(fileName))
        .build();
    }
  }
}
