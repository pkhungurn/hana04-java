package hana04.botan.util;

import org.inferred.freebuilder.FreeBuilder;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.GLFW_BLUE_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_DEPTH_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_GREEN_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RED_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.GLFW_STENCIL_BITS;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.NULL;

@FreeBuilder
public interface GlfwAppInitializer {
  int initialWindowWidth();

  int initialWindowHeight();

  String initialTitle();

  int glfwContextVersionMajor();

  int glfwContextVersionMinor();

  int glfwOpenglForwardCompat();

  int glfwOpenglProfile();

  int glfwSamples();

  int glfwRedBits();

  int glfwGreenBits();

  int glfwBlueBits();

  int glfwDepthBits();

  int glfwStencilBits();

  int glfwResizeable();

  static Builder builder() {
    return new Builder();
  }

  static long init(String initialTitle) {
    return builder().initialTitle(initialTitle).init();
  }

  class Builder extends GlfwAppInitializer_Builder {
    public Builder() {
      initialWindowWidth(512);
      initialWindowHeight(512);

      glfwContextVersionMajor(3);
      glfwContextVersionMinor(2);
      glfwOpenglForwardCompat(GL_TRUE);
      glfwOpenglProfile(GLFW_OPENGL_CORE_PROFILE);
      glfwSamples(1);
      glfwRedBits(32);
      glfwGreenBits(32);
      glfwBlueBits(32);
      glfwDepthBits(24);
      glfwStencilBits(8);
      glfwResizeable(GL_TRUE);
    }

    public long init() {
      GlfwAppInitializer initializer = build();

      UiUtil.initSwingLookAndField();
      GLFWErrorCallback.createPrint(System.err).set();
      if (!glfwInit()) {
        System.err.println("Could not initialize GLFW");
      }
      glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, initializer.glfwContextVersionMajor());
      glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, initializer.glfwContextVersionMinor());
      glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, initializer.glfwOpenglForwardCompat());
      glfwWindowHint(GLFW_OPENGL_PROFILE, initializer.glfwOpenglProfile());

      glfwWindowHint(GLFW_SAMPLES, initializer.glfwSamples());
      glfwWindowHint(GLFW_RED_BITS, initializer.glfwRedBits());
      glfwWindowHint(GLFW_GREEN_BITS, initializer.glfwGreenBits());
      glfwWindowHint(GLFW_BLUE_BITS, initializer.glfwBlueBits());
      glfwWindowHint(GLFW_DEPTH_BITS, initializer.glfwDepthBits());
      glfwWindowHint(GLFW_STENCIL_BITS, initializer.glfwStencilBits());

      glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

      long glWindowId = glfwCreateWindow(
        /* weight= */ initializer.initialWindowWidth(),
        /* height= */ initializer.initialWindowHeight(),
        /* title= */ initializer.initialTitle(),
        /* monitor= */ NULL,
        /* share= */ NULL);

      if (glWindowId == NULL) {
        throw new RuntimeException("Could not create GLFW window");
      }

      glfwMakeContextCurrent(glWindowId);
      GL.createCapabilities();
      glfwSwapInterval(1);
      return glWindowId;
    }
  }
}
