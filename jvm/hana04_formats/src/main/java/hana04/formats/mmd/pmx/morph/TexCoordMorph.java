
package hana04.formats.mmd.pmx.morph;

import hana04.base.util.BinaryIo;
import hana04.formats.mmd.pmx.PmxMorph;
import hana04.gfxbase.gfxtype.BinaryIoUtil;
import org.apache.commons.io.input.SwappedDataInputStream;

import javax.vecmath.Vector4f;
import java.io.IOException;
import java.util.ArrayList;

public class TexCoordMorph extends PmxMorph {
  public int texCoordId;
  public final ArrayList<Offset> offsets = new ArrayList<Offset>();

  public TexCoordMorph(int texCoordId) {
    this.texCoordId = texCoordId;
  }

  public void readOffsets(SwappedDataInputStream fin, int vertexIndexSize) throws IOException {
    int count = fin.readInt();
    for (int i = 0; i < count; i++) {
      Offset offset = new Offset();
      offset.read(fin, vertexIndexSize);
      offsets.add(offset);
    }
  }

  public static class Offset {
    public int vertexIndex;
    public final Vector4f value = new Vector4f();

    public void read(SwappedDataInputStream fin, int vertexIndexSize) throws IOException {
      vertexIndex = BinaryIo.readIntGivenSizeInBytes(fin, vertexIndexSize, true);
      BinaryIoUtil.readTuple4f(fin, value);
    }
  }
}
