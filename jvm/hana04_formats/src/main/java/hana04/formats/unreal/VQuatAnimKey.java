
package hana04.formats.unreal;

import hana04.gfxbase.gfxtype.BinaryIoUtil;
import org.apache.commons.io.input.SwappedDataInputStream;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.io.IOException;

public class VQuatAnimKey {
  public Vector3f position = new Vector3f();
  public Quat4f orientation = new Quat4f();
  public float time;

  public void read(SwappedDataInputStream fin) throws IOException {
    BinaryIoUtil.readTuple3f(fin, position);
    BinaryIoUtil.readTuple4f(fin, orientation);
    time = fin.readFloat();
  }
}
