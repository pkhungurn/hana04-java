package hana04.yuri.integrand.position;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.yuri.TypeIds;
import hana04.yuri.integrand.SensorIntegrand;

@HanaDeclareObject(
  parent = SensorIntegrand.class,
  typeId = TypeIds.TYPE_ID_SURFACE_POSITION_INTEGRAND,
  typeNames = {"shakuyaku.SurfacePositionIntegrand", "SurfacePositionIntegrand"})
public interface SurfacePositionIntegrand extends SensorIntegrand {
  @HanaProperty(1)
  Variable<Double> infinityDistance();
}
