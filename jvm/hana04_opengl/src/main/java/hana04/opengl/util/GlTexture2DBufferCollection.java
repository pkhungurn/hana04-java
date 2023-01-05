package hana04.opengl.util;

import hana04.opengl.wrapper.GlConstants;
import hana04.opengl.wrapper.GlFbo;
import hana04.opengl.wrapper.GlObject;
import hana04.opengl.wrapper.GlTexture2D;
import hana04.opengl.wrapper.GlWrapper;

public class GlTexture2DBufferCollection implements GlObject {
  private static int INTERNAL_FORMAT = GlConstants.GL_RGBA32F;
  public GlDoubleTexture2D[] colorBuffers;
  public GlTexture2D depthBuffer;
  public boolean hasDepthBuffer;

  public GlTexture2DBufferCollection(GlWrapper gl, int colorBufferCount, boolean hasDepthBuffer) {
    colorBuffers = new GlDoubleTexture2D[colorBufferCount];
    this.hasDepthBuffer = hasDepthBuffer;
    for (int i = 0; i < colorBuffers.length; i++) {
      colorBuffers[i] = new GlDoubleTexture2D(gl, INTERNAL_FORMAT);
    }
    if (hasDepthBuffer)
      depthBuffer = gl.createTexture2D(GlConstants.GL_DEPTH_COMPONENT24, false);
  }

  public void allocate(int width, int height, int format, int type) {
    for (GlDoubleTexture2D colorBuffer : colorBuffers) {
      if (colorBuffer.getWidth() != width || colorBuffer.getHeight() != height) {
        colorBuffer.allocate(width, height, format, type);
      }
    }
    if (hasDepthBuffer) {
      if (depthBuffer.getWidth() != width || depthBuffer.getHeight() != height) {
        depthBuffer.allocate(width, height,
          GlConstants.GL_DEPTH_COMPONENT, GlConstants.GL_UNSIGNED_INT);
      }
    }
  }

  public void allocate(int width, int height) {
    allocate(width, height, GlConstants.GL_RGBA, GlConstants.GL_FLOAT);
  }

  public int getHeight() {
    if (colorBuffers != null && colorBuffers[0] != null)
      return colorBuffers[0].getHeight();
    else
      return 0;
  }

  public int getWidth() {
    if (colorBuffers != null && colorBuffers[0] != null)
      return colorBuffers[0].getWidth();
    else
      return 0;
  }

  public void attachTo(GlFbo fbo) {
    attachTo(fbo, true);
  }

  public void attachTo(GlFbo fbo, boolean useDepth) {
    for (int i = 0; i < colorBuffers.length; i++) {
      fbo.attachColorBuffer(i, colorBuffers[i].getWriteBuffer());
    }
    if (hasDepthBuffer && useDepth) {
      fbo.attachDepthBuffer(depthBuffer);
    }
  }

  public void swap() {
    for (GlDoubleTexture2D colorBuffer : colorBuffers) {
      colorBuffer.swap();
    }
  }

  public void disposeGl() {
    for (GlDoubleTexture2D colorBuffer : colorBuffers) {
      if (colorBuffer != null)
        colorBuffer.disposeGl();
    }
    if (hasDepthBuffer && depthBuffer != null)
      depthBuffer.disposeGl();
  }

}
