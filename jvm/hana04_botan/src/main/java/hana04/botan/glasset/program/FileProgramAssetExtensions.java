package hana04.botan.glasset.program;

import com.google.common.base.Preconditions;
import hana04.apt.annotation.HanaDeclareCacheLoader;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.caching.CacheKey;
import hana04.base.caching.FilePathCacheKeyPart;
import hana04.base.caching.HanaCacheLoader;
import hana04.base.changeprop.AbstractDerivedSubject;
import hana04.base.util.TextIo;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Optional;

public class FileProgramAssetExtensions {
  public static final String PROTOCOL = "botan.FileProgramAsset";

  public static class Sources extends AbstractDerivedSubject implements ProgramSources {
    private final FileProgramAsset fileProgramData;
    private final TextIo textIo;
    private String vertexShaderSource = "";
    private String fragmentShaderSource = "";

    @HanaDeclareExtension(
      extensibleClass = FileProgramAsset.class,
      extensionClass = ProgramSources.class)
    public Sources(FileProgramAsset fileProgramData, TextIo textIo) {
      this.fileProgramData = fileProgramData;
      this.textIo = textIo;
      fileProgramData.vertexShaderFilePath().addObserver(this);
      fileProgramData.fragmentShaderFilePath().addObserver(this);
      update();
    }

    @Override
    protected long updateInternal() {
      fileProgramData.vertexShaderFilePath().update();
      fileProgramData.fragmentShaderFilePath().update();
      long newVersion = Math.max(
        fileProgramData.vertexShaderFilePath().version(),
        fileProgramData.fragmentShaderFilePath().version());

      String resolvedVertexShaderFileName =
        fileProgramData.vertexShaderFilePath().value().storedPath;
      vertexShaderSource = textIo.readTextFile(resolvedVertexShaderFileName);

      String resolvedFragmentShaderFileName =
        fileProgramData.fragmentShaderFilePath().value().storedPath;
      fragmentShaderSource = textIo.readTextFile(resolvedFragmentShaderFileName);

      return newVersion;
    }

    @Override
    public String getVertexShaderSource() {
      return vertexShaderSource;
    }

    @Override
    public String getFragmentShaderSource() {
      return fragmentShaderSource;
    }

    @Override
    public Optional<String> getVertexShaderFileName() {
      return Optional.of(fileProgramData.vertexShaderFilePath().value().storedPath);
    }

    @Override
    public Optional<String> getFragmentShaderFileName() {
      return Optional.of(fileProgramData.fragmentShaderFilePath().value().storedPath);
    }
  }

  @HanaDeclareCacheLoader({PROTOCOL, "FileProgramAsset"})
  public static
  class FileProgramAssetLoader implements HanaCacheLoader<ProgramAsset> {
    Provider<FileProgramAssetBuilder> assetBuilder;

    @Inject
    public FileProgramAssetLoader(Provider<FileProgramAssetBuilder> assetBuilder) {
      this.assetBuilder = assetBuilder;
    }

    @Override
    public ProgramAsset load(CacheKey key) {
      Preconditions.checkArgument(key.protocol.equals(PROTOCOL));
      Preconditions.checkArgument(key.parts.size() == 2);
      Preconditions.checkArgument(key.parts.get(0) instanceof FilePathCacheKeyPart);
      Preconditions.checkArgument(key.parts.get(1) instanceof FilePathCacheKeyPart);
      return assetBuilder.get()
        .vertexShaderFilePath(((FilePathCacheKeyPart) key.parts.get(0)).value)
        .fragmentShaderFilePath(((FilePathCacheKeyPart) key.parts.get(1)).value)
        .build();
    }
  }
}
