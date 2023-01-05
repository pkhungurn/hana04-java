package hana04.shakuyaku.texture.twodim.arithmetic;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.Variable;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;

@HanaDeclareObject(
  parent = TextureTwoDim.class,
  typeId = TypeIds.TYPE_ID_SCALE_TWO_DIM_TEXTURE,
  typeNames = {"shakuyaku.ScaleTwoDimTexture", "ScaleTwoDimTexture"})
public interface ScaleTwoDimTexture extends TextureTwoDim {
  @HanaProperty(1)
  Variable<Spectrum> spectrumScale();

  @HanaProperty(2)
  Variable<Double> alphaScale();

  @HanaProperty(3)
  Variable<Wrapped<TextureTwoDim>> texture();
}
