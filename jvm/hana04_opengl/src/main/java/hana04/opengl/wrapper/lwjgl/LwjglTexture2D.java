package hana04.opengl.wrapper.lwjgl;

import hana04.opengl.wrapper.GlConstants;
import hana04.opengl.wrapper.GlTexture2D;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class LwjglTexture2D extends LwjglTextureTwoDim implements GlTexture2D {
  public LwjglTexture2D() {
    super(GL_TEXTURE_2D, GlConstants.GL_RGBA8);
  }

  public LwjglTexture2D(int internalFormat) {
    super(GL_TEXTURE_2D, internalFormat);
  }

  public LwjglTexture2D(int internalFormat, boolean hasMipmap) {
    super(GL_TEXTURE_2D, internalFormat, hasMipmap);
  }

  @Override
  public void allocate(int width, int height, int format, int type) {
    super.allocate(width, height, format, type);
  }

  @Override
  public void allocate(int width, int height) {
    super.allocate(width, height);
  }
}
