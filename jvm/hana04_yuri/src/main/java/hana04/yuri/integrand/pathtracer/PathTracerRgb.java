package hana04.yuri.integrand.pathtracer;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.yuri.TypeIds;

import java.util.Optional;

@HanaDeclareObject(
  parent = PathTracer.class,
  typeId = TypeIds.TYPE_ID_PATH_TRACER_RGB,
  typeNames = {"shakuyaku.PathTracerRgb", "PathTracerRgb"})
public interface PathTracerRgb extends PathTracer {
  @HanaProperty(1)
  Variable<Double> terminationProb();

  @HanaProperty(2)
  Variable<Optional<Integer>> minDepth();

  @HanaProperty(3)
  Variable<Optional<Integer>> maxDepth();

  @HanaProperty(4)
  Variable<Boolean> computeAmbientIllumination();
}
