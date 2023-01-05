package hana04.gfxbase.serialize.spectrum;

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
import hana04.gfxbase.spectrum.scalar.Scalar;
import hana04.serialize.PrimitiveParsingUtil;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.Value;

import javax.inject.Inject;
import java.util.Map;

public class ScalarSerialization {
  static final String TYPE_NAME = "Scalar";

  @HanaDeclareReadableDeserializer(TYPE_NAME)
  public static class ReadableDeserializer implements TypeReadableDeserializer<Scalar> {
    @Inject
    public ReadableDeserializer() {
    }

    @Override
    public Scalar deserialize(
        Map json,
        hana04.base.serialize.readable.ReadableDeserializer deserializer) {
      return new Scalar(PrimitiveParsingUtil.parseDouble(json.get("value")));
    }

    @Override
    public Class<Scalar> getSerializedClass() {
      return Scalar.class;
    }
  }

  @HanaDeclareReadableSerializerByClass(Scalar.class)
  public static class ReadableSerializer implements TypeReadableSerializer<Scalar> {
    @Inject
    public ReadableSerializer() {
    }

    @Override
    public Map serialize(Scalar obj, hana04.base.serialize.readable.ReadableSerializer serializer) {
      return Maps.newHashMap(ImmutableMap.of("type", TYPE_NAME, "value", obj.toDouble()));
    }
  }

  @HanaDeclareBinarySerializerByClass(Scalar.class)
  public static class ScalarBinarySerializer implements TypeBinarySerializer<Scalar> {
    @Inject
    public ScalarBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_SCALAR;
    }

    @Override
    public void serialize(
        Scalar obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        messagePacker.packDouble(obj.toDouble());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_SCALAR)
  public static class ScalarBinaryDeserializer implements TypeBinaryDeserializer<Scalar> {
    @Inject
    public ScalarBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return Scalar.class;
    }

    @Override
    public Scalar deserialize(Value value, BinaryDeserializer deserializer) {
      return new Scalar(value.asFloatValue().toDouble());
    }
  }
}
