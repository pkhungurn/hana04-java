package hana04.shakuyaku.bsdf.classes.pmx;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.Variable;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.bsdf.Bsdf;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;

@HanaDeclareObject(
  parent = Bsdf.class,
  typeId = TypeIds.TYPE_ID_PMX_BSDF,
  typeNames = {"shakuyaku.PmxBsdf", "PmxBsdf"})
public interface PmxBsdf extends Bsdf {
  @HanaProperty(1)
  Variable<Spectrum> ambientReflectance();

  @HanaProperty(2)
  Variable<Spectrum> diffuseReflectance();

  @HanaProperty(3)
  Variable<Wrapped<TextureTwoDim>> texture();

  @HanaProperty(4)
  Variable<Double> alpha();

  @HanaProperty(5)
  Variable<Boolean> displayBothSides();

  @HanaProperty(6)
  Variable<Boolean> isOpaque();

  @HanaProperty(7)
  Variable<Boolean> drawEdge();

  @HanaProperty(8)
  Variable<Double> edgeThickness();

  @HanaProperty(9)
  Variable<Rgb> edgeColor();
}
