package hana04.mikumikubake.surface;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.formats.mmd.pmx.PmxModel;
import hana04.mikumikubake.TypeIds;
import hana04.shakuyaku.surface.mmd.geometry.MmdGeometry;

@HanaDeclareObject(
  parent = MmdGeometry.class,
  typeId = TypeIds.TYPE_ID_DIRECT_PMX_BASE_GEOMETRY,
  typeNames = {"mikumikubake.DirectPmxBaseGeometry", "DirectPmxBaseGeometry"})
public interface DirectPmxBaseGeometry extends MmdGeometry {
  @HanaProperty(1)
  PmxModel model();
}
