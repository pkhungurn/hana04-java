package hana04.shakuyaku.surface.mmd.geometry;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.Variable;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.sbtm.extensible.pose.SbtmPoseAsset;

@HanaDeclareObject(
  parent = MmdGeometry.class,
  typeId = TypeIds.TYPE_ID_MMD_POSED_GEOMETRY,
  typeNames = {"shakuyaku.MmdPosedGeometry", "MmdPosedGeometry"})
public interface MmdPosedGeometry extends MmdGeometry {
  @HanaProperty(1)
  Wrapped<MmdGeometry> base();

  @HanaProperty(2)
  Variable<Wrapped<SbtmPoseAsset>> pose();
}
