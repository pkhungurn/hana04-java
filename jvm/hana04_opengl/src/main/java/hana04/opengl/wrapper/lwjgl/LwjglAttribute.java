package hana04.opengl.wrapper.lwjgl;

import hana04.opengl.wrapper.GlAttribute;
import hana04.opengl.wrapper.GlAttributeSpec;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetActiveAttrib;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.system.MemoryStack.stackMallocInt;
import static org.lwjgl.system.MemoryStack.stackPush;

public class LwjglAttribute implements GlAttribute {
  private LwjglProgram program;
  private String name;
  private int size;
  private int type;
  private int location;
  private boolean enabled;

  public LwjglAttribute(LwjglProgram prog, int index) {
    this.program = prog;
    try (MemoryStack stack = stackPush()) {
      IntBuffer attribSize = stackMallocInt(1);
      IntBuffer attribType = stackMallocInt(1);
      this.name = glGetActiveAttrib(this.program.getId(), index, attribSize, attribType);
      this.size = attribSize.get(0);
      this.type = attribType.get(0);
    }
    this.location = glGetAttribLocation(this.program.getId(), this.name);
    this.enabled = false;
  }

  public String getName() {
    return name;
  }

  @Override
  public int getIndex() {
    return location;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int getType() {
    return type;
  }

  @Override
  public void setEnabled(boolean enabled) {
    if (enabled) {
      glEnableVertexAttribArray(location);
      this.enabled = true;
    } else {
      glDisableVertexAttribArray(location);
      this.enabled = false;
    }
  }

  public void setType(int type) {
    this.type = type;
  }

  public int getLocation() {
    return location;
  }

  public void setLocation(int location) {
    this.location = location;
  }

  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void setup(int size, int type, boolean normalized, int stride, long pointer) {
    glVertexAttribPointer(getLocation(), size, type, normalized, stride, pointer);
  }

  @Override
  public void setup(GlAttributeSpec spec) {
    setup(spec.size, spec.type, spec.normalized, spec.stride, spec.pointer);
  }

}
