package hana04.shakuyaku.bsdf.classes.smoothdielectric;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.bsdf.Bsdf;

@HanaDeclareObject(
  parent = Bsdf.class,
  typeId = TypeIds.TYPE_ID_SMOOTH_DIELECTRIC_BSDF,
  typeNames = {"shakuyaku.SmoothDielectricBsdf", "SmoothDielectricBsdf"})
public interface SmoothDielectricBsdf extends Bsdf {
  // Interior index of refraction (default: BK7 borosilicate optical glass).
  @HanaProperty(1)
  Variable<Double> intIor();

  // External idex of refraction (default: air).
  @HanaProperty(2)
  Variable<Double> extIor();

}
