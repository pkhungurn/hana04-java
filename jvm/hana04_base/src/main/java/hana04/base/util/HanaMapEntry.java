package hana04.base.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import dagger.Binds;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntKey;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import hana04.base.TypeIds;
import hana04.base.serialize.binary.BinaryDeserializer;
import hana04.base.serialize.binary.BinarySerializer;
import hana04.base.serialize.binary.TypeBinaryDeserializer;
import hana04.base.serialize.binary.TypeBinarySerializer;
import hana04.base.serialize.readable.ReadableDeserializer;
import hana04.base.serialize.readable.ReadableSerializer;
import hana04.base.serialize.readable.TypeReadableDeserializer;
import hana04.base.serialize.readable.TypeReadableSerializer;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.Value;

import javax.inject.Inject;
import java.util.Map;

public class HanaMapEntry {
  public static final String TYPE_NAME = "HanaMapEntry";

  private final Object key;
  private final Object value;

  public HanaMapEntry(Object key, Object value) {
    this.key = key;
    this.value = value;
  }

  public Object getKey() {
    return key;
  }

  public Object getValue() {
    return value;
  }


  public static class HanaMapEntryReadableSerializer implements TypeReadableSerializer<HanaMapEntry> {
    @Inject
    public HanaMapEntryReadableSerializer() {
      // NO-OP
    }

    @Override
    public Map serialize(HanaMapEntry obj, ReadableSerializer serializer) {
      return ImmutableMap.<String, Object>builder()
          .put("type", TYPE_NAME)
          .put("key", serializer.serialize(obj.getKey()))
          .put("value", serializer.serialize(obj.getValue()))
          .build();
    }
  }

  public static class HanaMapEntryReadableDeserializer implements TypeReadableDeserializer<HanaMapEntry> {

    @Inject
    public HanaMapEntryReadableDeserializer() {
      // NO-OP
    }

    @Override
    public Class<HanaMapEntry> getSerializedClass() {
      return HanaMapEntry.class;
    }

    @Override
    public HanaMapEntry deserialize(Map json, ReadableDeserializer deserializer) {
      Preconditions.checkNotNull(json.get("key"));
      Preconditions.checkNotNull(json.get("value"));
      Preconditions.checkArgument(json.get("key") instanceof Map);
      Preconditions.checkArgument(json.get("value") instanceof Map);
      Object key = deserializer.deserialize((Map) json.get("key"));
      Object value = deserializer.deserialize((Map) json.get("value"));
      return new HanaMapEntry(key, value);
    }
  }

  public static class HanaMapEntryBinarySerializer implements TypeBinarySerializer<HanaMapEntry> {
    @Inject
    public HanaMapEntryBinarySerializer() {
      // NO-OP
    }

    @Override
    public int typeId() {
      return TypeIds.TYPE_ID_HANA_MAP_ENTRY;
    }

    @Override
    public void serialize(
        HanaMapEntry obj, MessagePacker messagePacker, BinarySerializer serializer) {
      try {
        messagePacker.packArrayHeader(2);
        serializer.serialize(obj.getKey());
        serializer.serialize(obj.getValue());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static class HanaMapEntryBinaryDeserializer implements TypeBinaryDeserializer<HanaMapEntry> {
    @Inject
    public HanaMapEntryBinaryDeserializer() {
      // NO-OP
    }

    @Override
    public Class<?> getSerializedClass() {
      return HanaMapEntry.class;
    }

    @Override
    public HanaMapEntry deserialize(Value value, BinaryDeserializer deserializer) {
      ArrayValue arrayValue = value.asArrayValue();
      Object parseKey = deserializer.deserialize(arrayValue.get(0).asMapValue());
      Object parsedValue = deserializer.deserialize(arrayValue.get(1).asMapValue());
      return new HanaMapEntry(parseKey, parsedValue);
    }
  }

  @dagger.Module
  public static abstract class Module {
    @Binds
    @IntoMap
    @StringKey(TYPE_NAME)
    public abstract TypeReadableDeserializer<?> provideReadableDeserializer(
        HanaMapEntryReadableDeserializer deserializer);

    @Binds
    @IntoMap
    @ClassKey(HanaMapEntry.class)
    public abstract TypeReadableSerializer<?> provideReadableSerializer(HanaMapEntryReadableSerializer serializer);

    @Binds
    @IntoMap
    @IntKey(TypeIds.TYPE_ID_HANA_MAP_ENTRY)
    public abstract TypeBinaryDeserializer<?> provideBinaryDeserializer(HanaMapEntryBinaryDeserializer deserializer);

    @Binds
    @IntoMap
    @ClassKey(HanaMapEntry.class)
    public abstract TypeBinarySerializer<?> provideBinarySerializer(HanaMapEntryBinarySerializer serializer);
  }
}
