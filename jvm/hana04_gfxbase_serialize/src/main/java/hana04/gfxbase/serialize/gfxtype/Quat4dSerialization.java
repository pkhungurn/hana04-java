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
import javax.vecmath.Quat4d;
import java.util.Map;

public class Quat4dSerialization {
  public static final String TYPE_NAME = "Quat4d";

  @HanaDeclareReadableDeserializer(TYPE_NAME)
  public static class ReadableDeserializer implements TypeReadableDeserializer<Quat4d> {
    @Inject
    public ReadableDeserializer() {
    }

    @Override
    public Quat4d deserialize(
        Map json,
        hana04.base.serialize.readable.ReadableDeserializer deserializer) {
      return VectorParsingUtil.parseQuat4d(json.get("value"));
    }

    @Override
    public Class<Quat4d> getSerializedClass() {
      return Quat4d.class;
    }
  }

  @HanaDeclareReadableSerializerByClass(Quat4d.class)
  public static class ReadableSerializer implements TypeReadableSerializer<Quat4d> {
    @Inject
    public ReadableSerializer() {
    }

    @Override
    public Map serialize(Quat4d obj, hana04.base.serialize.readable.ReadableSerializer serializer) {
      return Maps.newHashMap(ImmutableMap.of(
          "type",
          TYPE_NAME,
          "value",
          Lists.newArrayList(obj.x, obj.y, obj.z, obj.w)));
    }
  }

  @HanaDeclareBinarySerializerByClass(Quat4d.class)
  public static class Quat4dBinarySerializer implements TypeBinarySerializer<Quat4d> {
    @Inject
    public Quat4dBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_QUAT4D;
    }

    @Override
    public void serialize(
        Quat4d obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        BinarySerializationUtil.packQuat4d(messagePacker, obj);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_QUAT4D)
  public static class Quat4dBinaryDeserializer implements TypeBinaryDeserializer<Quat4d> {
    @Inject
    public Quat4dBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return Quat4d.class;
    }

    @Override
    public Quat4d deserialize(Value value, BinaryDeserializer deserializer) {
      return BinarySerializationUtil.unpackQuat4d(value);
    }
  }
}
