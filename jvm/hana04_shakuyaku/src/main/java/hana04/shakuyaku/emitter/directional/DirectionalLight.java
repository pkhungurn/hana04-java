package hana04.shakuyaku.emitter.directional;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.gfxtype.Transform;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.emitter.Emitter;

@HanaDeclareObject(
  parent = Emitter.class,
  typeId = TypeIds.TYPE_ID_DIRECTION_LIGHT,
  typeNames = {"shakuyaku.DirectionalLight", "DirectionalLight"})
public interface DirectionalLight extends Emitter {
  @HanaProperty(1)
  Variable<Transform> toWorld();

  @HanaProperty(2)
  Variable<Spectrum> radiance();

  @HanaProperty(3)
  Variable<Double> samplingWeight();
}
