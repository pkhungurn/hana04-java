/*
 * This file is part of Wakame2, a research-oriented physically-based renderer by Pramook Khungurn.
 *
 * Copyright (c) 2016 by Pramook Khungurn.
 *
 *  Wakame2 is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License Version 3
 *  as published by the Free Software Foundation.
 *
 *  Wakame is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package hana04.opengl.wrapper.lwjgl;

import hana04.opengl.wrapper.GlConstants;
import hana04.opengl.wrapper.GlFbo;
import hana04.opengl.wrapper.GlProgram;
import hana04.opengl.wrapper.GlTexture;
import hana04.opengl.wrapper.GlTexture2D;
import hana04.opengl.wrapper.GlTexture3D;
import hana04.opengl.wrapper.GlTextureCubeMap;
import hana04.opengl.wrapper.GlTextureRect;
import hana04.opengl.wrapper.GlTextureUnit;
import hana04.opengl.wrapper.GlVao;
import hana04.opengl.wrapper.GlVbo;
import hana04.opengl.wrapper.GlWrapper;
import org.lwjgl.opengl.GL20;

import java.nio.ByteBuffer;
import java.util.Optional;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFlush;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.glGetBoolean;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glPointSize;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL11.glViewport;

public class LwjglGlWrapper implements GlWrapper {
  @Override
  public void disposeGl() {
    // NO-OP
  }

  @Override
  public GlProgram getCurrentProgram() {
    return LwjglProgram.getCurrent();
  }

  @Override
  public void useProgram(GlProgram program) {
    program.use();
  }

  @Override
  public void unuseProgram() {
    LwjglProgram.unuseProgram();
  }

  @Override
  public GlProgram createProgram(String vertexShaderSource, String fragmentShaderSource) {
    return new LwjglProgram(vertexShaderSource, fragmentShaderSource);
  }

  @Override
  public GlProgram createProgram(String vertexSource, Optional<String> vertexSourceFile,
      String fragmentSource, Optional<String> fragmentSourceFile) {
    return new LwjglProgram(vertexSource, vertexSourceFile, fragmentSource, fragmentSourceFile);
  }

  @Override
  public void destroyProgram(GlProgram program) {
    if (program instanceof LwjglProgram) {
      LwjglProgram joglProgram = (LwjglProgram) program;
      joglProgram.disposeGl();
    } else {
      throw new RuntimeException("The given program is not a LwjglProgram.");
    }
  }

  @Override
  public GlTexture2D createTexture2D(int internalFormat, boolean useMipmap) {
    return new LwjglTexture2D(internalFormat, useMipmap);
  }

  @Override
  public GlTextureRect createTextureRect(int internalFormat) {
    return new LwjglTextureRect(internalFormat);
  }

  @Override
  public GlTexture3D createTexture3D(int internalFormat) {
    return new LwjglTexture3D(internalFormat);
  }

  @Override
  public GlTextureCubeMap createTextureCubeMap(int internalFormat) {
    return new LwjglTextureCubeMap(internalFormat);
  }

  @Override
  public void destroyTexture(GlTexture texture) {
    if (texture instanceof LwjglTexture) {
      texture.disposeGl();
    } else {
      throw new RuntimeException("The given texture is not a LwjglTexture");
    }
  }

  @Override
  public int getTextureUnitCount() {
    return LwjglTextureUnit.getTextureUnitCount();
  }

  @Override
  public GlTextureUnit getTextureUnit(int index) {
    return LwjglTextureUnit.getTextureUnit(index);
  }

  @Override
  public GlTextureUnit getActiveTextureUnit() {
    return LwjglTextureUnit.getActiveTextureUnit();
  }

  @Override
  public void clearScreen(boolean colorBuffer, boolean depthBuffer) {
    if (colorBuffer && depthBuffer) {
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    } else if (colorBuffer && !depthBuffer) {
      glClear(GL_COLOR_BUFFER_BIT);
    } else if (!colorBuffer && depthBuffer) {
      glClear(GL_DEPTH_BUFFER_BIT);
    }
  }

  @Override
  public void setClearColor(double r, double g, double b, double a) {
    glClearColor((float) r, (float) g, (float) b, (float) a);
  }

  @Override
  public void setBlendingEnabled(boolean enabled) {
    if (enabled) {
      glEnable(GL_BLEND);
    } else {
      glDisable(GL_BLEND);
    }
  }

  @Override
  public void setBlendFunc(int sourceFunc, int destFunc) {
    glBlendFunc(sourceFunc, destFunc);
  }

  @Override
  public void setBlendFuncSeparate(int sourceRgbFunc, int destRgbFunc, int sourceAlphaFunc, int destAlphaFunc) {
    GL20.glBlendFuncSeparate(sourceRgbFunc, destRgbFunc, sourceAlphaFunc, destAlphaFunc);
  }

  @Override
  public boolean isBlendingEnabled() {
    return glGetBoolean(GL_BLEND);
  }

  @Override
  public void setDepthTestEnabled(boolean enabled) {
    if (enabled) {
      glEnable(GL_DEPTH_TEST);
    } else {
      glDisable(GL_DEPTH_TEST);
    }
  }

  @Override
  public boolean isDepthTestEnabled() {
    return glGetBoolean(GL_DEPTH_TEST);
  }

  @Override
  public GlFbo createFbo() {
    return new LwjglFbo();
  }

  @Override
  public void destroyFbo(GlFbo fbo) {
    if (fbo instanceof LwjglFbo) {
      fbo.disposeGl();
    } else {
      throw new RuntimeException("The given fbo is not a LwjglFbo.");
    }
  }

  @Override
  public GlVbo createVbo(int target) {
    if (target == GlConstants.GL_ARRAY_BUFFER) {
      return new LwjglVbo(LwjglVboTarget.ARRAY_BUFFER);
    } else if (target == GlConstants.GL_ELEMENT_ARRAY_BUFFER) {
      return new LwjglVbo(LwjglVboTarget.ELEMENT_ARRAY_BUFFER);
    } else {
      throw new RuntimeException("Invalid VBO target: " + target);
    }
  }

  @Override
  public void destroyVbo(GlVbo vbo) {
    if (vbo instanceof LwjglVbo) {
      vbo.disposeGl();
    } else {
      throw new RuntimeException("The given VBO is not a LwjglVbo");
    }

  }

  @Override
  public void unbindVbo(int target) {
    if (target == GlConstants.GL_ARRAY_BUFFER) {
      LwjglVboTarget.ARRAY_BUFFER.unbindVbo();
    } else if (target == GlConstants.GL_ELEMENT_ARRAY_BUFFER) {
      LwjglVboTarget.ELEMENT_ARRAY_BUFFER.unbindVbo();
    } else {
      throw new RuntimeException("Invalid VBO target: " + target);
    }
  }

  @Override
  public GlVbo getBoundVbo(int target) {
    if (target == GlConstants.GL_ARRAY_BUFFER) {
      return LwjglVboTarget.ARRAY_BUFFER.getBoundVbo();
    } else if (target == GlConstants.GL_ELEMENT_ARRAY_BUFFER) {
      return LwjglVboTarget.ELEMENT_ARRAY_BUFFER.getBoundVbo();
    } else {
      throw new RuntimeException("Invalid VBO target: " + target);
    }
  }

  @Override
  public void setViewport(int x, int y, int w, int h) {
    glViewport(x, y, w, h);
  }

  @Override
  public void setDepthFunc(int value) {
    glDepthFunc(value);
  }

  @Override
  public GlVao createVao() {
    return new LwjglVao();
  }

  @Override
  public void destroyVao(GlVao vao) {
    if (vao instanceof LwjglVao) {
      vao.disposeGl();
    } else {
      throw new RuntimeException("The given VAO is not an LwjglVao");
    }
  }

  @Override
  public void drawElements(int mode, int count, int start) {
    glDrawElements(mode, count, GL_UNSIGNED_INT, start * 4);
  }

  @Override
  public void cullFace(int mode) {
    glCullFace(mode);
  }

  @Override
  public void setFaceCullingEnabled(boolean enabled) {
    if (enabled) {
      glEnable(GL_CULL_FACE);
    } else {
      glDisable(GL_CULL_FACE);
    }
  }

  @Override
  public void flush() {
    glFlush();
  }

  @Override
  public void setPointSize(float size) {
    glPointSize(size);
  }

  @Override
  public void setLineWidth(float width) {
    glLineWidth(width);
  }

  @Override
  public void setFrontFace(int frontFace) {
    glFrontFace(frontFace);
  }

  @Override
  public void readPixels(int x, int y, int width, int height, int format, int type, ByteBuffer byteBuffer) {
    glReadPixels(x, y, width, height, format, type, byteBuffer);
  }
}
