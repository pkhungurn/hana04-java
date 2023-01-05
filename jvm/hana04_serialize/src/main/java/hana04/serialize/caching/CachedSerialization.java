package hana04.serialize.caching;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import hana04.apt.annotation.HanaDeclareBinaryDeserializer;
import hana04.apt.annotation.HanaDeclareBinarySerializerByClass;
import hana04.apt.annotation.HanaDeclareReadableDeserializer;
import hana04.apt.annotation.HanaDeclareReadableSerializerByClass;
import hana04.base.caching.CacheKey;
import hana04.base.caching.Cached;
import hana04.base.serialize.binary.BinaryDeserializer;
import hana04.base.serialize.binary.BinarySerializer;
import hana04.base.serialize.binary.TypeBinaryDeserializer;
import hana04.base.serialize.binary.TypeBinarySerializer;
import hana04.base.serialize.readable.JsonParsingUtil;
import hana04.base.serialize.readable.TypeReadableDeserializer;
import hana04.base.serialize.readable.TypeReadableSerializer;
import hana04.serialize.TypeIds;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.Value;

import javax.inject.Inject;
import java.util.Map;

public class CachedSerialization {
  public static final String TYPE_NAME = "Cached";

  @HanaDeclareReadableSerializerByClass(Cached.class)
  public static class ReadableSerializer implements TypeReadableSerializer<Cached> {
    @Inject
    public ReadableSerializer() {
      // NO-OP
    }

    @Override
    public Map serialize(Cached obj, hana04.base.serialize.readable.ReadableSerializer serializer) {
      return Maps.newHashMap(ImmutableMap
          .builder()
          .put("type", TYPE_NAME)
          .put("key", serializer.serialize(obj.key))
          .build());
    }
  }

  @HanaDeclareReadableDeserializer(TYPE_NAME)
  public static class ReadableDeserializer implements TypeReadableDeserializer<Cached> {
    @Inject
    public ReadableDeserializer() {
      // NO-OP
    }

    @Override
    public Cached deserialize(Map json, hana04.base.serialize.readable.ReadableDeserializer deserializer) {
      return Cached.withKey(deserializer.deserialize((Map) JsonParsingUtil.getProperty(json, "key")));
    }

    @Override
    public Class<Cached> getSerializedClass() {
      return Cached.class;
    }
  }

  @HanaDeclareBinarySerializerByClass(Cached.class)
  public static class CachedBinarySerializer implements TypeBinarySerializer<Cached> {
    @Inject
    public CachedBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_CACHED;
    }

    @Override
    public void serialize(
        Cached obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        serializer.serialize(obj.key);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_CACHED)
  public static class CachedBinaryDeserializer implements TypeBinaryDeserializer<Cached> {
    @Inject
    public CachedBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return Cached.class;
    }

    @Override
    public Cached deserialize(Value value, BinaryDeserializer deserializer) {
      CacheKey key = deserializer.deserialize(value.asMapValue());
      return Cached.withKey(key);
    }
  }
}
