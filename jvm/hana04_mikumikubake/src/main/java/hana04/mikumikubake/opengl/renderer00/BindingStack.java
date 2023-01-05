package hana04.mikumikubake.opengl.renderer00;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BindingStack {
  private final int maxDepth;
  private final List<Map<String, Object>> stack = new ArrayList<>();
  private int stackTop;

  public BindingStack() {
    this(128);
  }

  public BindingStack(int maxDepth) {
    Preconditions.checkArgument(maxDepth >= 1, "maximum binding stack depth must be more than 0.");
    this.maxDepth = maxDepth;
    for (int i = 0; i < maxDepth; i++) {
      stack.add(new HashMap<>());
    }
    stackTop = 0;
  }

  public void pushFrame() {
    if (stackTop == maxDepth) {
      throw new RuntimeException("frame count has reached maximum! no more frame to push!");
    }
    stackTop++;
    stack.get(stackTop).clear();
  }

  public void popFrame() {
    if (stackTop == 0) {
      throw new RuntimeException("attempt to pop the bottommost frame");
    }
    stackTop--;
  }

  public <T> T get(String name, Class<T> klass) {
    return (T) get(name);
  }

  public Object get(String name) {
    int position = stackTop;
    while (position >= 0) {
      Map<String, Object> bindings = stack.get(position);
      if (bindings.containsKey(name)) {
        return bindings.get(name);
      }
      position--;
    }
    throw new RuntimeException("binding for variable '" + name + "' not found.");
  }

  public boolean has(String name) {
    int position = stackTop;
    while (position >= 0) {
      Map<String, Object> bindings = stack.get(position);
      if (bindings.containsKey(name)) {
        return true;
      }
      position--;
    }
    return false;
  }

  public void set(String name, Object value) {
    if (stackTop < 0) {
      throw new RuntimeException("no frame in the stack");
    } else {
      Map<String, Object> bindings = stack.get(stackTop);
      bindings.put(name, value);
    }
  }
}