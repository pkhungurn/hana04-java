package hana04.opengl.wrapper.lwjgl;

import com.google.common.base.Preconditions;
import hana04.opengl.wrapper.GlVao;

import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL20.GL_MAX_VERTEX_ATTRIBS;
import static org.lwjgl.opengl.GL20.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_VERTEX_ATTRIB_ARRAY_ENABLED;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetVertexAttribi;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LwjglVao implements GlVao {
  private static LwjglVao bound = null;

  private int id;
  private int attributeCount;
  private boolean disposed = false;

  public static LwjglVao getBoundVao() {
    return bound;
  }

  LwjglVao() {
    id = glGenVertexArrays();
    attributeCount = glGetInteger(GL_MAX_VERTEX_ATTRIBS);
  }

  @Override
  public void bind() {
    Preconditions.checkState(!disposed);
    if (bound != null) {
      bound.unbind();
    }
    glBindVertexArray(id);
    bound = this;
  }

  @Override
  public void unbind() {
    Preconditions.checkState(!disposed);
    if (bound == this) {
      glBindVertexArray(0);
      bound = null;
    }
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public int getAttributeCount() {
    return attributeCount;
  }

  @Override
  public boolean isBound() {
    Preconditions.checkState(!disposed);
    return this == bound;
  }

  @Override
  public void disableAllAttribute() {
    for(int i=0;i<attributeCount;i++) {
      setAttributeEnabled(i, false);
    }
  }

  @Override
  public void setAttributeEnabled(int index, boolean enabled) {
    Preconditions.checkState(!disposed);
    Preconditions.checkState(isBound());
    if (enabled) {
      glEnableVertexAttribArray(index);
    } else {
      glDisableVertexAttribArray(index);
    }
  }

  @Override
  public boolean isAttributeEnabled(int index) {
    Preconditions.checkState(isBound() && !disposed);
    return glGetVertexAttribi(index, GL_VERTEX_ATTRIB_ARRAY_ENABLED) == GL_TRUE;
  }

  @Override
  public void disposeGl() {
    if (!disposed) {
      unbind();
      glDeleteVertexArrays(id);
      disposed = true;
    }
  }
}
