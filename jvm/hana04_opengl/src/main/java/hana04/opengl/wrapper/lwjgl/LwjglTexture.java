package hana04.opengl.wrapper.lwjgl;

import hana04.opengl.wrapper.GlConstants;
import hana04.opengl.wrapper.GlTexture;
import hana04.opengl.wrapper.GlTextureUnit;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;

public class LwjglTexture implements GlTexture {
  protected int minFilter;
  protected int magFilter;
  protected int wrapS;
  protected int wrapT;
  protected int wrapR;
  protected boolean disposed;
  protected int id;
  protected int target;
  protected LwjglTextureUnit boundTextureUnit;
  protected int internalFormat;

  public LwjglTexture(int target, int internalFormat) {
    this.id = glGenTextures();
    this.target = target;
    this.boundTextureUnit = null;
    this.internalFormat = internalFormat;
    this.disposed = false;

    minFilter = GlConstants.GL_NEAREST;
    magFilter = GlConstants.GL_NEAREST;
    wrapS = GlConstants.GL_REPEAT;
    wrapT = GlConstants.GL_REPEAT;
    wrapR = GlConstants.GL_REPEAT;
  }

  public boolean isDisposed() {
    return disposed;
  }

  public boolean isBound() {
    return boundTextureUnit != null;
  }

  public int getId() {
    return id;
  }

  public int getTarget() {
    return target;
  }

  @Override
  public void bindTo(GlTextureUnit textureUnit) {
    if (textureUnit instanceof LwjglTextureUnit) {
      bindTo((LwjglTextureUnit)textureUnit);
    } else {
      throw new RuntimeException("The given textureUnit is not a JoglTextureUnit");
    }
  }

  public int getInternalFormat() {
    return internalFormat;
  }

  public void bind() {
    bindTo(LwjglTextureUnit.getActiveTextureUnit());
  }

  private void bindTo(LwjglTextureUnit textureUnit) {
    if (isDisposed()) {
      throw new RuntimeException("program tries to bind a disposed texture");
    }

    textureUnit.bindTexture(this);
    boundTextureUnit = textureUnit;
  }

  public void unbind() {
    if (isBound()) {
      if (isDisposed()) {
        throw new RuntimeException("program tries to unbind a disposed texture");
      }

      boundTextureUnit.unbindTexture(this);
      boundTextureUnit = null;
    }
  }

  protected void setTextureParameters() {
    glTexParameteri(target, GL_TEXTURE_MAG_FILTER, magFilter);
    glTexParameteri(target, GL_TEXTURE_MIN_FILTER, minFilter);
    glTexParameteri(target, GL_TEXTURE_WRAP_S, wrapS);
    glTexParameteri(target, GL_TEXTURE_WRAP_T, wrapT);
    glTexParameteri(target, GL_TEXTURE_WRAP_R, wrapR);
  }

  public void useWith(GlTextureUnit unit) {
    enable();
    unit.activate();
    bindTo(unit);
    setTextureParameters();
  }

  public void use() {
    enable();
    bind();
    setTextureParameters();
  }

  public void unuse() {
    unbind();
    disable();
  }

  @Override
  public int getMagFilter() {
    return magFilter;
  }

  @Override
  public void setMagFilter(int value) {
    this.magFilter = value;
  }

  @Override
  public int getMinFilter() {
    return minFilter;
  }

  @Override
  public void setMinFilter(int value) {
    this.minFilter = value;
  }

  @Override
  public int getWrapS() {
    return wrapS;
  }

  @Override
  public void setWrapS(int value) {
    this.wrapS = value;
  }

  @Override
  public int getWrapT() {
    return wrapT;
  }

  @Override
  public void setWrapT(int value) {
    this.wrapT = value;
  }

  @Override
  public int getWrapR() {
    return wrapR;
  }

  @Override
  public void setWrapR(int value) {
    this.wrapR = value;
  }

  @Override
  public boolean isAllocated() {
    return false;
  }

  public void disposeGl() {
    if (!disposed) {
      if (isBound()) {
        unbind();
      }
      glDeleteTextures(id);
      disposed = true;
    }
  }

  public void enable() {
    //glEnable(target);
  }

  public void disable() {
    //glDisable(target);
  }
}
