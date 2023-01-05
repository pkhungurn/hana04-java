package hana04.yuri.film.zirr;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.yuri.TypeIds;
import hana04.yuri.film.Film;

@HanaDeclareObject(
    parent = Film.class,
    typeId = TypeIds.TYPE_ID_ZIRR_FIREFLY_REWEIGHTING_FILM,
    typeNames = {"shakuyaku.ZirrFireflyReweightingFilm", "ZirrFireflyReweightingFilm"})
public interface ZirrFireflyReweightingFilm extends Film {
  // The exponent base b from the paper.
  // The jth buffer covers the range [b^{j-1}, b^{j+1}] of values centered
  // around b^j.
  // Default value: 8
  @HanaProperty(1)
  Variable<Double> exponentBase();

  // The exponent of the buffer with the lowest value.
  // Default value: -2
  @HanaProperty(2)
  Variable<Integer> lowestExponent();

  // The exponent of the buffer with the heighest value.
  // Default value: 6
  @HanaProperty(3)
  Variable<Integer> highestExponent();

  // The sample count per pixel.
  // This should be kept in sync with the number in the sampler.
  @HanaProperty(4)
  Variable<Integer> sampleCount();

  // The outlier count, kappa, in the paper.
  // We consider eliminating samples where the its contribution is more than
  // N E[F] / kappa. (See paper.)
  // Default value: 32
  @HanaProperty(5)
  Variable<Double> outlierCount();

  // The sample count, kappa_min, below which we drop the sample
  // from consideration.
  // Default value: 1
  @HanaProperty(6)
  Variable<Double> minCount();

  @HanaProperty(7)
  Variable<Integer> width();

  @HanaProperty(8)
  Variable<Integer> height();
}
