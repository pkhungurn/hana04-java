package hana04.yuri.rfilter;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.yuri.TypeIds;

@HanaDeclareObject(
  parent = ReconstructionFilter.class,
  typeId = TypeIds.TYPE_ID_BOX_FILTER,
  typeNames = {"shakuyaku.BoxFilter", "BoxFilter"})
public interface BoxFilter extends ReconstructionFilter {
  // NO-OP
}