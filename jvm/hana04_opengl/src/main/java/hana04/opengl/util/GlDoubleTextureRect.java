package hana04.opengl.util;

import hana04.opengl.wrapper.GlConstants;
import hana04.opengl.wrapper.GlObject;
import hana04.opengl.wrapper.GlTextureRect;
import hana04.opengl.wrapper.GlWrapper;

public class GlDoubleTextureRect implements GlObject {
  private GlTextureRect[] buffers;
  private int readIndex = 0;
  private boolean disposed = false;

  public GlDoubleTextureRect(GlWrapper glWrapper) {
    this(glWrapper, GlConstants.GL_RGBA);
  }

  public GlDoubleTextureRect(GlWrapper glWrapper, int internalFormat) {
    buffers = new GlTextureRect[2];
    for (int i = 0; i < 2; i++) {
      buffers[i] = glWrapper.createTextureRect(internalFormat);
    }
  }

  public void allocate(int width, int height, int format, int type) {
    for (int i = 0; i < 2; i++) {
      buffers[i].allocate(width, height, format, type);
    }
  }

  @Override
  public void disposeGl() {
    if (!disposed) {
      buffers[0].disposeGl();
      buffers[1].disposeGl();
      disposed = true;
    }
  }

  public void swap() {
    readIndex = (readIndex + 1) % 2;
  }

  public GlTextureRect getReadBuffer() {
    return buffers[readIndex];
  }

  public GlTextureRect getWriteBuffer() {
    return buffers[(readIndex+1)%2];
  }

  public int getWidth() {
    return buffers[0].getWidth();
  }

  public int getHeight() {
    return buffers[0].getHeight();
  }
}
