package hana04.gfxbase.serialize.gfxtype;

import com.google.common.collect.ImmutableList;
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
import hana04.gfxbase.gfxtype.Aabb3d;
import hana04.gfxbase.gfxtype.BinarySerializationUtil;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.Value;

import javax.inject.Inject;
import java.util.Map;

public abstract class Aabb3dSerialization {
  public static final String TYPE_NAME = "Aabb3d";

  @HanaDeclareReadableSerializerByClass(Aabb3d.class)
  public static class Aabb3dReadableSerializer implements TypeReadableSerializer<Aabb3d> {
    @Inject
    public Aabb3dReadableSerializer() {
      // NO-OP
    }

    @Override
    public Map serialize(Aabb3d obj, ReadableSerializer serializer) {
      return Maps.newHashMap(
          ImmutableMap.of(
              "type",
              TYPE_NAME,
              "value",
              Lists.newArrayList(ImmutableList.of(
                  obj.pMin.x,
                  obj.pMin.y,
                  obj.pMin.z,
                  obj.pMax.x,
                  obj.pMax.y,
                  obj.pMax.z))));
    }
  }

  @HanaDeclareReadableDeserializer(TYPE_NAME)
  public static class Aabb3dReadableDeserializer implements TypeReadableDeserializer<Aabb3d> {

    @Inject
    public Aabb3dReadableDeserializer() {
      // NO-OP
    }

    @Override
    public Class<Aabb3d> getSerializedClass() {
      return Aabb3d.class;
    }

    @Override
    public Aabb3d deserialize(Map json, ReadableDeserializer deserializer) {
      return AabbParsingUtil.parseAabb3d(json.get("value"));
    }
  }

  @HanaDeclareBinarySerializerByClass(Aabb3d.class)
  public static class Aabb3dBinarySerializer implements TypeBinarySerializer<Aabb3d> {
    @Inject
    public Aabb3dBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_AABB3D;
    }

    @Override
    public void serialize(
        Aabb3d obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        BinarySerializationUtil.packAabb3d(messagePacker, obj);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_AABB3D)
  public static class Aabb3dBinaryDeserializer implements TypeBinaryDeserializer<Aabb3d> {
    @Inject
    public Aabb3dBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return Aabb3d.class;
    }

    @Override
    public Aabb3d deserialize(Value value, BinaryDeserializer deserializer) {
      return BinarySerializationUtil.unpackAabb3d(value);
    }
  }
}
