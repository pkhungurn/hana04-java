package hana04.shakuyaku.sbtm;

import com.cedarsoftware.util.io.JsonWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hana04.base.util.BinaryIo;
import hana04.base.util.TextIo;
import hana04.base.util.TypeUtil;
import hana04.gfxbase.gfxtype.BinaryIoUtil;
import hana04.serialize.PrimitiveParsingUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.SwappedDataInputStream;

import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents an skeleton data for a skeletal blended tri-mesh (SBTM).
 * <p>`
 * Contract: The first key frame has to has time 0. The last key
 * frame has to have the time equal to the duration of the skeleton.
 */
public class SbtmAnimation {
  private static final String MAGIC = "SBTMA";
  private double duration;
  private final HashMap<String, TreeMap<Double, SbtmBonePose>> boneMotions = new HashMap<>();
  private final HashMap<String, TreeMap<Double, Double>> morphMotions = new HashMap<>();
  private boolean repeating = false;

  /**
   * Create an empty animation.
   */
  public SbtmAnimation() {
    // NO-OP
  }

  /**
   * Perform a deep copy of the given animation.
   */
  public SbtmAnimation(SbtmAnimation other) {
    this.duration = other.duration;
    this.repeating = other.repeating;
    other.boneMotions.forEach((name, frames) -> {
      var newAnimation = new TreeMap<Double, SbtmBonePose>();
      frames.forEach((time, pose) -> newAnimation.put(time, new SbtmBonePose(pose)));
      boneMotions.put(name, newAnimation);
    });
    other.morphMotions.forEach((name, frames) -> {
      var newAnimation = new TreeMap<Double, Double>();
      frames.forEach(newAnimation::put);
      morphMotions.put(name, newAnimation);
    });
  }

  public double getDuration() {
    return duration;
  }

  public SbtmAnimation setDuration(double duration) {
    this.duration = duration;
    return this;
  }

  public SbtmAnimation setRepeating(boolean repeating) {
    this.repeating = repeating;
    return this;
  }

  public boolean getRepeating() {
    return repeating;
  }

  public SbtmAnimation addAll(SbtmAnimation other) {
    for (var boneName : other.boneMotions.keySet()) {
      var boneMotion = other.boneMotions.get(boneName);
      boneMotion.forEach((time, keyFrame) -> {
        putBoneKeyFrame(boneName, time, keyFrame);
      });
    }
    for (var morphName : other.morphMotions.keySet()) {
      var morphMotion = other.morphMotions.get(morphName);
      morphMotion.forEach((time, weight) -> {
        putMorphKeyFrame(morphName, time, weight);
      });
    }
    return this;
  }

  public SbtmAnimation putBoneKeyFrame(String boneName, double time, SbtmBonePose bonePose) {
    if (!boneMotions.containsKey(boneName)) {
      boneMotions.put(boneName, new TreeMap<>());
    }
    boneMotions.get(boneName).put(time, new SbtmBonePose(bonePose));
    return this;
  }

  public SbtmAnimation putMorphKeyFrame(String morphName, double time, double value) {
    if (!morphMotions.containsKey(morphName)) {
      morphMotions.put(morphName, new TreeMap<>());
    }
    morphMotions.get(morphName).put(time, value);
    return this;
  }

  public SbtmAnimation putPose(double time, SbtmPose pose) {
    pose.bonePoses.forEach((name, sbtmBonePose) -> putBoneKeyFrame(name, time, sbtmBonePose));
    pose.morphPoses.forEach((name, value) -> putMorphKeyFrame(name, time, value));
    return this;
  }

  void deserialize(SwappedDataInputStream fin) throws IOException {
    duration = fin.readDouble();

    int boneCount = fin.readInt();
    for (int i = 0; i < boneCount; i++) {
      String boneName = BinaryIo.readVariableLengthUtfString(fin);
      TreeMap<Double, SbtmBonePose> boneAnim = new TreeMap<>();
      int frameCount = fin.readInt();
      for (int j = 0; j < frameCount; j++) {
        double time = fin.readDouble();
        SbtmBonePose bonePose = new SbtmBonePose();
        BinaryIoUtil.readTuple3d(fin, bonePose.translation);
        BinaryIoUtil.readTuple4d(fin, bonePose.rotation);
        boneAnim.put(time, bonePose);
      }
      boneMotions.put(boneName, boneAnim);
    }

    int morphCount = fin.readInt();
    for (int i = 0; i < morphCount; i++) {
      String morphName = BinaryIo.readVariableLengthUtfString(fin);
      TreeMap<Double, Double> morphAnim = new TreeMap<>();
      int frameCount = fin.readInt();
      for (int j = 0; j < frameCount; j++) {
        double time = fin.readDouble();
        double weight = fin.readDouble();
        morphAnim.put(time, weight);
      }
      morphMotions.put(morphName, morphAnim);
    }
  }

  void serialize(DataOutputStream fout) throws IOException {
    BinaryIo.writeLittleEndianDouble(fout, duration);

    BinaryIo.writeLittleEndianInt(fout, boneMotions.size());
    for (Map.Entry<String, TreeMap<Double, SbtmBonePose>> entry : boneMotions.entrySet()) {
      String boneName = entry.getKey();
      TreeMap<Double, SbtmBonePose> boneAnim = entry.getValue();
      BinaryIo.writeLittleEndianVaryingLengthUtfString(fout, boneName);
      BinaryIo.writeLittleEndianInt(fout, boneAnim.size());
      for (Map.Entry<Double, SbtmBonePose> animEntry : boneAnim.entrySet()) {
        BinaryIo.writeLittleEndianDouble(fout, animEntry.getKey());
        SbtmBonePose bonePose = animEntry.getValue();
        BinaryIoUtil.writeLittleEndianTuple3d(fout, bonePose.translation);
        BinaryIoUtil.writeLittleEndianTuple4d(fout, bonePose.rotation);
      }
    }

    BinaryIo.writeLittleEndianInt(fout, morphMotions.size());
    for (Map.Entry<String, TreeMap<Double, Double>> entry : morphMotions.entrySet()) {
      String morphName = entry.getKey();
      TreeMap<Double, Double> morphAnim = entry.getValue();
      BinaryIo.writeLittleEndianVaryingLengthUtfString(fout, morphName);
      BinaryIo.writeLittleEndianInt(fout, morphAnim.size());
      for (Map.Entry<Double, Double> animEntry : morphAnim.entrySet()) {
        BinaryIo.writeLittleEndianDouble(fout, animEntry.getKey());
        BinaryIo.writeLittleEndianDouble(fout, animEntry.getValue());
      }
    }
  }

  public void getPose(double time, SbtmPose pose) {
    pose.clear();
    if (repeating) {
      time = time % duration;
    } else {
      time = Math.min(duration, Math.max(0, time));
    }

    for (Map.Entry<String, TreeMap<Double, SbtmBonePose>> entry : boneMotions.entrySet()) {
      String boneName = entry.getKey();
      TreeMap<Double, SbtmBonePose> keyFrames = entry.getValue();
      Map.Entry<Double, SbtmBonePose> e0 = keyFrames.floorEntry(time);
      Map.Entry<Double, SbtmBonePose> e1 = keyFrames.higherEntry(time);
      if (e1 == null) {
        e0 = keyFrames.lowerEntry(time);
        e1 = keyFrames.ceilingEntry(time);
      }

      double alpha = (time - e0.getKey()) / (e1.getKey() - e0.getKey());

      SbtmBonePose interp = new SbtmBonePose();
      interp.translation.scale(1 - alpha, e0.getValue().translation);
      interp.translation.scaleAdd(alpha, e1.getValue().translation, interp.translation);
      interp.rotation.interpolate(e0.getValue().rotation, e1.getValue().rotation, alpha);

      pose.bonePoses.put(boneName, interp);
    }

    for (Map.Entry<String, TreeMap<Double, Double>> entry : morphMotions.entrySet()) {
      String morphName = entry.getKey();
      TreeMap<Double, Double> keyFrames = entry.getValue();
      Map.Entry<Double, Double> e0 = keyFrames.floorEntry(time);
      Map.Entry<Double, Double> e1 = keyFrames.higherEntry(time);
      if (e1 == null) {
        e0 = keyFrames.lowerEntry(time);
        e1 = keyFrames.ceilingEntry(time);
      }
      double alpha = (time - e0.getKey()) / (e1.getKey() - e0.getKey());
      double value = e0.getValue() * (1 - alpha) + e1.getValue() * alpha;
      pose.morphPoses.put(morphName, value);
    }
  }

  public void saveBinary(Path path) {
    try {
      ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      DataOutputStream fout = new DataOutputStream(outStream);
      BinaryIo.writeLittleEndianVaryingLengthUtfString(fout, MAGIC);
      serialize(fout);
      fout.close();
      outStream.close();

      byte[] data = outStream.toByteArray();
      FileUtils.writeByteArrayToFile(path.toFile(), data);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static SbtmAnimation loadBinary(Path path) {
    try {
      long fileSize = Files.size(path);
      ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
      SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.READ);
      channel.read(buffer);
      channel.close();

      ByteArrayInputStream stream = new ByteArrayInputStream(buffer.array());
      SwappedDataInputStream swappedStream = new SwappedDataInputStream(stream);
      String magic = BinaryIo.readVariableLengthUtfString(swappedStream);
      Preconditions.checkArgument(magic.equals(MAGIC));
      SbtmAnimation sbtmAnimation = new SbtmAnimation();
      sbtmAnimation.deserialize(swappedStream);
      swappedStream.close();
      return sbtmAnimation;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Map<String, Object> serializerReadableBoneMotions(Map<String, TreeMap<Double, SbtmBonePose>> boneMotions) {
    Map<String, Object> output = new HashMap<>();
    for (Map.Entry<String, TreeMap<Double, SbtmBonePose>> entry : boneMotions.entrySet()) {
      String morphName = entry.getKey();
      TreeMap<Double, SbtmBonePose> boneMotion = entry.getValue();
      List<Object> outputKeyFrames = new ArrayList<>();
      for (Map.Entry<Double, SbtmBonePose> keyframe : boneMotion.entrySet()) {
        outputKeyFrames.add(ImmutableList.<Object>of(
            keyframe.getKey(),
            ImmutableList.of(
                keyframe.getValue().translation.x,
                keyframe.getValue().translation.y,
                keyframe.getValue().translation.z),
            ImmutableList.of(
                keyframe.getValue().rotation.x,
                keyframe.getValue().rotation.y,
                keyframe.getValue().rotation.z,
                keyframe.getValue().rotation.w)));
      }
      output.put(morphName, outputKeyFrames);
    }
    return output;
  }

  private Map<String, Object> serializeReadableMorphMotions(Map<String, TreeMap<Double, Double>> morphMotions) {
    Map<String, Object> output = new HashMap<>();
    for (Map.Entry<String, TreeMap<Double, Double>> entry : morphMotions.entrySet()) {
      String morphName = entry.getKey();
      TreeMap<Double, Double> morphMotion = entry.getValue();
      List<Object> outputKeyFrames = new ArrayList<>();
      for (Map.Entry<Double, Double> keyframe : morphMotion.entrySet()) {
        outputKeyFrames.add(ImmutableList.of(keyframe.getKey(), keyframe.getValue()));
      }
      output.put(morphName, outputKeyFrames);
    }
    return output;
  }

  public void saveReadable(Path path) {
    Map<String, Object> json = ImmutableMap.<String, Object>builder()
        .put("boneMotions", serializerReadableBoneMotions(boneMotions))
        .put("morphMotions", serializeReadableMorphMotions(morphMotions))
        .put("repeating", repeating)
        .put("duration", duration)
        .build();
    ObjectMapper mapper = new ObjectMapper();
    try {
      TextIo.writeTextFile(
          path,
          JsonWriter.formatJson(mapper.writeValueAsString(json)));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private static Vector3d deserializeVector3d(Object jsonObject) {
    List<Object> list = (List<Object>) jsonObject;
    return new Vector3d(
        PrimitiveParsingUtil.parseDouble(list.get(0)),
        PrimitiveParsingUtil.parseDouble(list.get(1)),
        PrimitiveParsingUtil.parseDouble(list.get(2)));
  }

  private static Quat4d deserializeQuat4d(Object jsonObject) {
    List<Object> list = (List<Object>) jsonObject;
    return new Quat4d(
        PrimitiveParsingUtil.parseDouble(list.get(0)),
        PrimitiveParsingUtil.parseDouble(list.get(1)),
        PrimitiveParsingUtil.parseDouble(list.get(2)),
        PrimitiveParsingUtil.parseDouble(list.get(3)));
  }

  private static void deserializeBoneMotions(Map<String, Object> boneMotions, SbtmAnimation output) {
    boneMotions.forEach((name, boneMotionObj) -> {
      List<Object> frameList = (List<Object>) boneMotionObj;
      frameList.forEach(frameObj -> {
        List<Object> frame = (List<Object>) frameObj;
        double time = PrimitiveParsingUtil.parseDouble(frame.get(0));
        Vector3d translation = deserializeVector3d(frame.get(1));
        Quat4d rotation = deserializeQuat4d(frame.get(2));
        output.putBoneKeyFrame(name, time, new SbtmBonePose(translation, rotation));
      });
    });
  }

  private static void deserializeMorphMotions(Map<String, Object> morphMotions, SbtmAnimation output) {
    morphMotions.forEach((name, morphMotionObj) -> {
      List<Object> frameList = (List<Object>) morphMotionObj;
      frameList.forEach(frameObj -> {
        List<Object> frame = (List<Object>) frameObj;
        double time = PrimitiveParsingUtil.parseDouble(frame.get(0));
        double weight = PrimitiveParsingUtil.parseDouble(frame.get(1));
        output.putMorphKeyFrame(name, time, weight);
      });
    });
  }

  public static SbtmAnimation loadReadable(Path path) {
    String content = TextIo.readTextFile(path);
    ObjectMapper mapper = new ObjectMapper();
    Object json;
    try {
      json = mapper.readValue(content, Object.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    SbtmAnimation output = new SbtmAnimation();
    Map<String, Object> jsonMap = (Map<String, Object>) json;
    if (jsonMap.containsKey("boneMotions")) {
      deserializeBoneMotions((Map<String, Object>) jsonMap.get("boneMotions"), output);
    }
    if (jsonMap.containsKey("morphMotions")) {
      deserializeMorphMotions((Map<String, Object>) jsonMap.get("morphMotions"), output);
    }
    if (jsonMap.containsKey("repeating")) {
      output.repeating = TypeUtil.cast(jsonMap.get("repeating"), Boolean.class);
    }
    if (jsonMap.containsKey("duration")) {
      output.duration = PrimitiveParsingUtil.parseDouble(jsonMap.get("duration"));
    } else {
      double maxBoneTime = output.boneMotions
          .values()
          .stream()
          .map(TreeMap::lastKey)
          .reduce(Math::max)
          .orElse(0.0);
      double maxMorphTime =
          output.morphMotions.values().stream().map(TreeMap::lastKey).reduce(Math::max).orElse(0.0);
      output.duration = Math.max(maxBoneTime, maxMorphTime);
    }
    return output;
  }

  public int getBoneMotionCount() {
    return boneMotions.size();
  }

  public int getMorphMotionCount() {
    return morphMotions.size();
  }
}
