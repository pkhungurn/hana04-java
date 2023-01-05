package hana04.base.serialize.binary;

import hana04.base.TypeIds;
import hana04.base.serialize.HanaSerializable;
import hana04.base.util.TypeUtil;
import hana04.base.util.UuidUtil;
import org.msgpack.core.MessagePacker;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BinarySerializer {

  public static final int TYPE_TAG = -1;
  public static final int UUID_TAG = -2;
  public static final int VALUE_TAG = -3;
  public static final int EXTENSION_TAG = -4;

  private final MessagePacker messagePacker;
  private final Optional<String> fileName;
  private final TypeIdToBinarySerializerMap typeIdToSerializer;
  private final ClassToBinarySerializerMap classToSerializer;
  private final Map<HanaSerializable, UUID> nodeToUuid = new HashMap<>();

  public BinarySerializer(
    MessagePacker messagePacker,
    Optional<String> fileName,
    TypeIdToBinarySerializerMap typeIdToSerializer,
    ClassToBinarySerializerMap classToSerializer) {
    this.messagePacker = messagePacker;
    this.fileName = fileName;
    this.typeIdToSerializer = typeIdToSerializer;
    this.classToSerializer = classToSerializer;
  }

  public void serialize(Object obj) {
    try {
      if (obj instanceof HanaSerializable && nodeToUuid.containsKey(obj)) {
        packLookup((HanaSerializable) obj);
        return;
      }
      if (obj instanceof HanaSerializable) {
        HanaSerializable obj_ = TypeUtil.cast(obj, HanaSerializable.class);
        TypeBinarySerializer serializer = typeIdToSerializer.get(obj_.getSerializedTypeId());
        packSerializable(obj_, serializer);
      } else {
        TypeBinarySerializer serializer = classToSerializer.get(obj.getClass());
        packNonSerializable(obj, serializer);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void packLookup(HanaSerializable obj) throws IOException {
    UUID uuid = nodeToUuid.get(obj);
    messagePacker.packMapHeader(2);
    {
      messagePacker.packInt(TYPE_TAG);
      messagePacker.packInt(TypeIds.TYPE_ID_LOOKUP);
    }
    {
      messagePacker.packInt(VALUE_TAG);
      UuidUtil.pack(messagePacker, uuid);
    }
  }

  private void packNonSerializable(Object obj, TypeBinarySerializer serializer) throws IOException {
    messagePacker.packMapHeader(2);
    {
      messagePacker.packInt(TYPE_TAG);
      messagePacker.packInt(serializer.typeId());
    }
    {
      messagePacker.packInt(VALUE_TAG);
      serializer.serialize(obj, messagePacker, this);
    }
  }

  private void packSerializable(HanaSerializable obj, TypeBinarySerializer serializer) throws IOException {

    messagePacker.packMapHeader(3);
    {
      messagePacker.packInt(TYPE_TAG);
      messagePacker.packInt(obj.getSerializedTypeId());
    }
    {
      messagePacker.packInt(VALUE_TAG);
      serializer.serialize(obj, messagePacker, this);
    }
    {
      UUID uuid = UUID.randomUUID();
      nodeToUuid.put(obj, uuid);
      messagePacker.packInt(UUID_TAG);
      UuidUtil.pack(messagePacker, uuid);
    }
  }

  public Optional<String> getFileName() {
    return fileName;
  }

  @Singleton
  public static class Factory {
    private final TypeIdToBinarySerializerMap typeIdToSerializer;
    private final ClassToBinarySerializerMap classToSerializer;

    @Inject
    public Factory(
      TypeIdToBinarySerializerMap typeIdToSerializer,
      ClassToBinarySerializerMap classToSerializer) {
      this.typeIdToSerializer = typeIdToSerializer;
      this.classToSerializer = classToSerializer;
    }

    public BinarySerializer create(MessagePacker messagePacker) {
      return new BinarySerializer(messagePacker, Optional.empty(), typeIdToSerializer, classToSerializer);
    }

    public BinarySerializer create(MessagePacker messagePacker, String fileName) {
      return new BinarySerializer(messagePacker, Optional.of(fileName), typeIdToSerializer, classToSerializer);
    }
  }
}
