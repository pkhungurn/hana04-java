package hana04.shakuyaku.texture.twodim.image;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareCacheLoader;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.apt.annotation.HanaProvideExtension;
import hana04.base.Component;
import hana04.base.caching.CacheKey;
import hana04.base.caching.FilePathCacheKeyPart;
import hana04.base.caching.HanaCacheLoader;
import hana04.base.caching.StringCacheKeyPart;
import hana04.base.changeprop.Constant;
import hana04.base.filesystem.FilePath;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;
import hana04.shakuyaku.texture.twodim.WrapMode;
import org.apache.commons.io.FilenameUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import java.nio.file.FileSystem;

public class ImageTextures {
  public static final ImmutableSet<String> SUPPORTED_EXTENSIONS = ImmutableSet.of(
      "jpg", "jpeg", "png", "bmp", "tga", "sph", "spa");
  public static final String PROTOCOL_NAME = "shakuyaku.ImageTexture";

  public static boolean supports(String fileName) {
    String extension = FilenameUtils.getExtension(fileName).toLowerCase();
    return SUPPORTED_EXTENSIONS.contains(extension);
  }

  @HanaProvideExtension(
      extensibleClass = ImageTexture.class,
      extensionClass = ImageLoadingFailBehaviorData.class)
  public static ImageLoadingFailBehaviorData providesImageLoadingFailBehavior(
      ImageTexture imageTexture,
      Provider<ImageLoadingFailBehaviorDataBuilder> ImageLoadingFailBehaviorData) {
    return ImageLoadingFailBehaviorData.get().build();
  }

  public static class ImageTextureDataVv extends Constant<ImageTextureData> implements ImageTextureData.Vv {
    @HanaDeclareExtension(
        extensibleClass = ImageTexture.class,
        extensionClass = ImageTextureData.Vv.class)
    ImageTextureDataVv(ImageTexture texture, FileSystem fileSystem) {
      super(ImageTextureData.createImageTextureData(
          texture.filePath(),
          fileSystem,
          texture.getExtension(ImageLoadingFailBehaviorData.class).defaultToBlackImageIfLoadingFail().value()));
    }
  }

  @HanaDeclareCacheLoader({PROTOCOL_NAME, "ImageTexture"})
  public static
  class CacheLoader_ implements HanaCacheLoader<ImageTexture> {
    private final Provider<ImageTextureBuilder> imageTextureBuilder;

    @Inject
    CacheLoader_(Provider<ImageTextureBuilder> imageTextureBuilder) {
      this.imageTextureBuilder = imageTextureBuilder;
    }

    @Override
    public ImageTexture load(CacheKey key) {
      Preconditions.checkArgument(key.parts.size() == 1 || key.parts.size() == 3);
      Preconditions.checkArgument(key.parts.get(0) instanceof FilePathCacheKeyPart);
      FilePath filePath = ((FilePathCacheKeyPart) key.parts.get(0)).value;
      if (key.parts.size() == 1) {
        return imageTextureBuilder.get().filePath(filePath).build();
      }
      Preconditions.checkArgument(key.parts.get(1) instanceof StringCacheKeyPart);
      Preconditions.checkArgument(key.parts.get(2) instanceof StringCacheKeyPart);
      WrapMode wrapS = WrapMode.fromString(((StringCacheKeyPart) key.parts.get(1)).value);
      WrapMode wrapT = WrapMode.fromString(((StringCacheKeyPart) key.parts.get(2)).value);
      return imageTextureBuilder.get().filePath(filePath).wrapS(wrapS).wrapT(wrapT).build();
    }
  }

  @HanaDeclareBuilder(ImageTexture.class)
  public static class ImageTextureBuilder extends ImageTexture__Impl__Builder<ImageTextureBuilder> {
    @Inject
    public ImageTextureBuilder(ImageTexture__ImplFactory factory) {
      super(factory);
      wrapS(WrapMode.REPEAT);
      wrapT(WrapMode.REPEAT);
    }

    public static ImageTextureBuilder builder(Component component) {
      return component.uberFactory().create(ImageTextureBuilder.class);
    }

    public static TextureTwoDim create(String fileName, Component component) {
      return builder(component)
        .filePath(FilePath.relative(fileName))
        .build();
    }
  }
}
