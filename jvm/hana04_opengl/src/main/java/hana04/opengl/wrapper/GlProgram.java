package hana04.opengl.wrapper;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public interface GlProgram extends GlObject {
  int getId();

  boolean isUsed();

  void use();

  void unuse();

  default void use(Consumer<GlProgram> code) {
    this.use();
    code.accept(this);
    this.unuse();
  }

  GlUniform getUniform(String name);

  Optional<GlUniform> uniform(String name);

  boolean hasUniform(String name);

  GlAttribute getAttribute(String name);

  Optional<GlAttribute> attribute(String name);

  boolean hasAttribute(String name);

  Map<String, ? extends GlUniform> getUniforms();

  Map<String, ? extends GlAttribute> getAttibutes();
}
