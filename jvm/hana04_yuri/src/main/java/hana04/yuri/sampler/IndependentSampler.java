package hana04.yuri.sampler;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.yuri.TypeIds;

@HanaDeclareObject(
    parent = Sampler.class,
    typeId = TypeIds.TYPE_ID_INDEPENDENT_SAMPLER,
    typeNames = {"shakuyaku.IndependentSampler", "IndependentSampler"})
public interface IndependentSampler extends Sampler {
  @HanaProperty(1)
  Variable<Integer> sampleCount();
}