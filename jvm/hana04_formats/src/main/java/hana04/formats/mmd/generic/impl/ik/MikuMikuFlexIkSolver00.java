package hana04.formats.mmd.generic.impl.ik;

import hana04.formats.mmd.generic.MmdModelPose;
import hana04.formats.mmd.generic.api.MmdBone;
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

public class MikuMikuFlexIkSolver00 implements MmdIkSolver, MmdIkSolverWithDebugFeatures {
  private static final Logger logger = LoggerFactory.getLogger(MikuMikuFlexIkSolver00.class);
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
    doLogStuffs = loggingEnabled;

    int iterationRemaining = maxUpdateCount;
    for (int iterationIndex = 0; iterationIndex < ikChain.iterationCount(); iterationIndex++) {
      if (iterationRemaining <= 0) {
        break;
      }
      if (doLogStuffs) {
        logger.info("iteration = " + iterationIndex);
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
        if (isOnlyX(link, linkBone, ikChain)) {
          updateLinkWithAxis(pose, ikChain, link, iterationIndex, new Vector3d(1, 0, 0));
        } else if (isOnlyY(link, linkBone, ikChain)) {
          updateLinkWithAxis(pose, ikChain, link, iterationIndex, new Vector3d(0, 1, 0));
        } else if (isOnlyZ(link, linkBone, ikChain)) {
          updateLinkWithAxis(pose, ikChain, link, iterationIndex, new Vector3d(0, 0, 1));
        } else {
          updateLinkNoAxis(pose, ikChain, link, iterationIndex);
        }
        iterationRemaining--;
      }
    }
  }

  private void updateLinkWithAxis(
      MmdModelPose pose,
      MmdIkChain ikChain,
      MmdIkLink link,
      int iterationIndex,
      Vector3d axis) {
    var model = pose.getModel();
    var targetBone = model.bones().get(ikChain.boneIndex());
    var effectorBone = model.bones().get(ikChain.effectorBoneIndex());
    var linkBone = model.bones().get(link.boneIndex());

    var linkInverseTransform = Matrix4dUtil.inverse(pose.getGlobalTransform(linkBone.index()));
    var targetGlobalPosition = pose.getGlobalPosition(targetBone.index());
    var effectorGlobalPostiion = pose.getGlobalPosition(effectorBone.index());
    var targetLocalPosition = new Vector3d(Matrix4dUtil.transform(linkInverseTransform, targetGlobalPosition));
    var effectorLocalPosition = new Vector3d(Matrix4dUtil.transform(linkInverseTransform, effectorGlobalPostiion));

    if (doLogStuffs) {
      logger.info("targetLocalPosition = " + targetLocalPosition);
      logger.info("effectorLocalPosition = " + effectorLocalPosition);
    }

    var targetLocalVec = projectToPlane(targetLocalPosition, axis);
    if (targetLocalVec.length() < EPSILON) {
      if (doLogStuffs) {
        logger.info("aborted because targetLocalVec.length() < EPSILON");
      }
      return;
    }
    var effectorLocalVec = projectToPlane(effectorLocalPosition, axis);
    if (effectorLocalVec.length() < EPSILON) {
      if (doLogStuffs) {
        logger.info("aborted because effectorLocalVec.length() < EPSILON");
      }
      return;
    }
    targetLocalVec = VecMathDUtil.normalize(targetLocalVec);
    effectorLocalVec = VecMathDUtil.normalize(effectorLocalVec);
    if (doLogStuffs) {
      logger.info("targetLocalVec = " + targetLocalVec);
      logger.info("effectorLocalVec = " + effectorLocalVec);
    }
    double dotProd = MathUtil.clamp(targetLocalVec.dot(effectorLocalVec), -1.0, 1.0);
    double angleRad = MathUtil.clamp(
        Math.acos(dotProd), -ikChain.iterationAngleLimitRad(), ikChain.iterationAngleLimitRad());
    axis = VecMathDUtil.cross(effectorLocalVec, targetLocalVec);
    if (axis.length() < EPSILON) {
      if (doLogStuffs) {
        logger.info("aborted because axis.length() < EPSILON. axis.length() = " + axis.length());
      }
      return;
    }
    axis = VecMathDUtil.normalize(axis);
    var diffRot = VecMathDUtil.createQuat(axis, Math.toDegrees(angleRad));
    var currentRot = pose.getStoredRotation(linkBone.index());
    var newRot = VecMathDUtil.mul(currentRot, diffRot);
    newRot = restrictRotation(newRot, link, pose.getModel(), iterationIndex, ikChain.iterationCount());
    pose.setStoredRotation(link.boneIndex(), newRot);
  }

  private Vector3d projectToPlane(Vector3d v, Vector3d n) {
    return VecMathDUtil.sub(v, VecMathDUtil.scale(n.dot(v), n));
  }

  private void updateLinkNoAxis(
      MmdModelPose pose,
      MmdIkChain ikChain,
      MmdIkLink link,
      int iterationIndex) {
    var model = pose.getModel();
    var targetBone = model.bones().get(ikChain.boneIndex());
    var effectorBone = model.bones().get(ikChain.effectorBoneIndex());
    var linkBone = model.bones().get(link.boneIndex());
    var linkInverseTransform = Matrix4dUtil.inverse(pose.getGlobalTransform(linkBone.index()));
    var targetGlobalPosition = pose.getGlobalPosition(targetBone.index());
    var effectorGlobalPostiion = pose.getGlobalPosition(effectorBone.index());
    var targetLocalPosition = new Vector3d(Matrix4dUtil.transform(linkInverseTransform, targetGlobalPosition));
    var effectorLocalPosition = new Vector3d(Matrix4dUtil.transform(linkInverseTransform, effectorGlobalPostiion));
    if (targetLocalPosition.length() < EPSILON) {
      if (doLogStuffs) {
        logger.info("aborted because targetLocalPosition.length() < EPSILON");
      }
      return;
    }
    if (effectorLocalPosition.length() < EPSILON) {
      if (doLogStuffs) {
        logger.info("aborted because effectorLocalPosition.length() < EPSILON");
      }
      return;
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
      return;
    }
    axis.normalize();
    var diffRot = VecMathDUtil.createQuat(axis, Math.toDegrees(angleRad));
    var currentRot = pose.getStoredRotation(linkBone.index());
    var newRot = VecMathDUtil.mul(currentRot, diffRot);
    newRot = restrictRotation(newRot, link, pose.getModel(), iterationIndex, ikChain.iterationCount());
    pose.setStoredRotation(link.boneIndex(), newRot);
  }

  boolean isOnlyX(MmdIkLink link, MmdBone bone, MmdIkChain ikChain) {
    var lowerBound = getAngleLowerBoundRad(link, bone, 0, ikChain.iterationCount());
    var upperBound = getAngleUpperBoundRad(link, bone, 0, ikChain.iterationCount());
    return isLimitingAngle(link, bone)
        && (lowerBound.x != 0 || upperBound.x != 0)
        && (lowerBound.y == 0 || upperBound.y == 0)
        && (lowerBound.z == 0 || upperBound.z == 0);
  }

  boolean isOnlyY(MmdIkLink link, MmdBone bone, MmdIkChain ikChain) {
    var lowerBound = getAngleLowerBoundRad(link, bone, 0, ikChain.iterationCount());
    var upperBound = getAngleUpperBoundRad(link, bone, 0, ikChain.iterationCount());
    return isLimitingAngle(link, bone)
        && (lowerBound.x == 0 || upperBound.x == 0)
        && (lowerBound.y != 0 || upperBound.y != 0)
        && (lowerBound.z == 0 || upperBound.z == 0);
  }

  boolean isOnlyZ(MmdIkLink link, MmdBone bone, MmdIkChain ikChain) {
    var lowerBound = getAngleLowerBoundRad(link, bone, 0, ikChain.iterationCount());
    var upperBound = getAngleUpperBoundRad(link, bone, 0, ikChain.iterationCount());
    return isLimitingAngle(link, bone)
        && (lowerBound.x == 0 || upperBound.x == 0)
        && (lowerBound.y == 0 || upperBound.y == 0)
        && (lowerBound.z != 0 || upperBound.z != 0);
  }

  @Override
  public void setMaxUpdateCount(int value) {
    this.maxUpdateCount = value;
  }

  private boolean isLimitingAngle(MmdIkLink link, MmdBone bone) {
    if (bone.isKnee()) {
      return true;
    }
    return link.isLimitingAngle();
  }

  private Vector3d getAngleLowerBoundRad(MmdIkLink link, MmdBone bone, int iteration, int iterationCount) {
    if (bone.isKnee()) {
      return new Vector3d(Math.toRadians(-180), 0, 0);
    } else {
      return new Vector3d(link.angleLowerBoundRad());
    }
  }

  private Vector3d getAngleUpperBoundRad(MmdIkLink link, MmdBone bone, int iteration, int iterationCount) {
    if (bone.isKnee()) {
      double alpha = Math.min(1.0, iteration * 1.0 / (9.0 * iterationCount / 10));
      return new Vector3d(Math.toRadians((1 - alpha) * (-45) + (alpha) * (0)), 0, 0);
    } else {
      return new Vector3d(link.angleUpperBoundRad());
    }
  }

  private Quat4d restrictRotation(Quat4d q, MmdIkLink link, MmdModel model, int iteration, int iterationCount) {
    if (link.boneIndex() < 0 || link.boneIndex() >= model.bones().size()) {
      return q;
    }
    var bone = model.bones().get(link.boneIndex());
    if (!isLimitingAngle(link, bone)) {
      return q;
    }
    EulerAngleDecompositionD eulerAngles = EulerAngleDecompositionD.decompose(q);
    VecMathDUtil.normalizeEulerAngle(eulerAngles.axisRot);
    if (doLogStuffs) {
      logger.info("[before] eulerAngles = " + eulerAngles.axisRot);
    }
    MathUtil.clamp(
        eulerAngles.axisRot,
        getAngleLowerBoundRad(link, bone, iteration, iterationCount),
        getAngleUpperBoundRad(link, bone, iteration, iterationCount));
    if (doLogStuffs) {
      logger.info("[after] eulerAngles = " + eulerAngles.axisRot);
      logger.info(getAngleLowerBoundRad(link, bone, iteration, iterationCount).toString());
      logger.info(getAngleUpperBoundRad(link, bone, iteration, iterationCount).toString());
    }
    var output = new Quat4d();
    eulerAngles.toQuaternion(output);
    return output;
  }

  public static class Factory implements MmdIkSolver.Factory {
    @Override
    public MikuMikuFlexIkSolver00 create() {
      return new MikuMikuFlexIkSolver00();
    }
  }
}
