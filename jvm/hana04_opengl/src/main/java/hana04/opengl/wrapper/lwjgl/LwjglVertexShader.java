package hana04.opengl.wrapper.lwjgl;

import java.util.Optional;

import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public class LwjglVertexShader extends LwjglShader {
  public LwjglVertexShader(String src) {
    this(src, Optional.empty());
  }

  public LwjglVertexShader(String src, Optional<String> srcFile) {
    super(GL_VERTEX_SHADER, src, srcFile);
  }
}
