package hana04.gfxbase.serialize.spectrum;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import hana04.gfxbase.serialize.TypeIds;
import hana04.gfxbase.serialize.gfxtype.VectorParsingUtil;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.gfxtype.BinarySerializationUtil;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.Value;

import javax.inject.Inject;
import javax.vecmath.Vector3d;
import java.util.Map;

public class RgbSerialization {
  static final String TYPE_NAME = "Rgb";

  @HanaDeclareReadableDeserializer(TYPE_NAME)
  public static class ReadableDeserializer implements TypeReadableDeserializer<Rgb> {
    @Inject
    public ReadableDeserializer() {
    }

    @Override
    public Rgb deserialize(
        Map json,
        hana04.base.serialize.readable.ReadableDeserializer deserializer) {
      Vector3d v = VectorParsingUtil.parseVector3d(json.get("value"));
      return new Rgb(v.x, v.y, v.z);
    }

    @Override
    public Class<Rgb> getSerializedClass() {
      return Rgb.class;
    }
  }

  @HanaDeclareReadableSerializerByClass(Rgb.class)
  public static class ReadableSerializer implements TypeReadableSerializer<Rgb> {
    @Inject
    public ReadableSerializer() {
    }

    @Override
    public Map serialize(Rgb obj, hana04.base.serialize.readable.ReadableSerializer serializer) {
      return Maps.newHashMap(ImmutableMap.of(
          "type",
          TYPE_NAME,
          "value",
          Lists.newArrayList(ImmutableList.of(obj.x, obj.y, obj.z))));
    }
  }

  @HanaDeclareBinarySerializerByClass(Rgb.class)
  public static class RgbBinarySerializer implements TypeBinarySerializer<Rgb> {
    @Inject
    public RgbBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_RGB;
    }

    @Override
    public void serialize(
        Rgb obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        BinarySerializationUtil.packTuple3d(messagePacker, obj);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_RGB)
  public static class RgbBinaryDeserializer implements TypeBinaryDeserializer<Rgb> {
    @Inject
    public RgbBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return Rgb.class;
    }

    @Override
    public Rgb deserialize(Value value, BinaryDeserializer deserializer) {
      return new Rgb(BinarySerializationUtil.unpackVector3d(value));
    }
  }
}
