package hana04.gfxbase.spectrum.rgb;

import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.SpectrumSpace;

public class RgbSpace implements SpectrumSpace<Rgb, Rgb> {
  public final static RgbSpace I = new RgbSpace();

  @Override
  public Rgb createZero() {
    return new Rgb(0, 0, 0);
  }

  @Override
  public Rgb createFromRgb(Rgb rgb) {
    return new Rgb(rgb);
  }

  @Override
  public Rgb scale(double c, Rgb a) {
    return Rgb.scale(c, a);
  }

  @Override
  public Rgb scale(Rgb a, double c) {
    return Rgb.scale(a, c);
  }

  @Override
  public Rgb add(Rgb a, Rgb b) {
    return Rgb.add(a, b);
  }

  @Override
  public Rgb mul(Rgb A, Rgb B) {
    return Rgb.mul(A, B);
  }

  @Override
  public Rgb copy(Rgb input) {
    return new Rgb(input);
  }

  @Override
  public Rgb createZeroTransform() {
    return new Rgb(0, 0, 0);
  }

  @Override
  public Rgb createIdentityTransform() {
    return new Rgb(1, 1, 1);
  }

  @Override
  public Rgb createTransformFromRgb(Rgb rgb) {
    return new Rgb(rgb);
  }

  @Override
  public Rgb createTransform(Rgb a) {
    return new Rgb(a);
  }

  @Override
  public Rgb createTransformFromScalar(double v) {
    return new Rgb(v, v, v);
  }

  @Override
  public Rgb transform(Rgb A, Rgb b) {
    return Rgb.mul(A, b);
  }

  @Override
  public Rgb convert(Spectrum spectrum) {
    return spectrum.spectrumToRgb();
  }


}
