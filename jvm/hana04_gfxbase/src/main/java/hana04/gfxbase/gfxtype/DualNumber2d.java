package hana04.gfxbase.gfxtype;

public class DualNumber2d {
  double r;
  double e;

  public DualNumber2d(double r, double e) {
    this.r = r;
    this.e = e;
  }

  public DualNumber2d() {
    this(0, 0);
  }

  public DualNumber2d add(DualNumber2d other) {
    return new DualNumber2d(r + other.r, e + other.e);
  }

  public void addI(DualNumber2d other) {
    this.r += other.r;
    this.e += other.e;
  }

  public DualNumber2d mul(DualNumber2d other) {
    return new DualNumber2d(r * other.r, r * other.e + e * other.r);
  }

  public void mulI(DualNumber2d other) {
    double r = this.r * other.r;
    double e = this.r * other.e + this.e * other.r;
    this.r = r;
    this.e = e;
  }

  public DualNumber2d scale(double c) {
    return new DualNumber2d(r * c, e * c);
  }

  public void scaleI(double c) {
    this.r *= c;
    this.e *= c;
  }

  public void conjugateI() {
    this.e *= -1;
  }

  public DualNumber2d conjugate() {
    return new DualNumber2d(r, -e);
  }

  public DualNumber2d inverse() {
    return new DualNumber2d(1 / r, -e / (r * r));
  }

  public void invertI() {
    double r = 1.0 / this.r;
    double e = -this.e / (this.r * this.r);
    this.r = r;
    this.e = e;
  }

  public DualNumber2d sqrt() {
    return new DualNumber2d(Math.sqrt(r), e / (2 * Math.sqrt(r)));
  }

  public void sqrtI() {
    double r = Math.sqrt(this.r);
    double e = this.e / (2 * Math.sqrt(this.r));
    this.r = r;
    this.e = e;
  }

  @Override
  public String toString() {
    return String.format("(%f, %f)", r, e);
  }
}
