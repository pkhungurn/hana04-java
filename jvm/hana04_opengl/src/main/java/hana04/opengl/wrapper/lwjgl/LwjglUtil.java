package hana04.opengl.wrapper.lwjgl;

import static org.lwjgl.opengl.GL11.GL_INVALID_ENUM;
import static org.lwjgl.opengl.GL11.GL_INVALID_OPERATION;
import static org.lwjgl.opengl.GL11.GL_INVALID_VALUE;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL30C.GL_INVALID_FRAMEBUFFER_OPERATION;

public class LwjglUtil {
  public static void checkError(String message) {
    int error = glGetError();
    if (error == GL_NO_ERROR) {
      return;
    }
    switch (error) {
      case GL_INVALID_ENUM:
        System.out.println(message + ": GL_INVALID_ENUM");
        break;

      case GL_INVALID_VALUE:
        System.out.println(message + ": GL_INVALID_VALUE");
        break;

      case GL_INVALID_OPERATION:
        System.out.println(message + ": GL_INVALID_OPERATION");
        break;

      case GL_INVALID_FRAMEBUFFER_OPERATION:
        System.out.println(message + ": GL_INVALID_FRAMEBUFFER_OPERATION");
        break;
    }
  }
}
