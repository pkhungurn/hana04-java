package hana04.opengl.wrapper.lwjgl;

import hana04.opengl.wrapper.GlProgram;
import hana04.opengl.wrapper.GlUniform;
import org.lwjgl.system.MemoryStack;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.glGetActiveUniform;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniform4i;
import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20C.glUniform2i;
import static org.lwjgl.opengl.GL20C.glUniform3i;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_IS_ROW_MAJOR;
import static org.lwjgl.opengl.GL31.glGetActiveUniformsi;
import static org.lwjgl.system.MemoryStack.stackCallocInt;
import static org.lwjgl.system.MemoryStack.stackMallocFloat;
import static org.lwjgl.system.MemoryStack.stackPush;

public class LwjglUniform implements GlUniform {
  // ************* Private variables *************
  LwjglProgram program;
  String name;
  int size;
  int type;
  int location;
  boolean isRowMajor;

  public LwjglUniform(LwjglProgram prog) {
    this.program = prog;
  }

  // ************* Public interface *************
  public LwjglUniform(LwjglProgram prog, int index) {
    this.program = prog;

    byte[] uniformName = new byte[512];

    // Get the uniform info (name, type, size)
    try (MemoryStack stack = stackPush()) {
      IntBuffer uniformSize = stackCallocInt(1);
      IntBuffer uniformType = stackCallocInt(1);
      this.name = glGetActiveUniform(this.program.getId(), index, uniformSize, uniformType);
      this.size = uniformSize.get(0);
      this.type = uniformType.get(0);
    }


    // Get the uniform location within the program
    this.location = glGetUniformLocation(this.program.getId(), this.name);

    this.isRowMajor = false; // Default is column major format

    // Some Intel drivers return -1 for "gl_" variables.
    // Since we shouldn't be setting "gl_" variables anyway, just skip this part
    // and return an incomplete GlUniform with a -1 location.
    if (this.location > -1) {
      try {
        this.isRowMajor = glGetActiveUniformsi(this.program.getId(), index, GL_UNIFORM_IS_ROW_MAJOR) != 0;
      } catch (Exception e) {
        // NO-OP
      }
    }
  }

  public boolean getIsRowMajor() {
    return isRowMajor;
  }

  public int getLocation() {
    return location;
  }

  public int getType() {
    return type;
  }

  public int getSize() {
    return size;
  }

  public String getName() {
    return name;
  }

  @Override
  public GlProgram getProgram() {
    return program;
  }

  public void set1Int(int x) {
    glUniform1i(this.location, x);
  }

  public void set2Int(int x, int y) {
    glUniform2i(this.location, x, y);
  }

  public void set3Int(int x, int y, int z) {
    glUniform3i(this.location, x, y, z);
  }

  public void set4Int(int x, int y, int z, int w) {
    glUniform4i(this.location, x, y, z, w);
  }

  public void set1Float(float x) {
    glUniform1f(this.location, x);
  }

  public void set2Float(float x, float y) {
    glUniform2f(this.location, x, y);
  }

  public void set3Float(float x, float y, float z) {
    glUniform3f(this.location, x, y, z);
  }

  public void set4Float(float x, float y, float z, float w) {
    glUniform4f(this.location, x, y, z, w);
  }

    /*
    public void setTuple2(javax_.vecmath.Tuple2f v) {
        glUniform2f(this.location, v.x, v.y);
    }

    public void setTuple3(javax_.vecmath.Tuple3f v) {
        glUniform3f(this.location, v.x, v.y, v.z);
    }

    public void setTuple4(javax_.vecmath.Tuple4f v) {
        glUniform4f(this.location, v.x, v.y, v.z, v.w);
    }
    */

  public void setMatrix4(Matrix4f mat) {
    try (MemoryStack stack = stackPush()) {
      FloatBuffer buf = stackMallocFloat(16);
      // We will pass the matrix elements in column major order
      for (int c = 0; c < 4; ++c) {
        for (int r = 0; r < 4; ++r) {
          buf.put(mat.getElement(r, c));
        }
      }
      buf.rewind();
      glUniformMatrix4fv(this.location, false, buf);
    }
  }

  public void setMatrix4(Matrix4d mat) {
    try (MemoryStack stack = stackPush()) {
      FloatBuffer buf = stackMallocFloat(16);
      // We will pass the matrix elements in column major order
      for (int c = 0; c < 4; ++c) {
        for (int r = 0; r < 4; ++r) {
          buf.put((float) mat.getElement(r, c));
        }
      }
      buf.rewind();
      glUniformMatrix4fv(this.location, false, buf);
    }
  }

  @Override
  public void setMatrix4(Object mat) {
    if (mat instanceof Matrix4d) {
      setMatrix4((Matrix4d)mat);
    } else if (mat instanceof Matrix4f) {
      setMatrix4((Matrix4f)mat);
    } else {
      throw new RuntimeException("mat is not a Matrix4d or Matrix4f");
    }
  }


  public void setMatrix3(Matrix3f mat) {
    try (MemoryStack stack = stackPush()) {
      FloatBuffer buf = stackMallocFloat(9);
      // We will pass the matrix elements in column major order
      for (int c = 0; c < 3; ++c) {
        for (int r = 0; r < 3; ++r) {
          buf.put(mat.getElement(r, c));
        }
      }
      buf.rewind();
      glUniformMatrix3fv(this.location, false, buf);
    }
  }

  public void setMatrix3(Matrix3d mat) {
    try (MemoryStack stack = stackPush()) {
      FloatBuffer buf = stackMallocFloat(9);
      // We will pass the matrix elements in column major order
      for (int c = 0; c < 3; ++c) {
        for (int r = 0; r < 3; ++r) {
          buf.put((float) mat.getElement(r, c));
        }
      }
      buf.rewind();
      glUniformMatrix3fv(this.location, false, buf);
    }
  }

  public void setMatrix3(Matrix4f mat) {
    try (MemoryStack stack = stackPush()) {
      FloatBuffer buf = stackMallocFloat(9);
      // We will pass the matrix elements in column major order
      for (int c = 0; c < 3; ++c) {
        for (int r = 0; r < 3; ++r) {
          buf.put(mat.getElement(r, c));
        }
      }
      buf.rewind();
      glUniformMatrix3fv(this.location, false, buf);
    }
  }

  public void setMatrix3(Matrix4d mat) {
    try (MemoryStack stack = stackPush()) {
      FloatBuffer buf = stackMallocFloat(9);
      // We will pass the matrix elements in column major order
      for (int c = 0; c < 3; ++c) {
        for (int r = 0; r < 3; ++r) {
          buf.put((float) mat.getElement(r, c));
        }
      }
      buf.rewind();
      glUniformMatrix3fv(this.location, false, buf);
    }
  }
}
