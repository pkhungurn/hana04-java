package hana04.shakuyaku.surface.mmd.geometry;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.filesystem.FilePath;
import hana04.shakuyaku.TypeIds;

@HanaDeclareObject(
  parent = MmdGeometry.class,
  typeId = TypeIds.TYPE_ID_MMD_BASE_GEOMETRY,
  typeNames = {"shakuyaku.MmdBaseGeometry", "MmdBaseGeometry"})
public interface MmdBaseGeometry extends MmdGeometry {
  @HanaProperty(1)
  FilePath modelFilePath();
}
