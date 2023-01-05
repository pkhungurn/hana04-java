package hana04.base.serialize.binary;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import hana04.base.TypeIds;
import hana04.base.extension.HanaExtensible;
import hana04.base.serialize.HanaLateDeserializable;
import hana04.base.serialize.HanaSerializable;
import hana04.base.util.UuidUtil;
import org.msgpack.value.ArrayValue;
import org.msgpack.value.MapValue;
import org.msgpack.value.Value;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static hana04.base.serialize.binary.BinarySerializer.TYPE_TAG;
import static hana04.base.serialize.binary.BinarySerializer.UUID_TAG;
import static hana04.base.serialize.binary.BinarySerializer.VALUE_TAG;

public class BinaryDeserializer {
  private final Optional<String> fileName;
  private final TypeIdToBinaryDeserializerMap typeIdToDeserializerMap;
  private final Map<UUID, HanaSerializable> uuidToObj = new HashMap<>();

  public BinaryDeserializer(
    Optional<String> fileName,
    TypeIdToBinaryDeserializerMap typeIdToDeserializerMap) {
    this.fileName = fileName;
    this.typeIdToDeserializerMap = typeIdToDeserializerMap;
  }

  public <T> T deserialize(MapValue value) {
    Map<Integer, Value> map = convertToIntMap(value);
    Preconditions.checkArgument(map.containsKey(TYPE_TAG));
    Preconditions.checkArgument(map.get(TYPE_TAG).isIntegerValue());
    Preconditions.checkArgument(map.containsKey(VALUE_TAG));

    int typeId = map.get(TYPE_TAG).asIntegerValue().asInt();
    if (typeId == TypeIds.TYPE_ID_LOOKUP) {
      UUID uuid = UuidUtil.unpack(map.get(VALUE_TAG));
      return (T) uuidToObj.get(uuid);
    }

    TypeBinaryDeserializer deserializer = typeIdToDeserializerMap.get(typeId);
    Object result = deserializer.deserialize(map.get(VALUE_TAG), this);
    if (result instanceof HanaSerializable) {
      UUID uuid;
      if (map.containsKey(UUID_TAG)) {
        uuid = UuidUtil.unpack(map.get(UUID_TAG));
      } else {
        uuid = UUID.randomUUID();
      }
      uuidToObj.put(uuid, (HanaSerializable) result);
    }

    return (T) result;
  }

  public static Map<Integer, Value> convertToIntMap(MapValue mapValue) {
    ImmutableMap.Builder<Integer, Value> output = ImmutableMap.builder();
    Value[] keyValues = mapValue.getKeyValueArray();
    int n = keyValues.length / 2;
    for (int i = 0; i < n; i++) {
      Value key = keyValues[2 * i];
      Value value = keyValues[2 * i + 1];
      Preconditions.checkArgument(key.isIntegerValue(), "a key is not an integer value");
      output.put(key.asIntegerValue().asInt(), value);
    }
    return output.build();
  }

  public Optional<String> getFileName() {
    return fileName;
  }

  @Singleton
  public static class Factory {
    private final TypeIdToBinaryDeserializerMap typeIdToDeserializerMap;

    @Inject
    public Factory(TypeIdToBinaryDeserializerMap typeIdToDeserializerMap) {
      this.typeIdToDeserializerMap = typeIdToDeserializerMap;
    }

    public BinaryDeserializer create() {
      return new BinaryDeserializer(Optional.empty(), typeIdToDeserializerMap);
    }

    public BinaryDeserializer create(String fileName) {
      return new BinaryDeserializer(Optional.of(fileName), typeIdToDeserializerMap);
    }
  }

  public void deserializeExtensions(ArrayValue arrayValue, HanaExtensible extensible) {
    int extensionCount = arrayValue.size();
    for (int i = 0; i < extensionCount; i++) {
      Value value = arrayValue.get(i);
      Preconditions.checkArgument(value.isMapValue());
      Map<Integer, Value> map = convertToIntMap(value.asMapValue());
      Preconditions.checkArgument(map.containsKey(TYPE_TAG));
      Preconditions.checkArgument(map.get(TYPE_TAG).isIntegerValue());
      Preconditions.checkArgument(map.containsKey(VALUE_TAG));
      Preconditions.checkArgument(map.get(VALUE_TAG).isMapValue());
      TypeBinaryDeserializer deserializer = typeIdToDeserializerMap.get(map.get(TYPE_TAG).asIntegerValue().asInt());
      Object ex = extensible.getExtension(deserializer.getSerializedClass());
      Preconditions.checkArgument(
        ex instanceof HanaLateDeserializable,
        "A serialized extension is not a LateDeserializable");
      HanaLateDeserializable lateDeserializable = (HanaLateDeserializable) ex;
      lateDeserializable.deserialize(map.get(VALUE_TAG).asMapValue(), this);
    }
  }
}

