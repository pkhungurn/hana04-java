package hana04.apt.codegen.hanaclass.mixin;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import hana04.apt.codegen.BindingGenerator;

import javax.annotation.processing.Filer;
import java.util.List;

public interface ClassMixin {
  void generateCode(Filer filer);

  List<BindingGenerator> getBindingGenerators();

  default void modifyImplClassBuilder(TypeSpec.Builder implClassBuilder) {
    // NO-OP
  }

  default void modifyImplClassConstructorBuilder(MethodSpec.Builder constructorBuilder) {
    // NO-OP
  }

  default void addPreBuildingImplClassConstructorCode(CodeBlock.Builder codeBuilder) {
    // NO-OP
  }

  default void addPostBuildingImplClassConstructorCode(CodeBlock.Builder codeBuilder) {
    // NO-OP
  }
}