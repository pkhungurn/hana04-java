package hana04.formats.mmd.util;

import hana04.gfxbase.gfxtype.VecMathDUtil;

import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

public class EulerAngleDecompositionD {
  public enum Type {
    XYZ,
    YZX,
    ZXY
  }

  public final Type type;
  public final Vector3d axisRot;

  private EulerAngleDecompositionD(Type type, Vector3d axisRot) {
    this.type = type;
    this.axisRot = axisRot;
  }

  public void toQuaternion(Quat4d output) {
    switch (type) {
      case XYZ:
        VecMathDUtil.eulerXYZToQuaternion(axisRot, output);
        break;
      case YZX:
        VecMathDUtil.eulerYZXToQuaternion(axisRot, output);
        break;
      case ZXY:
        VecMathDUtil.eulerZXYToQuaternion(axisRot, output);
        break;
    }
  }

  public Quat4d toQuaternion() {
    Quat4d output = new Quat4d();
    toQuaternion(output);
    return output;
  }

  public static EulerAngleDecompositionD decompose(Quat4d rot) {
    Vector3d axisRot = new Vector3d();
    Type type;
    if (VecMathDUtil.factorQuaternionXYZ(rot, axisRot))
      type = Type.XYZ;
    else if (VecMathDUtil.factorQuaternionYZX(rot, axisRot))
      type = Type.YZX;
    else {
      VecMathDUtil.factorQuaternionZXY(rot, axisRot);
      type = Type.ZXY;
    }
    return new EulerAngleDecompositionD(type, axisRot);
  }

  public static void main(String[] args) {
    var qx = VecMathDUtil.createQuat(new Vector3d(1,0,0), 90.0f);
    var qy = VecMathDUtil.createQuat(new Vector3d(0,1,0), 10.0f);
    var qz = VecMathDUtil.createQuat(new Vector3d(0,0,1), 10.0f);
    var q = VecMathDUtil.mul(VecMathDUtil.mul(qz, qy), qx);
    var decomposed = EulerAngleDecompositionD.decompose(q);
    System.out.println(q);
    System.out.println(decomposed.type);
    System.out.println(decomposed.axisRot);
    System.out.println(decomposed.toQuaternion());
  }
}
