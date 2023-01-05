package hana04.formats.mmd.pmd;

import hana04.base.util.BinaryIo;
import org.apache.commons.io.input.SwappedDataInputStream;

import java.io.DataOutputStream;
import java.io.IOException;

public class PmdIkChain {
  public int boneIndex;
  public int targetBoneIndex;
  public int iterationCount;
  public float quarterIterationAngleLimitRad;
  public int[] chainBoneIndices;
  public boolean isLeg;
  public boolean isLeftLeg;
  public boolean isRightLeg;

  public PmdIkChain() {
    // NO-OP
  }

  public int length() {
    return chainBoneIndices.length;
  }

  public void read(SwappedDataInputStream fin) throws IOException {
    boneIndex = fin.readUnsignedShort();
    targetBoneIndex = fin.readShort();
    int chainLength = fin.readByte();
    chainBoneIndices = new int[chainLength];
    iterationCount = fin.readUnsignedShort();
    quarterIterationAngleLimitRad = fin.readFloat();
    for (int i = 0; i < chainLength; i++) {
      chainBoneIndices[i] = fin.readShort();
    }
  }

  void write(DataOutputStream fout) throws IOException {
    BinaryIo.writeLittleEndianShort(fout, (short) boneIndex);
    BinaryIo.writeLittleEndianShort(fout, (short) targetBoneIndex);
    fout.write(chainBoneIndices.length);
    BinaryIo.writeLittleEndianShort(fout, (short) iterationCount);
    BinaryIo.writeLittleEndianFloat(fout, quarterIterationAngleLimitRad);
    for (int i = 0; i < chainBoneIndices.length; i++) {
      BinaryIo.writeLittleEndianShort(fout, (short) chainBoneIndices[i]);
    }
  }
}
