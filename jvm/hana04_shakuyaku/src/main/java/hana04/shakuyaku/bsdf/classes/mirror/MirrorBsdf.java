package hana04.shakuyaku.bsdf.classes.mirror;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.bsdf.Bsdf;

@HanaDeclareObject(
  parent = Bsdf.class,
  typeId = TypeIds.TYPE_ID_MIRROR_BSDF,
  typeNames = {"shakuyaku.MirrorBsdf", "MirrorBsdf"})
public interface MirrorBsdf extends Bsdf {
  // NO-OP
}
