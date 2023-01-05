package hana04.apt.codegen.hanaclass.mixin;

import com.google.auto.factory.Provided;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import hana04.apt.codegen.BindingGenerator;
import hana04.apt.codegen.hanaclass.HanaClassCodeGenerator;
import hana04.base.extension.AbstractHanaExtensible;
import hana04.base.extension.HanaExtensible;
import hana04.base.extension.HanaExtensionUberFactory;
import hana04.base.extension.validator.Validator;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.util.List;

public class ExtensibleMixin implements ClassMixin {
  private final HanaClassCodeGenerator classSpec;

  public ExtensibleMixin(HanaClassCodeGenerator classSpec) {
    this.classSpec = classSpec;
  }

  public void generateCode(Filer filer) {
    // NO-OP
  }

  public String getClassName() {
    return classSpec.getClassName();
  }

  public String getPackageName() {
    return classSpec.packageName;
  }

  public ClassName getPoetClassName() {
    return classSpec.getPoetClassName();
  }

  private String getExtensionFactoryClassName() {
    return getClassName() + "__ExtensionFactory";
  }

  ClassName getPoetExtensionFactoryClassName() {
    return ClassName.get(getPackageName(), getExtensionFactoryClassName());
  }

  @Override
  public void modifyImplClassBuilder(TypeSpec.Builder implClassBuilder) {
    implClassBuilder.superclass(AbstractHanaExtensible.class);

    implClassBuilder.addMethod(MethodSpec.methodBuilder("getExtensibleClass")
      .addAnnotation(Override.class)
      .addModifiers(Modifier.PROTECTED)
      .returns(ParameterizedTypeName.get(ClassName.get(Class.class),
        TypeVariableName.get(String.format("? extends %s", HanaExtensible.class.getName()))))
      .addStatement("return $T.class", getPoetClassName())
      .build());
  }

  @Override
  public void modifyImplClassConstructorBuilder(MethodSpec.Builder constructorBuilder) {
    constructorBuilder.addParameter(ParameterSpec
      .builder(HanaExtensionUberFactory.class, "extensionUberFactory")
      .addAnnotation(Provided.class)
      .build());
  }

  @Override
  public void addPreBuildingImplClassConstructorCode(CodeBlock.Builder codeBuilder) {
    codeBuilder.addStatement("super(extensionUberFactory)");
  }

  @Override
  public void addPostBuildingImplClassConstructorCode(CodeBlock.Builder codeBuilder) {
    codeBuilder.beginControlFlow("if (supportsExtension($T.class))", Validator.class)
        .addStatement("getExtension($T.class).validate()", Validator.class)
        .endControlFlow();
  }

  public List<BindingGenerator> getBindingGenerators() {
    return ImmutableList.of();
  }
}
