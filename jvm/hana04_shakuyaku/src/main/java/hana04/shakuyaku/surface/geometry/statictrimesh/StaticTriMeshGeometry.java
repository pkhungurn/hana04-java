package hana04.shakuyaku.surface.geometry.statictrimesh;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.filesystem.FilePath;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.surface.geometry.SurfaceGeometry;

@HanaDeclareObject(
  parent = SurfaceGeometry.class,
  typeId = TypeIds.TYPE_ID_STATIC_TRI_MESH_GEOMETRY,
  typeNames = {"shakuyaku.StaticTriMeshGeometry", "StaticTriMeshGeometry"})
public interface StaticTriMeshGeometry extends SurfaceGeometry {
  @HanaProperty(1)
  FilePath filePath();

}