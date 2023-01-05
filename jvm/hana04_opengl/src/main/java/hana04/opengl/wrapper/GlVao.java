package hana04.opengl.wrapper;

import java.util.function.Consumer;

public interface GlVao extends GlObject {
  void bind();

  void unbind();

  default void use(Consumer<GlVao> code) {
    this.bind();
    code.accept(this);
    this.unbind();
  }

  int getId();

  int getAttributeCount();

  void setAttributeEnabled(int index, boolean enabled);

  boolean isAttributeEnabled(int index);

  boolean isBound();

  void disableAllAttribute();
}
