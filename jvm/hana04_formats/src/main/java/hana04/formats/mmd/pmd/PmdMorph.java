
package hana04.formats.mmd.pmd;

import hana04.base.util.BinaryIo;
import hana04.formats.mmd.common.MmdMorphPanel;
import hana04.gfxbase.gfxtype.BinaryIoUtil;
import org.apache.commons.io.input.SwappedDataInputStream;

import javax.vecmath.Vector3f;
import java.io.DataOutputStream;
import java.io.IOException;

public class PmdMorph {
  public String japaneseName;
  public String englishName;
  public MmdMorphPanel panel;
  public int[] vertexIndices;
  public Vector3f[] displacements;

  public PmdMorph() {
    // NO-OP
  }

  public void read(SwappedDataInputStream fin) throws IOException {
    englishName = japaneseName = BinaryIo.readShiftJisString(fin, 20);

    int vertexCount = fin.readInt();
    panel = MmdMorphPanel.fromInt(fin.readByte());
    vertexIndices = new int[vertexCount];
    displacements = new Vector3f[vertexCount];
    for (int i = 0; i < vertexCount; i++) {
      int vertexIndex = fin.readInt();
      Vector3f v = new Vector3f();
      BinaryIoUtil.readTuple3f(fin, v);
      //v.z = -v.z;
      vertexIndices[i] = vertexIndex;
      displacements[i] = v;
    }
  }

  void write(DataOutputStream fout) throws IOException {
    byte[] japaneseNameArray = japaneseName.getBytes("Shift-JIS");
    BinaryIo.writeByteString(fout, japaneseNameArray, 20);
    BinaryIo.writeLittleEndianInt(fout, vertexIndices.length);
    fout.write(panel.getValue());
    for (int i = 0; i < vertexIndices.length; i++) {
      BinaryIo.writeLittleEndianInt(fout, vertexIndices[i]);
      BinaryIoUtil.writeLittleEndianTuple3f(fout, displacements[i]);
    }
  }
}
