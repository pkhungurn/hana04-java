package hana04.opengl.wrapper.lwjgl;

import hana04.opengl.wrapper.GlTextureThreeDim;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL12.glTexImage3D;

public class LwjglTextureThreeDim extends LwjglTexture implements GlTextureThreeDim {
  private static Logger logger = LoggerFactory.getLogger(LwjglTextureThreeDim.class);
  protected int width;
  protected int height;
  protected int depth;
  protected boolean allocated;

  public LwjglTextureThreeDim(int target, int internalFormat) {
    super(target, internalFormat);
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getDepth() {
    return depth;
  }

  public void setData(int width, int height, int depth, int format, int type, Buffer buffer) {
    this.width = width;
    this.height = height;
    this.depth = depth;

    LwjglTexture oldTexture = LwjglTextureUnit.getActiveTextureUnit().getBoundTexture();
    if (oldTexture != this) {
      bind();
    }

    logger.info("width = " + width);
    logger.info("height = " + height);
    logger.info("depth = " + depth);
    logger.info("format = " + format);
    logger.info("type = " + type);

    if (buffer != null) {
      buffer.rewind();
    }

    if (buffer == null) {
      glTexImage3D(target, 0, internalFormat, width, height, depth, 0, format, type, (ByteBuffer) null);
    } else if (buffer instanceof ByteBuffer) {
      glTexImage3D(target, 0, internalFormat, width, height, depth, 0, format, type, (ByteBuffer) buffer);
    } else if (buffer instanceof FloatBuffer) {
      glTexImage3D(target, 0, internalFormat, width, height, depth, 0, format, type, (FloatBuffer) buffer);
    } else {
      throw new RuntimeException("buffer of type " + buffer.getClass() + " is not supported");
    }

    if (oldTexture == null) {
      unbind();
    } else if (oldTexture != this) {
      oldTexture.bind();
    }

    allocated = true;
  }

  protected void allocate(int width, int height, int depth, int format, int type) {
    setData(width, height, depth, format, type, null);
  }

  public boolean isAllocated() {
    return allocated;
  }
}
