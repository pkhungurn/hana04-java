package hana04.yuri.integrand.directillum;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.yuri.TypeIds;

/**
 * Surface only direct illumination integrand.
 */
@HanaDeclareObject(
  parent = DirectIlluminationIntegrand.class,
  typeId = TypeIds.TYPE_ID_DIRECT_ILLUMINATION_INTEGRAND_RGB,
  typeNames = {"shakuyaku.DirectIlluminationIntegrandRgb", "DirectIlluminationIntegrandRgb"})
public interface DirectIlluminationIntegrandRgb extends DirectIlluminationIntegrand {
  @HanaProperty(1)
  Variable<String> strategy();
}