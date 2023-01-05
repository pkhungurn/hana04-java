
package hana04.formats.unreal;

import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;

public class PskPose {
  public final PskMesh pskMesh;
  public final ArrayList<VJointPos> jointXforms;

  public PskPose(PskMesh pskMesh) {
    this.pskMesh = pskMesh;

    jointXforms = new ArrayList<VJointPos>();
    for (int i = 0; i < pskMesh.getBoneCount(); i++) {
      VJointPos jp = new VJointPos();
      jointXforms.add(jp);
    }
    clear();
  }

  public void getBoneWorldPosition(int boneIndex, Point3f output) {
    Vector3f poseDisp = new Vector3f();
    Vector3f boneDisp = new Vector3f();
    Quat4f rotation = new Quat4f();
    Matrix4f xform = new Matrix4f();

    output.set(0, 0, 0);
    int current = boneIndex;
    while (current >= 0) {
      VBone bone = pskMesh.bones.get(current);
      VJointPos posePos = jointXforms.get(current);

      rotation.set(0, 0, 0, 1);
      rotation.mul(posePos.orientation);
      xform.setIdentity();
      xform.setRotation(rotation);
      xform.transform(output);

      output.add(posePos.position);

      current = bone.parentIndex;
    }
  }

  public void getBoneTransform(int boneIndex, Matrix4f output) {
    Quat4f rotation = new Quat4f();
    Matrix4f xform = new Matrix4f();

    output.setIdentity();
    int current = boneIndex;
    while (current >= 0) {
      VBone bone = pskMesh.bones.get(current);
      VJointPos jointPos = jointXforms.get(current);
      xform.setIdentity();
      xform.setRotation(jointPos.orientation);
      xform.setTranslation(jointPos.position);
      output.mul(xform, output);

      //System.out.println("jointPos.orientation = " + jointPos.orientation);
      //System.out.println("jointPos.orientation = " + jointPos.position);
      //System.out.println("current = " + current);
      //System.out.println(xform);

      current = bone.parentIndex;
    }
  }

  public void setPsaPose(PsaPose psaPose) {
    setPsaPose(psaPose, false);
  }

  public void setPsaPose(PsaPose psaPose, boolean rotationOnly) {
    if (psaPose == null) {
      return;
    }
    for (String boneName : psaPose.jointXforms.keySet()) {
      int boneIndex = pskMesh.getBoneIndex(boneName);
      if (boneIndex >= 0) {
        Pair<Vector3f, Quat4f> pair = psaPose.jointXforms.get(boneName);
        VJointPos jp = jointXforms.get(boneIndex);
        if (!rotationOnly) {
          jp.position.set(pair.getLeft());
        }
        jp.orientation.set(pair.getRight());
        if (boneIndex == 0) {
          //jp.orientation.y *= -1;
        } else {
          jp.orientation.x *= -1;
          jp.orientation.y *= -1;
          jp.orientation.z *= -1;
        }
      }
    }
  }

  public void clear() {
    for (int i = 0; i < pskMesh.getBoneCount(); i++) {
      VJointPos jp = jointXforms.get(i);
      VJointPos bonePos = pskMesh.bones.get(i).bonePose;
      jp.position.set(bonePos.position);
      jp.orientation.set(bonePos.orientation);
    }
  }
}
