
package hana04.formats.unreal;

import javax.vecmath.Point3f;
import java.util.HashSet;

public class UnrealUtil {
  public static HashSet<String> getCommonBones(PskMesh psk0, PskMesh psk1) {
    HashSet<String> pskBones0 = new HashSet<String>();
    int pskBoneCount = psk0.getBoneCount();
    for (int i = 0; i < pskBoneCount; i++) {
      pskBones0.add(psk0.bones.get(i).name);
    }

    HashSet<String> pskBones1 = new HashSet<String>();
    int psaBoneCount = psk1.bones.size();
    for (int i = 0; i < psaBoneCount; i++) {
      pskBones1.add(psk1.bones.get(i).name);
    }

    pskBones0.retainAll(pskBones1);

    return pskBones0;
  }

  public static float computeSkeletalDistances(PskMesh psk0, PskMesh psk1) {
    HashSet<String> commonBones = getCommonBones(psk0, psk1);
    float sum = 0;
    Point3f pskPos = new Point3f();
    Point3f psaPos = new Point3f();
    for (String boneName : commonBones) {
      int pskIndex = psk0.getBoneIndex(boneName);
      psk0.getBoneWorldPosition(pskIndex, pskPos);
      int psaIndex = psk1.getBoneIndex(boneName);
      psk1.getBoneWorldPosition(psaIndex, psaPos);
      sum += pskPos.distance(psaPos);
    }
    return sum + (psk0.getBoneCount() + psk1.getBoneCount() - 2 * commonBones.size()) * 100;
  }
}
