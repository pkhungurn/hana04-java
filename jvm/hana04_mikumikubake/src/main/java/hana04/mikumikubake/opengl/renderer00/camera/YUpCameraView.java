package hana04.mikumikubake.opengl.renderer00.camera;

import hana04.gfxbase.gfxtype.Matrix4dUtil;
import hana04.gfxbase.gfxtype.Transform;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

public class YUpCameraView implements CameraView {
  private double xAngle = 0;
  private double yAngle = 0;
  private double zAngle = 0;
  private double distance = 30;
  private final Point3d center = new Point3d(0, 0, 0);

  public YUpCameraView() {
    // NO-OP
  }

  public final BoxedValue<Double> boxedXAngle = new BoxedValue<Double>() {
    @Override
    public Double get() {
      return xAngle;
    }

    @Override
    public void set(Double value) {
      xAngle = value;
    }
  };

  public final BoxedValue<Double> boxedYAngle = new BoxedValue<Double>() {
    @Override
    public Double get() {
      return yAngle;
    }

    @Override
    public void set(Double value) {
      yAngle = value;
    }
  };

  public final BoxedValue<Double> boxedZAngle = new BoxedValue<Double>() {
    @Override
    public Double get() {
      return zAngle;
    }

    @Override
    public void set(Double value) {
      zAngle = value;
    }
  };

  public final BoxedValue<Double> boxedCenterX = new BoxedValue<Double>() {
    @Override
    public Double get() {
      return center.x;
    }

    @Override
    public void set(Double value) {
      center.x = value;
    }
  };

  public final BoxedValue<Double> boxedCenterY = new BoxedValue<Double>() {
    @Override
    public Double get() {
      return center.y;
    }

    @Override
    public void set(Double value) {
      center.y = value;
    }
  };

  public final BoxedValue<Double> boxedCenterZ = new BoxedValue<Double>() {
    @Override
    public Double get() {
      return center.z;
    }

    @Override
    public void set(Double value) {
      center.z = value;
    }
  };

  public final BoxedValue<Double> boxedDistance = new BoxedValue<Double>() {
    @Override
    public Double get() {
      return getDistance();
    }

    @Override
    public void set(Double value) {
      setDistance(value);
    }
  };

  public Transform getXform() {
    Matrix4d trans1 = Matrix4dUtil.createTranslation(center.x, center.y, center.z);

    Matrix4d xRot = Matrix4dUtil.createRotation(xAngle, 1, 0, 0);
    Matrix4d yRot = Matrix4dUtil.createRotation(yAngle, 0, 1, 0);
    Matrix4d zRot = Matrix4dUtil.createRotation(zAngle, 0, 0, 1);
    Matrix4d rot = Matrix4dUtil.createIdentity();
    rot.mul(zRot);
    rot.mul(yRot);
    rot.mul(xRot);

    Matrix4d trans2 = Matrix4dUtil.createTranslation(0, 0, distance);

    Matrix4d xform = Matrix4dUtil.createIdentity();
    xform.mul(trans1);
    xform.mul(rot);
    xform.mul(trans2);

    xform.invert();

    return new Transform(xform);
  }

  public void setCenter(double x, double y, double z) {
    center.set(x, y, z);
  }

  public void setDistance(double distance) {
    this.distance = Math.max(0.01, distance);
  }

  public double getXAngle() {
    return xAngle;
  }

  public void setXAngle(double xAngle) {
    this.xAngle = xAngle;
  }

  public double getYAngle() {
    return yAngle;
  }

  public void setYAngle(double yAngle) {
    this.yAngle = yAngle;
  }

  public double getZAngle() {
    return zAngle;
  }

  public void setZAngle(double zAngle) {
    this.zAngle = zAngle;
  }

  public double getDistance() {
    return distance;
  }

  public Point3d getCenter() {
    return center;
  }

  public void copy(YUpCameraView other) {
    xAngle = other.xAngle;
    yAngle = other.yAngle;
    zAngle = other.zAngle;
    center.set(other.center);
    distance = other.distance;
  }
}
