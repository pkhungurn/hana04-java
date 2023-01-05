package hana04.yuri.rfilter;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.yuri.TypeIds;

@HanaDeclareObject(
  parent = ReconstructionFilter.class,
  typeId = TypeIds.TYPE_ID_TENT_FILTER,
  typeNames = {"shakuyaku.TentFilter", "TentFilter"})
public interface TentFilter extends ReconstructionFilter {
  // NO-OP
}