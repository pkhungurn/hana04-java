package hana04.formats.mmd.generic;

import com.google.common.collect.ImmutableList;
import hana04.formats.mmd.generic.api.MmdModel;
import hana04.gfxbase.gfxtype.Matrix4dUtil;
import hana04.gfxbase.gfxtype.VecMathDUtil;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;
import java.util.Arrays;
import java.util.stream.IntStream;

import static com.google.common.collect.ImmutableList.toImmutableList;

public class MmdModelPose {
  private final MmdModel model;

  private final double[] morphWeight;
  private final ImmutableList<Vector3d> translation;
  private final ImmutableList<Quat4d> rotation;

  public MmdModelPose(MmdModel model) {
    this.model = model;

    morphWeight = new double[model.morphs().size()];
    clearMorphWeights();
    translation =
        IntStream.range(0, model.bones().size()).mapToObj(i -> new Vector3d(0, 0, 0)).collect(toImmutableList());
    rotation =
        IntStream.range(0, model.bones().size()).mapToObj(i -> new Quat4d(0, 0, 0, 1)).collect(toImmutableList());
  }

  public MmdModel getModel() {
    return model;
  }

  public void clearMorphWeights() {
    Arrays.fill(morphWeight, 0.0);
  }

  public double getMorphWeight(int morphIndex) {
    return morphWeight[morphIndex];
  }

  public Vector3d getStoredTranslation(int boneIndex) {
    return new Vector3d(translation.get(boneIndex));
  }

  public Quat4d getStoredRotation(int boneIndex) {
    return new Quat4d(rotation.get(boneIndex));
  }

  public void setStoredTranslation(int boneIndex, Tuple3d newValue) {
    translation.get(boneIndex).set(newValue);
  }

  public void setStoredRotation(int boneIndex, Quat4d newValue) {
    var bone = model.bones().get(boneIndex);
    if (bone.fixedAxis().isPresent()) {
      rotation.get(boneIndex).set(VecMathDUtil.restrictToAxis(newValue, new Vector3d(bone.fixedAxis().get())));
    } else {
      rotation.get(boneIndex).set(newValue);
    }
  }

  public void setStoredMorphWeight(int morphIndex, float value) {
    morphWeight[morphIndex] = value;
  }

  public Vector3d getLocalTranslation(int boneIndex) {
    Vector3d result = new Vector3d(0, 0, 0);
    result.add(translation.get(boneIndex));
    result.add(new Vector3d(model.bones().get(boneIndex).translationFromParent()));
    return result;
  }

  public Quat4d getLocalRotation(int boneIndex) {
    return getStoredRotation(boneIndex);
  }

  public void copy(MmdModelPose other) {
    assert other.model.morphs().size() == this.model.morphs().size();
    assert other.model.bones().size() == this.model.bones().size();

    System.arraycopy(other.morphWeight, 0, this.morphWeight, 0, other.model.morphs().size());

    for (int i = 0; i < other.model.bones().size(); i++) {
      this.setStoredTranslation(i, other.getStoredTranslation(i));
      this.setStoredRotation(i, other.getStoredRotation(i));
    }
  }

  public void clear() {
    Arrays.fill(morphWeight, 0.0);
    Vector3d zero = new Vector3d(0, 0, 0);
    Quat4d identity = new Quat4d(0, 0, 0, 1);
    for (int i = 0; i < model.bones().size(); i++) {
      this.setStoredTranslation(i, zero);
      this.setStoredRotation(i, identity);
    }
  }

  public Matrix4d getLocalTransform(int boneIndex) {
    Matrix4d result = Matrix4dUtil.createIdentity();
    result.setRotation(getStoredRotation(boneIndex));
    var bone = model.bones().get(boneIndex);
    result.setTranslation(VecMathDUtil.add(
        getStoredTranslation(boneIndex),
        new Vector3d(bone.translationFromParent())));
    return result;
  }

  public Matrix4d getGlobalTransform(int boneIndex) {
    var bone = model.bones().get(boneIndex);
    Matrix4d result = Matrix4dUtil.createIdentity();
    while (true) {
      var localTransform = getLocalTransform(bone.index());
      result.mul(localTransform, result);
      if (bone.parentIndex().isEmpty()) {
        break;
      }
      bone = model.bones().get(bone.parentIndex().get());
    }
    return result;
  }

  public void performFuyo(int boneIndex) {
    var bone = model.bones().get(boneIndex);
    if (bone.fuyoInfo().isEmpty()) {
      return;
    }
    var combiningInfo = bone.fuyoInfo().get();
    int sourceIndex = combiningInfo.getSourceBoneIndex();
    if (sourceIndex < 0 || sourceIndex >= model.bones().size() || sourceIndex == boneIndex) {
      return;
    }
    if (combiningInfo.isCombiningTranslation()) {
      var sourceTranslation = getStoredTranslation(sourceIndex);
      var currentTranslation = getStoredTranslation(boneIndex);
      var newTranslation =
          VecMathDUtil.add(
              currentTranslation,
              VecMathDUtil.scale(combiningInfo.getCoefficient(), sourceTranslation));
      setStoredTranslation(boneIndex, newTranslation);
    }
    if (combiningInfo.isCombiningRotation()) {
      var sourceRotation = getStoredRotation(sourceIndex);
      var currentRotation = getStoredRotation(boneIndex);
      if (combiningInfo.getCoefficient() < 0) {
        sourceRotation.inverse();
      }
      sourceRotation =
          VecMathDUtil.interpolate(
              new Quat4d(0, 0, 0, 1),
              sourceRotation,
              Math.abs(combiningInfo.getCoefficient()));
      // We left multiply because the source is a "parent" of a kind.
      var newRotation = VecMathDUtil.mul(sourceRotation, currentRotation);
      setStoredRotation(boneIndex, newRotation);
    }
  }

  public Point3d getGlobalPosition(int boneIndex) {
    Matrix4d M = getGlobalTransform(boneIndex);
    return new Point3d(M.m03, M.m13, M.m23);
  }

  public Matrix4d getParentGlobalTransform(int boneIndex) {
    var bone = model.bones().get(boneIndex);
    if (bone.parentIndex().isEmpty()) {
      return Matrix4dUtil.createIdentity();
    } else {
      return getGlobalTransform(bone.parentIndex().get());
    }
  }
}