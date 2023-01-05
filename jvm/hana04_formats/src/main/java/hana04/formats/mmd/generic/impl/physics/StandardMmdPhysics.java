package hana04.formats.mmd.generic.impl.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btGeneric6DofSpringConstraint;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import hana04.formats.mmd.common.MmdRigidBodyShapeType;
import hana04.formats.mmd.common.MmdRigidBodyType;
import hana04.formats.mmd.generic.MmdModelPose;
import hana04.formats.mmd.generic.api.MmdModel;
import hana04.formats.mmd.generic.api.physics.MmdJoint;
import hana04.formats.mmd.generic.api.physics.MmdPhysics;
import hana04.formats.mmd.generic.api.physics.MmdPhysicsWithDebugFeatures;
import hana04.formats.mmd.generic.api.physics.MmdRigidBody;
import hana04.formats.mmd.util.GdxMathUtil;
import hana04.gfxbase.gfxtype.Matrix4dUtil;
import hana04.gfxbase.gfxtype.TupleUtil;
import hana04.gfxbase.gfxtype.VecMathFUtil;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class StandardMmdPhysics implements MmdPhysics, MmdPhysicsWithDebugFeatures {
  private final MmdModel model;
  private btDiscreteDynamicsWorld dynamicsWorld;
  private btDefaultCollisionConfiguration collisionConfiguration;
  private btCollisionDispatcher dispatcher;
  private btBroadphaseInterface overlappingPairCache;
  private btSequentialImpulseConstraintSolver solver;
  private Matrix4f[] rb2Bone;
  private Matrix4f[] bone2Rb;
  private ArrayList<btRigidBody> rigidBodies = new ArrayList<>();
  private btGeneric6DofSpringConstraint[] constraints;
  private btCollisionShape[] collisionShapes;
  private int numSubSteps = 10;

  public static class Factory implements MmdPhysics.Factory {
    @Override
    public MmdPhysics create(MmdModel model) {
      return new StandardMmdPhysics(model);
    }
  }

  public StandardMmdPhysics(MmdModel model) {
    this.model = model;
    createDynamicsWorld();
    createRigidBodies();
    createConstraints();
  }

  public int getNumSubSteps() {
    return numSubSteps;
  }

  @Override
  public void setNumSubSteps(int numSubSteps) {
    this.numSubSteps = numSubSteps;
  }

  @Override
  public List<btRigidBody> getBtRigidBodies() {
    return rigidBodies;
  }

  private void createDynamicsWorld() {
    collisionConfiguration = new btDefaultCollisionConfiguration();
    dispatcher = new btCollisionDispatcher(collisionConfiguration);
    overlappingPairCache = new btDbvtBroadphase();
    solver = new btSequentialImpulseConstraintSolver();
    dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
    dynamicsWorld.setGravity(new Vector3(0, -9.8f * 10.0f, 0));
  }

  public void setGravity(float x, float y, float z) {
    dynamicsWorld.setGravity(new Vector3(x, y, z));
  }

  public void setGravity(Vector3f gravity) {
    dynamicsWorld.setGravity(new Vector3(gravity.x, gravity.y, gravity.z));
  }

  @Override
  public void update(MmdModelPose pose, float elapsedTimeInSeconds, boolean forcePhysicsToFollowPose) {
    setPose(pose);
    dynamicsWorld.stepSimulation(elapsedTimeInSeconds, numSubSteps);
    getPose(pose);
  }

  public void dispose() {
    for (int i = 0; i < model.joints().size(); i++) {
      if (constraints[i] != null) {
        dynamicsWorld.removeConstraint(constraints[i]);
        constraints[i].dispose();
        constraints[i] = null;
      }
    }
    for (int i = 0; i < model.rigidBodies().size(); i++) {
      dynamicsWorld.removeRigidBody(rigidBodies.get(i));
      if (rigidBodies.get(i).getMotionState() != null) {
        rigidBodies.get(i).getMotionState().dispose();
      }
      rigidBodies.get(i).dispose();
      if (collisionShapes[i] != null) {
        collisionShapes[i].dispose();
        collisionShapes[i] = null;
      }
    }
    rigidBodies.clear();

    dynamicsWorld.dispose();
    solver.dispose();
    overlappingPairCache.dispose();
    dispatcher.dispose();
    collisionConfiguration.dispose();

    solver = null;
    overlappingPairCache = null;
    dispatcher = null;
    collisionConfiguration = null;
    dynamicsWorld = null;
  }

  private void createRigidBodies() {
    Matrix4f rbMatrix = new Matrix4f();
    Quat4f rotation = new Quat4f();
    Vector3f translation = new Vector3f();

    rigidBodies.clear();
    rb2Bone = new Matrix4f[model.rigidBodies().size()];
    bone2Rb = new Matrix4f[model.rigidBodies().size()];
    collisionShapes = new btCollisionShape[model.rigidBodies().size()];
    for (int i = 0; i < model.rigidBodies().size(); i++) {
      MmdRigidBody mmdRb = model.rigidBodies().get(i);

      rb2Bone[i] = new Matrix4f();
      rb2Bone[i].setIdentity();
      VecMathFUtil.yawPitchRollToQuaternion(
          mmdRb.rotation().y, mmdRb.rotation().x, mmdRb.rotation().z, rotation);
      rb2Bone[i].setRotation(rotation);
      translation.set(mmdRb.position());
      if (mmdRb.boneIndex() >= 0 && mmdRb.boneIndex() < model.bones().size()) {
        translation.sub(model.bones().get(mmdRb.boneIndex()).restPosition());
      }
      rb2Bone[i].setTranslation(translation);
      bone2Rb[i] = new Matrix4f();
      bone2Rb[i].invert(rb2Bone[i]);

      btCollisionShape collisionShape = null;
      if (mmdRb.shape() == MmdRigidBodyShapeType.Sphere) {
        collisionShape = new btSphereShape(mmdRb.width());
      } else if (mmdRb.shape() == MmdRigidBodyShapeType.Box) {
        collisionShape = new btBoxShape(new Vector3(mmdRb.width(), mmdRb.height(), mmdRb.depth()));
      } else if (mmdRb.shape() == MmdRigidBodyShapeType.Capsule) {
        collisionShape = new btCapsuleShape(mmdRb.width(), mmdRb.height());
      } else {
        throw new RuntimeException("Invalid rigid body shape type");
      }
      collisionShapes[i] = collisionShape;

      float mass = (mmdRb.type() == MmdRigidBodyType.FollowBone) ? 0 : mmdRb.mass();
      Vector3 localInertia = new Vector3(0, 0, 0);
      if (mass != 0) {
        collisionShape.calculateLocalInertia(mass, localInertia);
      }

      rbMatrix.setIdentity();
      rbMatrix.setTranslation(mmdRb.position());
      rbMatrix.setRotation(rotation);
      btDefaultMotionState motionState = new btDefaultMotionState(GdxMathUtil.convertToGdxMatrix(rbMatrix));

      btRigidBody rigidBody = new btRigidBody(mass, motionState, collisionShape, localInertia);
      rigidBody.setRestitution(mmdRb.restitution());
      rigidBody.setFriction(mmdRb.friction());
      rigidBody.setDamping(mmdRb.positionDamping(), mmdRb.rotationDamping());
      rigidBody.setActivationState(Collision.DISABLE_DEACTIVATION);
      if (mmdRb.type() == MmdRigidBodyType.FollowBone) {
        rigidBody.setCollisionFlags(btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT
            | rigidBody.getCollisionFlags());
      }
      short group = (short) Math.pow(2, mmdRb.groupIndex());
      dynamicsWorld.addRigidBody(rigidBody, group, (short) mmdRb.hitWithGroupFlags());
      rigidBodies.add(rigidBody);
    }
  }

  private void createConstraints() {
    Matrix4f bodyAWorldInv = new Matrix4f();
    Matrix4f bodyBWorldInv = new Matrix4f();
    Matrix4f xformA = new Matrix4f();
    Matrix4f xformB = new Matrix4f();

    constraints = new btGeneric6DofSpringConstraint[model.joints().size()];
    for (int i = 0; i < model.joints().size(); i++) {
      MmdJoint mmdJoint = model.joints().get(i);

      int bodyAIndex = mmdJoint.rigidBodyIndices().get(0);
      if (bodyAIndex < 0) {
        continue;
      }
      btRigidBody bodyA = rigidBodies.get(bodyAIndex);
      GdxMathUtil.gdxToMatrix4f(bodyA.getWorldTransform(), bodyAWorldInv);
      bodyAWorldInv.invert();

      int bodyBIndex = mmdJoint.rigidBodyIndices().get(1);
      if (bodyBIndex < 0) {
        continue;
      }
      btRigidBody bodyB = rigidBodies.get(bodyBIndex);
      GdxMathUtil.gdxToMatrix4f(bodyB.getWorldTransform(), bodyBWorldInv);
      bodyBWorldInv.invert();

      Quat4f jointRotation = new Quat4f();
      VecMathFUtil.yawPitchRollToQuaternion(
          mmdJoint.rotation().y, mmdJoint.rotation().x, mmdJoint.rotation().z, jointRotation);
      Matrix4f jointXform4f = new Matrix4f();
      jointXform4f.setIdentity();
      jointXform4f.setRotation(jointRotation);
      jointXform4f.setTranslation(mmdJoint.position());

      xformA.mul(bodyAWorldInv, jointXform4f);
      xformB.mul(bodyBWorldInv, jointXform4f);

      btGeneric6DofSpringConstraint constraint = new btGeneric6DofSpringConstraint(
          bodyA, bodyB, GdxMathUtil.convertToGdxMatrix(xformA), GdxMathUtil.convertToGdxMatrix(xformB), true);

      constraint.setLinearLowerLimit(new Vector3(
          mmdJoint.linearLowerLimit().x, mmdJoint.linearLowerLimit().y, mmdJoint.linearLowerLimit().z));
      constraint.setLinearUpperLimit(new Vector3(
          mmdJoint.linearUpperLimit().x, mmdJoint.linearUpperLimit().y, mmdJoint.linearUpperLimit().z));

      constraint.setAngularLowerLimit(new Vector3(
          mmdJoint.angularLowerLimit().x, mmdJoint.angularLowerLimit().y, mmdJoint.angularLowerLimit().z));
      constraint.setAngularUpperLimit(new Vector3(
          mmdJoint.angularUpperLimit().x, mmdJoint.angularUpperLimit().y, mmdJoint.angularUpperLimit().z));

      for (int j = 0; j < 3; j++) {
        if (mmdJoint.springLinearStiffness().get(j) != 0) {
          constraint.setStiffness(j, mmdJoint.springLinearStiffness().get(j));
          constraint.enableSpring(j, true);
        }
        if (mmdJoint.springAngularStiffness().get(j) != 0) {
          constraint.setStiffness(j + 3, mmdJoint.springAngularStiffness().get(j));
          constraint.enableSpring(j + 3, true);
        }
      }

      constraint.calculateTransforms();
      constraint.setEquilibriumPoint();
      dynamicsWorld.addConstraint(constraint);
      constraints[i] = constraint;
    }
  }

  public void setPose(MmdModelPose pose) {
    if (pose.getModel() != model) {
      throw new RuntimeException("outPose's model is not the same as physics's model");
    }

    Matrix4f boneXform = new Matrix4f();
    Matrix4f rbXform = new Matrix4f();
    Matrix4 m = new Matrix4();

    for (int i = 0; i < rigidBodies.size(); i++) {
      var mmdRb = model.rigidBodies().get(i);
      if (mmdRb.boneIndex() < 0 || mmdRb.boneIndex() >= model.bones().size()) {
        continue;
      }
      if (mmdRb.type() == MmdRigidBodyType.FollowBone) {
        boneXform.set(pose.getGlobalTransform(mmdRb.boneIndex()));
        rbXform.mul(boneXform, rb2Bone[i]);
        Matrix4 gdx = GdxMathUtil.convertToGdxMatrix(rbXform);
        rigidBodies.get(i).getMotionState().setWorldTransform(gdx);
      }
      if (mmdRb.boneIndex() >= -1 && mmdRb.type() == MmdRigidBodyType.PhysicsWithBonePosition) {
        boneXform.set(pose.getGlobalTransform(mmdRb.boneIndex()));
        rbXform.mul(boneXform, rb2Bone[i]);
        rigidBodies.get(i).getMotionState().getWorldTransform(m);
        m.setTranslation(rbXform.m03, rbXform.m13, rbXform.m23);
        rigidBodies.get(i).getMotionState().setWorldTransform(m);
      }
    }
  }

  public void update(MmdModelPose pose, float elapsedTimeInSeconds) {
    setPose(pose);
    dynamicsWorld.stepSimulation(elapsedTimeInSeconds, numSubSteps);
    getPose(pose);
  }

  private void getPose(MmdModelPose pose) {
    Matrix4f boneXform = new Matrix4f();
    Matrix4f parentBoneXformInv = new Matrix4f();
    Matrix4f rbXform = new Matrix4f();
    Vector3f translation = new Vector3f();
    Quat4f rotation = new Quat4f();
    Matrix4 gdxWorldXform = new Matrix4();

    for (int i = 0; i < rigidBodies.size(); i++) {
      var mmdRb = model.rigidBodies().get(i);
      if (mmdRb.boneIndex() < 0 || mmdRb.boneIndex() >= model.bones().size()) {
        continue;
      }
      if (mmdRb.type() == MmdRigidBodyType.FollowBone) {
        continue;
      }
      var bone = model.bones().get(mmdRb.boneIndex());

      rigidBodies.get(i).getMotionState().getWorldTransform(gdxWorldXform);
      if (Float.isNaN(gdxWorldXform.val[Matrix4.M00])) {
        continue;
      }
      GdxMathUtil.gdxToMatrix4f(gdxWorldXform, rbXform);

      if (bone.parentIndex().isPresent()) {
        parentBoneXformInv.set(Matrix4dUtil.inverse(pose.getGlobalTransform(bone.parentIndex().get())));
      } else {
        parentBoneXformInv.setIdentity();
      }

      boneXform.set(parentBoneXformInv);
      boneXform.mul(rbXform);
      boneXform.mul(bone2Rb[i]);

      boneXform.get(translation);
      boneXform.get(rotation);
      rotation.normalize();
      translation.sub(bone.translationFromParent());

      if (!mmdRb.type().equals(MmdRigidBodyType.PhysicsWithBonePosition)) {
        if (!TupleUtil.isNaN(translation)) {
          pose.setStoredTranslation(mmdRb.boneIndex(), new Vector3d(translation));
        } else {
          pose.setStoredTranslation(mmdRb.boneIndex(), new Vector3d(0, 0, 0));
        }
      }
      if (!TupleUtil.isNaN(rotation)) {
        pose.setStoredRotation(mmdRb.boneIndex(), new Quat4d(rotation));
      } else {
        pose.setStoredRotation(mmdRb.boneIndex(), new Quat4d(0, 0, 0, 1));
      }
    }
  }
}
