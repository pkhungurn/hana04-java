package hana04.yuri.integrand.pathtracer;

import hana04.apt.annotation.HanaDeclareExtensibleInterface;
import hana04.base.changeprop.Variable;
import hana04.yuri.integrand.SensorIntegrand;

import java.util.Optional;

@HanaDeclareExtensibleInterface(SensorIntegrand.class)
public interface PathTracer extends SensorIntegrand {
  Variable<Double> terminationProb();

  Variable<Optional<Integer>> minDepth();

  Variable<Optional<Integer>> maxDepth();

  Variable<Boolean> computeAmbientIllumination();
}
