package hana04.mikumikubake.surface;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.formats.mmd.pmd.PmdModel;
import hana04.mikumikubake.TypeIds;
import hana04.shakuyaku.surface.mmd.geometry.MmdGeometry;

@HanaDeclareObject(
  parent = MmdGeometry.class,
  typeId = TypeIds.TYPE_ID_DIRECT_PMD_BASE_GEOMETRY,
  typeNames = {"mikumikubake.DirectPmdBaseGeometry", "DirectPmdBaseGeometry"})
public interface DirectPmdBaseGeometry extends MmdGeometry {
  @HanaProperty(1)
  PmdModel model();
}
