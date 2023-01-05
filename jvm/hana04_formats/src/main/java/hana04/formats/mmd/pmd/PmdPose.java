package hana04.formats.mmd.pmd;

import hana04.formats.mmd.vpd.VpdPose;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.nio.FloatBuffer;

public class PmdPose {
  private final FloatBuffer boneDisplacements;
  private final FloatBuffer boneRotations;
  private final FloatBuffer morphWeights;
  final PmdModel model;

  public PmdPose(PmdModel model) {
    this.model = model;
    boneDisplacements = FloatBuffer.allocate(model.bones.size() * 3);
    boneRotations = FloatBuffer.allocate(model.bones.size() * 4);
    morphWeights = FloatBuffer.allocate(model.morphs.size());
    for (int i = 0; i < model.bones.size(); i++) {
      setBoneDisplacement(i, 0, 0, 0);
      setBoneRotation(i, 0, 0, 0, 1);
    }
    for (int i = 0; i < model.morphs.size(); i++) {
      setMorphWeight(i, 0);
    }
  }

  public void setBoneDisplacement(int boneIndex, float x, float y, float z) {
    boneDisplacements.put(boneIndex * 3 + 0, x);
    boneDisplacements.put(boneIndex * 3 + 1, y);
    boneDisplacements.put(boneIndex * 3 + 2, z);
  }

  public void setBoneDisplacement(int boneIndex, Vector3f v) {
    boneDisplacements.put(boneIndex * 3 + 0, v.x);
    boneDisplacements.put(boneIndex * 3 + 1, v.y);
    boneDisplacements.put(boneIndex * 3 + 2, v.z);
  }

  public void getBoneDisplacement(int boneIndex, Vector3f v) {
    v.x = boneDisplacements.get(boneIndex * 3 + 0);
    v.y = boneDisplacements.get(boneIndex * 3 + 1);
    v.z = boneDisplacements.get(boneIndex * 3 + 2);
  }

  public void setBoneRotation(int boneIndex, float x, float y, float z, float w) {
    boneRotations.put(boneIndex * 4 + 0, x);
    boneRotations.put(boneIndex * 4 + 1, y);
    boneRotations.put(boneIndex * 4 + 2, z);
    boneRotations.put(boneIndex * 4 + 3, w);
  }

  public void setBoneRotation(int boneIndex, Quat4f q) {
    boneRotations.put(boneIndex * 4 + 0, q.x);
    boneRotations.put(boneIndex * 4 + 1, q.y);
    boneRotations.put(boneIndex * 4 + 2, q.z);
    boneRotations.put(boneIndex * 4 + 3, q.w);
  }

  private void getRawBoneRotation(int boneIndex, Quat4f q) {
    q.x = boneRotations.get(boneIndex * 4 + 0);
    q.y = boneRotations.get(boneIndex * 4 + 1);
    q.z = boneRotations.get(boneIndex * 4 + 2);
    q.w = boneRotations.get(boneIndex * 4 + 3);

  }

  public void getBoneRotation(int boneIndex, Quat4f q) {
    if (model.bones.get(boneIndex).type.equals(PmdBoneType.CopyingRotation)) {
      int parentIndex = model.bones.get(boneIndex).influenceInfo;
      Quat4f parentRot = new Quat4f();
      getRawBoneRotation(parentIndex, parentRot);
      getRawBoneRotation(boneIndex, q);
      q.mul(parentRot);
    } else {
      q.x = boneRotations.get(boneIndex * 4 + 0);
      q.y = boneRotations.get(boneIndex * 4 + 1);
      q.z = boneRotations.get(boneIndex * 4 + 2);
      q.w = boneRotations.get(boneIndex * 4 + 3);
    }
  }

  public void setMorphWeight(int morphIndex, float value) {
    morphWeights.put(morphIndex, value);
  }

  public float getMorphWeight(int morphIndex) {
    return morphWeights.get(morphIndex);
  }

  public int boneCount() {
    return model.bones.size();
  }

  public int morphCount() {
    return model.morphs.size();
  }

  public void clear() {
    for (int i = 0; i < boneCount(); i++) {
      setBoneDisplacement(i, 0, 0, 0);
      setBoneRotation(i, 0, 0, 0, 1);
    }
    for (int i = 0; i < morphCount(); i++) {
      setMorphWeight(i, 0);
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
      pose.getBonePose(boneName, displacement, rotation);
      int index = model.getBoneIndex(boneName);
      if (index < 0) {
        continue;
      }
      setBoneDisplacement(index, displacement);
      setBoneRotation(index, rotation);
    }
    for (String morphName : pose.morphNames()) {
      float weight = pose.getMorphWeight(morphName);
      int index = model.getMorphIndex(morphName);
      if (index < 0) {
        continue;
      }
      setMorphWeight(index, weight);
    }
  }

  public void copy(PmdPose other) {
    if (other.model != this.model) {
      throw new RuntimeException("model are not the same");
    }

    for (int i = 0; i < model.bones.size(); i++) {
      boneDisplacements.put(3 * i + 0, other.boneDisplacements.get(3 * i + 0));
      boneDisplacements.put(3 * i + 1, other.boneDisplacements.get(3 * i + 1));
      boneDisplacements.put(3 * i + 2, other.boneDisplacements.get(3 * i + 2));
      boneRotations.put(4 * i + 0, other.boneRotations.get(4 * i + 0));
      boneRotations.put(4 * i + 1, other.boneRotations.get(4 * i + 1));
      boneRotations.put(4 * i + 2, other.boneRotations.get(4 * i + 2));
      boneRotations.put(4 * i + 3, other.boneRotations.get(4 * i + 3));
    }

    for (int i = 0; i < morphWeights.capacity(); i++) {
      morphWeights.put(i, other.morphWeights.get(i));
    }
  }

  public void getBoneTransform(int boneIndex, Matrix4f output) {
    Vector3f poseDisp = new Vector3f();
    Vector3f boneDisp = new Vector3f();
    Quat4f rotation = new Quat4f();
    Matrix4f xform = new Matrix4f();

    output.setIdentity();
    int current = boneIndex;
    while (current >= 0) {
      PmdBone bone = model.bones.get(current);

      xform.setIdentity();
      getBoneRotation(current, rotation);
      xform.setRotation(rotation);
      getBoneDisplacement(current, poseDisp);
      bone.getDisplacementFromParent(boneDisp);
      poseDisp.add(boneDisp);
      xform.setTranslation(poseDisp);
      output.mul(xform, output);

      current = bone.parentIndex;
    }
  }

  public void getInverseBoneTransform(int boneIndex, Matrix4f output) {
    Vector3f poseDisp = new Vector3f();
    Vector3f boneDisp = new Vector3f();
    Quat4f rotation = new Quat4f();
    Matrix4f xform = new Matrix4f();

    output.setIdentity();
    int current = boneIndex;
    while (current >= 0) {
      PmdBone bone = model.bones.get(current);

      //if (current != boneIndex)
      //{
      getBoneRotation(current, rotation);
      rotation.inverse();
      xform.setIdentity();
      xform.setRotation(rotation);
      output.mul(xform);
      //}

      getBoneDisplacement(current, poseDisp);
      poseDisp.negate();
      bone.getDisplacementFromParent(boneDisp);
      poseDisp.sub(boneDisp);
      xform.setIdentity();
      xform.setTranslation(poseDisp);
      output.mul(xform);

      current = bone.parentIndex;
    }
  }

  public void getBoneWorldPosition(int boneIndex, Point3f output) {
    Vector3f poseDisp = new Vector3f();
    Vector3f boneDisp = new Vector3f();
    Quat4f rotation = new Quat4f();
    Matrix4f xform = new Matrix4f();

    output.set(0, 0, 0);
    int current = boneIndex;
    while (current >= 0) {
      xform.setIdentity();

      PmdBone bone = model.bones.get(current);
      getBoneRotation(current, rotation);
      xform.setRotation(rotation);
      xform.transform(output);

      bone.getDisplacementFromParent(boneDisp);
      output.add(boneDisp);
      getBoneDisplacement(current, poseDisp);
      output.add(poseDisp);

      current = bone.parentIndex;
    }
  }

  public PmdModel getModel() {
    return model;
  }

  public void copyTo(VpdPose pose) {
    pose.clobber();
    Vector3f disp = new Vector3f();
    Quat4f rot = new Quat4f();
    for (int i = 0; i < model.bones.size(); i++) {
      PmdBone bone = model.bones.get(i);
      getBoneDisplacement(i, disp);
      getBoneRotation(i, rot);
      pose.setBonePose(bone.japaneseName, disp, rot);
    }
    for (int i = 0; i < model.morphs.size(); i++) {
      PmdMorph morph = model.morphs.get(i);
      float weight = getMorphWeight(i);
      pose.setMorphWeight(morph.japaneseName, weight);
    }
  }
}
