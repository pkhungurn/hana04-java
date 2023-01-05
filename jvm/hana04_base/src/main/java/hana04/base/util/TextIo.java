package hana04.base.util;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Path;

public class TextIo {
  private final FileSystem fileSystem;

  @Inject
  public TextIo(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public static String readTextFile(Path path) {
    return new String(BinaryIo.readByteArrayFromFile(path), StandardCharsets.UTF_8);
  }

  public static void writeTextFile(Path path, String content) {
    BinaryIo.writeByteArrayToFile(path, content.getBytes(StandardCharsets.UTF_8));
  }

  public String readTextFile(String fileName) {
    return readTextFile(fileSystem.getPath(fileName));
  }

  public void writeTextFile(String fileName, String content) {
    writeTextFile(fileSystem.getPath(fileName), content);
  }
}
