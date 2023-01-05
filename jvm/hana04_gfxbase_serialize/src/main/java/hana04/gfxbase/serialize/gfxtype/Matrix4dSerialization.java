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
import org.msgpack.core.MessagePacker;
import org.msgpack.value.Value;

import javax.inject.Inject;
import javax.vecmath.Matrix4d;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class Matrix4dSerialization {
  public static final String TYPE_NAME = "Matrix4d";

  @HanaDeclareReadableDeserializer(TYPE_NAME)
  public static class ReadableDeserializer implements TypeReadableDeserializer<Matrix4d> {
    @Inject
    public ReadableDeserializer() {
    }

    @Override
    public Matrix4d deserialize(
        Map json,
        hana04.base.serialize.readable.ReadableDeserializer deserializer) {
      return VectorParsingUtil.parseMatrix4d(json);
    }

    @Override
    public Class<Matrix4d> getSerializedClass() {
      return Matrix4d.class;
    }
  }

  @HanaDeclareReadableSerializerByClass(Matrix4d.class)
  public static class ReadableSerializer implements TypeReadableSerializer<Matrix4d> {
    @Inject
    public ReadableSerializer() {
    }

    @Override
    public Map serialize(Matrix4d obj, hana04.base.serialize.readable.ReadableSerializer serializer) {
      return Maps.newHashMap(ImmutableMap.of("type", TYPE_NAME, "value", readableSerializeValue(obj)));
    }
  }

  public static List<List<Double>> readableSerializeValue(Matrix4d obj) {
    return Arrays.asList(
        Arrays.asList(obj.m00, obj.m01, obj.m02, obj.m03),
        Arrays.asList(obj.m10, obj.m11, obj.m12, obj.m13),
        Arrays.asList(obj.m20, obj.m21, obj.m22, obj.m23),
        Arrays.asList(obj.m30, obj.m31, obj.m32, obj.m33));
  }

  @HanaDeclareBinarySerializerByClass(Matrix4d.class)
  public static class Matrix4dBinarySerializer implements TypeBinarySerializer<Matrix4d> {
    @Inject
    public Matrix4dBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_MATRIX4D;
    }

    @Override
    public void serialize(
        Matrix4d obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        BinarySerializationUtil.packMatrix4d(messagePacker, obj);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_MATRIX4D)
  public static class Matrix4dBinaryDeserializer implements TypeBinaryDeserializer<Matrix4d> {
    @Inject
    public Matrix4dBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return Matrix4d.class;
    }

    @Override
    public Matrix4d deserialize(Value value, BinaryDeserializer deserializer) {
      return BinarySerializationUtil.unpackMatrix4d(value);
    }
  }
}
