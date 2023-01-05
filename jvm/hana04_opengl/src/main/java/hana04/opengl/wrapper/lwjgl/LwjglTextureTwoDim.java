package hana04.opengl.wrapper.lwjgl;

import com.google.common.base.Preconditions;
import hana04.opengl.wrapper.GlConstants;
import hana04.opengl.wrapper.GlTextureTwoDim;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.glGetTexImage;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_BASE_LEVEL;
import static org.lwjgl.opengl.GL12C.GL_TEXTURE_MAX_LEVEL;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class LwjglTextureTwoDim extends LwjglTexture implements GlTextureTwoDim {
  private static Logger logger = LoggerFactory.getLogger(LwjglTextureTwoDim.class);
  protected int width;
  protected int height;
  protected boolean allocated;
  protected boolean textureHasMipmap;

  public LwjglTextureTwoDim(int target, int internalFormat) {
    this(target, internalFormat, true);
  }

  public LwjglTextureTwoDim(int target, int internalFormat, boolean hasMipmap) {
    super(target, internalFormat);
    allocated = false;
    if (hasMipmap) {
      minFilter = GlConstants.GL_LINEAR_MIPMAP_LINEAR;
      magFilter = GlConstants.GL_LINEAR;
    } else {
      minFilter = GlConstants.GL_NEAREST;
      magFilter = GlConstants.GL_NEAREST;
    }
    this.textureHasMipmap = hasMipmap;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public void setData(int width, int height, int format, int type, Buffer buffer) {
    this.width = width;
    this.height = height;

    LwjglTexture oldTexture = LwjglTextureUnit.getActiveTextureUnit().getBoundTexture();
    if (oldTexture != this) {
      bind();
    }
    glTexParameteri(target, GL_TEXTURE_BASE_LEVEL, 0);
    glTexParameteri(target, GL_TEXTURE_MAX_LEVEL, 0);

    /*
    logger.debug("width = " + width);
    logger.debug("height = " + height);
    logger.debug("format = " + format);
    logger.debug("type = " + type);
    */

    if (buffer != null) {
      buffer.rewind();
      //logger.debug("buffer size = " + buffer.capacity());
    }

    if (buffer == null) {
      glTexImage2D(target, 0, internalFormat, width, height, 0, format, type, (ByteBuffer) null);
    } else if (buffer instanceof ByteBuffer) {
      glTexImage2D(target, 0, internalFormat, width, height, 0, format, type, (ByteBuffer) buffer);
    } else if (buffer instanceof FloatBuffer) {
      glTexImage2D(target, 0, internalFormat, width, height, 0, format, type, (FloatBuffer) buffer);
    } else {
      throw new RuntimeException("buffer of instance " + buffer.getClass() + " is not supported");
    }

    if (textureHasMipmap) {
      glGenerateMipmap(target);
    }

    if (oldTexture == null) {
      unbind();
    } else if (oldTexture != this) {
      oldTexture.bind();
    }

    allocated = true;
  }

  public void getData(int format, int type, Buffer buffer) {
    LwjglTexture oldTexture = LwjglTextureUnit.getActiveTextureUnit().getBoundTexture();
    if (oldTexture != this) {
      bind();
    }

    Preconditions.checkNotNull(buffer);

    if (buffer instanceof ByteBuffer) {
      glGetTexImage(target, 0, format, type, (ByteBuffer)buffer);
    } else if (buffer instanceof FloatBuffer) {
      glGetTexImage(target, 0, format, type, (FloatBuffer)buffer);
    } else {
      throw new RuntimeException("buffer of instance " + buffer.getClass() + " is not supported");
    }

    if (oldTexture == null) {
      unbind();
    } else if (oldTexture != this) {
      oldTexture.bind();
    }
  }

  public boolean isAllocated() {
    return allocated;
  }

  public boolean hasMipmap() {
    return textureHasMipmap;
  }
}
