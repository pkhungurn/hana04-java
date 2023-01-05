package hana04.shakuyaku.bsdf.classes.passthrough;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.bsdf.Bsdf;

@HanaDeclareObject(
    parent = Bsdf.class,
    typeId = TypeIds.TYPE_ID_PASS_THROUGHT_BSDF,
    typeNames = {"shakuyaku.PassThroughBsdf", "PassThroughBsdf"})
public interface PassThroughBsdf extends Bsdf {
  // NO-OP
}
