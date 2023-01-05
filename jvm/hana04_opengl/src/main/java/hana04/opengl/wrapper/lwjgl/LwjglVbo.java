package hana04.opengl.wrapper.lwjgl;

import hana04.opengl.wrapper.GlVbo;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

public class LwjglVbo implements GlVbo {
  private int id;
  private LwjglVboTarget target;
  private boolean disposed;

  public LwjglVbo(LwjglVboTarget target) {
    this.target = target;
    id = glGenBuffers();
  }

  public void bind() {
    if (target.getBoundVbo() != null) {
      target.getBoundVbo().unbind();
    }
    glBindBuffer(target.getConstant(), id);
    target.setBoundVbo(this);
  }

  public void unbind() {
    if (target.getBoundVbo() == this) {
      glBindBuffer(target.getConstant(), 0);
      target.setBoundVbo(null);
    }
  }

  public void use() {
    bind();
  }

  public void unuse() {
    unbind();
  }

  public boolean isBound() {
    return target.getBoundVbo() == this;
  }

  public int getId() {
    return id;
  }

  @Override
  public void setData(Buffer buffer) {
    bind();
    buffer.rewind();
    if (buffer instanceof ByteBuffer) {
      glBufferData(target.getConstant(), (ByteBuffer) buffer, GL_STATIC_DRAW);
    } else if (buffer instanceof IntBuffer) {
      glBufferData(target.getConstant(), (IntBuffer) buffer, GL_STATIC_DRAW);
    } else if (buffer instanceof FloatBuffer) {
      glBufferData(target.getConstant(), (FloatBuffer) buffer, GL_STATIC_DRAW);
    } else {
      throw new RuntimeException("Buffer of type " + buffer.getClass() + " is not supported!");
    }
    unbind();
  }

  public void disposeGl() {
    if (!disposed) {
      if (isBound())
        unbind();
      glDeleteBuffers(id);
      disposed = true;
    }
  }
}
