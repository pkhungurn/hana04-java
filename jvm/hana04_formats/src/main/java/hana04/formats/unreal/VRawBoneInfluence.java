
package hana04.formats.unreal;

import org.apache.commons.io.input.SwappedDataInputStream;

import java.io.IOException;

public class VRawBoneInfluence {
  public float weight;
  public int pointIndex;
  public int boneIndex;

  public void read(SwappedDataInputStream fin) throws IOException {
    weight = fin.readFloat();
    pointIndex = fin.readInt();
    boneIndex = fin.readInt();
  }
}
