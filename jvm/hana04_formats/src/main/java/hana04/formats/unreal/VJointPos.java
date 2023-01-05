
package hana04.formats.unreal;

import hana04.gfxbase.gfxtype.BinaryIoUtil;
import org.apache.commons.io.input.SwappedDataInputStream;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.io.IOException;

public class VJointPos {
  public Quat4f orientation = new Quat4f();
  public Vector3f position = new Vector3f();
  float length;
  float xSize, ySize, zSize;

  public void read(SwappedDataInputStream fin) throws IOException {
    BinaryIoUtil.readTuple4f(fin, orientation);
    BinaryIoUtil.readTuple3f(fin, position);
    //orientation.x *= -1;
    //orientation.y *= -1;
    //orientation.z *= -1;
    length = fin.readFloat();
    xSize = fin.readFloat();
    ySize = fin.readFloat();
    zSize = fin.readFloat();
  }
}
