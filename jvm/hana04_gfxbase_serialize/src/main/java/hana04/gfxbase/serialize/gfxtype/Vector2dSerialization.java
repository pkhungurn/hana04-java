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
import org.inferred.freebuilder.shaded.com.google.common.collect.Lists;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.Value;

import javax.inject.Inject;
import javax.vecmath.Vector2d;
import java.util.Map;

public abstract class Vector2dSerialization {
  public static final String TYPE_NAME = "Vector2d";

  @HanaDeclareReadableDeserializer(TYPE_NAME)
  public static class ReadableDeserializer implements TypeReadableDeserializer<Vector2d> {
    @Inject
    public ReadableDeserializer() {
    }

    @Override
    public Vector2d deserialize(
        Map json,
        hana04.base.serialize.readable.ReadableDeserializer deserializer) {
      return VectorParsingUtil.parseVector2d(json.get("value"));
    }

    @Override
    public Class<Vector2d> getSerializedClass() {
      return Vector2d.class;
    }
  }

  @HanaDeclareReadableSerializerByClass(Vector2d.class)
  public static class ReadableSerializer implements TypeReadableSerializer<Vector2d> {
    @Inject
    public ReadableSerializer() {
    }

    @Override
    public Map serialize(Vector2d obj, hana04.base.serialize.readable.ReadableSerializer serializer) {
      return Maps.newHashMap(ImmutableMap.of("type", TYPE_NAME, "value", Lists.newArrayList(obj.x, obj.y)));
    }
  }

  @HanaDeclareBinarySerializerByClass(Vector2d.class)
  public static class Vector2dBinarySerializer implements TypeBinarySerializer<Vector2d> {
    @Inject
    public Vector2dBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_VECTOR2D;
    }

    @Override
    public void serialize(
        Vector2d obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        BinarySerializationUtil.packVector2d(messagePacker, obj);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_VECTOR2D)
  public static class Vector2dBinaryDeserializer implements TypeBinaryDeserializer<Vector2d> {
    @Inject
    public Vector2dBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return Vector2d.class;
    }

    @Override
    public Vector2d deserialize(Value value, BinaryDeserializer deserializer) {
      return BinarySerializationUtil.unpackVector2d(value);
    }
  }
}
