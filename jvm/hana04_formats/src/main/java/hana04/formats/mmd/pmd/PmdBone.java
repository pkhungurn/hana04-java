package hana04.formats.mmd.pmd;

import hana04.base.util.BinaryIo;
import hana04.gfxbase.gfxtype.BinaryIoUtil;
import org.apache.commons.io.input.SwappedDataInputStream;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.io.DataOutputStream;
import java.io.IOException;

public class PmdBone {
  public String japaneseName;
  public String englishName;
  public short parentIndex;
  public short tailIndex;
  public PmdBoneType type;
  public short influenceInfo;
  public final Point3f position = new Point3f();
  public PmdBone parent;
  public boolean isKnee;
  public boolean controlledByPhysics = false;

  public PmdBone() {
    // NO-OP
  }

  public void getDisplacementFromParent(Vector3f result) {
    if (parent == null) {
      result.set(position);
    } else {
      result.sub(position, parent.position);
    }
  }

  public void read(SwappedDataInputStream fin) throws IOException {
    japaneseName = BinaryIo.readShiftJisString(fin, 20);
    englishName = japaneseName;
    parentIndex = fin.readShort();
    tailIndex = fin.readShort();
    type = PmdBoneType.fromInt(fin.readByte());
    influenceInfo = fin.readShort();
    BinaryIoUtil.readTuple3f(fin, position);
    isKnee = japaneseName.equals("左ひざ") || japaneseName.equals("右ひざ");
  }

  public void write(DataOutputStream fout) throws IOException {
    byte[] japaneseNameArray = japaneseName.getBytes("Shift-JIS");
    BinaryIo.writeByteString(fout, japaneseNameArray, 20);
    BinaryIo.writeLittleEndianShort(fout, parentIndex);
    BinaryIo.writeLittleEndianShort(fout, tailIndex);
    fout.write(type.getValue());
    BinaryIo.writeLittleEndianShort(fout, influenceInfo);
    BinaryIoUtil.writeLittleEndianTuple3f(fout, position);
  }

}
