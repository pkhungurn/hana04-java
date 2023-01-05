package hana04.shakuyaku.sbtm.extensible.animation;

import com.google.common.base.Preconditions;
import hana04.apt.annotation.HanaDeclareCacheLoader;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.caching.CacheKey;
import hana04.base.caching.FilePathCacheKeyPart;
import hana04.base.caching.HanaCacheLoader;
import hana04.base.changeprop.Variable;
import hana04.base.filesystem.FilePath;

import javax.inject.Inject;
import javax.inject.Provider;
import java.nio.file.FileSystem;

public class FileSbtmAnimationAssetExtensions {
  public static final String PROTOCOL_NAME = "shakuyaku.FileSbtmAnimationAsset";

  public static class SbtmAnimationVv_ extends FileSbtmAnimationVv {
    @HanaDeclareExtension(
      extensibleClass = FileSbtmAnimationAsset.class,
      extensionClass = SbtmAnimationVv.class
    )
    public SbtmAnimationVv_(FileSbtmAnimationAsset asset, FileSystem fileSystem) {
      super(new Variable<>(asset.filePath()), fileSystem);
    }
  }

  @HanaDeclareCacheLoader({PROTOCOL_NAME, "FileSbtmAnimationAsset"})
  public static
  class CacheLoader_ implements HanaCacheLoader<FileSbtmAnimationAsset> {
    private final Provider<FileSbtmAnimationAssetBuilder> builderProvider;

    @Inject
    public CacheLoader_(Provider<FileSbtmAnimationAssetBuilder> builderProvider) {
      this.builderProvider = builderProvider;
    }

    @Override
    public FileSbtmAnimationAsset load(CacheKey key) {
      Preconditions.checkArgument(key.protocol.equals(PROTOCOL_NAME));
      Preconditions.checkArgument(key.parts.size() == 1);
      Preconditions.checkArgument(key.parts.get(0) instanceof FilePathCacheKeyPart);
      FilePath filePath = ((FilePathCacheKeyPart) key.parts.get(0)).value;
      return builderProvider.get().filePath(filePath).build();
    }
  }
}
