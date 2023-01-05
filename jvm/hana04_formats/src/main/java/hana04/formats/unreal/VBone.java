
package hana04.formats.unreal;

import hana04.base.util.BinaryIo;
import org.apache.commons.io.input.SwappedDataInputStream;

import java.io.IOException;

public class VBone {
  public String name;
  public int flags;
  public int numChildren;
  public int parentIndex;
  public VJointPos bonePose = new VJointPos();

  public void read(SwappedDataInputStream fin) throws IOException {
    name = BinaryIo.readUtfString(fin, 64);
    flags = fin.readInt();
    numChildren = fin.readInt();
    parentIndex = fin.readInt();
    bonePose.read(fin);
  }
}
