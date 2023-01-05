package hana04.formats.mmd.generic;

import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.google.common.collect.ImmutableList;
import hana04.formats.mmd.generic.api.MmdBone;
import hana04.formats.mmd.generic.api.MmdModel;
import hana04.formats.mmd.generic.api.ik.MmdIkSolver;
import hana04.formats.mmd.generic.api.ik.MmdIkSolverWithDebugFeatures;
import hana04.formats.mmd.generic.api.physics.MmdPhysics;
import hana04.formats.mmd.generic.api.physics.MmdPhysicsWithDebugFeatures;
import hana04.formats.mmd.vpd.VpdPose;

import javax.vecmath.Matrix4d;
import javax.vecmath.Quat4d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableListMultimap.toImmutableListMultimap;

public class MmdAnimatedInstance {
  private final MmdModel model;
  private final MmdIkSolver ikSolver;
  private final MmdPhysics physics;
  private final MmdModelPose inputPose;
  private final MmdModelPose outputPose;
  private boolean physicsEnabled = true;
  private boolean ikEnabled = true;
  private final BoneOrder boneOrder;

  public MmdAnimatedInstance(
      MmdModel model,
      MmdIkSolver.Factory ikSolverFactory,
      MmdPhysics.Factory physicsFactory) {
    this.model = model;
    this.ikSolver = ikSolverFactory.create();
    this.physics = physicsFactory.create(model);
    this.inputPose = new MmdModelPose(model);
    this.outputPose = new MmdModelPose(model);
    this.boneOrder = new BoneOrder(model);
  }

  public boolean isIkEnabled() {
    return ikEnabled;
  }

  public void setIkEnabled(boolean ikEnabled) {
    this.ikEnabled = ikEnabled;
  }

  public void setPhysicsEnabled(boolean value) {
    physicsEnabled = value;
  }

  public boolean getPhysicsEnable() {
    return physicsEnabled;
  }

  public void setInputPost(VpdPose vpdPose) {
    inputPose.clear();
    Vector3f translation = new Vector3f();
    Quat4f rotation = new Quat4f();
    vpdPose.boneNames().forEach(boneName -> {
      model.boneIndex(boneName).ifPresent(boneIndex -> {
        vpdPose.getBonePose(boneName, translation, rotation);
        inputPose.setStoredRotation(boneIndex, new Quat4d(rotation));
        inputPose.setStoredTranslation(boneIndex, new Vector3d(translation));
      });
    });
    vpdPose.morphNames().forEach(morphName -> {
      model.morphIndex(morphName).ifPresent(morphIndex -> {
        inputPose.setStoredMorphWeight(morphIndex, vpdPose.getMorphWeight(morphName));
      });
    });
  }

  public void getOutputPose(VpdPose vpdPose) {
    vpdPose.clear();
    for (int i = 0; i < model.bones().size(); i++) {
      var bone = model.bones().get(i);
      var rotation = outputPose.getStoredRotation(i);
      var translation = outputPose.getStoredTranslation(i);
      vpdPose.setBonePose(bone.japaneseName(), new Vector3f(translation), new Quat4f(rotation));
    }
    for (int i = 0; i < model.morphs().size(); i++) {
      vpdPose.setMorphWeight(model.morphs().get(i).japaneseName(), (float) outputPose.getMorphWeight(i));
    }
  }

  public void update(float elapsedTime, boolean forcePhysicsToFollowPose) {
    outputPose.clear();
    outputPose.copy(inputPose);
    boolean printStuffs = lastMaxUpdateCount != ikMaxUpdateCount;
    if (ikSolver instanceof MmdIkSolverWithDebugFeatures) {
      MmdIkSolverWithDebugFeatures ikSovlerWithDebugFeatures = (MmdIkSolverWithDebugFeatures) ikSolver;
      ikSovlerWithDebugFeatures.setLoggingEnabled(printStuffs);
    }
    boneOrder.beforePhysicsLevels.forEach(this::updateIkAndFuyo);
    if (physicsEnabled) {
      physics.update(outputPose, elapsedTime, forcePhysicsToFollowPose);
    }
    boneOrder.afterPhysicsLevels.forEach(this::updateIkAndFuyo);
    lastMaxUpdateCount = ikMaxUpdateCount;
  }

  private void updateIkAndFuyo(ImmutableList<? extends MmdBone> bones) {
    if (ikEnabled) {
      for (var bone : bones) {
        if (bone.ikChain().isEmpty()) {
          continue;
        }
        ikSolver.solve(outputPose, bone.ikChain().get());
      }
    }
    for (var bone : bones) {
      if (bone.fuyoInfo().isEmpty()) {
        continue;
      }
      outputPose.performFuyo(bone.index());
    }
  }

  public void dispose() {
    physics.dispose();
  }

  static class BoneOrder {
    ImmutableList<ImmutableList<? extends MmdBone>> beforePhysicsLevels;
    ImmutableList<ImmutableList<? extends MmdBone>> afterPhysicsLevels;

    BoneOrder(MmdModel model) {
      beforePhysicsLevels = model.bones().stream()
          .filter(bone -> !bone.transformsAfterPhysics())
          .collect(toImmutableListMultimap(
              MmdBone::transformLevel,
              bone -> bone))
          .asMap()
          .values()
          .stream()
          .map(bones -> bones.stream().sorted(MmdBone::compareTransformOrder).collect(toImmutableList()))
          .collect(toImmutableList());
      afterPhysicsLevels = model.bones().stream()
          .filter(MmdBone::transformsAfterPhysics)
          .collect(toImmutableListMultimap(
              MmdBone::transformLevel,
              bone -> bone))
          .asMap()
          .values()
          .stream()
          .map(bones -> bones.stream().sorted(MmdBone::compareTransformOrder).collect(toImmutableList()))
          .collect(toImmutableList());
    }
  }

  private int ikMaxUpdateCount = 100000;
  private int lastMaxUpdateCount = 100000;

  public void setIkMaxUpdateCount(int value) {
    this.ikMaxUpdateCount = value;
    if (ikSolver instanceof MmdIkSolverWithDebugFeatures) {
      MmdIkSolverWithDebugFeatures ikSolverWithDebugFeatures = (MmdIkSolverWithDebugFeatures) ikSolver;
      ikSolverWithDebugFeatures.setMaxUpdateCount(value);
    }
  }

  public Optional<Matrix4d> getBoneGlobaTransform(String boneName) {
    if (model.boneIndex(boneName).isPresent()) {
      return Optional.of(outputPose.getGlobalTransform(model.boneIndex(boneName).get()));
    } else {
      return Optional.empty();
    }
  }

  public Optional<List<btRigidBody>> getBtRigidBodies() {
    if (physics instanceof MmdPhysicsWithDebugFeatures) {
      MmdPhysicsWithDebugFeatures mmdPhysicsWithDebugFeatures = (MmdPhysicsWithDebugFeatures) physics;
      return Optional.of(mmdPhysicsWithDebugFeatures.getBtRigidBodies());
    } else {
      return Optional.empty();
    }
  }

  public MmdModelPose getOutputPose() {
    return outputPose;
  }

  public MmdModelPose getInputPose() {
    return inputPose;
  }

  public MmdPhysics getPhysics() {
    return physics;
  }

  public MmdIkSolver getIkSolver() {
    return ikSolver;
  }
}
