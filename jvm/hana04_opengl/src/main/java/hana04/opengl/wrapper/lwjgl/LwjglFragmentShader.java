package hana04.opengl.wrapper.lwjgl;

import java.util.Optional;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;

public class LwjglFragmentShader extends LwjglShader {
  public LwjglFragmentShader(String src) {
    this(src, Optional.empty());
  }

  public LwjglFragmentShader(String src, Optional<String> srcFile) {
    super(GL_FRAGMENT_SHADER, src, srcFile);
  }

}
