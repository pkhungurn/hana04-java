package hana04.opengl.wrapper.lwjgl;

import hana04.opengl.wrapper.GlConstants;
import hana04.opengl.wrapper.GlTextureCubeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE_CUBE_MAP_POSITIVE_X;

public class LwjglTextureCubeMap extends LwjglTexture implements GlTextureCubeMap {
  private static Logger logger = LoggerFactory.getLogger(LwjglTextureCubeMap.class);
  private int size = 0;
  private boolean allocated = false;

  public LwjglTextureCubeMap(int internalFormat) {
    super(GL_TEXTURE_CUBE_MAP, internalFormat);
    wrapS = GlConstants.GL_CLAMP_TO_EDGE;
    wrapT = GlConstants.GL_CLAMP_TO_EDGE;
    wrapR = GlConstants.GL_CLAMP_TO_EDGE;
    minFilter = GlConstants.GL_LINEAR;
    magFilter = GlConstants.GL_LINEAR;
  }

  public int getSize() {
    return size;
  }

  public void allocate(int size, int format, int type) {
    this.size = size;
    LwjglTexture oldTexture = LwjglTextureUnit.getActiveTextureUnit().getBoundTexture();
    if (oldTexture != this) {
      bind();
    }
    bind();
    /* Allocate space for all cube map faces. */
    for (int i = 0; i < 6; i++) {
      glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, internalFormat, size, size, 0, format, type,
        (ByteBuffer) null);
    }
    if (oldTexture == null) {
      unbind();
    } else if (oldTexture != this) {
      oldTexture.bind();
    }
    allocated = true;
  }

  public void allocate(int size) {
    allocate(size, GlConstants.GL_RGBA, GlConstants.GL_UNSIGNED_BYTE);
  }

  public void setData(int size, int format, int type, Buffer[] buffers) {
    this.size = size;

    LwjglTexture oldTexture = LwjglTextureUnit.getActiveTextureUnit().getBoundTexture();
    if (oldTexture != this) {
      bind();
    }

    for (int i = 0; i < 6; i++) {
      setData(i, size, format, type, buffers[i]);
    }

    if (oldTexture == null) {
      unbind();
    } else if (oldTexture != this) {
      oldTexture.bind();
    }

    allocated = true;
  }

  private void setData(int side, int size, int format, int type, Buffer buffer) {
        /*
        logger.debug("side = " + side);
        logger.debug("size = " + size);
        logger.debug("format = " + format);
        logger.debug("type = " + type);
        */

    if (buffer != null) {
      buffer.rewind();
      logger.debug("buffer size = " + buffer.capacity());
    }

    if (buffer == null) {
      glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + side, 0, internalFormat, size, size, 0, format, type,
        (ByteBuffer) null);
    } else if (buffer instanceof ByteBuffer) {
      glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + side, 0, internalFormat, size, size, 0, format, type,
        (ByteBuffer) buffer);
    } else if (buffer instanceof FloatBuffer) {
      glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + side, 0, internalFormat, size, size, 0, format, type,
        (FloatBuffer) buffer);
    } else {
      throw new RuntimeException("buffer of type " + buffer.getClass() + " is not supported");
    }
  }
}
