package hana04.serialize.caching;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import hana04.apt.annotation.HanaDeclareBinaryDeserializer;
import hana04.apt.annotation.HanaDeclareBinarySerializerByClass;
import hana04.apt.annotation.HanaDeclareReadableDeserializer;
import hana04.apt.annotation.HanaDeclareReadableSerializerByClass;
import hana04.base.caching.CacheKey;
import hana04.base.caching.CacheKeyPart;
import hana04.base.caching.FilePathCacheKeyPart;
import hana04.base.caching.StringCacheKeyPart;
import hana04.base.filesystem.FilePath;
import hana04.base.serialize.binary.BinaryDeserializer;
import hana04.base.serialize.binary.BinarySerializer;
import hana04.base.serialize.binary.TypeBinaryDeserializer;
import hana04.base.serialize.binary.TypeBinarySerializer;
import hana04.base.serialize.readable.JsonParsingUtil;
import hana04.base.serialize.readable.TypeReadableDeserializer;
import hana04.base.serialize.readable.TypeReadableSerializer;
import hana04.base.util.StringSerializationUtil;
import hana04.serialize.TypeIds;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.Value;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CacheKeySerialization {
  public static final String TYPE_NAME = "CacheKey";

  @HanaDeclareReadableSerializerByClass(CacheKey.class)
  public static class ReadableSerializer implements TypeReadableSerializer<CacheKey> {
    @Inject
    public ReadableSerializer() {
    }

    @Override
    public Map serialize(CacheKey obj, hana04.base.serialize.readable.ReadableSerializer serializer) {
      ArrayList partList = new ArrayList();
      for (CacheKeyPart part : obj.parts) {
        if (part instanceof StringCacheKeyPart) {
          partList.add(((StringCacheKeyPart) part).value);
        } else {
          FilePath filePath = ((FilePathCacheKeyPart) part).value;
          partList.add(serializer.serialize(filePath));
        }
      }
      return Maps.newHashMap(ImmutableMap.builder()
          .put("type", TYPE_NAME)
          .put("protocol", obj.protocol)
          .put("parts", partList)
          .build());
    }
  }

  @HanaDeclareReadableDeserializer(TYPE_NAME)
  public static class ReadableDeserializer implements TypeReadableDeserializer<CacheKey> {
    @Inject
    public ReadableDeserializer() {
      // NO-OP
    }

    @Override
    public CacheKey deserialize(Map json, hana04.base.serialize.readable.ReadableDeserializer deserializer) {
      CacheKey.Builder builder = CacheKey.builder();
      builder.protocol((String) JsonParsingUtil.getProperty(json, "protocol"));
      List partList = (List) JsonParsingUtil.getProperty(json, "parts");
      for (Object part : partList) {
        if (part instanceof String) {
          builder.addStringPart((String) part);
        } else if (part instanceof Map) {
          builder.addFilePathPart(deserializer.deserialize((Map) part));
        } else {
          throw new RuntimeException("Invalid serialized CacheKey part value.");
        }
      }
      return builder.build();
    }

    @Override
    public Class<CacheKey> getSerializedClass() {
      return CacheKey.class;
    }
  }

  @HanaDeclareBinarySerializerByClass(CacheKey.class)
  public static class CacheKeyBinarySerializer implements TypeBinarySerializer<CacheKey> {
    @Inject
    public CacheKeyBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_CACHE_KEY;
    }

    @Override
    public void serialize(
        CacheKey obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        messagePacker.packArrayHeader(2);
        StringSerializationUtil.packString(messagePacker, obj.protocol);
        messagePacker.packArrayHeader(obj.parts.size());
        for (int i = 0; i < obj.parts.size(); i++) {
          CacheKeyPart keyPart = obj.parts.get(i);
          if (keyPart instanceof StringCacheKeyPart) {
            String s = ((StringCacheKeyPart) keyPart).value;
            serializer.serialize(s);
          } else {
            FilePath filePath = ((FilePathCacheKeyPart) keyPart).value;
            serializer.serialize(filePath);
          }
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  @HanaDeclareBinaryDeserializer(TypeIds.TYPE_ID_CACHE_KEY)
  public static class CacheKeyBinaryDeserializer implements TypeBinaryDeserializer<CacheKey> {
    @Inject
    public CacheKeyBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return CacheKey.class;
    }

    @Override
    public CacheKey deserialize(Value value, BinaryDeserializer deserializer) {
      ArrayValue arrayValue = value.asArrayValue();
      String protocol = arrayValue.get(0).asStringValue().asString();
      CacheKey.Builder builder = CacheKey.builder().
          protocol(protocol);
      ArrayValue parts = arrayValue.get(1).asArrayValue();
      for (int i = 0; i < parts.size(); i++) {
        Object part = deserializer.deserialize(parts.get(i).asMapValue());
        if (part instanceof String) {
          builder.addStringPart((String) part);
        } else {
          builder.addFilePathPart((FilePath) part);
        }
      }
      return builder.build();
    }
  }
}
