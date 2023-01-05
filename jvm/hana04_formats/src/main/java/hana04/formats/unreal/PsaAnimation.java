package hana04.formats.unreal;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.Collections;

public class PsaAnimation {
  public final PsaPackage psaPackage;
  public final int index;
  public final ArrayList<ArrayList<VQuatAnimKey>> tracks = new ArrayList<ArrayList<VQuatAnimKey>>();
  public final ArrayList<Float> startTimes = new ArrayList<Float>();
  public final float length;

  public PsaAnimation(PsaPackage psaPackage, int index) {
    this.psaPackage = psaPackage;
    this.index = index;

    int boneCount = psaPackage.bones.size();
    int start = psaPackage.animInfos.get(index).firstRawFrame;

    int frameCount = psaPackage.animInfos.get(index).numRawFrames;
    float animationLength = 0;
    for (int i = 0; i < boneCount; i++) {
      ArrayList<VQuatAnimKey> track = new ArrayList<VQuatAnimKey>();
      for (int j = 0; j < frameCount; j++) {
        int k = (start + j) * boneCount + i;
        VQuatAnimKey key = psaPackage.rawKeys.get(k);
        track.add(key);
      }
      tracks.add(track);
    }

    float current = 0;
    ArrayList<VQuatAnimKey> track = tracks.get(0);
    for (int j = 0; j < track.size(); j++) {
      startTimes.add(current);
      current += track.get(j).time;
    }
    animationLength = current;
    length = animationLength;
  }

  public void getPose(float time, PsaPose pose) {
    pose.clear();
    time = time % length;

    int index = Collections.binarySearch(startTimes, time);
    float alpha;
    if (index >= 0) {
      alpha = 0;
    } else {
      index = -(index + 1) - 1;
      alpha = (time - startTimes.get(index)) / tracks.get(0).get(index).time;
    }

    int boneCount = psaPackage.bones.size();
    for (int boneIndex = 0; boneIndex < boneCount; boneIndex++) {
      String boneName = psaPackage.bones.get(boneIndex).name;

      VQuatAnimKey key0 = tracks.get(boneIndex).get(index);
      VQuatAnimKey key1 = tracks.get(boneIndex).get((index + 1) % (tracks.get(boneIndex).size()));

      Vector3f d = new Vector3f();
      d.scale(1 - alpha, key0.position);
      d.scaleAdd(alpha, key1.position, d);

      Quat4f q = new Quat4f();
      q.interpolate(key0.orientation, key1.orientation, alpha);

      pose.set(boneName, d, q);
    }
  }
}
