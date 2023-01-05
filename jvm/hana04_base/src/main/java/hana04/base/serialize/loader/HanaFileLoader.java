package hana04.base.serialize.loader;

import com.google.common.base.Preconditions;
import hana04.base.caching.CacheKey;
import hana04.base.caching.FilePathCacheKeyPart;
import hana04.base.caching.HanaCacheLoader;
import hana04.base.filesystem.FilePath;
import hana04.base.serialize.FileDeserializer;

import javax.inject.Inject;

public class HanaFileLoader implements HanaCacheLoader<Object> {
  public static final String PROTOCOL_NAME = "HanaFile";

  private final FileDeserializer fileDeserializer;

  @Inject
  public HanaFileLoader(FileDeserializer fileDeserializer) {
    this.fileDeserializer = fileDeserializer;
  }

  @Override
  public Object load(CacheKey key) {
    Preconditions.checkArgument(key.protocol.equals(PROTOCOL_NAME));
    Preconditions.checkArgument(key.parts.get(0) instanceof FilePathCacheKeyPart);
    FilePath filePath = ((FilePathCacheKeyPart)key.parts.get(0)).value;
    return fileDeserializer.deserialize(filePath.storedPath);
  }
}
