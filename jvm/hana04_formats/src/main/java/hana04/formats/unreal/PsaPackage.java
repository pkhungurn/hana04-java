package hana04.formats.unreal;

import org.apache.commons.io.input.SwappedDataInputStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class PsaPackage {
  public VChunkHeader header = new VChunkHeader();
  public VChunkHeader boneHeader = new VChunkHeader();
  public ArrayList<VBone> bones = new ArrayList<VBone>();
  public VChunkHeader animInfosHeader = new VChunkHeader();
  public ArrayList<AnimInfoBinary> animInfos = new ArrayList<AnimInfoBinary>();
  public VChunkHeader rawKeysHeader = new VChunkHeader();
  public ArrayList<VQuatAnimKey> rawKeys = new ArrayList<VQuatAnimKey>();
  public ArrayList<PsaAnimation> animations = new ArrayList<PsaAnimation>();

  public void read(SwappedDataInputStream fin) throws IOException {
    header.read(fin);

    boneHeader.read(fin);
    for (int i = 0; i < boneHeader.dataCount; i++) {
      VBone bone = new VBone();
      bone.read(fin);
      bones.add(bone);
      //System.out.println(bone.name);
    }
    if (bones.size() > 0) {
      bones.get(0).parentIndex = -1;
    }

    animInfosHeader.read(fin);
    for (int i = 0; i < animInfosHeader.dataCount; i++) {
      AnimInfoBinary animInfo = new AnimInfoBinary();
      animInfo.read(fin);
      animInfos.add(animInfo);
    }

    rawKeysHeader.read(fin);
    for (int i = 0; i < rawKeysHeader.dataCount; i++) {
      VQuatAnimKey key = new VQuatAnimKey();
      key.read(fin);
      rawKeys.add(key);
    }

    createAnimations();
  }

  private void createAnimations() {
    for (int i = 0; i < animInfos.size(); i++) {
      PsaAnimation anim = new PsaAnimation(this, i);
      animations.add(anim);
    }
  }

  public static PsaPackage load(String fileName) throws IOException {
    PsaPackage anim = new PsaPackage();
    File inputFile = new File(fileName);
    long fileSize = inputFile.length();
    if (fileSize > Integer.MAX_VALUE) {
      throw new RuntimeException("file to large to load");
    }
    ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
    FileInputStream fileInputStream = new FileInputStream(inputFile);
    FileChannel channel = fileInputStream.getChannel();
    channel.read(buffer);
    channel.close();
    fileInputStream.close();
    buffer.rewind();
    ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer.array());
    SwappedDataInputStream fin = new SwappedDataInputStream(inputStream);
    anim.read(fin);
    fin.close();

    return anim;
  }

  public PsaAnimation getAnimation(String name) {
    for (int i = 0; i < animInfos.size(); i++) {
      String animName = animInfos.get(i).name;
      if (animName.equals(name)) {
        return animations.get(i);
      }
    }
    return null;
  }

  public int getBoneIndex(String boneName) {
    for (int i = 0; i < bones.size(); i++) {
      VBone bone = bones.get(i);
      if (bone.name.equals(boneName)) {
        return i;
      }
    }
    return -1;
  }
}
