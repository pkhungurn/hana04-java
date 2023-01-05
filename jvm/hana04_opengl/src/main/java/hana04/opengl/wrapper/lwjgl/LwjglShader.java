package hana04.opengl.wrapper.lwjgl;

import hana04.opengl.wrapper.GlObject;

import java.util.Optional;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_TRUE;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20C.glShaderSource;

public class LwjglShader implements GlObject {
  private final int type; // GL_FRAGMENT_SHADER or GL_VERTEX_SHADER
  private int id;
  private boolean disposed;

  /**
   * Create a shader from a source code text.
   *
   * @param shaderType the type of the shader
   * @param src        the source code
   * @param fileName   the name of the file that contains the source code.
   */
  public LwjglShader(int shaderType, String src, Optional<String> fileName) {
    this.type = shaderType;
    this.id = glCreateShader(this.type);
    glShaderSource(this.id, src);
    glCompileShader(this.id);
    boolean compileSuccessful = glGetShaderi(this.id, GL_COMPILE_STATUS) == GL_TRUE;
    if (compileSuccessful) {
      return;
    }
    if (fileName.isPresent()) {
      throw new RuntimeException("Compiliation error in " + fileName + "\n" + getInfoLog(this.id));
    }
    throw new RuntimeException("Compilation error " + getInfoLog(this.id));
  }

  public LwjglShader(int shaderType, String src) {
    this(shaderType, src, Optional.empty());
  }

  public int getId() {
    return this.id;
  }

  public void disposeGl() {
    if (!disposed) {
      glDeleteShader(this.id);
      disposed = true;
    }
  }

  private void setSource(String source) {
    // Attach the GLSL source code

  }

  private static String getInfoLog(int shaderId) {
    return glGetShaderInfoLog(shaderId);
  }
}
