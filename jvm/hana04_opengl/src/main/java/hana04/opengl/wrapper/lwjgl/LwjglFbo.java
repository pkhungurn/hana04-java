package hana04.opengl.wrapper.lwjgl;

import hana04.opengl.wrapper.GlFbo;
import hana04.opengl.wrapper.GlTexture;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT10;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT11;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT12;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT13;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT14;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT15;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT2;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT3;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT4;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT5;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT6;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT7;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT8;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT9;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_UNSUPPORTED;
import static org.lwjgl.opengl.GL30.GL_MAX_COLOR_ATTACHMENTS;
import static org.lwjgl.opengl.GL30.GL_NONE;
import static org.lwjgl.opengl.GL30.GL_STENCIL_ATTACHMENT;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glDrawBuffer;
import static org.lwjgl.opengl.GL30.glDrawBuffers;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGetInteger;
import static org.lwjgl.opengl.GL30.glReadBuffer;
import static org.lwjgl.system.MemoryStack.stackMallocInt;
import static org.lwjgl.system.MemoryStack.stackPush;

public class LwjglFbo implements GlFbo {
  private static Logger logger = LoggerFactory.getLogger(LwjglFbo.class);

  /**
   * Static members.
   */
  private static boolean staticInitialized = false;
  private static int numColorAttachements;
  private static LwjglFbo boundFbo = null;

  /**
   * Constants for different color attachments.
   */
  private static int[] COLOR_ATTACHMENTS = {GL_COLOR_ATTACHMENT0,
    GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2,
    GL_COLOR_ATTACHMENT3, GL_COLOR_ATTACHMENT4,
    GL_COLOR_ATTACHMENT5, GL_COLOR_ATTACHMENT6,
    GL_COLOR_ATTACHMENT7, GL_COLOR_ATTACHMENT8,
    GL_COLOR_ATTACHMENT9, GL_COLOR_ATTACHMENT10,
    GL_COLOR_ATTACHMENT11, GL_COLOR_ATTACHMENT12,
    GL_COLOR_ATTACHMENT13, GL_COLOR_ATTACHMENT14,
    GL_COLOR_ATTACHMENT15};

  /**
   * Instance members
   */
  private int id;
  private boolean disposed = false;
  private LwjglTexture[] colorAttachements;
  private int[] colorAttachmentTargets;
  private LwjglTexture depthAttachment;
  private int depthAttachmentTarget;
  private LwjglTexture stencilAttachment;
  private int stencilAttachmentTarget;
  private boolean bound = false;

  public static void staticInitialize() {
    if (!staticInitialized) {
      numColorAttachements = glGetInteger(GL_MAX_COLOR_ATTACHMENTS);
      logger.info("num color attachement = " + numColorAttachements);
      staticInitialized = true;
    }
  }

  public LwjglFbo() {
    staticInitialize();
    this.id = glGenFramebuffers();
    colorAttachements = new LwjglTexture[numColorAttachements];
    colorAttachmentTargets = new int[numColorAttachements];
  }

  public int getId() {
    return id;
  }

  public void bind() {
    if (boundFbo != null) {
      boundFbo.unbind();
    }
    glBindFramebuffer(GL_FRAMEBUFFER, id);
    boundFbo = this;
    bound = true;
  }

  public void unbind() {
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    boundFbo = null;
    bound = false;
  }

  public boolean isBound() {
    return bound;
  }

  public void disposeGl() {
    unbind();
    if (!disposed) {
      glDeleteFramebuffers(id);
      disposed = true;
    }
  }

  @Override
  public GlTexture getColorAttachment(int index) {
    return colorAttachements[index];
  }

  public int getColorAttachmentTarget(int index) {
    return colorAttachmentTargets[index];
  }

  public GlTexture getDepthAttachment() {
    return depthAttachment;
  }

  public int getDepthAttachmentTarget(int index) {
    return depthAttachmentTarget;
  }

  public GlTexture getStencilAttachment() {
    return stencilAttachment;
  }

  public int getStencilAttachmentTarget() {
    return stencilAttachmentTarget;
  }

  public static GlFbo getBoundFbo() {
    return boundFbo;
  }

  public void checkBound() {
    if (!isBound()) {
      throw new RuntimeException("the fbo is not bound");
    }
  }

  public void attachColorBuffer(int index, GlTexture texture) {
    attachColorBuffer(index, texture.getTarget(), texture);
  }

  public void attachColorBuffer(int index, int target, GlTexture texture) {
    if (texture instanceof LwjglTexture) {
      checkBound();
      glFramebufferTexture2D(GL_FRAMEBUFFER, COLOR_ATTACHMENTS[index],
        target, texture.getId(), 0);
      colorAttachements[index] = (LwjglTexture) texture;
      colorAttachmentTargets[index] = target;
    } else {
      throw new RuntimeException("The given texture is not a JoglTexture.");
    }
  }

  public void detachColorBuffer(int index) {
    checkBound();
    if (colorAttachements[index] != null) {
      glFramebufferTexture2D(GL_FRAMEBUFFER,
        COLOR_ATTACHMENTS[index],
        colorAttachmentTargets[index], 0, 0);
      colorAttachements[index] = null;
      colorAttachmentTargets[index] = 0;
    }
  }

  public void detachColorBuffer(int index, int target) {
    checkBound();
    glFramebufferTexture2D(GL_FRAMEBUFFER, COLOR_ATTACHMENTS[index],
      target, 0, 0);
    colorAttachements[index] = null;
    colorAttachmentTargets[index] = 0;
  }

  public void attachDepthBuffer(GlTexture texture) {
    attachDepthBuffer(texture.getTarget(), texture);
  }

  public void attachDepthBuffer(int target, GlTexture texture) {
    if (texture instanceof LwjglTexture) {
      checkBound();
      glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT,
        target, texture.getId(), 0);
      depthAttachment = (LwjglTexture) texture;
      depthAttachmentTarget = target;
    } else {
      throw new RuntimeException("The given texture is not a LwjglTexture.");
    }
  }

  public void detachDepthBuffer() {
    checkBound();
    if (depthAttachment != null) {
      glFramebufferTexture2D(GL_FRAMEBUFFER,
        GL_DEPTH_ATTACHMENT, depthAttachmentTarget, 0, 0);
      depthAttachment = null;
      depthAttachmentTarget = 0;
    }
  }

  public void detachDepthBuffer(int target) {
    checkBound();
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT,
      target, 0, 0);
    depthAttachment = null;
    depthAttachmentTarget = 0;
  }

  public void attachStencilBuffer(GlTexture texture) {
    attachStencilBuffer(texture.getTarget(), texture);
  }

  public void attachStencilBuffer(int target, GlTexture texture) {
    if (texture instanceof LwjglTexture) {
      checkBound();
      glFramebufferTexture2D(GL_FRAMEBUFFER,
        GL_STENCIL_ATTACHMENT, target,
        texture.getId(), 0);
      stencilAttachment = (LwjglTexture) texture;
      stencilAttachmentTarget = target;
    } else {
      throw new RuntimeException("The given texture is not a LwjglTexture.");
    }
  }

  public void detachStencilBuffer() {
    checkBound();
    if (stencilAttachment != null) {
      glFramebufferTexture2D(GL_FRAMEBUFFER,
        GL_STENCIL_ATTACHMENT, stencilAttachmentTarget,
        0, 0);
      stencilAttachment = null;
      stencilAttachmentTarget = 0;
    }
  }

  public void detachStencilBuffer(int target) {
    checkBound();
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT,
      target, 0, 0);
    stencilAttachment = null;
    stencilAttachmentTarget = 0;
  }

  public void drawToNone() {
    checkBound();
    glDrawBuffer(GL_NONE);
  }

  public void readFromNone() {
    checkBound();
    glReadBuffer(GL_NONE);
  }

  public void drawTo(int start, int count) {
    checkBound();
    try (MemoryStack stack = stackPush()) {
      IntBuffer buffer = stackMallocInt(count);
      for (int i = start; i < start + count; i++) {
        buffer.put(i - start, COLOR_ATTACHMENTS[i]);
      }
      glDrawBuffers(buffer);
    }
  }

  public void readFrom(int index) {
    checkBound();
    glReadBuffer(COLOR_ATTACHMENTS[index]);
  }

  public void drawTo(GlTexture t0) {
    checkBound();
    attachColorBuffer(0, t0);
    drawTo(0, 1);
  }

  public void drawTo(GlTexture t0, GlTexture t1) {
    checkBound();
    attachColorBuffer(0, t0);
    attachColorBuffer(1, t1);
    drawTo(0, 2);
  }

  public void drawTo(GlTexture t0, GlTexture t1, GlTexture t2) {
    checkBound();
    attachColorBuffer(0, t0);
    attachColorBuffer(1, t1);
    attachColorBuffer(2, t2);
    drawTo(0, 3);
  }

  public void drawTo(GlTexture t0, GlTexture t1, GlTexture t2, GlTexture t3) {
    checkBound();
    attachColorBuffer(0, t0);
    attachColorBuffer(1, t1);
    attachColorBuffer(2, t2);
    attachColorBuffer(3, t3);
    drawTo(0, 4);
  }

  public void detachAllColorBuffers() {
    checkBound();
    for (int i = 0; i < numColorAttachements; i++) {
      if (colorAttachements[i] != null) {
        detachColorBuffer(i);
      }
    }
  }

  public void detachAll() {
    checkBound();
    detachAllColorBuffers();
    detachDepthBuffer();
  }

  public static void checkStatus() {
    int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
    switch (status) {
      case GL_FRAMEBUFFER_COMPLETE:
        return;
      case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
        throw new RuntimeException(
          "frame buffer incomplete: incomplete attachement");
      case GL_FRAMEBUFFER_UNSUPPORTED:
        throw new RuntimeException("Unsupported frame buffer format");
      case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
        throw new RuntimeException(
          "frame buffer incomplete: missing attachment");
      case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
        throw new RuntimeException(
          "frame buffer incomplete: missing draw buffer");
      case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
        throw new RuntimeException(
          "frame buffer incomplete: missing read buffer");
    }
  }
}
