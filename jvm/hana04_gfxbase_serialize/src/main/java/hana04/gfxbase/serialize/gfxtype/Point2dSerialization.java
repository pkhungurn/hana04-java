package hana04.gfxbase.serialize.gfxtype;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import hana04.apt.annotation.HanaDeclareBinarySerializerByClass;
import hana04.apt.annotation.HanaDeclareReadableDeserializer;
import hana04.apt.annotation.HanaDeclareReadableSerializerByClass;
import hana04.base.serialize.binary.BinarySerializer;
import hana04.base.serialize.binary.TypeBinarySerializer;
import hana04.base.serialize.readable.ReadableDeserializer;
import hana04.base.serialize.readable.ReadableSerializer;
import hana04.base.serialize.readable.TypeReadableDeserializer;
import hana04.base.serialize.readable.TypeReadableSerializer;
import hana04.gfxbase.serialize.TypeIds;
import hana04.gfxbase.gfxtype.BinarySerializationUtil;
import org.msgpack.core.MessagePacker;

import javax.inject.Inject;
import javax.vecmath.Point2d;
import java.util.Map;

public abstract class Point2dSerialization {
  public static final String TYPE_NAME = "Point2d";

  @HanaDeclareReadableSerializerByClass(Point2d.class)
  public static class Point2dReadableSerializer implements TypeReadableSerializer<Point2d> {
    @Inject
    public Point2dReadableSerializer() {
      // NO-OP
    }

    @Override
    public Map serialize(Point2d obj, ReadableSerializer serializer) {
      return Maps.newHashMap(ImmutableMap.of(
          "type", TYPE_NAME, "value", Lists.newArrayList(obj.x, obj.y)));
    }
  }

  @HanaDeclareReadableDeserializer(TYPE_NAME)
  public static class Point2dReadableDeserializer implements TypeReadableDeserializer<Point2d> {

    @Inject
    public Point2dReadableDeserializer() {
      // NO-OP
    }

    @Override
    public Class<Point2d> getSerializedClass() {
      return Point2d.class;
    }

    @Override
    public Point2d deserialize(Map json, ReadableDeserializer deserializer) {
      return new Point2d(VectorParsingUtil.parseVector2d(json.get("value")));
    }
  }

  @HanaDeclareBinarySerializerByClass(Point2d.class)
  public static class Point2dBinarySerializer implements TypeBinarySerializer<Point2d> {
    @Inject
    public Point2dBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_POINT2D;
    }

    @Override
    public void serialize(
        Point2d obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        BinarySerializationUtil.packPoint2d(messagePacker, obj);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}
