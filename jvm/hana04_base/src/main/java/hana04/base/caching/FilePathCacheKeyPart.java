package hana04.base.caching;

import hana04.base.filesystem.FilePath;

public class FilePathCacheKeyPart implements CacheKeyPart {
  public final FilePath value;

  public FilePathCacheKeyPart(FilePath value) {
    this.value = value;
  }

  @Override
  public String getStringPart() {
    return "FilePath:::" + value.storedPath;
  }
}
