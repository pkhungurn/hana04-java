package hana04.gfxbase.gfxtype;

import javax.vecmath.Tuple4d;

public class UnnormalizedQuat4d {
  public double x, y, z, w;

  public UnnormalizedQuat4d() {
    this.x = 0;
    this.y = 0;
    this.z = 0;
    this.w = 1;
  }

  public UnnormalizedQuat4d(double x, double y, double z, double w) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }

  public void mulI(UnnormalizedQuat4d other) {
    double w = this.w * other.w - this.x * other.x - this.y * other.y - this.z * other.z;
    double x = this.w * other.x + this.x * other.w + this.y * other.z - this.z * other.y;
    double y = this.w * other.y + this.y * other.w + this.z * other.x - this.x * other.z;
    double z = this.w * other.z + this.z * other.w + this.x * other.y - this.y * other.x;
    this.w = w;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public UnnormalizedQuat4d mul(UnnormalizedQuat4d other) {
    double w = this.w * other.w - this.x * other.x - this.y * other.y - this.z * other.z;
    double x = this.w * other.x + this.x * other.w + this.y * other.z - this.z * other.y;
    double y = this.w * other.y + this.y * other.w + this.z * other.x - this.x * other.z;
    double z = this.w * other.z + this.z * other.w + this.x * other.y - this.y * other.x;
    return new UnnormalizedQuat4d(x, y, z, w);
  }

  public void mulI(UnnormalizedQuat4d a, UnnormalizedQuat4d b) {
    double w = a.w * b.w - a.x * b.x - a.y * b.y - a.z * b.z;
    double x = a.w * b.x + a.x * b.w + a.y * b.z - a.z * b.y;
    double y = a.w * b.y + a.y * b.w + a.z * b.x - a.x * b.z;
    double z = a.w * b.z + a.z * b.w + a.x * b.y - a.y * b.x;
    this.w = w;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public double normSquared() {
    return x * x + y * y + z * z + w * w;
  }

  public double norm() {
    return Math.sqrt(normSquared());
  }

  public void normalizeI() {
    double l = norm();
    x /= l;
    y /= l;
    z /= l;
    w /= l;
  }

  public UnnormalizedQuat4d normalized() {
    double l = norm();
    return new UnnormalizedQuat4d(x / l, y / l, z / l, w / l);
  }

  public double dot(UnnormalizedQuat4d other) {
    return this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w;
  }

  public void conjugateI() {
    x *= -1;
    y *= -1;
    z *= -1;
  }

  public UnnormalizedQuat4d conjugate() {
    return new UnnormalizedQuat4d(-x, -y, -z, w);
  }

  public UnnormalizedQuat4d inverse() {
    double l = norm();
    return new UnnormalizedQuat4d(-x / l, -y / l, -z / l, w / l);
  }

  public void invertI() {
    double l = norm();
    x = -x / l;
    y = -y / l;
    z = -z / l;
    w = w / l;
  }

  public void set(UnnormalizedQuat4d other) {
    this.x = other.x;
    this.y = other.y;
    this.z = other.z;
    this.w = other.w;
  }

  public void set(Tuple4d other) {
    this.x = other.x;
    this.y = other.y;
    this.z = other.z;
    this.w = other.w;
  }

  public void set(double x, double y, double z, double w) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }

  public void addI(UnnormalizedQuat4d other) {
    this.x += other.x;
    this.y += other.y;
    this.z += other.z;
    this.w += other.w;
  }

  public UnnormalizedQuat4d add(UnnormalizedQuat4d other) {
    return new UnnormalizedQuat4d(x + other.x, y + other.y, z + other.z, w + other.w);
  }

  public void scaleI(double c) {
    this.x *= c;
    this.y *= c;
    this.z *= c;
    this.w *= c;
  }

  public UnnormalizedQuat4d scale(double c) {
    return new UnnormalizedQuat4d(c * x, c * y, c * z, c * w);
  }

  @Override
  public String toString() {
    return String.format("(%f, %f, %f, %f)", x, y, z, w);
  }
}
