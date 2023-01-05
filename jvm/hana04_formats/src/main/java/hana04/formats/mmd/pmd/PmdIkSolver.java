package hana04.formats.mmd.pmd;

import hana04.formats.mmd.util.EulerAngleDecompositionF;
import hana04.gfxbase.gfxtype.VecMathFUtil;
import hana04.gfxbase.util.MathUtil;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

class PmdIkSolver {
  static void solve(PmdPose pose, boolean useLegBias) {
    PmdModel model = pose.getModel();
    for (int i = 0; i < model.ikChains.size(); i++) {
      PmdIkChain ikChain = model.ikChains.get(i);
      solve(pose, ikChain, useLegBias);
    }
  }

  static void solve(PmdPose pose, PmdIkChain ikChain, boolean useLegIkBias) {
    if (useLegIkBias) {
      addLegIkBias(pose, ikChain);
    }
    for (int iterationIndex = 0; iterationIndex < ikChain.iterationCount; iterationIndex++) {
      for (int boneIndex = 0; boneIndex < ikChain.chainBoneIndices.length; boneIndex++) {
        solve(pose, ikChain, ikChain.chainBoneIndices[boneIndex]);
      }
    }
  }

  static void addLegIkBias(PmdPose pose, PmdIkChain chain) {
    PmdModel model = pose.getModel();
    PmdBone targetBone = model.bones.get(chain.boneIndex);
    if (targetBone.japaneseName.equals("左足ＩＫ")) {
      for (int i = 0; i < chain.chainBoneIndices.length; i++) {
        PmdBone chainBone = model.bones.get(chain.chainBoneIndices[i]);
        if (chainBone.japaneseName.equals("左ひざ")) {
          Quat4f rot = new Quat4f();
          rot.set(new AxisAngle4f(new Vector3f(1, 0, 0), -(float) (Math.PI / 2)));
          muliplyRightBoneRot(pose, chain.chainBoneIndices[i], rot);
        }
        if (chainBone.japaneseName.equals("左足")) {
          Quat4f rot = new Quat4f();
          pose.getBoneRotation(chain.chainBoneIndices[i], rot);
          EulerAngleDecompositionF eulerAngles = EulerAngleDecompositionF.decompose(rot);

          if (eulerAngles.axisRot.x < Math.PI / 4) {
            eulerAngles.axisRot.x = (float) Math.PI / 4;
          }
          eulerAngles.axisRot.y = (float) MathUtil.clamp(eulerAngles.axisRot.y, -Math.PI / 4, Math.PI / 4);
          eulerAngles.axisRot.z = (float) MathUtil.clamp(eulerAngles.axisRot.z, -Math.PI / 8, Math.PI / 8);

          eulerAngles.toQuaternion(rot);
          pose.setBoneRotation(chain.chainBoneIndices[i], rot);
        }

      }
    }
    if (targetBone.japaneseName.equals("右足ＩＫ")) {
      for (int i = 0; i < chain.chainBoneIndices.length; i++) {
        PmdBone chainBone = model.bones.get(chain.chainBoneIndices[i]);
        if (chainBone.japaneseName.equals("右ひざ")) {
          Quat4f rot = new Quat4f();
          rot.set(new AxisAngle4f(new Vector3f(1, 0, 0), -(float) (Math.PI / 2)));
          muliplyRightBoneRot(pose, chain.chainBoneIndices[i], rot);
        }
        if (chainBone.japaneseName.equals("右足")) {
          Quat4f rot = new Quat4f();
          pose.getBoneRotation(chain.chainBoneIndices[i], rot);
          EulerAngleDecompositionF eulerAngles = EulerAngleDecompositionF.decompose(rot);

          if (eulerAngles.axisRot.x < Math.PI / 4) {
            eulerAngles.axisRot.x = (float) Math.PI / 4;
          }
          eulerAngles.axisRot.y = (float) MathUtil.clamp(eulerAngles.axisRot.y, -Math.PI / 4, Math.PI / 4);
          eulerAngles.axisRot.z = (float) MathUtil.clamp(eulerAngles.axisRot.z, -Math.PI / 8, Math.PI / 8);

          eulerAngles.toQuaternion(rot);
          pose.setBoneRotation(chain.chainBoneIndices[i], rot);
        }
      }
    }
  }

  static void muliplyRightBoneRot(PmdPose pose, int boneIndex, Quat4f rot) {
    Quat4f boneRot = new Quat4f();
    pose.getBoneRotation(boneIndex, boneRot);
    boneRot.mul(rot);
    pose.setBoneRotation(boneIndex, boneRot);
  }

  private static final Vector3f KNEE_ANGLE_LOWER_BOUND = new Vector3f(-(float) Math.PI, 0, 0);
  private static final Vector3f KNEE_ANGLE_UPPER_BOUND = new Vector3f(0, 0, 0);

  static void solve(PmdPose pose, PmdIkChain ikChain, int boneIndex) {
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

    PmdModel model = pose.getModel();
    pose.getBoneWorldPosition(ikChain.boneIndex, targetGlobal);

    pose.getBoneWorldPosition(ikChain.targetBoneIndex, effectorGlobal);

    pose.getInverseBoneTransform(boneIndex, boneInverseXform);
    boneInverseXform.transform(effectorGlobal, effectorLocal);
    boneToEffector.set(effectorLocal);
    boneToEffector.normalize();
    boneInverseXform.transform(targetGlobal, targetLocal);
    boneToTarget.set(targetLocal);
    boneToTarget.normalize();

    float dot = boneToTarget.dot(boneToEffector);
    if (dot > 1) dot = 1;
    float rotationAngle = MathUtil.clamp((float) Math.acos(dot),
      (float) (-ikChain.quarterIterationAngleLimitRad * Math.PI), (float) (ikChain.quarterIterationAngleLimitRad * Math.PI));
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
    muliplyRightBoneRot(pose, boneIndex, rotation);

    if (!ikChain.isLeg) {
      return;
    }
    PmdBone bone = model.bones.get(boneIndex);
    if (!bone.isKnee) {
      return;
    }

    Quat4f ikLinkRot = new Quat4f();
    pose.getBoneRotation(boneIndex, ikLinkRot);
    EulerAngleDecompositionF eulerAngles = EulerAngleDecompositionF.decompose(ikLinkRot);
    VecMathFUtil.normalizeEulerAngle(eulerAngles.axisRot);
    MathUtil.clamp(eulerAngles.axisRot, KNEE_ANGLE_LOWER_BOUND, KNEE_ANGLE_UPPER_BOUND);
    eulerAngles.toQuaternion(ikLinkRot);
    pose.setBoneRotation(boneIndex, ikLinkRot);
  }
}
