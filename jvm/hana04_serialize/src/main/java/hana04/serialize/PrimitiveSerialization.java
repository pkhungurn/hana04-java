package hana04.serialize;

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
import hana04.base.serialize.readable.ReadableDeserializer;
import hana04.base.serialize.readable.ReadableSerializer;
import hana04.base.serialize.readable.TypeReadableDeserializer;
import hana04.base.serialize.readable.TypeReadableSerializer;
import hana04.base.util.StringSerializationUtil;
import hana04.base.util.TypeUtil;
import hana04.base.util.UuidUtil;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.Value;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class PrimitiveSerialization {
  public static final String INTEGER_TYPE_NAME = "Integer";

  @HanaDeclareReadableSerializerByClass(Integer.class)
  public static class IntegerReadableSerializer implements TypeReadableSerializer<Integer> {

    @Inject
    public IntegerReadableSerializer() {
    }

    @Override
    public Map serialize(Integer obj, ReadableSerializer serializer) {
      return Maps.newHashMap(ImmutableMap.of("type", INTEGER_TYPE_NAME, "value", obj));
    }
  }

  @HanaDeclareBinarySerializerByClass(Integer.class)
  public static class IntegerBinarySerializer implements TypeBinarySerializer<Integer> {

    @Inject
    public IntegerBinarySerializer() {
      // NO-OP
    }

    @Override
    public void serialize(
        Integer obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        messagePacker.packInt(obj);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_INTEGER;
    }
  }

  @HanaDeclareReadableDeserializer(INTEGER_TYPE_NAME)
  public static class IntegerReadableDeserializer implements TypeReadableDeserializer<Integer> {

    @Inject
    public IntegerReadableDeserializer() {
    }

    @Override
    public Integer deserialize(Map json, ReadableDeserializer deserializer) {
      return PrimitiveParsingUtil.parseInteger(json.get("value"));
    }

    @Override
    public Class<Integer> getSerializedClass() {
      return Integer.class;
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_INTEGER)
  public static class IntegerBinaryDeserializer implements TypeBinaryDeserializer<Integer> {

    @Inject
    public IntegerBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Integer deserialize(Value value, BinaryDeserializer deserializer) {
      return value.asIntegerValue().asInt();
    }

    @Override
    public Class<?> getSerializedClass() {
      return Integer.class;
    }
  }

  public static final String FLOAT_TYPE_NAME = "Float";

  @HanaDeclareReadableSerializerByClass(Float.class)
  public static class FloatReadableSerializer implements TypeReadableSerializer<Float> {
    @Inject
    public FloatReadableSerializer() {
      // NO-OP
    }

    @Override
    public Map serialize(Float obj, ReadableSerializer serializer) {
      return Maps.newHashMap(ImmutableMap.of("type", FLOAT_TYPE_NAME, "value", obj));
    }
  }

  @HanaDeclareReadableDeserializer(FLOAT_TYPE_NAME)
  public static class FloatReadableDeserializer implements TypeReadableDeserializer<Float> {

    @Inject
    public FloatReadableDeserializer() {
      // NO-OP
    }

    @Override
    public Class<Float> getSerializedClass() {
      return Float.class;
    }

    @Override
    public Float deserialize(Map json, ReadableDeserializer deserializer) {
      return PrimitiveParsingUtil.parseDouble(json.get("value")).floatValue();
    }
  }

  @HanaDeclareBinarySerializerByClass(Float.class)
  public static class FloatBinarySerializer implements TypeBinarySerializer<Float> {
    @Inject
    public FloatBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_FLOAT;
    }

    @Override
    public void serialize(
        Float obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        messagePacker.packFloat(obj);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_FLOAT)
  public static class FloatBinaryDeserializer implements TypeBinaryDeserializer<Float> {
    @Inject
    public FloatBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return Float.class;
    }

    @Override
    public Float deserialize(Value value, BinaryDeserializer deserializer) {
      return value.asFloatValue().toFloat();
    }
  }

  public static final String DOUBLE_TYPE_NAME = "Double";

  @HanaDeclareReadableSerializerByClass(Double.class)
  public static class DoubleReadableSerializer implements TypeReadableSerializer<Double> {

    @Inject
    public DoubleReadableSerializer() {
    }

    @Override
    public Map serialize(Double obj, ReadableSerializer serializer) {
      return Maps.newHashMap(ImmutableMap.of("type", DOUBLE_TYPE_NAME, "value", obj));
    }
  }

  @HanaDeclareBinarySerializerByClass(Double.class)
  public static class DoubleBinarySerializer implements TypeBinarySerializer<Double> {
    @Inject
    public DoubleBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_DOUBLE;
    }

    @Override
    public void serialize(
        Double obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        messagePacker.packDouble(obj);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareReadableDeserializer(DOUBLE_TYPE_NAME)
  public static class DoubleReadableDeserializer implements TypeReadableDeserializer<Double> {
    @Inject
    public DoubleReadableDeserializer() {
    }

    @Override
    public Double deserialize(Map json, ReadableDeserializer deserializer) {
      return PrimitiveParsingUtil.parseDouble(json.get("value"));
    }

    @Override
    public Class<Double> getSerializedClass() {
      return Double.class;
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_DOUBLE)
  public static class DoubleBinaryDeserializer implements TypeBinaryDeserializer<Double> {

    @Inject
    public DoubleBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Double deserialize(Value value, BinaryDeserializer deserializer) {
      return value.asFloatValue().toDouble();
    }

    @Override
    public Class<?> getSerializedClass() {
      return Double.class;
    }
  }

  public static final String LONG_TYPE_NAME = "Long";

  @HanaDeclareReadableSerializerByClass(Long.class)
  public static class LongReadableSerializer implements TypeReadableSerializer<Long> {
    @Inject
    public LongReadableSerializer() {
      // NO-OP
    }

    @Override
    public Map serialize(Long obj, ReadableSerializer serializer) {
      return Maps.newHashMap(ImmutableMap.of("type", LONG_TYPE_NAME, "value", obj));
    }
  }

  @HanaDeclareReadableDeserializer(LONG_TYPE_NAME)
  public static class LongReadableDeserializer implements TypeReadableDeserializer<Long> {

    @Inject
    public LongReadableDeserializer() {
      // NO-OP
    }

    @Override
    public Class<Long> getSerializedClass() {
      return Long.class;
    }

    @Override
    public Long deserialize(Map json, ReadableDeserializer deserializer) {
      return PrimitiveParsingUtil.parseLong(json.get("value"));
    }
  }

  @HanaDeclareBinarySerializerByClass(Long.class)
  public static class LongBinarySerializer implements TypeBinarySerializer<Long> {
    @Inject
    public LongBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_LONG;
    }

    @Override
    public void serialize(
        Long obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        messagePacker.packLong(obj);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_LONG)
  public static class LongBinaryDeserializer implements TypeBinaryDeserializer<Long> {
    @Inject
    public LongBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return Long.class;
    }

    @Override
    public Long deserialize(Value value, BinaryDeserializer deserializer) {
      return value.asIntegerValue().asLong();
    }
  }

  public static final String UUID_TYPE_NAME = "Uuid";

  @HanaDeclareReadableSerializerByClass(UUID.class)
  public static class UuidReadableSerializer implements TypeReadableSerializer<UUID> {

    @Inject
    public UuidReadableSerializer() {
    }

    @Override
    public Map serialize(UUID obj, ReadableSerializer serializer) {
      return Maps.newHashMap(ImmutableMap.of("type", UUID_TYPE_NAME, "value", obj.toString()));
    }
  }

  @HanaDeclareReadableDeserializer(UUID_TYPE_NAME)
  public static class UuidReadableDeserializer implements TypeReadableDeserializer<UUID> {
    @Inject
    public UuidReadableDeserializer() {
    }

    @Override
    public UUID deserialize(Map json, ReadableDeserializer deserializer) {
      return UUID.fromString(TypeUtil.cast(json.get("value"), String.class));
    }

    @Override
    public Class<UUID> getSerializedClass() {
      return UUID.class;
    }
  }

  @HanaDeclareBinarySerializerByClass(UUID.class)
  public static class UUIDBinarySerializer implements TypeBinarySerializer<UUID> {
    @Inject
    public UUIDBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_UUID;
    }

    @Override
    public void serialize(
        UUID obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        UuidUtil.pack(messagePacker, obj);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_UUID)
  public static class UUIDBinaryDeserializer implements TypeBinaryDeserializer<UUID> {
    @Inject
    public UUIDBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return UUID.class;
    }

    @Override
    public UUID deserialize(Value value, BinaryDeserializer deserializer) {
      return UuidUtil.unpack(value);
    }
  }

  public static final String STRING_TYPE_NAME = "String";

  @HanaDeclareReadableSerializerByClass(String.class)
  public static class StringReadableSerializer implements TypeReadableSerializer<String> {
    @Inject
    public StringReadableSerializer() {
    }

    @Override
    public Map serialize(String obj, ReadableSerializer serializer) {
      return Maps.newHashMap(ImmutableMap.of(
          "type", STRING_TYPE_NAME,
          "value", obj));
    }
  }

  @HanaDeclareReadableDeserializer(STRING_TYPE_NAME)
  public static class StringReadableDeserializer implements TypeReadableDeserializer<String> {
    @Inject
    public StringReadableDeserializer() {
    }

    @Override
    public String deserialize(Map json, ReadableDeserializer deserializer) {
      return TypeUtil.cast(json.get("value"), String.class);
    }

    @Override
    public Class<String> getSerializedClass() {
      return String.class;
    }
  }

  @HanaDeclareBinarySerializerByClass(String.class)
  public static class StringBinarySerializer implements TypeBinarySerializer<String> {
    @Inject
    public StringBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_STRING;
    }

    @Override
    public void serialize(
        String obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        StringSerializationUtil.packString(messagePacker, obj);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_STRING)
  public static class StringBinaryDeserializer implements TypeBinaryDeserializer<String> {
    @Inject
    public StringBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return String.class;
    }

    @Override
    public String deserialize(Value value, BinaryDeserializer deserializer) {
      return value.asStringValue().asString();
    }
  }

  public static final String BOOLEAN_TYPE_NAME = "Boolean";

  @HanaDeclareReadableSerializerByClass(Boolean.class)
  public static class BooleanReadableSerializer implements TypeReadableSerializer<Boolean> {
    @Inject
    public BooleanReadableSerializer() {
    }

    @Override
    public Map serialize(Boolean obj, ReadableSerializer serializer) {
      return Maps.newHashMap(ImmutableMap.of(
          "type", BOOLEAN_TYPE_NAME,
          "value", obj));
    }
  }

  @HanaDeclareReadableDeserializer(BOOLEAN_TYPE_NAME)
  public static class BooleanReadableDeserializer implements TypeReadableDeserializer<Boolean> {
    @Inject
    public BooleanReadableDeserializer() {
    }

    @Override
    public Boolean deserialize(Map json, ReadableDeserializer deserializer) {
      return TypeUtil.cast(json.get("value"), Boolean.class);
    }

    @Override
    public Class<Boolean> getSerializedClass() {
      return Boolean.class;
    }
  }

  @HanaDeclareBinarySerializerByClass(Boolean.class)
  public static class BooleanBinarySerializer implements TypeBinarySerializer<Boolean> {
    @Inject
    public BooleanBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_BOOLEAN;
    }

    @Override
    public void serialize(
        Boolean obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        messagePacker.packBoolean(obj);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_BOOLEAN)
  public static class BooleanBinaryDeserializer implements TypeBinaryDeserializer<Boolean> {
    @Inject
    public BooleanBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return Boolean.class;
    }

    @Override
    public Boolean deserialize(Value value, BinaryDeserializer deserializer) {
      return value.asBooleanValue().getBoolean();
    }
  }
}
