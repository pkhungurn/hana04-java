package hana04.base.serialize;

import hana04.base.serialize.binary.BinarySerializer;
import hana04.base.serialize.readable.ReadableSerializer;
import org.apache.commons.io.FilenameUtils;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

@Singleton
public class FileSerializer {
  private final BinarySerializer.Factory binarySerializerFactory;
  private final ReadableSerializer.Factory readableSerializerFactory;
  private final FileSystem fileSystem;

  @Inject
  public FileSerializer(
    BinarySerializer.Factory binarySerializerFactory,
    ReadableSerializer.Factory readableSerializerFactory,
    FileSystem fileSystem) {
    this.binarySerializerFactory = binarySerializerFactory;
    this.readableSerializerFactory = readableSerializerFactory;
    this.fileSystem = fileSystem;
  }

  public <T> void serialize(T obj, String fileName) {
    serialize(obj, fileName, /* prettyPrint= */ true);
  }

  public <T> void serialize(T obj, String fileName, boolean prettyPrint) {
    Path filePath = fileSystem.getPath(fileName);
    String extension = FilenameUtils.getExtension(fileName).toLowerCase();
    if (extension.equals(HanaSerializable.MESSAGE_PACK_EXTENSION)) {
      try {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MessagePacker packer = MessagePack.newDefaultPacker(outputStream);
        binarySerializerFactory.create(packer, fileName).serialize(obj);
        packer.flush();
        outputStream.flush();
        byte[] bytes = outputStream.toByteArray();

        if (filePath.getParent() != null) {
          Files.createDirectories(filePath.getParent());
        }
        if (!Files.exists(filePath)) {
          Files.createFile(filePath);
        }
        ByteChannel byteChannel = Files.newByteChannel(
            filePath,
            EnumSet.of(
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE));
        byteChannel.write(ByteBuffer.wrap(bytes));
        byteChannel.close();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else if (extension.equals(HanaSerializable.JSON_EXTENSION)) {
      ReadableSerializer.save(filePath, readableSerializerFactory.create(fileName).serialize(obj), prettyPrint);
    } else {
      throw new IllegalArgumentException("Unsupported extension: " + extension);
    }
  }
}
