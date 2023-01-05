package hana04.botan.glasset.texture;

import hana04.base.filesystem.FilePath;
import hana04.botan.cache.GlObjectCache;
import hana04.botan.cache.GlObjectRecord;
import hana04.botan.glasset.provider.GlTexture2DProvider;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.spectrum.rgb.RgbSpace;
import hana04.gfxbase.util.MathUtil;
import hana04.opengl.wrapper.GlConstants;
import hana04.opengl.wrapper.GlTexture2D;
import hana04.opengl.wrapper.GlWrapper;
import hana04.shakuyaku.texture.twodim.image.ImageTextureData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.stream.IntStream;

public class RgbaGlTexture2DProvider implements GlTexture2DProvider {
  private static final Logger logger = LoggerFactory.getLogger(ImageTextureExtension.RgbaGlTexture2DProvider_.class);

  private final FilePath filePath;
  private final ImageTextureData.Vv imageTextureDataVv;
  private final GlObjectCache glObjectCache;
  private final GlWrapper glWrapper;

  protected RgbaGlTexture2DProvider(
      FilePath filePath,
      ImageTextureData.Vv imageTextureDataVv,
      GlObjectCache glObjectCache,
      GlWrapper glWrapper) {
    this.filePath = filePath;
    this.imageTextureDataVv = imageTextureDataVv;
    this.glObjectCache = glObjectCache;
    this.glWrapper = glWrapper;
  }

  @Override
  public void updateGlResource(GlObjectRecord record) {
    imageTextureDataVv.update();
    GlTexture2D texture = null;
    boolean needUpdate = false;
    if (record.resource == null) {
      texture = glWrapper.createTexture2D(GlConstants.GL_RGBA8, true);
      record.resource = texture;
      needUpdate = true;
    } else if (record.version != imageTextureDataVv.version()) {
      texture = (GlTexture2D) record.resource;
      needUpdate = true;
    }
    if (needUpdate) {
      logger.debug("texture file name = " + filePath.storedPath);
      updateTexture2D(texture);
      record.version = imageTextureDataVv.version();;
      record.sizeInBytes = imageTextureDataVv.value().getWidth() * imageTextureDataVv.value().getHeight() * 4;
    }
  }

  private void updateTexture2D(GlTexture2D glTexture2D) {
    glTexture2D.setData(
        imageTextureDataVv.value().getWidth(),
        imageTextureDataVv.value().getHeight(),
        GlConstants.GL_RGBA,
        GlConstants.GL_UNSIGNED_BYTE,
        getBuffer());
  }

  private static byte doubleToByte(double x) {
    x = MathUtil.clamp(x, 0.0, 1.0);
    return (byte) (0xff & (int) Math.round(x * 255.0));
  }

  private Buffer getBuffer() {
    ImageTextureData data = imageTextureDataVv.value();
    int sizeInBytes = data.getWidth() * data.getHeight() * 4;
    ByteBuffer buffer = ByteBuffer.allocateDirect(sizeInBytes).order(ByteOrder.nativeOrder());
    IntStream.range(0, data.getHeight()).parallel().forEach(y -> {
      for (int x = 0; x < data.getWidth(); x++) {
        Rgb rgb = RgbSpace.I.convert(data.getSpectrum(x, y));
        double alpha = data.getAlpha(x, y);
        int pixelIndex = y * data.getWidth() + x;
        buffer.put(4 * pixelIndex + 0, doubleToByte(rgb.x));
        buffer.put(4 * pixelIndex + 1, doubleToByte(rgb.y));
        buffer.put(4 * pixelIndex + 2, doubleToByte(rgb.z));
        buffer.put(4 * pixelIndex + 3, doubleToByte(alpha));
      }
    });
    return buffer;
  }

  @Override
  public GlTexture2D getGlObject() {
    return (GlTexture2D) glObjectCache.getGLResource(this);
  }
}