package hana04.gfxbase.spectrum;

import hana04.gfxbase.spectrum.rgb.Rgb;

public interface SpectrumSpace<T extends Spectrum, V extends SpectrumTransform<T>> {
  T createZero();

  T createFromRgb(Rgb rgb);

  T scale(double c, T a);

  T scale(T a, double c);

  T add(T a, T b);

  T copy(T input);

  V createZeroTransform();

  V createIdentityTransform();

  V createTransformFromRgb(Rgb rgb);

  V createTransform(T a);

  V createTransformFromScalar(double v);

  T transform(V A, T b);

  T convert(Spectrum spectrum);

  V scale(double c, V A);

  V scale(V A, double c);

  V add(V A, V B);

  V mul(V A, V B);

  default V convertAndCreateTransform(Spectrum a) {
    return createTransform(convert(a));
  }
}
