package hana04.shakuyaku.shadinghack.ambientlight;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.shadinghack.ShadingHack;

@HanaDeclareObject(
  parent = ShadingHack.class,
  typeId = TypeIds.TYPE_ID_CONSTANT_AMBIENT_LIGHT,
  typeNames = {"shakuyaku.ConstantAmbientLight", "ConstantAmbientLight"})
public interface ConstantAmbientLight extends ShadingHack {
  @HanaProperty(1)
  Variable<Spectrum> intensity();
}
