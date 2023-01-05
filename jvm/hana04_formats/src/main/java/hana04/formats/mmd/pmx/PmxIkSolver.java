package hana04.formats.mmd.pmx;

import hana04.formats.mmd.util.EulerAngleDecompositionF;
import hana04.gfxbase.gfxtype.VecMathFUtil;
import hana04.gfxbase.util.MathUtil;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class PmxIkSolver {
  public static void solve(PmxPose pose, boolean useLegIkBias) {
    PmxModel model = pose.getModel();
    for (int i = 0; i < model.getBoneCount(); i++) {
      PmxBone bone = model.getBoneByOrder(i);
      if (bone.isIk()) {
        solve(pose, bone.boneIndex, useLegIkBias);
      }
    }
  }

  public static void solve(PmxPose pose, int boneIndex, boolean useLegIkBias) {
    PmxModel model = pose.getModel();
    PmxBone bone = model.getBone(boneIndex);
    if (!bone.isIk())
      return;
    if (bone.ikTargetBoneIndex < 0)
      return;
    if (useLegIkBias) {
      addLegIkBias(pose, bone);
    }

    Point3f lastEffectorPos = getEffectorPos(pose, bone);
    for (int j = 0; j < bone.ikLoopCount; j++) {
      for (int k = 0; k < bone.ikLinks.size(); k++) {
        solve(pose, bone, k);
      }
      Point3f newEffectorPos = getEffectorPos(pose, bone);
      float distance = lastEffectorPos.distance(newEffectorPos);
      if (distance < 1e-6) {
        break;
      }
      lastEffectorPos = newEffectorPos;
    }
  }

  private static void muliplyRightBoneRot(PmxPose pose, int boneIndex, Quat4f rot) {
    Quat4f boneRot = new Quat4f();
    pose.getBoneRotation(boneIndex, boneRot);
    boneRot.mul(rot);
    pose.setBoneRotation(boneIndex, boneRot);
  }

  private static void addLegIkBias(PmxPose pose, PmxBone targetBone) {
    PmxModel model = pose.getModel();
    if (targetBone.japaneseName.equals("左足ＩＫ")) {
      for (int i = 0; i < targetBone.ikLinks.size(); i++) {
        PmxIkLink ikLink = targetBone.ikLinks.get(i);
        if (model.getBone(ikLink.boneIndex).japaneseName.equals("左ひざ")) {
          Quat4f rot = new Quat4f();
          rot.set(new AxisAngle4f(new Vector3f(1, 0, 0), -(float) (Math.PI / 2)));
          muliplyRightBoneRot(pose, ikLink.boneIndex, rot);
        }

        if (model.getBone(ikLink.boneIndex).japaneseName.equals("左足")) {
          Quat4f rot = new Quat4f();
          pose.getBoneRotation(ikLink.boneIndex, rot);
          EulerAngleDecompositionF eulerAngles = EulerAngleDecompositionF.decompose(rot);

          if (eulerAngles.axisRot.x < Math.PI / 4) {
            eulerAngles.axisRot.x = (float) Math.PI / 4;
          }
          eulerAngles.axisRot.y = (float) MathUtil.clamp(eulerAngles.axisRot.y, -Math.PI / 4, Math.PI / 4);
          eulerAngles.axisRot.z = (float) MathUtil.clamp(eulerAngles.axisRot.z, -Math.PI / 8, Math.PI / 8);

          eulerAngles.toQuaternion(rot);
          pose.setBoneRotation(ikLink.boneIndex, rot);
        }

      }
    }
    if (targetBone.japaneseName.equals("右足ＩＫ")) {
      for (int i = 0; i < targetBone.ikLinks.size(); i++) {
        PmxIkLink ikLink = targetBone.ikLinks.get(i);
        if (model.getBone(ikLink.boneIndex).japaneseName.equals("右ひざ")) {
          Quat4f rot = new Quat4f();
          rot.set(new AxisAngle4f(new Vector3f(1, 0, 0), -(float) (Math.PI / 2)));
          muliplyRightBoneRot(pose, ikLink.boneIndex, rot);
        }


        if (model.getBone(ikLink.boneIndex).japaneseName.equals("右足")) {
          Quat4f rot = new Quat4f();
          pose.getBoneRotation(ikLink.boneIndex, rot);
          EulerAngleDecompositionF eulerAngles = EulerAngleDecompositionF.decompose(rot);

          if (eulerAngles.axisRot.x < Math.PI / 4) {
            eulerAngles.axisRot.x = (float) Math.PI / 4;
          }
          eulerAngles.axisRot.y = (float) MathUtil.clamp(eulerAngles.axisRot.y, -Math.PI / 4, Math.PI / 4);
          eulerAngles.axisRot.z = (float) MathUtil.clamp(eulerAngles.axisRot.z, -Math.PI / 8, Math.PI / 8);

          eulerAngles.toQuaternion(rot);
          pose.setBoneRotation(ikLink.boneIndex, rot);
        }
      }
    }
  }

  public static Point3f getEffectorPos(PmxPose pose, PmxBone ikBone) {
    Point3f effectorGlobal = new Point3f();
    PmxModel model = pose.getModel();
    PmxBone effectorBone = model.getBone(ikBone.ikTargetBoneIndex);
    pose.getBoneWorldPosition(effectorBone.boneIndex, effectorGlobal);
    return effectorGlobal;
  }

  public static void solve(PmxPose pose, PmxBone ikBone, int boneIndex) {
    Point3f effectorGlobal = new Point3f();
    Point3f targetGlobal = new Point3f();
    Matrix4f boneInverseXform = new Matrix4f();
    Point3f targetLocal = new Point3f();
    Point3f effectorLocal = new Point3f();
    Vector3f boneToTarget = new Vector3f();
    Vector3f boneToEffector = new Vector3f();
    Vector3f rotationAxis = new Vector3f();
    Quat4f rotation = new Quat4f();
    AxisAngle4f axisAngle = new AxisAngle4f();

    PmxModel model = pose.getModel();
    PmxBone effectorBone = model.getBone(ikBone.ikTargetBoneIndex);
    pose.getBoneWorldPosition(effectorBone.boneIndex, effectorGlobal);

    pose.getBoneWorldPosition(ikBone.boneIndex, targetGlobal);
    PmxIkLink ikLink = ikBone.ikLinks.get(boneIndex);

    pose.getInverseBoneTransform(ikLink.boneIndex, boneInverseXform);
    boneInverseXform.transform(effectorGlobal, effectorLocal);
    boneToEffector.set(effectorLocal);
    boneToEffector.normalize();
    boneInverseXform.transform(targetGlobal, targetLocal);
    boneToTarget.set(targetLocal);
    boneToTarget.normalize();

    float dot = boneToTarget.dot(boneToEffector);
    if (dot > 1) dot = 1;
    float rotationAngle = MathUtil.clamp((float) Math.acos(dot),
      -ikBone.ikAngleLimit, ikBone.ikAngleLimit);
    if (Float.isNaN(rotationAngle)) {
      return;
    }
    if (Math.abs(rotationAngle) < 1e-3) {
      return;
    }

    rotationAxis.cross(boneToEffector, boneToTarget);
    rotationAxis.normalize();
    axisAngle.set(rotationAxis.x, rotationAxis.y, rotationAxis.z, rotationAngle);
    rotation.set(axisAngle);
    muliplyRightBoneRot(pose, ikLink.boneIndex, rotation);

    if (!ikLink.angleLimited) {
      return;
    }

    Quat4f ikLinkRot = new Quat4f();
    pose.getBoneRotation(ikLink.boneIndex, ikLinkRot);
    EulerAngleDecompositionF eulerAngles = EulerAngleDecompositionF.decompose(ikLinkRot);
    VecMathFUtil.normalizeEulerAngle(eulerAngles.axisRot);
    MathUtil.clamp(eulerAngles.axisRot, ikLink.angleLowerBound, ikLink.angleUpperBound);
    eulerAngles.toQuaternion(ikLinkRot);
    pose.setBoneRotation(ikLink.boneIndex, ikLinkRot);
  }

}
