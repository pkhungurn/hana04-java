package hana04.shakuyaku.bsdf.classes.simpmicrofacet;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.Variable;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.bsdf.Bsdf;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;

@HanaDeclareObject(
  parent = Bsdf.class,
  typeId = TypeIds.TYPE_ID_SIMPLE_MICROFACET_BSDF,
  typeNames = {"shakuyaku.SimpleMicrofacetBsdf", "SimpleMicrofacetBsdf"})
public interface SimpleMicrofacetBsdf extends Bsdf {
  /**
   * The create reflectance
   */
  @HanaProperty(1)
  Variable<Wrapped<TextureTwoDim>> diffuseReflectance();

  /**
   * Surface roughness of the specular reflectance.
   */
  @HanaProperty(2)
  Variable<Wrapped<TextureTwoDim>> roughness();

  /**
   * The internal index of reflectance.
   */
  @HanaProperty(3)
  Variable<Double> intIor();

  /**
   * The external index of reflectance.
   */
  @HanaProperty(4)
  Variable<Double> extIor();
}