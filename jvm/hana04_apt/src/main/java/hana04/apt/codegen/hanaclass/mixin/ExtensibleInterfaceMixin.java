package hana04.apt.codegen.hanaclass.mixin;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import dagger.Binds;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import hana04.apt.codegen.BindingGenerator;
import hana04.apt.codegen.hanaclass.HanaClassCodeGenerator;
import hana04.apt.processor.HanaAnnotationProcessor;
import hana04.apt.util.Util;
import hana04.base.extension.HanaExtensionFactory;
import hana04.base.extension.HanaExtensionFactoryMap;
import hana04.base.extension.annotation.HanaExtensibleSuperclass;
import hana04.base.extension.donothing.DoNothingExtension;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ExtensibleInterfaceMixin implements ClassMixin {
  private final HanaClassCodeGenerator classSpec;
  private final ClassName superClass;

  public ExtensibleInterfaceMixin(HanaClassCodeGenerator classSpec, ClassName superClass) {
    this.classSpec = classSpec;
    this.superClass = superClass;
  }

  @Override
  public void generateCode(Filer filer) {
    generateExtensionFactoryFile(filer);
    generateExtensionFactoryMapFile(filer);
    generateDoNothingExtensionFactoryFile(filer);
  }

  public String getClassName() {
    return classSpec.getClassName();
  }

  public String getUniqueClassName() {
    return classSpec.getUniqueClassName();
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

  private ClassName getPoetExtensionFactoryClassName() {
    return ClassName.get(classSpec.packageName, getClassName() + "__ExtensionFactory");
  }

  private void generateExtensionFactoryFile(Filer filer) {
    TypeSpec.Builder extensionFactoryBuilder = TypeSpec.interfaceBuilder(getExtensionFactoryClassName())
      .addModifiers(Modifier.PUBLIC)
      .addAnnotation(AnnotationSpec.builder(Generated.class)
        .addMember("value", "$S", HanaAnnotationProcessor.class)
        .build())
      .addSuperinterface(
        ParameterizedTypeName.get(ClassName.get(HanaExtensionFactory.class),
          getPoetClassName()));
    JavaFile javaFile = JavaFile.builder(getPackageName(), extensionFactoryBuilder.build()).build();
    try {
      javaFile.writeTo(filer);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String getExtensionFactoryMapClassName() {
    return getClassName() + "__ExtensionFactoryMap";
  }

  private ClassName getPoetExtensionFactoryMapClassName() {
    return ClassName.get(getPackageName(), getExtensionFactoryMapClassName());
  }

  private void generateExtensionFactoryMapFile(Filer filer) {
    TypeSpec.Builder extensionFactoryMapBuilder = TypeSpec.classBuilder(getExtensionFactoryMapClassName())
      .addAnnotation(AnnotationSpec.builder(Generated.class)
        .addMember("value", "$S", HanaAnnotationProcessor.class)
        .build())
      .addAnnotation(Singleton.class)
      .addSuperinterface(HanaExtensionFactoryMap.class)
      .addModifiers(Modifier.PUBLIC);

    TypeName mapType = ParameterizedTypeName.get(ClassName.get(Map.class),
      ParameterizedTypeName.get(ClassName.get(Class.class), TypeVariableName.get("?")),
      getPoetExtensionFactoryClassName());
    TypeName classType = ParameterizedTypeName.get(ClassName.get(Class.class), TypeVariableName.get("?"));

    String classToExtensionFactoryName = "classToExtensionFactory";

    extensionFactoryMapBuilder.addField(FieldSpec
      .builder(mapType, classToExtensionFactoryName)
      .addModifiers(Modifier.PRIVATE)
      .build());

    extensionFactoryMapBuilder.addMethod(MethodSpec.constructorBuilder()
      .addModifiers(Modifier.PUBLIC)
      .addAnnotation(Inject.class)
      .addParameter(ParameterSpec.builder(mapType, classToExtensionFactoryName).build())
      .addStatement(String.format("this.%s = %s", classToExtensionFactoryName, classToExtensionFactoryName))
      .build());

    extensionFactoryMapBuilder.addMethod(MethodSpec.methodBuilder("get")
      .addModifiers(Modifier.PUBLIC)
      .addAnnotation(Override.class)
      .addParameter(classType, "extensionClass")
      .returns(HanaExtensionFactory.class)
      .addStatement(String.format("return %s.get(extensionClass)", classToExtensionFactoryName))
      .build());

    extensionFactoryMapBuilder.addMethod(MethodSpec.methodBuilder("containsKey")
      .addModifiers(Modifier.PUBLIC)
      .addAnnotation(Override.class)
      .addParameter(classType, "extensionClass")
      .returns(TypeName.BOOLEAN)
      .addStatement(String.format("return %s.containsKey(extensionClass)", classToExtensionFactoryName))
      .build());

    JavaFile javaFile = JavaFile.builder(getPackageName(), extensionFactoryMapBuilder.build()).build();
    try {
      javaFile.writeTo(filer);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String getDoNothingExtensionClassName() {
    return getClassName() + "__DoNothingExtensionFactory";
  }

  private ClassName getPoetDoNothingExtensionClassName() {
    return ClassName.get(getPackageName(), getDoNothingExtensionClassName());
  }

  private void generateDoNothingExtensionFactoryFile(Filer filer) {
    TypeSpec.Builder doNothingExtension = TypeSpec.classBuilder(getDoNothingExtensionClassName())
      .addAnnotation(AnnotationSpec.builder(Generated.class)
        .addMember("value", "$S", HanaAnnotationProcessor.class)
        .build())
      .addModifiers(Modifier.PUBLIC)
      .addSuperinterface(getPoetExtensionFactoryClassName());

    doNothingExtension.addMethod(MethodSpec
      .constructorBuilder()
      .addAnnotation(Inject.class)
      .build());
    doNothingExtension.addMethod(MethodSpec
      .methodBuilder("create")
      .addAnnotation(Override.class)
      .addModifiers(Modifier.PUBLIC)
      .returns(DoNothingExtension.class)
      .addParameter(ParameterSpec.builder(getPoetClassName(), "instance").build())
      .addStatement("return new $T() {}", DoNothingExtension.class)
      .build());

    Util.writeJavaFile(filer, getPackageName(), doNothingExtension.build());
  }

  @Override
  public List<BindingGenerator> getBindingGenerators() {
    return ImmutableList.of(
      new ExtensibleSuperclassBindingGenerator(),
      new ExtensibleFactoryMapBindingGenerator(),
      new DoNothingExtensionBindingGenerator());
  }

  class ExtensibleSuperclassBindingGenerator implements BindingGenerator {

    @Override
    public void generate(TypeSpec.Builder moduleClassBuilder) {
      moduleClassBuilder.addMethod(MethodSpec
        .methodBuilder("provide__" + getUniqueClassName() + "__extensibleSuperclass")
        .addModifiers(Modifier.STATIC)
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Provides.class)
        .addAnnotation(IntoMap.class)
        .addAnnotation(HanaExtensibleSuperclass.class)
        .addAnnotation(AnnotationSpec.builder(ClassKey.class)
          .addMember("value",
            CodeBlock.builder().add("$T.class", getPoetClassName()).build())
          .build())
        .returns(ParameterizedTypeName.get(ClassName.get(Class.class), TypeVariableName.get("?")))
        .addStatement("return $T.class", superClass)
        .build());
    }

    @Override
    public String getBindingLocationPackageName() {
      return getPackageName();
    }
  }

  class ExtensibleFactoryMapBindingGenerator implements BindingGenerator {

    @Override
    public void generate(TypeSpec.Builder moduleClassBuilder) {
      moduleClassBuilder.addMethod(MethodSpec
        .methodBuilder("provide__" + getUniqueClassName() + "__extensibleFactoryMap")
        .addModifiers(Modifier.ABSTRACT)
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Binds.class)
        .addAnnotation(IntoMap.class)
        .addAnnotation(AnnotationSpec.builder(ClassKey.class)
          .addMember("value",
            CodeBlock.builder().add("$T.class", getPoetClassName()).build())
          .build())
        .returns(HanaExtensionFactoryMap.class)
        .addParameter(ParameterSpec.builder(getPoetExtensionFactoryMapClassName(), "extensionFactoryMap").build())
        .build());
    }

    @Override
    public String getBindingLocationPackageName() {
      return getPackageName();
    }
  }

  class DoNothingExtensionBindingGenerator implements BindingGenerator {

    @Override
    public void generate(TypeSpec.Builder moduleClassBuilder) {
      moduleClassBuilder.addMethod(MethodSpec
        .methodBuilder("provide__" + getUniqueClassName() + "__DoNothingExtension__ExtensionFactory")
        .addModifiers(Modifier.ABSTRACT)
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Binds.class)
        .addAnnotation(IntoMap.class)
        .addAnnotation(AnnotationSpec.builder(ClassKey.class)
          .addMember("value",
            CodeBlock.builder().add("$T.class", DoNothingExtension.class).build())
          .build())
        .returns(getPoetExtensionFactoryClassName())
        .addParameter(ParameterSpec.builder(getPoetDoNothingExtensionClassName(), "factory").build())
        .build());
    }

    @Override
    public String getBindingLocationPackageName() {
      return getPackageName();
    }
  }
}
