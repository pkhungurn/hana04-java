
package hana04.formats.mmd.pmx;

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
import hana04.formats.mmd.util.GdxMathUtil;
import hana04.gfxbase.gfxtype.TupleUtil;
import hana04.gfxbase.gfxtype.VecMathFUtil;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class PmxModelPhysics {
  public PmxModel model;
  public btDiscreteDynamicsWorld dynamicsWorld;
  public btDefaultCollisionConfiguration collisionConfiguration;
  public btCollisionDispatcher dispatcher;
  public btBroadphaseInterface overlappingPairCache;
  public btSequentialImpulseConstraintSolver solver;
  public Matrix4f[] rb2Bone;
  public Matrix4f[] bone2Rb;
  public btRigidBody[] rigidBodies;
  public btGeneric6DofSpringConstraint[] constraints;
  public btCollisionShape[] collisionShapes;

  public PmxModelPhysics(PmxModel model) {
    this.model = model;
    createDynamicsWorld();
    createRigidBodies();
    createConstraints();
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

  public void dispose() {
    for (int i = 0; i < model.getJointCount(); i++) {
      dynamicsWorld.removeConstraint(constraints[i]);
      constraints[i].dispose();
      constraints[i] = null;
    }
    for (int i = 0; i < model.getRigidBodyCount(); i++) {
      dynamicsWorld.removeRigidBody(rigidBodies[i]);
      if (rigidBodies[i].getMotionState() != null) {
        rigidBodies[i].getMotionState().dispose();
      }
      rigidBodies[i].dispose();
      rigidBodies[i] = null;
      collisionShapes[i].dispose();
      collisionShapes[i] = null;
    }

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

    rb2Bone = new Matrix4f[model.getRigidBodyCount()];
    bone2Rb = new Matrix4f[model.getRigidBodyCount()];
    rigidBodies = new btRigidBody[model.getRigidBodyCount()];
    collisionShapes = new btCollisionShape[model.getRigidBodyCount()];
    for (int i = 0; i < model.getRigidBodyCount(); i++) {
      PmxRigidBody pmxRb = model.getRigidBody(i);

      rb2Bone[i] = new Matrix4f();
      rb2Bone[i].setIdentity();
      VecMathFUtil.yawPitchRollToQuaternion(
        pmxRb.rotation.y, pmxRb.rotation.x, pmxRb.rotation.z, rotation);
      rb2Bone[i].setRotation(rotation);
      translation.set(pmxRb.position);
      if (pmxRb.boneIndex >= 0) {
        translation.sub(model.getBone(pmxRb.boneIndex).position);
      }
      rb2Bone[i].setTranslation(translation);
      bone2Rb[i] = new Matrix4f();
      bone2Rb[i].invert(rb2Bone[i]);

      btCollisionShape collisionShape = null;
      if (pmxRb.shape == MmdRigidBodyShapeType.Sphere) {
        collisionShape = new btSphereShape(pmxRb.width);
      } else if (pmxRb.shape == MmdRigidBodyShapeType.Box) {
        collisionShape = new btBoxShape(new Vector3(pmxRb.width, pmxRb.height, pmxRb.depth));
      } else if (pmxRb.shape == MmdRigidBodyShapeType.Capsule) {
        collisionShape = new btCapsuleShape(pmxRb.width, pmxRb.height);
      } else {
        throw new RuntimeException("Invalid rigid body shape type");
      }
      collisionShapes[i] = collisionShape;

      float mass = (pmxRb.type == MmdRigidBodyType.FollowBone) ? 0 : pmxRb.mass;
      Vector3 localInertia = new Vector3();
      collisionShape.calculateLocalInertia(mass, localInertia);

      rbMatrix.setIdentity();
      rbMatrix.setTranslation(pmxRb.position);
      rbMatrix.setRotation(rotation);
      btDefaultMotionState motionState = new btDefaultMotionState(GdxMathUtil.convertToGdxMatrix(rbMatrix));

      btRigidBody rigidBody = new btRigidBody(mass, motionState, collisionShape, localInertia);
      rigidBody.setRestitution(pmxRb.restitution);
      rigidBody.setFriction(pmxRb.friction);
      rigidBody.setDamping(pmxRb.positionDamping, pmxRb.rotationDamping);
      rigidBody.setActivationState(Collision.DISABLE_DEACTIVATION);
      if (pmxRb.type == MmdRigidBodyType.FollowBone) {
        rigidBody.setCollisionFlags(btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT | rigidBody.getCollisionFlags());
      }
      short group = (short) Math.pow(2, pmxRb.groupIndex);
      dynamicsWorld.addRigidBody(rigidBody, group, (short) pmxRb.hitWithGroupFlags);
      rigidBodies[i] = rigidBody;
    }
  }

  private void createConstraints() {
    Matrix4f bodyAWorldInv = new Matrix4f();
    Matrix4f bodyBWorldInv = new Matrix4f();
    Matrix4f xformA = new Matrix4f();
    Matrix4f xformB = new Matrix4f();

    constraints = new btGeneric6DofSpringConstraint[model.getJointCount()];
    for (int i = 0; i < model.getJointCount(); i++) {
      PmxJoint pmxJoint = model.getJoint(i);

      int bodyAIndex = pmxJoint.rigidBodies[0];
      if (bodyAIndex < 0)
        continue;
      btRigidBody bodyA = rigidBodies[bodyAIndex];
      GdxMathUtil.gdxToMatrix4f(bodyA.getWorldTransform(), bodyAWorldInv);
      bodyAWorldInv.invert();

      int bodyBIndex = pmxJoint.rigidBodies[1];
      if (bodyBIndex < 0)
        continue;
      btRigidBody bodyB = rigidBodies[bodyBIndex];
      GdxMathUtil.gdxToMatrix4f(bodyB.getWorldTransform(), bodyBWorldInv);
      bodyBWorldInv.invert();

      Quat4f jointRotation = new Quat4f();
      VecMathFUtil.yawPitchRollToQuaternion(
        pmxJoint.rotation.y, pmxJoint.rotation.x, pmxJoint.rotation.z, jointRotation);
      Matrix4f jointXform4f = new Matrix4f();
      jointXform4f.setIdentity();
      jointXform4f.setRotation(jointRotation);
      jointXform4f.setTranslation(pmxJoint.position);

      xformA.mul(bodyAWorldInv, jointXform4f);
      xformB.mul(bodyBWorldInv, jointXform4f);

      btGeneric6DofSpringConstraint constraint = new btGeneric6DofSpringConstraint(
        bodyA, bodyB, GdxMathUtil.convertToGdxMatrix(xformA), GdxMathUtil.convertToGdxMatrix(xformB), true);

      constraint.setLinearLowerLimit(new Vector3(
        pmxJoint.linearLowerLimit.x, pmxJoint.linearLowerLimit.y, pmxJoint.linearLowerLimit.z));
      constraint.setLinearUpperLimit(new Vector3(
        pmxJoint.linearUpperLimit.x, pmxJoint.linearUpperLimit.y, pmxJoint.linearUpperLimit.z));

      constraint.setAngularLowerLimit(new Vector3(
        pmxJoint.angularLowerLimit.x, pmxJoint.angularLowerLimit.y, pmxJoint.angularLowerLimit.z));
      constraint.setAngularUpperLimit(new Vector3(
        pmxJoint.angularUpperLimit.x, pmxJoint.angularUpperLimit.y, pmxJoint.angularUpperLimit.z));

      for (int j = 0; j < 3; j++) {
        constraint.setStiffness(j, pmxJoint.springLinearStiffness[j]);
        constraint.enableSpring(j, true);
        constraint.setStiffness(j + 3, pmxJoint.springAngularStiffness[j]);
        constraint.enableSpring(j + 3, true);
      }

      constraint.calculateTransforms();
      constraint.setEquilibriumPoint();
      dynamicsWorld.addConstraint(constraint);
      constraints[i] = constraint;
    }
  }

  public void setPose(PmxPose pose) {
    if (pose.getModel() != model) {
      throw new RuntimeException("outPose's model is not the same as physics's model");
    }

    Matrix4f boneXform = new Matrix4f();
    Matrix4f rbXform = new Matrix4f();
    Point3f bonePosition = new Point3f();
    Point3f rbPosition = new Point3f();
    Matrix4 m = new Matrix4();

    for (int i = 0; i < rigidBodies.length; i++) {
      PmxRigidBody pmxRb = model.getRigidBody(i);
      if (pmxRb.type == MmdRigidBodyType.FollowBone) {
        pose.getBoneTransform(pmxRb.boneIndex, boneXform);
        rbXform.mul(boneXform, rb2Bone[i]);
        Matrix4 gdx = GdxMathUtil.convertToGdxMatrix(rbXform);
        rigidBodies[i].getMotionState().setWorldTransform(gdx);
      }
      if (pmxRb.boneIndex != -1 && pmxRb.type == MmdRigidBodyType.PhysicsWithBonePosition) {
        pose.getBoneWorldPosition(pmxRb.boneIndex, bonePosition);
        rbPosition.set(pmxRb.position);
        rbPosition.sub(model.getBone(pmxRb.boneIndex).position);
        rbPosition.add(bonePosition);
        rigidBodies[i].getMotionState().getWorldTransform(m);
        m.setTranslation(rbPosition.x, rbPosition.y, rbPosition.z);
        rigidBodies[i].getMotionState().setWorldTransform(m);
      }
    }
  }

  public void update(PmxPose pose, float elapsedTimeInSeconds) {
    setPose(pose);
    dynamicsWorld.stepSimulation(elapsedTimeInSeconds, 10);
    getPose(pose);
  }

  public void updateWithNoPhysics(PmxPose pose) {
    for (int i = 0; i < rigidBodies.length; i++) {
      PmxRigidBody pmxRb = model.getRigidBody(i);
      if (pmxRb.boneIndex < 0) {
        continue;
      }
      if (pmxRb.type == MmdRigidBodyType.FollowBone) {
        continue;
      }
      if (!pmxRb.type.equals(MmdRigidBodyType.PhysicsWithBonePosition)) {
        pose.setBoneDisplacement(pmxRb.boneIndex, new Vector3f(0, 0, 0));
      }
      pose.setBoneRotation(pmxRb.boneIndex, new Quat4f(0, 0, 0, 1));
    }
  }

  private void getPose(PmxPose pose) {
    Matrix4f boneXform = new Matrix4f();
    Matrix4f parentBoneXformInv = new Matrix4f();
    Matrix4f rbXform = new Matrix4f();
    Vector3f translation = new Vector3f();
    Quat4f rotation = new Quat4f();
    Matrix4 gdxWorldXform = new Matrix4();

    for (int i = 0; i < rigidBodies.length; i++) {
      PmxRigidBody pmxRb = model.getRigidBody(i);
      if (pmxRb.boneIndex == -1) continue;
      if (pmxRb.type == MmdRigidBodyType.FollowBone) continue;
      PmxBone bone = model.getBone(pmxRb.boneIndex);

      rigidBodies[i].getMotionState().getWorldTransform(gdxWorldXform);
      if (Float.isNaN(gdxWorldXform.val[Matrix4.M00])) continue;
      GdxMathUtil.gdxToMatrix4f(gdxWorldXform, rbXform);

      if (bone.parentIndex != -1) {
        pose.getInverseBoneTransform(bone.parentIndex, parentBoneXformInv);
      } else {
        parentBoneXformInv.setIdentity();
      }

      boneXform.set(parentBoneXformInv);
      boneXform.mul(rbXform);
      boneXform.mul(bone2Rb[i]);

      boneXform.get(translation);
      boneXform.get(rotation);
      translation.sub(bone.displacementFromParent);

      if (!pmxRb.type.equals(MmdRigidBodyType.PhysicsWithBonePosition)) {
        if (!TupleUtil.isNaN(translation)) {
          pose.setBoneDisplacement(pmxRb.boneIndex, translation);
        } else {
          pose.setBoneDisplacement(pmxRb.boneIndex, new Vector3f(0, 0, 0));
        }
      }
      if (!TupleUtil.isNaN(rotation)) {
        pose.setBoneRotation(pmxRb.boneIndex, rotation);
      } else {
        pose.setBoneRotation(pmxRb.boneIndex, new Quat4f(0, 0, 0, 1));
      }
    }
  }


  /*
  private void alignRigidBodies(PmxPose pose) {
    Point3f bonePosition = new Point3f();
    Point3f rbPosition = new Point3f();
    Matrix4 m = new Matrix4();

    for (int i = 0; i < rigidBodies.length; i++) {
      PmxRigidBody pmxRb = model.getRigidBody(i);
      if (pmxRb.boneIndex != -1 && pmxRb.type == PmdRigidBodyType.PhysicsWithBonePosition) {
        pose.getBoneWorldPosition(pmxRb.boneIndex, bonePosition);
        rbPosition.set(pmxRb.position);
        rbPosition.sub(model.getBone(pmxRb.boneIndex).position);
        rbPosition.add(bonePosition);
        rigidBodies[i].getMotionState().getWorldTransform(m);
        m.setTranslation(rbPosition.x, rbPosition.y, rbPosition.z);
        rigidBodies[i].getMotionState().setWorldTransform(m);
      }
    }
  }
  */

  public void resetPhysics(PmxPose pose) {
    for (int i = 0; i < constraints.length; i++) {
      dynamicsWorld.removeConstraint(constraints[i]);
    }

    for (int i = 0; i < rigidBodies.length; i++) {
      dynamicsWorld.removeRigidBody(rigidBodies[i]);
    }

    Matrix4f boneXform = new Matrix4f();
    Matrix4f rbXform = new Matrix4f();
    for (int i = 0; i < rigidBodies.length; i++) {
      PmxRigidBody pmxRb = model.getRigidBody(i);
      pose.getBoneTransform(pmxRb.boneIndex, boneXform);
      rbXform.mul(boneXform, rb2Bone[i]);
      Matrix4 gdx = GdxMathUtil.convertToGdxMatrix(rbXform);
      rigidBodies[i].getMotionState().setWorldTransform(gdx);
      rigidBodies[i].clearForces();
      rigidBodies[i].setLinearVelocity(Vector3.Zero);
      rigidBodies[i].setAngularVelocity(Vector3.Zero);
    }

    for (int i = 0; i < rigidBodies.length; i++) {
      PmxRigidBody pmxRb = model.getRigidBody(i);
      btRigidBody rb = rigidBodies[i];
      short group = (short) Math.pow(2, pmxRb.groupIndex);
      dynamicsWorld.addRigidBody(rb, group, (short) pmxRb.hitWithGroupFlags);
    }

    for (int i = 0; i < constraints.length; i++) {
      dynamicsWorld.addConstraint(constraints[i]);
    }

    for (int i = 0; i < rigidBodies.length; i++) {
      rigidBodies[i].activate();
    }

    dynamicsWorld.clearForces();
    dynamicsWorld.getConstraintSolver().reset();
  }
}
