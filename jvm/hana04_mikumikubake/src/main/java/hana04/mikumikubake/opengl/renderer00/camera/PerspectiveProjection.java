package hana04.mikumikubake.opengl.renderer00.camera;

import hana04.gfxbase.gfxtype.Matrix4fUtil;
import hana04.gfxbase.gfxtype.Transform;
import hana04.gfxbase.util.MathUtil;

import javax.vecmath.Matrix4d;

public class PerspectiveProjection implements CameraProjection {
  private double fovY = 45.0;
  private double near = 0.1;
  private double far = 1000.0;

  public final BoxedValue<Double> boxedFovY = new BoxedValue<Double>() {
    @Override
    public Double get() {
      return getFovY();
    }

    @Override
    public void set(Double value) {
      if (value <= 0 || value >= 180.0) {
        return;
      }
      setFovY(value);
    }
  };

  public final BoxedValue<Double> boxedNear = new BoxedValue<Double>() {
    @Override
    public Double get() {
      return getNear();
    }

    @Override
    public void set(Double value) {
      if (value <= 0) {
        return;
      }
      setNear(value);
    }
  };

  public final BoxedValue<Double> boxedFar = new BoxedValue<Double>() {
    @Override
    public Double get() {
      return getFar();
    }

    @Override
    public void set(Double value) {
      if (value <= 0) {
        return;
      }
      setFar(value);
    }
  };

  @Override
  public Transform getXform(double shiftX, double shiftY, int screenWidth, int screenHeight) {
    float aspectRatio = Math.max(1.0f, screenWidth) * 1.0f / Math.max(1.0f, screenHeight);
    double imagePlaneHalfHeight = Math.tan(MathUtil.degToRad(fovY) / 2) * near;
    double imagePlaneHalfWidth = aspectRatio * imagePlaneHalfHeight;
    double left = -imagePlaneHalfWidth + shiftX * (2 * imagePlaneHalfWidth) / screenWidth;
    double right = imagePlaneHalfWidth + shiftX * (2 * imagePlaneHalfWidth) / screenWidth;
    double bottom = -imagePlaneHalfHeight + shiftY * (2 * imagePlaneHalfHeight) / screenHeight;
    double top = imagePlaneHalfHeight + shiftY * (2 * imagePlaneHalfHeight) / screenHeight;

    Matrix4d projectionMatrix = new Matrix4d(
      Matrix4fUtil.createPerspectiveMatrix(
        (float) left, (float) right,
        (float) bottom, (float) top,
        (float) near, (float) far));
    return new Transform(projectionMatrix);
  }

  public double getFovY() {
    return fovY;
  }

  public void setFovY(double fovY) {
    this.fovY = fovY;
  }

  @Override
  public double getNear() {
    return near;
  }

  public void setNear(double near) {
    this.near = near;
  }

  @Override
  public double getFar() {
    return far;
  }

  public void setFar(double far) {
    this.far = far;
  }

  public void copy(PerspectiveProjection other) {
    this.near = other.near;
    this.far = other.far;
    this.fovY = other.fovY;
  }
}
