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
import javax.vecmath.Vector3d;
import java.util.Map;

public abstract class Vector3dSerialization {
  static final String TYPE_NAME = "Vector3d";

  @HanaDeclareReadableDeserializer(TYPE_NAME)
  public static class ReadableDeserializer implements TypeReadableDeserializer<Vector3d> {
    @Inject
    public ReadableDeserializer() {
    }

    @Override
    public Vector3d deserialize(
        Map json,
        hana04.base.serialize.readable.ReadableDeserializer deserializer) {
      return VectorParsingUtil.parseVector3d(json.get("value"));
    }

    @Override
    public Class<Vector3d> getSerializedClass() {
      return Vector3d.class;
    }
  }

  @HanaDeclareReadableSerializerByClass(Vector3d.class)
  public static class ReadableSerializer implements TypeReadableSerializer<Vector3d> {
    @Inject
    public ReadableSerializer() {
    }

    @Override
    public Map serialize(Vector3d obj, hana04.base.serialize.readable.ReadableSerializer serializer) {
      return Maps.newHashMap(ImmutableMap.of("type", TYPE_NAME, "value", Lists.newArrayList(obj.x, obj.y, obj.z)));
    }
  }

  @HanaDeclareBinarySerializerByClass(Vector3d.class)
  public static class Vector3dBinarySerializer implements TypeBinarySerializer<Vector3d> {
    @Inject
    public Vector3dBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_VECTOR3D;
    }

    @Override
    public void serialize(
        Vector3d obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        BinarySerializationUtil.packVector3d(messagePacker, obj);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_VECTOR3D)
  public static class Vector3dBinaryDeserializer implements TypeBinaryDeserializer<Vector3d> {
    @Inject
    public Vector3dBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return Vector3d.class;
    }

    @Override
    public Vector3d deserialize(Value value, BinaryDeserializer deserializer) {
      return BinarySerializationUtil.unpackVector3d(value);
    }
  }
}
