package hana04.base.serialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import hana04.base.Component;
import hana04.base.extension.HanaExtensible;
import hana04.base.serialize.binary.BinarySerializer;
import hana04.base.serialize.binary.TypeBinarySerializer;
import hana04.base.serialize.readable.ReadableDeserializer;
import hana04.base.serialize.readable.ReadableSerializer;
import hana04.base.serialize.readable.TypeReadableSerializer;
import hana04.base.util.TextIo;
import org.msgpack.core.MessagePacker;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface HanaSerializable {
  String MESSAGE_PACK_EXTENSION = "msgpck";
  String JSON_EXTENSION = "json";

  String getSerializedTypeName();

  int getSerializedTypeId();

  List getReadableChildrenList(ReadableSerializer serializer);

  void binarySerializeContent(MessagePacker messagePacker, BinarySerializer binarySerializer);

  class ReadableSerializer_<T extends HanaSerializable> implements TypeReadableSerializer<T> {
    @Override
    public Map serialize(T obj, ReadableSerializer serializer) {
      Map result = new HashMap();
      result.put("type", obj.getSerializedTypeName());
      result.put("children", obj.getReadableChildrenList(serializer));
      if (!(obj instanceof HanaExtensible)) {
        return result;
      }
      result.put("extensions", ExtensionSerialization.serializeExtensions((HanaExtensible) obj, serializer));
      return result;
    }
  }

  class BinarySerializer_<T extends HanaSerializable> implements TypeBinarySerializer<T> {
    private final int typeId;

    public BinarySerializer_(int typeId) {
      this.typeId = typeId;
    }

    @Override
    public void serialize(T obj, MessagePacker messagePacker, BinarySerializer serializer) {
      obj.binarySerializeContent(messagePacker, serializer);
    }

    @Override
    public int typeId() {
      return typeId;
    }
  }

  static Object readableDeserialize(
    String fileName,
    FileSystem fileSystem,
    ReadableDeserializer.Factory readableDeserializerFactory) {
    String resolvedFileName = fileName;
    Path path = fileSystem.getPath(resolvedFileName);
    String content = TextIo.readTextFile(path);
    ObjectMapper mapper = new ObjectMapper();
    try {
      Map map = mapper.readValue(content, Map.class);
      return readableDeserializerFactory.create(resolvedFileName).deserialize(map);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  static void readableSerialize(HanaSerializable serializable, String fileName, Component component) {
    FileSystem fileSystem = component.fileSystem();
    ReadableSerializer serializer = component.readableSerializerFactory().create(fileName);
    Map serialized = serializer.serialize(serializable);
    ReadableSerializer.save(fileSystem.getPath(fileName), serialized);
  }

  static Object readableDeserialize(String fileName, Component component) {
    return readableDeserialize(
      fileName,
      component.fileSystem(),
      component.readableDeserializerFactory());
  }
}
