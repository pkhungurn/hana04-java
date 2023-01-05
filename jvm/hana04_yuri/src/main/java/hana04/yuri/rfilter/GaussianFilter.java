package hana04.yuri.rfilter;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.yuri.TypeIds;

/**
 * Truncated shifted-down normalized Gaussian filter.
 */
@HanaDeclareObject(
  parent = ReconstructionFilter.class,
  typeId = TypeIds.TYPE_ID_GAUSSIAN_FILTER,
  typeNames = {"shakuyaku.GaussianFilter", "GaussianFilter"})
public interface GaussianFilter extends ReconstructionFilter {
  @HanaProperty(1)
  Variable<Double> radius();

  @HanaProperty(2)
  Variable<Double> stddev();

}