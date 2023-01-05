package hana04.opengl.wrapper;

import java.nio.Buffer;
import java.util.function.Consumer;

public interface GlVbo extends GlObject {
  void bind();

  void unbind();

  default void use(Consumer<GlVbo> code) {
    this.bind();
    code.accept(this);
    this.unbind();
  }

  boolean isBound();

  int getId();

  void setData(Buffer buffer);
}
