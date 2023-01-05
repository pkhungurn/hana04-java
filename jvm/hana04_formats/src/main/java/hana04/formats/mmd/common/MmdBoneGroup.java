package hana04.formats.mmd.common;

import org.apache.commons.io.input.SwappedDataInputStream;

public class MmdBoneGroup {
  public String japaneseName;
  public String englishName;
  public int[] boneIndices;

  public MmdBoneGroup() {
    // NO-OP
  }

  public void read(SwappedDataInputStream fin) {

  }
}
