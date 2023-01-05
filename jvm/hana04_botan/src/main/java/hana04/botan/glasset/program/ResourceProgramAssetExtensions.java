package hana04.botan.glasset.program;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import hana04.apt.annotation.HanaDeclareCacheLoader;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.caching.CacheKey;
import hana04.base.caching.HanaCacheLoader;
import hana04.base.caching.StringCacheKeyPart;
import hana04.base.changeprop.AbstractDerivedSubject;
import hana04.base.changeprop.Variable;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Optional;

public class ResourceProgramAssetExtensions {
  public static final String PROTOCOL = "botan.ResourceProgramAsset";

  public static class Sources extends AbstractDerivedSubject implements ProgramSources {
    private final ResourceProgramAsset resourceProgramAsset;
    private String vertexShaderSource = "";
    private String fragmentShaderSource = "";

    @HanaDeclareExtension(
      extensibleClass = ResourceProgramAsset.class,
      extensionClass = ProgramSources.class)
    public Sources(ResourceProgramAsset resourceProgramAsset) {
      this.resourceProgramAsset = resourceProgramAsset;
      forceUpdate();
    }


    @Override
    protected long updateInternal() {
      Variable<String> vertexShaderResourceName = resourceProgramAsset.vertexShaderResourceName();
      Variable<String> fragmentShaderResourceName = resourceProgramAsset.fragmentShaderResourceName();
      vertexShaderResourceName.update();
      fragmentShaderResourceName.update();
      long newVersion = Math.max(vertexShaderResourceName.version(), fragmentShaderResourceName.version());

      try {
        vertexShaderSource = IOUtils.toString(
          getClass().getResourceAsStream(resourceProgramAsset.vertexShaderResourceName().value()),
          Charsets.UTF_8);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      try {
        fragmentShaderSource = IOUtils.toString(
          getClass().getResourceAsStream(resourceProgramAsset.fragmentShaderResourceName().value()),
          Charsets.UTF_8);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }

      return Math.max(version() + 1, newVersion);
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
      return Optional.of(resourceProgramAsset.vertexShaderResourceName().value());
    }

    @Override
    public Optional<String> getFragmentShaderFileName() {
      return Optional.of(resourceProgramAsset.fragmentShaderResourceName().value());
    }
  }

  @HanaDeclareCacheLoader({PROTOCOL, "ResourceProgramAsset"})
  public static
  class ResourceProgramAssetLoader implements HanaCacheLoader<ProgramAsset> {
    private final Provider<ResourceProgramAssetBuilder> assetBuilder;

    @Inject
    public ResourceProgramAssetLoader(
      Provider<ResourceProgramAssetBuilder> assetBuilder) {
      this.assetBuilder = assetBuilder;
    }

    @Override
    public ProgramAsset load(CacheKey key) {
      Preconditions.checkArgument(key.protocol.equals(PROTOCOL));
      Preconditions.checkArgument(key.parts.size() == 2);
      Preconditions.checkArgument(key.parts.get(0) instanceof StringCacheKeyPart);
      Preconditions.checkArgument(key.parts.get(1) instanceof StringCacheKeyPart);
      return assetBuilder.get()
        .vertexShaderResourceName(((StringCacheKeyPart) key.parts.get(0)).value)
        .fragmentShaderResourceName(((StringCacheKeyPart) key.parts.get(1)).value)
        .build();
    }
  }
}
