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

public class MikuMikuFlexIkSolver01 implements MmdIkSolver, MmdIkSolverWithDebugFeatures {
  private static final Logger logger = LoggerFactory.getLogger(MikuMikuFlexIkSolver01.class);
  private static final double EPSILON = 1e-8;

  private int maxUpdateCount = 100000;
  private boolean loggingEnabled = false;
  private boolean doLogStuffs = false;

  @Override
  public void setLoggingEnabled(boolean loggingEnabled) {
    this.loggingEnabled = loggingEnabled;
  }

  @Override
  public void solve(MmdModelPose pose, MmdIkChain ikChain) {
    doLogStuffs = loggingEnabled;
    var model = pose.getModel();
    if (ikChain.boneIndex() < 0 || ikChain.boneIndex() >= model.bones().size()) {
      return;
    }
    if (ikChain.effectorBoneIndex() < 0 || ikChain.boneIndex() >= model.bones().size()) {
      return;
    }

    var isLegIk = isLegIkChain(ikChain, pose.getModel());
    if (doLogStuffs) {
      logger.info("isLegIk = " + isLegIk);
    }
    boolean analyticSolveSuccessful = false;
    if (isLegIk) {
      analyticSolveSuccessful = analyticallySolveForKneeAngle(pose, ikChain);
    }

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
        if (isLegIk && linkBone.isKnee() && analyticSolveSuccessful) {
          if (doLogStuffs) {
            logger.info("Skip knee bone update because of successful analytical solve.");
          }
          iterationRemaining--;
          continue;
        }
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

  private boolean analyticallySolveForKneeAngle(MmdModelPose pose, MmdIkChain ikChain) {
    var model = pose.getModel();

    var targetBone = model.bones().get(ikChain.boneIndex());
    var ankleBone = model.bones().get(ikChain.effectorBoneIndex());
    var kneeBone = ikChain
        .ikLinks()
        .stream()
        .map(ikLink -> model.bones().get(ikLink.boneIndex()))
        .filter(bone -> bone.japaneseName().contains("ひざ"))
        .findFirst().get();
    var legBone = ikChain
        .ikLinks()
        .stream()
        .map(ikLink -> model.bones().get(ikLink.boneIndex()))
        .filter(bone -> bone.japaneseName().contains("足"))
        .findFirst().get();

    var xAxis = new Vector3d(1, 0, 0);
    var anklePos = projectToPlane(new Vector3d(ankleBone.restPosition()), xAxis);
    var kneePos = projectToPlane(new Vector3d(kneeBone.restPosition()), xAxis);
    var legPos = projectToPlane(new Vector3d(legBone.restPosition()), xAxis);

    var ankleSubKnee = VecMathDUtil.sub(anklePos, kneePos);
    var legSubKnee = VecMathDUtil.sub(legPos, kneePos);
    var thighLength = ankleSubKnee.length();
    var shinLength = legSubKnee.length();
    if (thighLength < EPSILON || shinLength < EPSILON) {
      return false;
    }
    var crossProd = VecMathDUtil.cross(ankleSubKnee, legSubKnee).x;
    if (crossProd >= 0) {
      return false;
    }
    var ankleSubKneeUnit = VecMathDUtil.normalize(ankleSubKnee);
    var legSubKneeUnit = VecMathDUtil.normalize(legSubKnee);
    var kneeAngle = Math.acos(MathUtil.clamp(ankleSubKneeUnit.dot(legSubKneeUnit), -1.0, 1.0));

    var legBoneGlobalPosiion = pose.getGlobalPosition(legBone.index());
    var targetBoneGlobalPosition = pose.getGlobalPosition(targetBone.index());
    double targetDistance = legBoneGlobalPosiion.distance(targetBoneGlobalPosition);
    if (doLogStuffs) {
      logger.info("targetDistance = " + targetDistance);
    }

    double maxDistance = ankleBone.restPosition().distance(legBone.restPosition());
    if (doLogStuffs) {
      logger.info("maxDistance = " + maxDistance);
    }
    if (targetDistance >= maxDistance) {
      pose.setStoredRotation(kneeBone.index(), new Quat4d(0, 0, 0, 1));
      return true;
    }

    Vector3d ankleSubLeg =
        VecMathDUtil.sub(new Vector3d(ankleBone.restPosition()), new Vector3d(legBone.restPosition()));
    double xDelta = ankleSubLeg.x;
    double yzDelta = Math.abs(shinLength - thighLength);
    double minDistance = Math.sqrt(xDelta * xDelta + yzDelta * yzDelta);
    if (doLogStuffs) {
      logger.info("minDistance = " + minDistance);
    }

    if (targetDistance <= minDistance) {
      var newRotation = VecMathDUtil.createQuat(xAxis, Math.toDegrees(-kneeAngle));
      pose.setStoredRotation(kneeBone.index(), newRotation);
      return true;
    }

    var projectedTargetDistance = Math.sqrt(targetDistance * targetDistance - xDelta * xDelta);
    var p = projectedTargetDistance;
    var s = shinLength;
    var t = thighLength;
    var cosTheta = (p * p - t * t - s * s) / (2 * t * s);
    if (doLogStuffs) {
      logger.info("p = " + p);
      logger.info("t = " + t);
      logger.info("s = " + s);
      logger.info("cosTheta = " + cosTheta);
    }
    var theta = Math.acos(MathUtil.clamp(cosTheta, -1.0, 1.0));
    var newKneeAngle = Math.PI - theta;
    var delta = newKneeAngle - kneeAngle;
    var newRotation = VecMathDUtil.createQuat(xAxis, Math.toDegrees(delta));
    if (doLogStuffs) {
      logger.info("kneeAngle = " + Math.toDegrees(kneeAngle));
      logger.info("newKneeAngle = " + Math.toDegrees(newKneeAngle));
      logger.info("delta = " + Math.toDegrees(delta));
    }
    pose.setStoredRotation(kneeBone.index(), newRotation);
    if (doLogStuffs) {
      var ankleGlobalPosition = pose.getGlobalPosition(ankleBone.index());
      logger.info("leg-ankle distance = " + ankleGlobalPosition.distance(legBoneGlobalPosiion));
      logger.info("targetDistance = " + targetDistance);
    }
    return true;
  }

  private boolean isLegIkChain(MmdIkChain ikChain, MmdModel model) {
    var targetBone = model.bones().get(ikChain.boneIndex());
    if (!targetBone.japaneseName().contains("足ＩＫ")) {
      return false;
    }

    var effectorBone = model.bones().get(ikChain.effectorBoneIndex());
    if (!effectorBone.japaneseName().contains("足首")) {
      return false;
    }

    if (ikChain.ikLinks().size() != 2) {
      return false;
    }

    long kneeCount = ikChain
        .ikLinks()
        .stream()
        .map(ikLink -> model.bones().get(ikLink.boneIndex()))
        .map(MmdBone::japaneseName)
        .filter(name -> name.contains("ひざ"))
        .count();
    if (kneeCount != 1) {
      return false;
    }

    long legCount = ikChain
        .ikLinks()
        .stream()
        .map(ikLink -> model.bones().get(ikLink.boneIndex()))
        .map(MmdBone::japaneseName)
        .filter(name -> name.contains("足"))
        .count();
    if (legCount != 1) {
      return false;
    }

    return true;
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
    if (doLogStuffs) {
      logger.info("axis = " + axis);
    }
    var diffRot = VecMathDUtil.createQuat(axis, Math.toDegrees(angleRad));
    var currentRot = pose.getStoredRotation(linkBone.index());
    var newRot = VecMathDUtil.mul(currentRot, diffRot);
    newRot = restrictRotation(newRot, link, pose.getModel(), iterationIndex, ikChain.iterationCount());
    pose.setStoredRotation(link.boneIndex(), newRot);
  }

  private static Vector3d projectToPlane(Vector3d v, Vector3d n) {
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
    public MikuMikuFlexIkSolver01 create() {
      return new MikuMikuFlexIkSolver01();
    }
  }
}
