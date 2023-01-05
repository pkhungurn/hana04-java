package hana04.yuri.texture.twodim.checker;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;
import hana04.yuri.TypeIds;

@HanaDeclareObject(
    parent = TextureTwoDim.class,
    typeId = TypeIds.TYPE_ID_CHECKER_TWO_DIM_TEXTURE,
    typeNames = {"shakuyaku.CheckerTwoDimTexture", "CheckerTwoDimTexture"})
public interface CheckerTwoDimTexture extends TextureTwoDim {
  @HanaProperty(1)
  Variable<Spectrum> evenSpectrum();

  @HanaProperty(2)
  Variable<Spectrum> oddSpectrum();

  @HanaProperty(3)
  Variable<Double> uCellSize();

  @HanaProperty(4)
  Variable<Double> vCellSize();
}
