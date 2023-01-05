package hana04.opengl.wrapper.lwjgl;

import hana04.opengl.wrapper.GlConstants;
import hana04.opengl.wrapper.GlTextureRect;

import static org.lwjgl.opengl.GL31.GL_TEXTURE_RECTANGLE;

public class LwjglTextureRect extends LwjglTextureTwoDim implements GlTextureRect {
  public LwjglTextureRect() {
    this(GlConstants.GL_RGBA8);
  }

  public LwjglTextureRect(int internalFormat) {
    super(GL_TEXTURE_RECTANGLE, internalFormat, false);
    wrapS = GlConstants.GL_CLAMP_TO_EDGE;
    wrapT = GlConstants.GL_CLAMP_TO_EDGE;
    wrapR = GlConstants.GL_CLAMP_TO_EDGE;
    magFilter = GlConstants.GL_NEAREST;
    minFilter = GlConstants.GL_NEAREST;
  }
}
