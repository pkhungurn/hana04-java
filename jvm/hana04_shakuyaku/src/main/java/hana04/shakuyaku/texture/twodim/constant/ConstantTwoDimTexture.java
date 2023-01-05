package hana04.shakuyaku.texture.twodim.constant;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;

/**
 * A texture of constant spectrum color value. It is always opaque.
 */
@HanaDeclareObject(
  parent = TextureTwoDim.class,
  typeId = TypeIds.TYPE_ID_CONSTANT_TWO_DIM_TEXTURE,
  typeNames = {"shakuyaku.ConstantTwoDimTexture", "ConstantTwoDimTexture"})
public interface ConstantTwoDimTexture extends TextureTwoDim {
  @HanaProperty(1)
  Variable<Spectrum> spectrum();

  @HanaProperty(2)
  Variable<Double> alpha();
}