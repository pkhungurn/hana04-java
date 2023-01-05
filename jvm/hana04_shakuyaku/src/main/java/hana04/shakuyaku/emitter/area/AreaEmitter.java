package hana04.shakuyaku.emitter.area;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.emitter.Emitter;

@HanaDeclareObject(
  parent = Emitter.class,
  typeId = TypeIds.TYPE_ID_AREA_EMITTER,
  typeNames = {"shakuyaku.AreaEmitter", "AreaEmitter"})
public interface AreaEmitter extends Emitter {
  @HanaProperty(1)
  Variable<Spectrum> radiance();

  @HanaProperty(2)
  Variable<Double> samplingWeight();
}