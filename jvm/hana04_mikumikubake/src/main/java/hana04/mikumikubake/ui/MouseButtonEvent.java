package hana04.mikumikubake.ui;

import org.inferred.freebuilder.FreeBuilder;

import static org.lwjgl.glfw.GLFW.GLFW_MOD_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

@FreeBuilder
public interface MouseButtonEvent {
  enum Button {
    LEFT,
    RIGHT,
    MIDDLE;

    public static Button parse(int button) {
      switch (button) {
        case GLFW_MOUSE_BUTTON_LEFT:
          return LEFT;
        case GLFW_MOUSE_BUTTON_RIGHT:
          return RIGHT;
        case GLFW_MOUSE_BUTTON_MIDDLE:
          return MIDDLE;
        default:
          throw new IllegalArgumentException("invalid button value: " + button);
      }
    }
  }

  enum Action {
    PRESS,
    RELEASE;

    public static Action parse(int action) {
      switch (action) {
        case GLFW_PRESS:
          return PRESS;
        case GLFW_RELEASE:
          return RELEASE;
        default:
          throw new IllegalArgumentException("invalid action value: " + action);
      }
    }
  }

  Button button();

  Action action();

  boolean shiftPressed();

  boolean ctrlPressed();

  boolean altPressed();

  class Builder extends MouseButtonEvent_Builder { }

  static Builder builder() {
    return new Builder();
  }

  default boolean noModifiers() {
    return !shiftPressed() && !ctrlPressed() && !altPressed();
  }

  default boolean onlyShiftPressed() {
    return shiftPressed() && !ctrlPressed() && !altPressed();
  }

  default boolean onlyCtrlPressed() {
    return !shiftPressed() && ctrlPressed() && !altPressed();
  }

  default boolean onlyAltPressed() {
    return !shiftPressed() && !ctrlPressed() && altPressed();
  }

  static MouseButtonEvent create(int button, int action, int mods) {
    return MouseButtonEvent.builder()
      .button(MouseButtonEvent.Button.parse(button))
      .action(MouseButtonEvent.Action.parse(action))
      .shiftPressed((mods & GLFW_MOD_SHIFT) != 0)
      .ctrlPressed((mods & GLFW_MOD_CONTROL) != 0)
      .altPressed((mods & GLFW_MOD_ALT) != 0)
      .build();
  }
}
