package hana04.base.filesystem;

import com.google.common.base.Preconditions;
import hana04.base.util.PathUtil;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

public class FilePath {
  public final boolean saveAsRelative;
  public final String storedPath;

  public FilePath(boolean saveAsRelative, String storedPath) {
    Preconditions.checkNotNull(storedPath);
    Preconditions.checkState(!storedPath.isEmpty());
    this.saveAsRelative = saveAsRelative;
    this.storedPath = storedPath;
  }

  public static FilePath absolute(String path) {
    return new FilePath(false, path);
  }

  public static FilePath relative(String path) {
    return new FilePath(true, path);
  }

  public String getSerializedPath(Optional<String> fileName) {
    if (saveAsRelative) {
      return fileName.map(fName ->
        PathUtil.relativizeSecondToFirstDir(fileName.get(), storedPath)
      ).orElse(storedPath);
    } else {
      return storedPath;
    }
  }

  public static String computePathToStore(String rawPath, Optional<String> fileName) {
    return fileName.map(fName -> {
      String dir = Paths.get(fName).getParent().toString();
      return FilenameUtils.separatorsToUnix(FilenameUtils.normalize(dir + File.separator + rawPath));
    }).orElse(rawPath);
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof FilePath)) {
      return false;
    }
    FilePath otherFilePath = (FilePath) other;
    return Objects.equals(storedPath, otherFilePath.storedPath) &&
      Objects.equals(saveAsRelative, otherFilePath.saveAsRelative);
  }

  @Override
  public int hashCode() {
    return Objects.hash(saveAsRelative, storedPath);
  }
}