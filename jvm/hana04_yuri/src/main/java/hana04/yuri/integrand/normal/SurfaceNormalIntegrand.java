package hana04.yuri.integrand.normal;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.yuri.TypeIds;
import hana04.yuri.integrand.SensorIntegrand;

@HanaDeclareObject(
    parent = SensorIntegrand.class,
    typeId = TypeIds.TYPE_ID_SURFACE_NORMAL_INTEGRAND,
    typeNames = {"shakuyaku.SurfaceNormalIntegrand", "SurfaceNormalIntegrand"})
public interface SurfaceNormalIntegrand extends SensorIntegrand {
  @HanaProperty(1)
  Variable<String> normalToRecord();
}
