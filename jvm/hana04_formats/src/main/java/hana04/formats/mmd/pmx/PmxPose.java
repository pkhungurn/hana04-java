
package hana04.formats.mmd.pmx;

import hana04.formats.mmd.vpd.VpdPose;
import hana04.gfxbase.gfxtype.TupleUtil;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

public class PmxPose {
  private static final Quat4f IDENTITY_QUATERNION = new Quat4f(0,0,0,1);
  private final PmxModel model;
  private final Vector3f[] boneDisplacements;
  private final Quat4f[] boneRotations;
  private final float[] morphWeights;

  public PmxPose(PmxModel model) {
    this.model = model;
    boneDisplacements = new Vector3f[model.getBoneCount()];
    boneRotations = new Quat4f[model.getBoneCount()];
    for (int i = 0; i < model.getBoneCount(); i++) {
      boneDisplacements[i] = new Vector3f(0,0,0);
      boneRotations[i] = new Quat4f(0, 0, 0, 1);
    }
    morphWeights = new float[model.getMorphCount()];
  }

  public PmxModel getModel() {
    return model;
  }

  public void clear() {
    for (int i = 0; i < model.getBoneCount(); i++) {
      boneDisplacements[i].set(0, 0, 0);
      boneRotations[i].set(0, 0, 0, 1);
    }
    for (int i = 0; i < model.getMorphCount(); i++) {
      morphWeights[i] = 0;
    }
  }

  public void copy(VpdPose pose) {
    clear();
    if (pose == null) {
      return;
    }

    Vector3f displacement = new Vector3f();
    Quat4f rotation = new Quat4f();

    for (String boneName : pose.boneNames()) {
      if (model.hasBone(boneName)) {
        pose.getBonePose(boneName, displacement, rotation);
        PmxBone bone = model.getBone(boneName);
        boneDisplacements[bone.boneIndex].set(displacement);
        boneRotations[bone.boneIndex].set(rotation);
      }
    }
    for (String morphName : pose.morphNames()) {
      if (model.hasMorph(morphName)) {
        float weight = pose.getMorphWeight(morphName);
        PmxMorph morph = model.getMorph(morphName);
        morphWeights[morph.morphIndex] = weight;
      }
    }
  }

  public void copy(PmxPose other) {
    if (other.model != this.model) {
      throw new RuntimeException("model are not the same");
    }
    for (int i = 0; i < model.getBoneCount(); i++) {
      boneDisplacements[i].set(other.boneDisplacements[i]);
      boneRotations[i].set(other.boneRotations[i]);
    }
    for (int i = 0; i < model.getMorphCount(); i++) {
      morphWeights[i] = other.morphWeights[i];
    }
  }

  public float getMorphWeight(int morphIndex) {
    return morphWeights[morphIndex];
  }

  public void getBoneDisplacement(int boneIndex, Tuple3f output) {
    PmxBone bone = model.getBone(boneIndex);
    if (bone.isCopyingTranslation()) {
      output.scale(bone.copyRate, boneDisplacements[bone.copySourceBoneIndex]);
    } else {
      output.set(boneDisplacements[boneIndex]);
    }
  }

  public void getStoredBoneRotation(int boneIndex, Quat4f output) {
    output.set(boneRotations[boneIndex]);
  }

  public void getBoneRotation(int boneIndex, Quat4f output) {
    PmxBone bone = model.getBone(boneIndex);
    if (bone.isCopyingRotation() && bone.copySourceBoneIndex >= 0) {
      Quat4f toCopy = new Quat4f();
      Quat4f parentRot = new Quat4f(boneRotations[bone.copySourceBoneIndex]);
      if (bone.copyRate < 0) {
        parentRot.inverse();
      }
      toCopy.interpolate(IDENTITY_QUATERNION, parentRot, Math.abs(bone.copyRate));
      output.set(boneRotations[boneIndex]);
      output.mul(toCopy);
    } else {
      output.set(boneRotations[boneIndex]);
    }
  }

  public void getStoredBonePose(int boneIndex, Tuple3f disp, Quat4f rot) {
    PmxBone bone = model.getBone(boneIndex);
    rot.set(boneRotations[bone.boneIndex]);
    disp.set(boneDisplacements[bone.boneIndex]);
  }

  public void getBonePose(int boneIndex, Tuple3f disp, Quat4f rot) {
    PmxBone bone = model.getBone(boneIndex);
    getBoneRotation(boneIndex, rot);
    getBoneDisplacement(boneIndex, disp);
  }

  public void getBoneTransform(int boneIndex, Matrix4f output) {
    Vector3f boneDisp = new Vector3f();
    Matrix4f xform = new Matrix4f();

    output.setIdentity();
    int current = boneIndex;
    Quat4f boneRot = new Quat4f();
    while (current >= 0) {
      PmxBone bone = model.getBone(current);
      getBonePose(current, boneDisp, boneRot);
      xform.setIdentity();
      if (!TupleUtil.isNaN(boneRot)) {
        xform.setRotation(boneRot);
      }
      if (!TupleUtil.isNaN(boneDisp)) {
        boneDisp.add(bone.displacementFromParent);
      } else {
        boneDisp.set(bone.displacementFromParent);
      }
      xform.setTranslation(boneDisp);
      output.mul(xform, output);
      if (current == bone.parentIndex)
        break;
      current = bone.parentIndex;
    }
  }

  public void getBoneWorldPosition(int boneIndex, Tuple3f output) {
    if (boneIndex < 0)
      output.set(0, 0, 0);

    Vector3f poseDisp = new Vector3f();
    Vector3f boneDisp = new Vector3f();
    Quat4f boneRot = new Quat4f();
    Point3f pos = new Point3f();
    Matrix4f xform = new Matrix4f();

    pos.set(0, 0, 0);
    int current = boneIndex;
    while (current >= 0) {
      PmxBone bone = model.getBone(current);
      getBonePose(current, boneDisp, boneRot);

      xform.setIdentity();
      xform.setRotation(boneRot);
      xform.transform(pos);
      pos.add(boneDisp);
      pos.add(bone.displacementFromParent);

      if (current == bone.parentIndex)
        break;
      current = bone.parentIndex;
    }

    output.set(pos);
  }

  public void getInverseBoneTransform(int boneIndex, Matrix4f output) {
    Vector3f poseDisp = new Vector3f();
    Vector3f boneDisp = new Vector3f();
    Quat4f boneRot = new Quat4f();
    Matrix4f xform = new Matrix4f();

    output.setIdentity();
    int current = boneIndex;
    while (current >= 0) {
      PmxBone bone = model.getBone(current);
      getBonePose(current, boneDisp, boneRot);

      boneRot.inverse();
      xform.setIdentity();
      xform.setRotation(boneRot);
      output.mul(xform);

      poseDisp.set(boneDisp);
      poseDisp.negate();
      poseDisp.sub(bone.displacementFromParent);
      xform.setIdentity();
      xform.setTranslation(poseDisp);
      output.mul(xform);

      if (current == bone.parentIndex)
        break;
      current = bone.parentIndex;
    }
  }

  public float getMorphWeights(int i) {
    return morphWeights[i];
  }

  public void getBoneWorldRotation(int boneIndex, Quat4f output) {
    output.set(0, 0, 0, 1);
    if (boneIndex == -1) {
      output.set(0, 0, 0, 1);
      return;
    }
    Quat4f boneRot = new Quat4f();
    int current = boneIndex;
    while (current >= 0) {
      PmxBone bone = model.getBone(current);
      getBoneRotation(current, boneRot);
      output.mul(boneRot, output);
      if (current == bone.parentIndex)
        break;
      current = bone.parentIndex;
    }
  }

  public static void interpolate(PmxPose p0, PmxPose p1, float alpha, PmxPose out) {
    if (p1.getModel() != p0.getModel() || p1.getModel() != out.getModel()) {
      throw new RuntimeException("poses to interpolate must be the same!");
    }
    for (int i = 0; i < p0.boneDisplacements.length; i++) {
      out.boneDisplacements[i].scale(1 - alpha, p0.boneDisplacements[i]);
      out.boneDisplacements[i].scaleAdd(alpha, p1.boneDisplacements[i], out.boneDisplacements[i]);
      out.boneRotations[i].interpolate(p0.boneRotations[i], p1.boneRotations[i], alpha);
    }
    for (int i = 0; i < p0.morphWeights.length; i++) {
      out.morphWeights[i] = p0.morphWeights[i] * (1 - alpha) + p1.morphWeights[i] * alpha;
    }
  }

  public void setBoneDisplacement(int boneIndex, Vector3f v) {
    boneDisplacements[boneIndex].set(v.x, v.y, v.z);
  }

  public void setBoneRotation(int boneIndex, Quat4f q) {
    boneRotations[boneIndex].set(q.x, q.y, q.z, q.w);
  }

  public void setMorphWeight(int morphIndex, float value) {
    morphWeights[morphIndex] = value;
  }

  public void copyTo(VpdPose pose) {
    pose.clobber();
    Vector3f boneDisp = new Vector3f();
    Quat4f boneRot = new Quat4f();
    for (int i = 0; i < model.getBoneCount(); i++) {
      PmxBone bone = model.getBone(i);
      getBonePose(i, boneDisp, boneRot);
      pose.setBonePose(bone.japaneseName, boneDisp, boneRot);
    }
    for (int i = 0; i < model.getMorphCount(); i++) {
      PmxMorph morph = model.getMorph(i);
      pose.setMorphWeight(morph.japaneseName, getMorphWeight(i));
    }
  }
}
