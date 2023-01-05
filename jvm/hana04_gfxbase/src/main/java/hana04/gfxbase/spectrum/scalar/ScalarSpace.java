package hana04.gfxbase.spectrum.scalar;

import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.SpectrumSpace;
import hana04.gfxbase.spectrum.rgb.Rgb;

public class ScalarSpace implements SpectrumSpace<Scalar, Scalar> {
  public final static ScalarSpace I = new ScalarSpace();

  @Override
  public Scalar createZero() {
    return new Scalar(0);
  }

  @Override
  public Scalar createFromRgb(Rgb rgb) {
    return new Scalar(rgb.getLuminance());
  }

  @Override
  public Scalar scale(double c, Scalar a) {
    return new Scalar(c * a.toDouble());
  }

  @Override
  public Scalar scale(Scalar a, double c) {
    return new Scalar(c * a.toDouble());
  }

  @Override
  public Scalar add(Scalar a, Scalar b) {
    return new Scalar(a.toDouble() + b.toDouble());
  }

  @Override
  public Scalar mul(Scalar A, Scalar B) {
    return new Scalar(A.toDouble() * B.toDouble());
  }

  @Override
  public Scalar copy(Scalar input) {
    return new Scalar(input.toDouble());
  }

  @Override
  public Scalar createZeroTransform() {
    return new Scalar(0);
  }

  @Override
  public Scalar createIdentityTransform() {
    return new Scalar(1);
  }

  @Override
  public Scalar createTransformFromRgb(Rgb rgb) {
    return new Scalar(rgb.getLuminance());
  }

  @Override
  public Scalar createTransform(Scalar a) {
    return a;
  }

  @Override
  public Scalar createTransformFromScalar(double v) {
    return new Scalar(v);
  }

  @Override
  public Scalar transform(Scalar A, Scalar b) {
    return new Scalar(A.toDouble() * b.toDouble());
  }

  @Override
  public Scalar convert(Spectrum spectrum) {
    if (spectrum instanceof Rgb) {
      return createFromRgb((Rgb) spectrum);
    } else if (spectrum instanceof Scalar) {
      return createTransform((Scalar) spectrum);
    }
    throw new IllegalArgumentException("Conversion from " + spectrum.getClass() + " is not supported");
  }
}
