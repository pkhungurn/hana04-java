package hana04.shakuyaku.bsdf.classes.twosided;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.Variable;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.bsdf.Bsdf;

import java.util.List;

@HanaDeclareObject(
    parent = Bsdf.class,
    typeId = TypeIds.TYPE_ID_TWO_SIDED_BSDF,
    typeNames = {"shakuyaku.TwoSidedBsdf", "TwoSidedBsdf"})
public interface TwoSidedBsdf extends Bsdf {
  @HanaProperty(1)
  Variable<List<Wrapped<Bsdf>>> side();
}
