package hana04.shakuyaku.bsdf.classes.alpha;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.Variable;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.bsdf.Bsdf;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;

import java.util.Optional;

@HanaDeclareObject(
    parent = Bsdf.class,
    typeId = TypeIds.TYPE_ID_ALPHA_BSDF,
    typeNames = {"shakuyaku.AlphaBsdf", "AlphaBsdf"})
public interface AlphaBsdf extends Bsdf {
  @HanaProperty(1)
  Variable<Double> baseAlpha();

  @HanaProperty(2)
  Variable<Optional<Wrapped<TextureTwoDim>>> alphaTexture();

  @HanaProperty(3)
  Variable<Wrapped<Bsdf>> baseBsdf();
}
