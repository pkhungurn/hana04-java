package hana04.serialize.caching;

import com.google.common.collect.ImmutableMap;
import hana04.apt.annotation.HanaDeclareBinaryDeserializer;
import hana04.apt.annotation.HanaDeclareBinarySerializerByClass;
import hana04.apt.annotation.HanaDeclareReadableDeserializer;
import hana04.apt.annotation.HanaDeclareReadableSerializerByClass;
import hana04.base.caching.Direct;
import hana04.base.serialize.binary.BinaryDeserializer;
import hana04.base.serialize.binary.BinarySerializer;
import hana04.base.serialize.binary.TypeBinaryDeserializer;
import hana04.base.serialize.binary.TypeBinarySerializer;
import hana04.base.serialize.readable.JsonParsingUtil;
import hana04.base.serialize.readable.ReadableDeserializer;
import hana04.base.serialize.readable.ReadableSerializer;
import hana04.base.serialize.readable.TypeReadableDeserializer;
import hana04.base.serialize.readable.TypeReadableSerializer;
import hana04.serialize.TypeIds;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.Preconditions;
import org.msgpack.value.Value;

import javax.inject.Inject;
import java.util.Map;

public class DirectSerialization {
  public static final String TYPE_NAME = "Direct";

  @HanaDeclareReadableSerializerByClass(Direct.class)
  public static class DirectReadableSerializer implements TypeReadableSerializer<Direct> {
    @Inject
    public DirectReadableSerializer() {
      // NO-OP
    }

    @Override
    public Map serialize(Direct obj, ReadableSerializer serializer) {
      return ImmutableMap.of("type", TYPE_NAME, "value", serializer.serialize(obj.value));
    }
  }

  @HanaDeclareReadableDeserializer(TYPE_NAME)
  public static class DirectReadableDeserializer implements TypeReadableDeserializer<Direct> {

    @Inject
    public DirectReadableDeserializer() {
      // NO-OP
    }

    @Override
    public Class<Direct> getSerializedClass() {
      return Direct.class;
    }

    @Override
    public Direct deserialize(Map json, ReadableDeserializer deserializer) {
      return Direct.of(deserializer.deserialize((Map) JsonParsingUtil.getProperty(json, "value")));
    }
  }

  @HanaDeclareBinarySerializerByClass(Direct.class)
  public static class DirectBinarySerializer implements TypeBinarySerializer<Direct> {
    @Inject
    public DirectBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_DIRECT;
    }

    @Override
    public void serialize(
        Direct obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        serializer.serialize(obj.value);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_DIRECT)
  public static class DirectBinaryDeserializer implements TypeBinaryDeserializer<Direct> {
    @Inject
    public DirectBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return Direct.class;
    }

    @Override
    public Direct deserialize(Value value, BinaryDeserializer deserializer) {
      Preconditions.checkArgument(value.isMapValue());
      return Direct.of(deserializer.deserialize(value.asMapValue()));
    }
  }
}
