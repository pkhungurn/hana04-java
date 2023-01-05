package hana04.yuri.integrand.directillum;

import hana04.apt.annotation.HanaDeclareExtensibleInterface;
import hana04.base.changeprop.Variable;
import hana04.yuri.integrand.SensorIntegrand;

@HanaDeclareExtensibleInterface(SensorIntegrand.class)
public interface DirectIlluminationIntegrand extends SensorIntegrand {
  Variable<String> strategy();
}
