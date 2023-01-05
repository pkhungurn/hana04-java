package hana04.gfxbase.spectrum.rgb;

import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.SpectrumTransform;
import hana04.gfxbase.spectrum.util.SrgbUtil;
import hana04.gfxbase.gfxtype.TupleUtil;

import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import java.awt.Color;
import java.io.Serializable;

public class Rgb extends Tuple3d implements Serializable, Spectrum, SpectrumTransform<Rgb> {
  public static final String TYPE_NAME = "Rgb";

  public Rgb(float var1, float var2, float var3) {
    super(var1, var2, var3);
  }

  public Rgb(double var1, double var2, double var3) {
    super(var1, var2, var3);
  }

  public Rgb(double[] var1) {
    super(var1);
  }

  public Rgb(Tuple3f var1) {
    super(var1);
  }

  public Rgb(Tuple3d var1) {
    super(var1);
  }

  public Rgb(Color var1) {
    super((float) var1.getRed() / 255.0, (float) var1.getGreen() / 255.0F, (float) var1.getBlue() / 255.0F);
  }

  public double getLuminance() {
    return this.x * 0.212671 + this.y * 0.715160 + this.z * 0.072169;
  }

  public Rgb() {
    // NO-OP
  }

  public final void set(Color var1) {
    this.x = var1.getRed() / 255.0;
    this.y = var1.getGreen() / 255.0;
    this.z = var1.getBlue() / 255.0;
  }

  public final Color get() {
    int var1 = (int) Math.round(this.x * 255.0);
    int var2 = (int) Math.round(this.y * 255.0);
    int var3 = (int) Math.round(this.z * 255.0);
    return new Color(var1, var2, var3);
  }

  public void mul(Tuple3d v) {
    this.x *= v.x;
    this.y *= v.y;
    this.z *= v.z;
  }

  public void div(Tuple3d v) {
    this.x /= v.x;
    this.y /= v.y;
    this.z /= v.z;
  }

  public void clampNegative() {
    x = Math.max(0, x);
    y = Math.max(0, y);
    z = Math.max(0, z);
  }

  public static Rgb rgb(double r, double g, double b) {
    return new Rgb(r, g, b);
  }

  public static Rgb add(Rgb a, Rgb b) {
    return new Rgb(a.x + b.x, a.y + b.y, a.z + b.z);
  }

  public static Rgb sub(Rgb a, Rgb b) {
    return new Rgb(a.x - b.x, a.y - b.y, a.z - b.z);
  }

  public static Rgb mul(Rgb a, Rgb b) {
    return new Rgb(a.x * b.x, a.y * b.y, a.z * b.z);
  }

  public static Rgb div(Rgb a, Rgb b) {
    return new Rgb(a.x / b.x, a.y / b.y, a.z / b.z);
  }

  public static Rgb recip(Rgb a) {
    return new Rgb(1 / a.x, 1 / a.y, 1 / a.z);
  }

  public static Rgb scale(Rgb a, double c) {
    return new Rgb(a.x * c, a.y * c, a.z * c);
  }

  public static Rgb scale(double c, Rgb a) {
    return new Rgb(a.x * c, a.y * c, a.z * c);
  }

  public static Rgb exp(Rgb a) {
    return new Rgb(Math.exp(a.x), Math.exp(a.y), Math.exp(a.z));
  }

  public static Rgb parseSrgb(String value) {
    String[] comps = value.trim().split("[\\s,]+");
    Rgb result = new Rgb();
    result.x = SrgbUtil.srgbToLinear(Double.valueOf(comps[0]));
    result.y = SrgbUtil.srgbToLinear(Double.valueOf(comps[1]));
    result.z = SrgbUtil.srgbToLinear(Double.valueOf(comps[2]));
    return result;
  }

  public static Rgb parseSrgbHex(String value) {
    value = value.trim();
    if (value.length() == 7)
      value = value.substring(1, 7);
    if (value.length() != 6) {
      throw new RuntimeException("Invalid color hex value: " + value);
    }
    double r = Integer.valueOf(value.substring(0, 2), 16) / 255.0;
    double g = Integer.valueOf(value.substring(2, 4), 16) / 255.0;
    double b = Integer.valueOf(value.substring(4, 6), 16) / 255.0;
    Rgb result = new Rgb();
    result.x = SrgbUtil.srgbToLinear(r);
    result.y = SrgbUtil.srgbToLinear(g);
    result.z = SrgbUtil.srgbToLinear(b);
    return result;
  }

  @Override
  public Rgb spectrumToRgb() {
    return new Rgb(this);
  }

  @Override
  public double spectrumAverage() {
    return (x + y + z) / 3.0;
  }

  @Override
  public double spectrumMaxComponent() {
    return Math.max(x, Math.max(y, z));
  }

  @Override
  public double spectrumMinComponent() {
    return Math.min(x, Math.min(y, z));
  }

  @Override
  public boolean isZero() {
    return x == 0 && y == 0 && z == 0;
  }

  @Override
  public boolean isNaN() {
    return TupleUtil.isNaN(this);
  }
}
