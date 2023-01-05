package hana04.base.util;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileIo {
  private final FileSystem fileSystem;

  @Inject
  public FileIo(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public void makeDirsForFile(String fileName) {
    try {
      Files.createDirectories(fileSystem.getPath(fileName).getParent());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static InputStream loadFileAndExposeAsInputStream(Path path) throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(Math.toIntExact(Files.size(path)));
    ByteChannel channel = Files.newByteChannel(path, StandardOpenOption.READ);
    channel.read(buffer);
    channel.close();
    buffer.rewind();
    return new ByteArrayInputStream(buffer.array());
  }
}
