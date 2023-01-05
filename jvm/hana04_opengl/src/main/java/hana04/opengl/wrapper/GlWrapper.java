package hana04.opengl.wrapper;

import java.nio.ByteBuffer;
import java.util.Optional;

public interface GlWrapper extends GlObject {
  GlProgram getCurrentProgram();

  void useProgram(GlProgram program);

  void unuseProgram();

  GlProgram createProgram(String vertexShaderSource, String fragmentShaderSource);

  GlProgram createProgram(String vertexSource, Optional<String> vertexSourceFile,
      String fragmentSource, Optional<String> fragmentSourceFile);

  void destroyProgram(GlProgram program);

  GlTexture2D createTexture2D(int internalFormat, boolean useMipmap);

  GlTextureRect createTextureRect(int internalFormat);

  GlTexture3D createTexture3D(int internalFormat);

  GlTextureCubeMap createTextureCubeMap(int internalFormat);

  void destroyTexture(GlTexture texture);

  int getTextureUnitCount();

  GlTextureUnit getTextureUnit(int index);

  GlTextureUnit getActiveTextureUnit();

  void clearScreen(boolean colorBuffer, boolean depthBuffer);

  void setClearColor(double r, double g, double b, double a);

  void setBlendingEnabled(boolean enabled);

  void setBlendFunc(int sourceFunc, int destFunc);

  void setBlendFuncSeparate(int sourceRgbFunc, int destRgbFunc, int sourceAlphaFunc, int destAlphaFunc);

  boolean isBlendingEnabled();

  void setDepthTestEnabled(boolean enabled);

  boolean isDepthTestEnabled();

  GlFbo createFbo();

  void destroyFbo(GlFbo fbo);

  GlVbo createVbo(int target);

  void destroyVbo(GlVbo vbo);

  void unbindVbo(int target);

  GlVbo getBoundVbo(int target);

  void setViewport(int x, int y, int w, int h);

  void setDepthFunc(int value);

  GlVao createVao();

  void destroyVao(GlVao vao);

  /**
   * Wrapper the call to glDrawElement with the index in the index buffer
   * assumed to be of type int.
   *
   * @param mode  OpenGL glConstant specifying the type of geometry to draw
   * @param count the number of indices in the index buffer to render
   * @param start the start offset of the index to render
   */
  void drawElements(int mode, int count, int start);

  void cullFace(int mode);

  void setFaceCullingEnabled(boolean enabled);

  void flush();

  void setPointSize(float size);

  void setLineWidth(float width);

  void setFrontFace(int frontFace);

  void readPixels(int x, int y, int width, int height, int format, int type, ByteBuffer buffer);
}
