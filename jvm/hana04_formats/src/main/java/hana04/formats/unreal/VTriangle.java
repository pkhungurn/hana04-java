
package hana04.formats.unreal;

import org.apache.commons.io.input.SwappedDataInputStream;

import java.io.IOException;

public class VTriangle {
  public int[] vertexIndices = new int[3];
  public byte matIndex;
  public byte auxMatIndex;
  public int smoothingGroups;

  public void read(SwappedDataInputStream fin) throws IOException {
    for (int i = 0; i < 3; i++) {
      vertexIndices[i] = fin.readUnsignedShort();
    }
    matIndex = fin.readByte();
    auxMatIndex = fin.readByte();
    smoothingGroups = fin.readInt();
  }
}
