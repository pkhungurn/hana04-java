package hana04.formats.unreal;

import org.apache.commons.io.input.SwappedDataInputStream;

import java.io.IOException;

public class VVertex {
  public int pointIndex;
  public float u, v;
  public byte matIndex;
  public byte reserved;

  public void read(SwappedDataInputStream fin) throws IOException {
    pointIndex = fin.readShort();
    matIndex = fin.readByte();
    reserved = fin.readByte();
    u = fin.readFloat();
    v = fin.readFloat();
  }
}
