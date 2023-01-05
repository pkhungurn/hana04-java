package hana04.base.serialize;

import hana04.base.serialize.binary.BinaryDeserializer;
import hana04.base.serialize.readable.ReadableDeserializer;
import hana04.base.util.JsonIo;
import org.apache.commons.io.FilenameUtils;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.Value;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.Map;

@Singleton
public class FileDeserializer {
  private final BinaryDeserializer.Factory binaryDeserializerFactory;
  private final ReadableDeserializer.Factory readableDeserializerFactory;
  private JsonIo jsonIo;
  private final FileSystem fileSystem;

  @Inject
  public FileDeserializer(
      BinaryDeserializer.Factory binaryDeserializerFactory,
      ReadableDeserializer.Factory readableDeserializerFactory,
      JsonIo jsonIo,
      FileSystem fileSystem) {
    this.binaryDeserializerFactory = binaryDeserializerFactory;
    this.readableDeserializerFactory = readableDeserializerFactory;
    this.jsonIo = jsonIo;
    this.fileSystem = fileSystem;
  }

  public <T> T deserialize(String fileName) {
    String extension = FilenameUtils.getExtension(fileName).toLowerCase();
    if (extension.equals(HanaSerializable.MESSAGE_PACK_EXTENSION)) {
      return deserializeBinary(fileName);
    } else if (extension.equals(HanaSerializable.JSON_EXTENSION)) {
      return deserializeReadable(fileName);
    } else {
      throw new IllegalArgumentException("Unsupported extension: " + extension);
    }
  }

  public <T> T deserializeReadable(String fileName) {
    Map map = (Map) jsonIo.load(fileName);
    return readableDeserializerFactory.create(fileName).deserialize(map);
  }

  public <T> T deserializeBinary(String fileName) {
    try {
      InputStream inputStream = Files.newInputStream(fileSystem.getPath(fileName));
      MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(inputStream);
      Value value = unpacker.unpackValue();
      return binaryDeserializerFactory.create(fileName).deserialize(value.asMapValue());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
