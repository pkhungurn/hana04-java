package hana04.gfxbase.gfxtype;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Represents a 3D affine transformation, its inverse, and inverse transfose.
 */
public class Transform {
  public static final String TYPE_NAME = "Transform";
  /**
   * The matrix of the transformation.
   */
  public final Matrix4d m = new Matrix4d();
  /**
   * The inverse of the transformation.
   */
  public final Matrix4d mi = new Matrix4d();
  /**
   * The inverse transpose of the transformation.
   */
  public final Matrix4d mit = new Matrix4d();
  /**
   * Whether the transform is perserving orientation.
   */
  private boolean isPreservingOrientation;

  /**
   * Creates the identity transformation.
   */
  public Transform() {
    m.setIdentity();
    mi.setIdentity();
    mit.setIdentity();
    isPreservingOrientation = true;
  }

  /**
   * Create a transformation from a given matrix.
   *
   * @param m the matrix of the transformation.
   */
  public Transform(Matrix4d m) {
    this.m.set(m);
    updateFromM();
  }

  /**
   * Copy from another Transform.
   *
   * @param other the transform to copy from
   */
  public void set(Transform other) {
    this.m.set(other.m);
    this.mi.set(other.mi);
    this.mit.set(other.mit);
    this.isPreservingOrientation = other.isPreservingOrientation;
  }

  public void set(Matrix4d m) {
    this.m.set(m);
    updateFromM();
  }

  /**
   * Set the value of this transform to the inverse of the given transfrom T.
   *
   * @param T
   */
  public void invert(Transform T) {
    m.set(T.mi);
    mi.set(T.m);
    mit.set(mi);
    mit.transpose();
  }

  /**
   * Invert the transformation in place.
   */
  public void invert() {
    Matrix4d temp = new Matrix4d();
    temp.set(this.m);
    this.m.set(this.mi);
    this.mi.set(temp);
    this.mit.set(this.mi);
    this.mit.transpose();
  }

  public String toString() {
    return m.toString();
  }

  public void updateFromM() {
    mi.invert(m);
    mit.set(mi);
    mit.transpose();
    isPreservingOrientation = m.determinant() > 0;
  }

  /**
   * Serialize the instance into a stream.
   *
   * @param stream the stream
   */
  public void serialize(DataOutputStream stream) throws IOException {
    Matrix4dUtil.serialize(m, stream);
  }

  /**
   * Deserialize the instance from a stream
   *
   * @param stream the stream
   * @throws IOException
   */
  public void deserialize(DataInputStream stream) throws IOException {
    Matrix4dUtil.deserialize(stream, m);
    updateFromM();
  }

  /**
   * Set this transformation to the identity transformation.
   */
  public void setIdentity() {
    m.setIdentity();
    mi.setIdentity();
    mit.setIdentity();
    isPreservingOrientation = true;
  }

  /**
   * Premultiply this transform with the given transform.
   *
   * @param xform a transform
   */
  public void preMultiply(Transform xform) {
    this.m.mul(xform.m, this.m);
    this.mi.mul(xform.mi);
    this.mit.mul(xform.mit, this.mit);
    isPreservingOrientation = this.m.determinant() > 0;
  }

  /**
   * Multiply this transform with the given transform.
   *
   * @param xform a transform
   */
  public void mul(Transform xform) {
    this.m.mul(xform.m);
    this.mi.mul(xform.mi, this.mi);
    this.mit.mul(xform.mit);
    isPreservingOrientation = this.m.determinant() > 0;
  }

  /**
   * Transform the given point.
   */
  public Point3d transform(Point3d p) {
    return Matrix4dUtil.transform(m, p);
  }

  /**
   * Whether the transform is preserving orientation.
   */
  public boolean isPreservingOrientation() {
    return isPreservingOrientation;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Matrix4d m = Matrix4dUtil.createIdentity();

    public Builder translate(double x, double y, double z) {
      m.mul(Matrix4dUtil.createTranslation(x, y, z));
      return this;
    }

    public Builder translate(Tuple3d v) {
      m.mul(Matrix4dUtil.createTranslation(v.x, v.y, v.z));
      return this;
    }

    public Builder scale(double x, double y, double z) {
      m.mul(Matrix4dUtil.createScaling(x, y, z));
      return this;
    }

    public Builder scale(Tuple3d v) {
      m.mul(Matrix4dUtil.createScaling(v.x, v.y, v.z));
      return this;
    }

    public Builder rotate(double angle, double x, double y, double z) {
      m.mul(Matrix4dUtil.createRotation(angle, x, y, z));
      return this;
    }

    public Builder rotate(double angle, Tuple3d axis) {
      m.mul(Matrix4dUtil.createRotation(angle, axis.x, axis.y, axis.z));
      return this;
    }

    public Builder lookAt(double eyeX, double eyeY, double eyeZ,
                          double atX, double atY, double atZ,
                          double upX, double upY, double upZ) {
      m.mul(Matrix4dUtil.createLookAtMatrix(
        eyeX, eyeY, eyeZ,
        atX, atY, atZ,
        upX, upY, upZ));
      return this;
    }

    public Builder lookAt(Tuple3d eye, Tuple3d at, Tuple3d up) {
      m.mul(Matrix4dUtil.createLookAtMatrix(
        eye.x, eye.y, eye.z,
        at.x, at.y, at.z,
        up.x, up.y, up.z));
      return this;
    }

    public Transform build() {
      return new Transform(m);
    }
  }

  public static Transform identity() {
    return new Transform(Matrix4dUtil.createIdentity());
  }

  public static Transform copy(Transform source) {
    return new Transform(source.m);
  }
}
