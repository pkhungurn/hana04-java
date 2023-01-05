package hana04.formats.mmd.util;

import hana04.gfxbase.gfxtype.VecMathFUtil;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class EulerAngleDecompositionF {
  public enum Type {
    XYZ,
    YZX,
    ZXY
  }

  public final Type type;
  public final Vector3f axisRot;

  private EulerAngleDecompositionF(Type type, Vector3f axisRot) {
    this.type = type;
    this.axisRot = axisRot;
  }

  public void toQuaternion(Quat4f output) {
    switch (type) {
      case XYZ:
        VecMathFUtil.eulerXYZToQuaternion(axisRot, output);
        break;
      case YZX:
        VecMathFUtil.eulerYZXToQuaternion(axisRot, output);
        break;
      case ZXY:
        VecMathFUtil.eulerZXYToQuaternion(axisRot, output);
        break;
    }
  }

  public Quat4f toQuaternion() {
    Quat4f output = new Quat4f();
    toQuaternion(output);
    return output;
  }

  public static EulerAngleDecompositionF decompose(Quat4f rot) {
    Vector3f axisRot = new Vector3f();
    Type type;
    if (VecMathFUtil.factorQuaternionXYZ(rot, axisRot))
      type = Type.XYZ;
    else if (VecMathFUtil.factorQuaternionYZX(rot, axisRot))
      type = Type.YZX;
    else {
      VecMathFUtil.factorQuaternionZXY(rot, axisRot);
      type = Type.ZXY;
    }
    return new EulerAngleDecompositionF(type, axisRot);
  }

  public static void main(String[] args) {
    var qx = VecMathFUtil.create(new Vector3f(1,0,0), 90.0f);
    var qy = VecMathFUtil.create(new Vector3f(0,1,0), 10.0f);
    var qz = VecMathFUtil.create(new Vector3f(0,0,1), 10.0f);
    var q = VecMathFUtil.mul(VecMathFUtil.mul(qz, qy), qx);
    var decomposed = EulerAngleDecompositionF.decompose(q);
    System.out.println(q);
    System.out.println(decomposed.type);
    System.out.println(decomposed.axisRot);
    System.out.println(decomposed.toQuaternion());
  }
}
