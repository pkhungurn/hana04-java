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
import hana04.base.serialize.readable.ReadableDeserializer;
import hana04.base.serialize.readable.ReadableSerializer;
import hana04.base.serialize.readable.TypeReadableDeserializer;
import hana04.base.serialize.readable.TypeReadableSerializer;
import hana04.gfxbase.serialize.TypeIds;
import hana04.gfxbase.gfxtype.BinarySerializationUtil;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.Value;

import javax.inject.Inject;
import javax.vecmath.Point3i;
import java.util.Map;

public class Point3iSerialization {
  public static final String TYPE_NAME = "Point3i";

  @HanaDeclareReadableSerializerByClass(Point3i.class)
  public static class Point3iReadableSerializer implements TypeReadableSerializer<Point3i> {
    @Inject
    public Point3iReadableSerializer() {
      // NO-OP
    }

    @Override
    public Map serialize(Point3i obj, ReadableSerializer serializer) {
      return Maps.newHashMap(ImmutableMap.of("type", TYPE_NAME, "value", Lists.newArrayList(obj.x, obj.y, obj.z)));
    }
  }

  @HanaDeclareReadableDeserializer(TYPE_NAME)
  public static class Point3iReadableDeserializer implements TypeReadableDeserializer<Point3i> {

    @Inject
    public Point3iReadableDeserializer() {
      // NO-OP
    }

    @Override
    public Class<Point3i> getSerializedClass() {
      return Point3i.class;
    }

    @Override
    public Point3i deserialize(Map json, ReadableDeserializer deserializer) {
      return VectorParsingUtil.parsePoint3i(json);
    }
  }

  @HanaDeclareBinarySerializerByClass(Point3i.class)
  public static class Point3iBinarySerializer implements TypeBinarySerializer<Point3i> {
    @Inject
    public Point3iBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_POINT3I;
    }

    @Override
    public void serialize(
        Point3i obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        BinarySerializationUtil.packPoint3i(messagePacker, obj);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_POINT3I)
  public static class Point3iBinaryDeserializer implements TypeBinaryDeserializer<Point3i> {
    @Inject
    public Point3iBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return Point3i.class;
    }

    @Override
    public Point3i deserialize(Value value, BinaryDeserializer deserializer) {
      return BinarySerializationUtil.unpackPoint3i(value);
    }
  }
}
