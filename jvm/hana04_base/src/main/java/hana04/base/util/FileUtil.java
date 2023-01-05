package hana04.base.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

public final class FileUtil {
  private FileUtil() {
    // NO-OP
  }

  public static FileTime getLastModifiedTime(Path path) {
    try {
      return Files.getLastModifiedTime(path);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
