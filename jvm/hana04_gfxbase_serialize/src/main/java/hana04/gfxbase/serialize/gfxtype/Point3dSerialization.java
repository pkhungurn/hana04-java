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
import javax.vecmath.Point3d;
import java.util.Map;

public abstract class Point3dSerialization {
  public static final String TYPE_NAME = "Point3d";

  @HanaDeclareReadableDeserializer(TYPE_NAME)
  public static class ReadableDeserializer implements TypeReadableDeserializer<Point3d> {
    @Inject
    public ReadableDeserializer() {
    }

    public Point3d deserialize(
        Map json,
        hana04.base.serialize.readable.ReadableDeserializer deserializer) {
      return VectorParsingUtil.parsePoint3d(json.get("value"));
    }

    @Override
    public Class<Point3d> getSerializedClass() {
      return Point3d.class;
    }
  }

  @HanaDeclareReadableSerializerByClass(Point3d.class)
  public static class ReadableSerializer implements TypeReadableSerializer<Point3d> {
    @Inject
    public ReadableSerializer() {
    }

    @Override
    public Map serialize(Point3d obj, hana04.base.serialize.readable.ReadableSerializer serializer) {
      return Maps.newHashMap(ImmutableMap.of("type", TYPE_NAME, "value", Lists.newArrayList(obj.x, obj.y, obj.z)));
    }
  }

  @HanaDeclareBinarySerializerByClass(Point3d.class)
  public static class Point3dBinarySerializer implements TypeBinarySerializer<Point3d> {
    @Inject
    public Point3dBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_POINT3D;
    }

    @Override
    public void serialize(
        Point3d obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        BinarySerializationUtil.packPoint3d(messagePacker, obj);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_POINT3D)
  public static class Point3dBinaryDeserializer implements TypeBinaryDeserializer<Point3d> {
    @Inject
    public Point3dBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return Point3d.class;
    }

    @Override
    public Point3d deserialize(Value value, BinaryDeserializer deserializer) {
      return BinarySerializationUtil.unpackPoint3d(value);
    }
  }
}
