package hana04.gfxbase.serialize.gfxtype;

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
import hana04.gfxbase.gfxtype.BinarySerializationUtil;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.Value;

import javax.inject.Inject;
import javax.vecmath.Vector4d;
import java.util.Map;

public abstract class Vector4dSerialization {
  public static final String TYPE_NAME = "Vector4d";

  @HanaDeclareReadableDeserializer(TYPE_NAME)
  public static class ReadableDeserializer implements TypeReadableDeserializer<Vector4d> {
    @Inject
    public ReadableDeserializer() {
    }

    @Override
    public Vector4d deserialize(
        Map json,
        hana04.base.serialize.readable.ReadableDeserializer deserializer) {
      return VectorParsingUtil.parseVector4d(json.get("value"));
    }

    @Override
    public Class<Vector4d> getSerializedClass() {
      return Vector4d.class;
    }
  }

  @HanaDeclareReadableSerializerByClass(Vector4d.class)
  public static class ReadableSerializer implements TypeReadableSerializer<Vector4d> {
    @Inject
    public ReadableSerializer() {
    }

    @Override
    public Map serialize(Vector4d obj, hana04.base.serialize.readable.ReadableSerializer serializer) {
      return Maps.newHashMap(ImmutableMap.of(
          "type",
          TYPE_NAME,
          "value",
          Lists.newArrayList(obj.x, obj.y, obj.z, obj.w)));
    }
  }

  @HanaDeclareBinarySerializerByClass(Vector4d.class)
  public static class Vector4dBinarySerializer implements TypeBinarySerializer<Vector4d> {
    @Inject
    public Vector4dBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_VECTOR4D;
    }

    @Override
    public void serialize(
        Vector4d obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        BinarySerializationUtil.packVector4d(messagePacker, obj);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_VECTOR4D)
  public static class Vector4dBinaryDeserializer implements TypeBinaryDeserializer<Vector4d> {
    @Inject
    public Vector4dBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return Vector4d.class;
    }

    @Override
    public Vector4d deserialize(Value value, BinaryDeserializer deserializer) {
      return BinarySerializationUtil.unpackVector4d(value);
    }
  }
}
