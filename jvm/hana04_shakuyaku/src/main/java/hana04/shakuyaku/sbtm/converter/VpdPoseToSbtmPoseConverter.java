package hana04.shakuyaku.sbtm.converter;

import hana04.formats.mmd.vpd.VpdPose;
import hana04.shakuyaku.sbtm.SbtmPose;

import javax.vecmath.Quat4d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

public class VpdPoseToSbtmPoseConverter {
  public static SbtmPose convert(VpdPose pose) {
    SbtmPose output = new SbtmPose();

    Vector3f displacement = new Vector3f();
    Quat4f rotation = new Quat4f();
    for (String boneName : pose.boneNames()) {
      pose.getBonePose(boneName, displacement, rotation);
      output.setBonePose(boneName, new Vector3d(displacement), new Quat4d(rotation));
    }

    for (String morphName : pose.morphNames()) {
      output.setMorphPose(morphName, pose.getMorphWeight(morphName));
    }

    return output;
  }
}
