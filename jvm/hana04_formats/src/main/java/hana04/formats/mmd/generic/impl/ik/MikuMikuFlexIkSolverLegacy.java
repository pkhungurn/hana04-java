package hana04.formats.mmd.generic.impl.ik;

import hana04.formats.mmd.generic.MmdModelPose;
import hana04.formats.mmd.generic.api.MmdModel;
import hana04.formats.mmd.generic.api.ik.MmdIkChain;
import hana04.formats.mmd.generic.api.ik.MmdIkLink;
import hana04.formats.mmd.generic.api.ik.MmdIkSolver;
import hana04.formats.mmd.generic.api.ik.MmdIkSolverWithDebugFeatures;
import hana04.formats.mmd.util.EulerAngleDecompositionD;
import hana04.gfxbase.gfxtype.Matrix4dUtil;
import hana04.gfxbase.gfxtype.VecMathDUtil;
import hana04.gfxbase.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

public class MikuMikuFlexIkSolverLegacy implements MmdIkSolver, MmdIkSolverWithDebugFeatures {
  private static final Logger logger = LoggerFactory.getLogger(MmdIkSolverWithDebugFeatures.class);
  private static final double EPSILON = 1e-8;

  private int maxUpdateCount = 10000;
  private boolean loggingEnabled = false;
  private boolean doLogStuffs = false;

  @Override
  public void setLoggingEnabled(boolean loggingEnabled) {
    this.loggingEnabled = loggingEnabled;
  }

  @Override
  public void solve(MmdModelPose pose, MmdIkChain ikChain) {
    var model = pose.getModel();
    if (ikChain.boneIndex() < 0 || ikChain.boneIndex() >= model.bones().size()) {
      return;
    }
    if (ikChain.effectorBoneIndex() < 0 || ikChain.boneIndex() >= model.bones().size()) {
      return;
    }
    var targetBone = model.bones().get(ikChain.boneIndex());
    var effectorBone = model.bones().get(ikChain.effectorBoneIndex());

    doLogStuffs = loggingEnabled && targetBone.japaneseName().equals("右足ＩＫ");

    int iterationRemaining = maxUpdateCount;
    for (int i = 0; i < ikChain.iterationCount(); i++) {
      if (iterationRemaining <= 0) {
        break;
      }
      if (doLogStuffs) {
        logger.info("iteration = " + i);
      }
      for (var link : ikChain.ikLinks()) {
        if (iterationRemaining <= 0) {
          break;
        }
        if (link.boneIndex() < 0 || link.boneIndex() >= model.bones().size()) {
          iterationRemaining--;
          continue;
        }
        var linkBone = model.bones().get(link.boneIndex());
        if (doLogStuffs) {
          logger.info("link = " + linkBone.japaneseName());
        }
        var linkInverseTransform = Matrix4dUtil.inverse(pose.getGlobalTransform(linkBone.index()));
        var targetGlobalPosition = pose.getGlobalPosition(targetBone.index());
        var effectorGlobalPostiion = pose.getGlobalPosition(effectorBone.index());
        var targetLocalPosition = new Vector3d(Matrix4dUtil.transform(linkInverseTransform, targetGlobalPosition));
        var effectorLocalPosition = new Vector3d(Matrix4dUtil.transform(linkInverseTransform, effectorGlobalPostiion));
        if (targetLocalPosition.length() < EPSILON) {
          iterationRemaining--;
          if (doLogStuffs) {
            logger.info("aborted because targetLocalPosition.length() < EPSILON");
          }
          continue;
        }
        if (effectorLocalPosition.length() < EPSILON) {
          iterationRemaining--;
          if (doLogStuffs) {
            logger.info("aborted because effectorLocalPosition.length() < EPSILON");
          }
          continue;
        }
        var targetLocalVec = VecMathDUtil.normalize(targetLocalPosition);
        var effectorLocalVec = VecMathDUtil.normalize(effectorLocalPosition);

        var dotProd = MathUtil.clamp(targetLocalVec.dot(effectorLocalVec), -1.0, 1.0);
        var angleRad = MathUtil.clamp(
            Math.acos(dotProd), -ikChain.iterationAngleLimitRad(), ikChain.iterationAngleLimitRad());
        var axis = VecMathDUtil.cross(effectorLocalVec, targetLocalVec);
        if (axis.length() < EPSILON) {
          if (doLogStuffs) {
            logger.info("aborted because axis.length() < EPSILON. axis.length() = " + axis.length());
          }
          iterationRemaining--;
          continue;
        }
        axis.normalize();
        var diffRot = VecMathDUtil.createQuat(axis, (float) Math.toDegrees(angleRad));
        var currentRot = pose.getStoredRotation(linkBone.index());
        var newRot = VecMathDUtil.mul(currentRot, diffRot);
        newRot = restrictRotation(newRot, link, pose.getModel());
        pose.setStoredRotation(link.boneIndex(), newRot);
        iterationRemaining--;
      }
    }
  }

  @Override
  public void setMaxUpdateCount(int value) {
    this.maxUpdateCount = value;
  }

  private Quat4d restrictRotation(Quat4d q, MmdIkLink link, MmdModel model) {
    if (!link.isLimitingAngle()) {
      return q;
    }
    EulerAngleDecompositionD eulerAngles = EulerAngleDecompositionD.decompose(q);
    VecMathDUtil.normalizeEulerAngle(eulerAngles.axisRot);
    MathUtil.clamp(
        eulerAngles.axisRot,
        new Vector3d(link.angleLowerBoundRad()),
        new Vector3d(link.angleUpperBoundRad()));
    var output = new Quat4d();
    eulerAngles.toQuaternion(output);
    return output;
  }

  public static class Factory implements MmdIkSolver.Factory {
    @Override
    public MikuMikuFlexIkSolverLegacy create() {
      return new MikuMikuFlexIkSolverLegacy();
    }
  }
}
