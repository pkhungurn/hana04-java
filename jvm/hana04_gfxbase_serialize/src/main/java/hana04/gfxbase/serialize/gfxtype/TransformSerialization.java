package hana04.gfxbase.serialize.gfxtype;

import com.google.common.collect.ImmutableMap;
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
import hana04.gfxbase.gfxtype.Transform;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.Value;

import javax.inject.Inject;
import java.util.Map;

public class TransformSerialization {
  public static final String TYPE_NAME = "Transform";

  @HanaDeclareReadableDeserializer(TYPE_NAME)
  public static class ReadableDeserializer implements TypeReadableDeserializer<Transform> {
    @Inject
    public ReadableDeserializer() {
    }

    @Override
    public Transform deserialize(Map json, hana04.base.serialize.readable.ReadableDeserializer deserializer) {
      return new Transform(VectorParsingUtil.parseMatrix4d(json));
    }

    @Override
    public Class<Transform> getSerializedClass() {
      return Transform.class;
    }
  }

  @HanaDeclareReadableSerializerByClass(Transform.class)
  public static class ReadableSerializer implements TypeReadableSerializer<Transform> {
    @Inject
    public ReadableSerializer() {
    }

    @Override
    public Map serialize(Transform obj, hana04.base.serialize.readable.ReadableSerializer serializer) {
      return Maps.newHashMap(ImmutableMap.of(
          "type", TYPE_NAME,
          "value", Matrix4dSerialization.readableSerializeValue(obj.m)));
    }
  }

  @HanaDeclareBinarySerializerByClass(Transform.class)
  public static class TransformBinarySerializer implements TypeBinarySerializer<Transform> {
    @Inject
    public TransformBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_TRANSFORM;
    }

    @Override
    public void serialize(
        Transform obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        BinarySerializationUtil.packTransform(messagePacker, obj);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_TRANSFORM)
  public static class TransformBinaryDeserializer implements TypeBinaryDeserializer<Transform> {
    @Inject
    public TransformBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return Transform.class;
    }

    @Override
    public Transform deserialize(Value value, BinaryDeserializer deserializer) {
      return BinarySerializationUtil.unpackTransform(value);
    }
  }
}
