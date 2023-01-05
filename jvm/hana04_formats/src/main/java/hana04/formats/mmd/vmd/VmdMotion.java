
package hana04.formats.mmd.vmd;

import hana04.base.util.BinaryIo;
import hana04.formats.mmd.pmd.PmdModel;
import hana04.formats.mmd.pmd.PmdPose;
import hana04.formats.mmd.pmx.PmxBone;
import hana04.formats.mmd.pmx.PmxModel;
import hana04.formats.mmd.pmx.PmxMorph;
import hana04.formats.mmd.pmx.PmxPose;
import hana04.formats.mmd.vpd.VpdPose;
import org.apache.commons.io.input.SwappedDataInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class VmdMotion {
  private static Logger logger = LoggerFactory.getLogger(VmdMotion.class);
  public String modelName = "";
  public final Map<String, VmdBoneMotion> boneMotions = new HashMap<String, VmdBoneMotion>();
  public final Map<String, VmdMorphMotion> morphMotions = new HashMap<String, VmdMorphMotion>();

  public VmdMotion() {
    // NO-OP
  }

  private void read(SwappedDataInputStream reader) throws IOException {
    String magic = BinaryIo.readShiftJisString(reader, 30);
    if (!magic.equals("Vocaloid Motion Data 0002")) {
      throw new RuntimeException("magic is wrong: " + magic);
    }
    modelName = BinaryIo.readShiftJisString(reader, 20);

    int boneMotionCount = reader.readInt();
    //logger.debug(String.format("boneMotionCount = %d", boneMotionCount));
    for (int i = 0; i < boneMotionCount; i++) {
      String boneName = BinaryIo.readShiftJisString(reader, 15);
      if (!boneMotions.containsKey(boneName)) {
        VmdBoneMotion boneMotion = new VmdBoneMotion();
        boneMotion.boneName = boneName;
        boneMotions.put(boneName, boneMotion);
      }
      VmdBoneKeyframe keyframe = new VmdBoneKeyframe();
      keyframe.read(reader);
      boneMotions.get(boneName).keyFrames.add(keyframe);
    }

    int morphMotionCount = reader.readInt();
    //logger.debug(String.format("morphMotionCount = %d", morphMotionCount));
    for (int i = 0; i < morphMotionCount; i++) {
      String morphName = BinaryIo.readShiftJisString(reader, 15);
      if (!morphMotions.containsKey(morphName)) {
        VmdMorphMotion morphMotion = new VmdMorphMotion();
        morphMotion.morphName = morphName;
        morphMotions.put(morphName, morphMotion);
      }
      VmdMorphKeyframe keyframe = new VmdMorphKeyframe();
      keyframe.read(reader);
      morphMotions.get(morphName).keyFrames.add(keyframe);
    }


    for (VmdBoneMotion boneMotion : boneMotions.values()) {
      boneMotion.sortAndRemoveDuplicates();
    }

    for (VmdMorphMotion morphMotion : morphMotions.values()) {
      morphMotion.sortAndRemoveDuplicates();
    }
  }

  public void getPose(float time, VpdPose pose) {
    Vector3f displacement = new Vector3f();
    Quat4f rotation = new Quat4f();

    pose.clear();
    for (VmdBoneMotion boneMotion : boneMotions.values()) {
      boneMotion.evaluate(time, displacement, rotation);
      pose.setBonePose(boneMotion.boneName, displacement, rotation);
    }
    for (VmdMorphMotion morphMotion : morphMotions.values()) {
      float weight = morphMotion.evaluate(time);
      pose.setMorphWeight(morphMotion.morphName, weight);
    }
  }

  public void getPose(float time, PmdPose pose) {
    Vector3f displacement = new Vector3f();
    Quat4f rotation = new Quat4f();

    PmdModel model = pose.getModel();
    pose.clear();
    for (VmdBoneMotion boneMotion : boneMotions.values()) {
      boneMotion.evaluate(time, displacement, rotation);
      int boneIndex = model.getBoneIndex(boneMotion.boneName);
      if (boneIndex >= 0) {
        pose.setBoneDisplacement(boneIndex, displacement);
        pose.setBoneRotation(boneIndex, rotation);
      }
    }
    for (VmdMorphMotion morphMotion : morphMotions.values()) {
      float weight = morphMotion.evaluate(time);
      int morphIndex = model.getMorphIndex(morphMotion.morphName);
      if (morphIndex >= 0) {
        pose.setMorphWeight(morphIndex, weight);
      }
    }
  }

  public void getPose(float time, PmxPose pose) {
    Vector3f displacement = new Vector3f();
    Quat4f rotation = new Quat4f();

    PmxModel model = pose.getModel();
    pose.clear();
    for (VmdBoneMotion boneMotion : boneMotions.values()) {
      boneMotion.evaluate(time, displacement, rotation);
      PmxBone bone = model.getBone(boneMotion.boneName);
      if (bone == null) {
        continue;
      }
      if (bone.boneIndex >= 0) {
        pose.setBoneDisplacement(bone.boneIndex, displacement);
        pose.setBoneRotation(bone.boneIndex, rotation);
      }
    }
    for (VmdMorphMotion morphMotion : morphMotions.values()) {
      float weight = morphMotion.evaluate(time);
      PmxMorph morph = model.getMorph(morphMotion.morphName);
      if (morph == null) {
        continue;
      }
      if (morph.morphIndex >= 0) {
        pose.setMorphWeight(morph.morphIndex, weight);
      }
    }
  }

  public static VmdMotion load(String fileName) throws IOException {
    File theFile = new File(fileName);

    long fileSize = theFile.length();
    if (fileSize > Integer.MAX_VALUE) {
      throw new RuntimeException("file too large to load");
    }
    ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
    FileInputStream fileInputStream = new FileInputStream(theFile);
    FileChannel channel = fileInputStream.getChannel();
    channel.read(buffer);
    channel.close();
    fileInputStream.close();
    buffer.rewind();
    ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer.array());

    SwappedDataInputStream fin = new SwappedDataInputStream(inputStream);
    VmdMotion result = new VmdMotion();
    result.read(fin);
    fin.close();

    return result;
  }

  public static VmdMotion load(Path filePath) {
    try {
      long fileSize = Files.size(filePath);
      if (fileSize > Integer.MAX_VALUE) {
        throw new RuntimeException("file to large to load");
      }
      ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
      SeekableByteChannel channel = Files.newByteChannel(filePath, StandardOpenOption.READ);
      channel.read(buffer);
      channel.close();
      buffer.rewind();
      ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer.array());

      SwappedDataInputStream fin = new SwappedDataInputStream(inputStream);
      VmdMotion result = new VmdMotion();
      result.read(fin);
      fin.close();

      return result;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void save(String fileName) throws IOException {
    File file = new File(fileName);
    File directory = file.getParentFile();

    FileOutputStream outStream = new FileOutputStream(fileName);
    DataOutputStream fout = new DataOutputStream(outStream);

    // Magic
    BinaryIo.writeByteString(fout,
      "Vocaloid Motion Data 0002".getBytes(),
      30);

    // Model name
    BinaryIo.writeShiftJISString(fout, modelName, 20);

    // Bone key frames
    int boneFrameCount = getTotalBoneKeyFrameCount();
    //logger.debug(String.format("boneFrameCount = %d", boneFrameCount));
    BinaryIo.writeLittleEndianInt(fout, boneFrameCount);
    for (VmdBoneMotion boneMotion : boneMotions.values()) {
      for (VmdBoneKeyframe keyFrame : boneMotion.keyFrames) {
        BinaryIo.writeShiftJISString(fout, boneMotion.boneName, 15);
        keyFrame.write(fout);
      }
    }

    // Morph key frames
    int morphFrameCount = getTotalMorphFrameCount();
    BinaryIo.writeLittleEndianInt(fout, morphFrameCount);
    //logger.debug(String.format("morphFrameCount = %d", morphFrameCount));
    for (VmdMorphMotion morphMotion : morphMotions.values()) {
      for (VmdMorphKeyframe keyFrame : morphMotion.keyFrames) {
        BinaryIo.writeShiftJISString(fout, morphMotion.morphName, 15);
        keyFrame.write(fout);
      }
    }

    // Camera key frames
    BinaryIo.writeLittleEndianInt(fout, 0);

    // Light key frames
    BinaryIo.writeLittleEndianInt(fout, 0);

    // Shadow key frames
    BinaryIo.writeLittleEndianInt(fout, 0);

    fout.close();
    outStream.close();
  }

  public int getTotalBoneKeyFrameCount() {
    int count = 0;
    for (VmdBoneMotion motion : boneMotions.values()) {
      count += motion.keyFrames.size();
    }
    return count;
  }

  public int getTotalMorphFrameCount() {
    int count = 0;
    for (VmdMorphMotion motion : morphMotions.values()) {
      count += motion.keyFrames.size();
    }
    return count;
  }

  public int getMaxFrame() {
    int answer = 0;

    for (VmdBoneMotion boneMotion : boneMotions.values()) {
      answer = Math.max(answer, boneMotion.getLastFrame());
    }

    for (VmdMorphMotion morphMotion : morphMotions.values()) {
      answer = Math.max(answer, morphMotion.getLastFrame());
    }

    return answer;
  }

  public void clearMotions() {
    boneMotions.clear();
    morphMotions.clear();
  }

  public void clearAndCopyMotionNames(VmdMotion other) {
    clearMotions();
    for (String boneName : other.boneMotions.keySet()) {
      VmdBoneMotion boneMotion = new VmdBoneMotion();
      boneMotion.boneName = boneName;
      this.boneMotions.put(boneName, boneMotion);
    }
    for (String morphName : other.morphMotions.keySet()) {
      VmdMorphMotion morphMotion = new VmdMorphMotion();
      morphMotion.morphName = morphName;
      this.morphMotions.put(morphName, morphMotion);
    }
  }
}
