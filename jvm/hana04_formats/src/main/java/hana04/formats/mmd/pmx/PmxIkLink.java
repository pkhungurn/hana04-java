
package hana04.formats.mmd.pmx;

import hana04.base.util.BinaryIo;
import hana04.gfxbase.gfxtype.BinaryIoUtil;
import org.apache.commons.io.input.SwappedDataInputStream;

import javax.vecmath.Vector3f;
import java.io.IOException;

public class PmxIkLink {
  public int boneIndex;
  public boolean angleLimited;
  public final Vector3f angleLowerBound = new Vector3f();
  public final Vector3f angleUpperBound = new Vector3f();

  public void read(SwappedDataInputStream fin, int boneIndexSize) throws IOException {
    boneIndex = BinaryIo.readIntGivenSizeInBytes(fin, boneIndexSize, true);
    angleLimited = fin.readByte() == 1;
    if (angleLimited) {
      BinaryIoUtil.readTuple3f(fin, angleLowerBound);
      BinaryIoUtil.readTuple3f(fin, angleUpperBound);
    }
  }
}
