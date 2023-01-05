package hana04.shakuyaku.sbtm;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import hana04.apt.annotation.HanaDeclareBinaryDeserializer;
import hana04.apt.annotation.HanaDeclareBinarySerializerByClass;
import hana04.apt.annotation.HanaDeclareReadableDeserializer;
import hana04.apt.annotation.HanaDeclareReadableSerializerByClass;
import hana04.base.serialize.binary.BinaryDeserializer;
import hana04.base.serialize.binary.BinarySerializer;
import hana04.base.serialize.binary.TypeBinaryDeserializer;
import hana04.base.serialize.binary.TypeBinarySerializer;
import hana04.base.serialize.readable.TypeReadableDeserializer;
import hana04.base.serialize.readable.TypeReadableSerializer;
import hana04.base.util.StringSerializationUtil;
import hana04.gfxbase.serialize.gfxtype.VectorParsingUtil;
import hana04.gfxbase.gfxtype.BinarySerializationUtil;
import hana04.serialize.PrimitiveParsingUtil;
import hana04.shakuyaku.TypeIds;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.Value;

import javax.inject.Inject;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.util.HashMap;
import java.util.Map;

public class SbtmPose {
  public static final String TYPE_NAME = "SbtmPose";

  public final HashMap<String, SbtmBonePose> bonePoses = new HashMap<>();
  public final HashMap<String, Double> morphPoses = new HashMap<>();

  public SbtmPose() {
    // NO-OP
  }

  public SbtmPose set(SbtmPose other) {
    for (Map.Entry<String, SbtmBonePose> otherEntry : other.bonePoses.entrySet()) {
      String boneName = otherEntry.getKey();
      SbtmBonePose otherBonePose = otherEntry.getValue();
      if (bonePoses.containsKey(otherEntry.getKey())) {
        bonePoses.get(otherEntry.getKey()).set(otherBonePose);
      } else {
        bonePoses.put(otherEntry.getKey(), new SbtmBonePose(otherBonePose));
      }
    }
    for (Map.Entry<String, SbtmBonePose> thisEntry : bonePoses.entrySet()) {
      if (!other.bonePoses.containsKey(thisEntry.getKey())) {
        SbtmBonePose thisBonePose = thisEntry.getValue();
        thisBonePose.translation.set(0, 0, 0);
        thisBonePose.rotation.set(0, 0, 0, 1);
      }
    }

    for (Map.Entry<String, Double> otherEntry : other.morphPoses.entrySet()) {
      String morphName = otherEntry.getKey();
      double otherMorphWeight = otherEntry.getValue();
      morphPoses.put(morphName, otherMorphWeight);
    }
    for (Map.Entry<String, Double> thisEntry : morphPoses.entrySet()) {
      if (!other.morphPoses.containsKey(thisEntry.getKey())) {
        morphPoses.put(thisEntry.getKey(), 0.0);
      }
    }
    return this;
  }

  public void getBonePose(String boneName, SbtmBonePose out) {
    if (bonePoses.containsKey(boneName)) {
      SbtmBonePose bonePose = bonePoses.get(boneName);
      out.set(bonePose);
    } else {
      out.clear();
    }
  }

  public void getBonePose(String boneName, Vector3d translation, Quat4d rotation) {
    if (bonePoses.containsKey(boneName)) {
      SbtmBonePose bonePose = bonePoses.get(boneName);
      translation.set(bonePose.translation);
      rotation.set(bonePose.rotation);
    } else {
      translation.set(0, 0, 0);
      rotation.set(0, 0, 0, 1);
    }
  }

  public SbtmPose setBonePose(String boneName, Vector3d translation, Quat4d rotation) {
    if (!bonePoses.containsKey(boneName)) {
      SbtmBonePose bonePose = new SbtmBonePose();
      bonePoses.put(boneName, bonePose);
    }
    SbtmBonePose bonePose = bonePoses.get(boneName);
    bonePose.translation.set(translation);
    bonePose.rotation.set(rotation);
    return this;
  }

  public SbtmPose setMorphPose(String morphName, double value) {
    morphPoses.put(morphName, value);
    return this;
  }

  public SbtmPose clear() {
    bonePoses.clear();
    morphPoses.clear();
    return this;
  }

  @HanaDeclareReadableSerializerByClass(SbtmPose.class)
  public static class ReadableSerializer implements TypeReadableSerializer<SbtmPose> {
    @Inject
    public ReadableSerializer() {
      // NO-OP
    }

    @Override
    public Map serialize(SbtmPose obj, hana04.base.serialize.readable.ReadableSerializer serializer) {
      HashMap<String, Object> result = new HashMap<>();
      result.put("type", TYPE_NAME);

      HashMap<String, Object> bonePoseMap = new HashMap<>();
      for (String boneName : obj.bonePoses.keySet()) {
        SbtmBonePose bonePose = obj.bonePoses.get(boneName);
        HashMap<String, Object> poseMap = new HashMap<>();
        poseMap.put(
            "translation",
            Lists.newArrayList(bonePose.translation.x, bonePose.translation.y, bonePose.translation.z));
        poseMap.put(
            "rotation",
            Lists.newArrayList(bonePose.rotation.x, bonePose.rotation.y, bonePose.rotation.z, bonePose.rotation.w));
      }

      HashMap<String, Object> morphPoseMap = new HashMap<>();
      for (String morphName : obj.morphPoses.keySet()) {
        morphPoseMap.put(morphName, obj.morphPoses.get(morphName));
      }

      result.put("bones", bonePoseMap);
      result.put("morphs", morphPoseMap);
      return result;
    }
  }

  @HanaDeclareReadableDeserializer(TYPE_NAME)
  public static class ReadableDeserializer implements TypeReadableDeserializer<SbtmPose> {
    @Inject
    public ReadableDeserializer() {
      // NO-OP
    }

    @Override
    public SbtmPose deserialize(Map json, hana04.base.serialize.readable.ReadableDeserializer deserializer) {
      SbtmPose pose = new SbtmPose();

      Preconditions.checkArgument(json.containsKey("type"));
      Preconditions.checkArgument(json.get("type").equals(TYPE_NAME));

      Preconditions.checkArgument(json.containsKey("bones"));
      Preconditions.checkArgument(json.get("bones") instanceof Map);
      Map<String, Object> bonesMap = (Map<String, Object>) json.get("bones");
      for (Map.Entry<String, Object> bonesMapEntry : bonesMap.entrySet()) {
        String boneName = bonesMapEntry.getKey();

        Preconditions.checkArgument(bonesMapEntry.getValue() instanceof Map);
        Map<String, Object> entryMap = (Map<String, Object>) bonesMapEntry.getValue();
        Preconditions.checkArgument(entryMap.containsKey("translation"));
        Vector3d translation = VectorParsingUtil.parseVector3d(entryMap.get("translation"));
        Preconditions.checkArgument(entryMap.containsKey("rotation"));
        Vector4d rotation = VectorParsingUtil.parseVector4d(entryMap.get("rotation"));

        SbtmBonePose bonePose = new SbtmBonePose();
        bonePose.translation.set(translation);
        bonePose.rotation.set(rotation);
        pose.bonePoses.put(boneName, bonePose);
      }

      Preconditions.checkArgument(json.containsKey("morphs"));
      Preconditions.checkArgument(json.get("morphs") instanceof Map);
      Map<String, Object> morphsMap = (Map<String, Object>) json.get("morphs");
      for (Map.Entry<String, Object> morphsMapEntry : morphsMap.entrySet()) {
        String morphName = morphsMapEntry.getKey();
        Double value = PrimitiveParsingUtil.parseDouble(morphsMapEntry.getValue());
        pose.morphPoses.put(morphName, value);
      }

      return pose;
    }

    @Override
    public Class<SbtmPose> getSerializedClass() {
      return SbtmPose.class;
    }
  }

  @HanaDeclareBinarySerializerByClass(SbtmPose.class)
  public static class SbtmPoseBinarySerializer implements TypeBinarySerializer<SbtmPose> {
    @Inject
    public SbtmPoseBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_SBTM_POSE;
    }

    @Override
    public void serialize(
        SbtmPose obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        messagePacker.packArrayHeader(2);
        {
          messagePacker.packArrayHeader(obj.bonePoses.size());
          for (Map.Entry<String, SbtmBonePose> entry : obj.bonePoses.entrySet()) {
            String key = entry.getKey();
            SbtmBonePose bonePose = entry.getValue();
            Vector3d translation = bonePose.translation;
            Quat4d rotation = bonePose.rotation;

            messagePacker.packArrayHeader(3);
            StringSerializationUtil.packString(messagePacker, key);
            BinarySerializationUtil.packVector3d(messagePacker, translation);
            BinarySerializationUtil.packQuat4d(messagePacker, rotation);
          }
        }
        {
          messagePacker.packArrayHeader(obj.morphPoses.size());
          for (Map.Entry<String, Double> entry : obj.morphPoses.entrySet()) {
            String key = entry.getKey();
            Double weight = entry.getValue();

            messagePacker.packArrayHeader(2);
            StringSerializationUtil.packString(messagePacker, key);
            messagePacker.packDouble(weight);
          }
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_SBTM_POSE)
  public static class SbtmPoseBinaryDeserializer implements TypeBinaryDeserializer<SbtmPose> {
    @Inject
    public SbtmPoseBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return SbtmPose.class;
    }

    @Override
    public SbtmPose deserialize(Value value, BinaryDeserializer deserializer) {
      SbtmPose sbtmPose = new SbtmPose();
      ArrayValue topValue = value.asArrayValue();
      {
        ArrayValue bonePosesValue = topValue.get(0).asArrayValue();
        for (int i = 0; i < bonePosesValue.size(); i++) {
          ArrayValue bonePose = bonePosesValue.get(i).asArrayValue();
          String key = bonePose.get(0).asStringValue().asString();
          Vector3d translation = BinarySerializationUtil.unpackVector3d(bonePose.get(1));
          Quat4d rotation = BinarySerializationUtil.unpackQuat4d(bonePose.get(2));
          sbtmPose.bonePoses.put(key, new SbtmBonePose(translation, rotation));
        }
      }
      {
        ArrayValue morphPosesValue = topValue.get(1).asArrayValue();
        for (int i = 0; i < morphPosesValue.size(); i++) {
          ArrayValue morphPose = morphPosesValue.get(i).asArrayValue();
          String key = morphPose.get(0).asStringValue().asString();
          Double weight = morphPose.get(1).asFloatValue().toDouble();
          sbtmPose.morphPoses.put(key, weight);
        }
      }
      return sbtmPose;
    }
  }
}
