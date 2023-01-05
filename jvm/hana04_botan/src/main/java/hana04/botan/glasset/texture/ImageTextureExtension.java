package hana04.botan.glasset.texture;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.botan.cache.GlObjectCache;
import hana04.botan.glasset.provider.GlTexture2DProvider;
import hana04.opengl.wrapper.GlWrapper;
import hana04.shakuyaku.texture.twodim.image.ImageTexture;
import hana04.shakuyaku.texture.twodim.image.ImageTextureData;

public final class ImageTextureExtension {
  private ImageTextureExtension() {
    // NO-OP
  }

  public static class RgbaGlTexture2DProvider_ extends RgbaGlTexture2DProvider {
    @HanaDeclareExtension(
        extensibleClass = ImageTexture.class,
        extensionClass = GlTexture2DProvider.class)
    public RgbaGlTexture2DProvider_(ImageTexture imageTexture, GlWrapper glWrapper, GlObjectCache glObjectCache) {
      super(imageTexture.filePath(), imageTexture.getExtension(ImageTextureData.Vv.class), glObjectCache, glWrapper);
    }
  }
}
