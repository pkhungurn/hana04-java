package hana04.apt.codegen;

import com.squareup.javapoet.TypeSpec;

public interface BindingGenerator {
  void generate(TypeSpec.Builder moduleClassBuilder);

  String getBindingLocationPackageName();
}
