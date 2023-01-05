package hana04.gfxbase.serialize.gfxtype;

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
import hana04.gfxbase.gfxtype.Aabb2d;
import hana04.gfxbase.gfxtype.BinarySerializationUtil;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.Value;

import javax.inject.Inject;
import java.util.Map;

public abstract class Aabb2dSerialization {
  public static final String TYPE_NAME = "Aabb2d";

  @HanaDeclareReadableDeserializer(TYPE_NAME)
  public static class ReadableDeserializer implements TypeReadableDeserializer<Aabb2d> {
    @Inject
    public ReadableDeserializer() {
    }

    @Override
    public Aabb2d deserialize(
        Map json,
        hana04.base.serialize.readable.ReadableDeserializer deserializer) {
      return AabbParsingUtil.parseAabb2d(json.get("value"));
    }

    @Override
    public Class<Aabb2d> getSerializedClass() {
      return Aabb2d.class;
    }
  }

  @HanaDeclareReadableSerializerByClass(Aabb2d.class)
  public static class ReadableSerializer implements TypeReadableSerializer<Aabb2d> {
    @Inject
    public ReadableSerializer() {
    }

    @Override
    public Map serialize(Aabb2d obj, hana04.base.serialize.readable.ReadableSerializer serializer) {
      return Maps.newHashMap(ImmutableMap.of(
          "type",
          TYPE_NAME,
          "value",
          Lists.newArrayList(ImmutableList.of(obj.pMin.x, obj.pMin.y, obj.pMax.x, obj.pMax.y))));
    }
  }

  @HanaDeclareBinarySerializerByClass(Aabb2d.class)
  public static class Aabb2dBinarySerializer implements TypeBinarySerializer<Aabb2d> {
    @Inject
    public Aabb2dBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_AABB2D;
    }

    @Override
    public void serialize(
        Aabb2d obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        BinarySerializationUtil.packAabb2d(messagePacker, obj);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_AABB2D)
  public static class Aabb2dBinaryDeserializer implements TypeBinaryDeserializer<Aabb2d> {
    @Inject
    public Aabb2dBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return Aabb2d.class;
    }

    @Override
    public Aabb2d deserialize(Value value, BinaryDeserializer deserializer) {
      return BinarySerializationUtil.unpackAabb2d(value);
    }
  }
}
