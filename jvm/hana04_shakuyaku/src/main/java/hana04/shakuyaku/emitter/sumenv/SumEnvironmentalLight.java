package hana04.shakuyaku.emitter.sumenv;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.Variable;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.emitter.Emitter;

import java.util.List;

@HanaDeclareObject(
    parent = Emitter.class,
    typeId = TypeIds.TYPE_ID_SUM_ENVIRONMENT_LIGHT,
    typeNames = {"shakuyaku.SumEnvironmentalLight", "SumEnvironmentalLight"})
public interface SumEnvironmentalLight extends Emitter {
  @HanaProperty(1)
  Variable<List<Wrapped<Emitter>>> emitters();

  @HanaProperty(2)
  Variable<Double> samplingWeight();
}
