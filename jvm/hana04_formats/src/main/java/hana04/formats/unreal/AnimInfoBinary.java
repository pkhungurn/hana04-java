package hana04.formats.unreal;

import hana04.base.util.BinaryIo;
import org.apache.commons.io.input.SwappedDataInputStream;

import java.io.IOException;

public class AnimInfoBinary {
  public String name = "";
  public String groupName = "";

  public int totalBones;
  public int rootInclude;
  public int keyCompressionStyle;
  public int keyQuotum;
  public float keyReduction;
  public float trackTime;
  public float animRate;
  public int startBone;
  public int firstRawFrame;
  public int numRawFrames;

  public void read(SwappedDataInputStream fin) throws IOException {
    name = BinaryIo.readUtfString(fin, 64);
    groupName = BinaryIo.readUtfString(fin, 64);

    totalBones = fin.readInt();
    rootInclude = fin.readInt();
    keyCompressionStyle = fin.readInt();
    keyQuotum = fin.readInt();
    keyReduction = fin.readFloat();
    trackTime = fin.readFloat();
    animRate = fin.readFloat();
    startBone = fin.readInt();
    firstRawFrame = fin.readInt();
    numRawFrames = fin.readInt();
  }
}
