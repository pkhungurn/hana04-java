package hana04.shakuyaku.texture.twodim;

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
import hana04.base.util.StringSerializationUtil;
import hana04.base.util.TypeUtil;
import hana04.shakuyaku.TypeIds;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.Value;

import javax.inject.Inject;
import java.util.Map;

public enum WrapMode {
  CLAMP,
  REPEAT;

  public String toString() {
    switch (this) {
      case CLAMP:
        return "clamp";
      case REPEAT:
        return "repeat";
      default:
        throw new IllegalArgumentException("It is not possible to reach this code path");
    }
  }

  public static WrapMode fromString(String s) {
    switch (s.toLowerCase()) {
      case "clamp":
        return CLAMP;
      case "repeat":
        return REPEAT;
      default:
        throw new IllegalArgumentException("Invalid string value: " + s);
    }
  }

  public static final String TYPE_NAME = "texture.twodim.WrapMode";

  @HanaDeclareReadableSerializerByClass(WrapMode.class)
  public static class ReadableSerializer implements TypeReadableSerializer<WrapMode> {
    @Inject
    public ReadableSerializer() {
    }

    @Override
    public Map serialize(WrapMode obj, hana04.base.serialize.readable.ReadableSerializer serializer) {
      return Maps.newHashMap(ImmutableMap.of(
          "type", TYPE_NAME,
          "value", obj.toString()
      ));
    }
  }

  @HanaDeclareReadableDeserializer(TYPE_NAME)
  public static class ReadableDeserializer implements TypeReadableDeserializer<WrapMode> {
    @Inject
    public ReadableDeserializer() {
    }

    @Override
    public WrapMode deserialize(Map json, hana04.base.serialize.readable.ReadableDeserializer deserializer) {
      return WrapMode.fromString(TypeUtil.cast(json.get("value"), String.class));
    }

    @Override
    public Class<WrapMode> getSerializedClass() {
      return WrapMode.class;
    }
  }

  @HanaDeclareBinarySerializerByClass(WrapMode.class)
  public static class WrapModeBinarySerializer implements TypeBinarySerializer<WrapMode> {
    @Inject
    public WrapModeBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_TEXTURE_TWO_DIM_WRAP_MODE;
    }

    @Override
    public void serialize(
        WrapMode obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        StringSerializationUtil.packString(messagePacker, obj.toString());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_TEXTURE_TWO_DIM_WRAP_MODE)
  public static class WrapModeBinaryDeserializer implements TypeBinaryDeserializer<WrapMode> {
    @Inject
    public WrapModeBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return WrapMode.class;
    }

    @Override
    public WrapMode deserialize(Value value, BinaryDeserializer deserializer) {
      return WrapMode.fromString(value.asStringValue().asString());
    }
  }
}
