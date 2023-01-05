package hana04.apt.codegen.hanaclass;

import com.google.auto.factory.AutoFactory;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import dagger.Binds;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import hana04.apt.codegen.BindingGenerator;
import hana04.apt.codegen.hanaclass.mixin.ClassMixin;
import hana04.apt.codegen.hanaclass.property.PropertySpec;
import hana04.apt.processor.HanaAnnotationProcessor;
import hana04.base.extension.FluentBuilder;
import hana04.base.extension.FluentBuilderFactory;
import org.inferred.freebuilder.FreeBuilder;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.List;

public class HanaConcreteClassCodeGenerator extends HanaClassCodeGenerator {
  public HanaConcreteClassCodeGenerator(Element element, String packageName, List<PropertySpec> propertySpecs) {
    super(element, packageName, propertySpecs);
  }

  public void generateCode(Filer filer) {
    super.generateCode(filer);
    generateRawDataFile(filer);
    generateImplClass(filer);
    generateImplBuilderClass(filer);
    generateBuilderClass(filer);
    generateBuilderFactoryClass(filer);
  }

  private String getRawDataClassName() {
    return getClassName() + "__RawData";
  }

  public ClassName getPoetRawDataClassName() {
    return ClassName.get(packageName, getRawDataClassName());
  }

  private String getRawDataBuilderClassName() {
    return getRawDataClassName() + "_Builder";
  }

  ClassName getPoetRawDataBuilderClassName() {
    return ClassName.get(packageName, getRawDataBuilderClassName());
  }

  private void generateRawDataFile(Filer filer) {
    TypeSpec.Builder rawDataClassBuilder = TypeSpec.interfaceBuilder(getRawDataClassName())
        .addAnnotation(AnnotationSpec.builder(Generated.class)
            .addMember("value", "$S", HanaAnnotationProcessor.class)
            .build())
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(FreeBuilder.class);
    for (PropertySpec propertySpec : propertySpecs) {
      rawDataClassBuilder.addMethod(MethodSpec.methodBuilder(propertySpec.name)
          .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
          .returns(propertySpec.type.getRawDataTypeName())
          .build());
    }
    TypeSpec.Builder builderBuilder = TypeSpec.classBuilder("Builder")
        .addAnnotation(AnnotationSpec.builder(Generated.class)
            .addMember("value", "$S", HanaAnnotationProcessor.class)
            .build())
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .superclass(getPoetRawDataBuilderClassName());
    rawDataClassBuilder.addType(builderBuilder.build());
    JavaFile javaFile = JavaFile.builder(packageName, rawDataClassBuilder.build()).build();
    try {
      javaFile.writeTo(filer);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String getImplClassName() {
    return getClassName() + "__Impl";
  }

  private ClassName getPoetImplClassName() {
    return ClassName.get(packageName, getImplClassName());
  }

  private void generateImplClass(Filer filer) {
    TypeSpec.Builder implClassBuilder = TypeSpec.classBuilder(getImplClassName())
        .addAnnotation(AnnotationSpec.builder(Generated.class)
            .addMember("value", "$S", HanaAnnotationProcessor.class)
            .build())
        .addSuperinterface(getPoetClassName())
        .addModifiers(Modifier.PUBLIC);

    for (PropertySpec propertySpec : propertySpecs) {
      implClassBuilder.addField(FieldSpec.builder(propertySpec.type.getTypeName(), propertySpec.name)
          .addModifiers(Modifier.PRIVATE).build());
      implClassBuilder.addMethod(MethodSpec.methodBuilder(propertySpec.name)
          .addModifiers(Modifier.PUBLIC)
          .returns(propertySpec.type.getTypeName())
          .addAnnotation(Override.class)
          .addStatement("return " + propertySpec.name)
          .build());
    }

    addImplClassContructor(implClassBuilder);
    addOtherImplClassMethods(implClassBuilder);

    JavaFile javaFile = JavaFile.builder(packageName, implClassBuilder.build()).build();
    try {
      javaFile.writeTo(filer);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void addOtherImplClassMethods(TypeSpec.Builder implClassBuilder) {
    for (ClassMixin mixin : mixins) {
      mixin.modifyImplClassBuilder(implClassBuilder);
    }
  }

  private void addImplClassContructor(TypeSpec.Builder implClassBuilder) {
    MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
        .addAnnotation(AutoFactory.class)
        .addModifiers(Modifier.PUBLIC)
        .addParameter(ParameterSpec.builder(getPoetRawDataClassName(), "rawData").build());
    for (ClassMixin mixin : mixins) {
      mixin.modifyImplClassConstructorBuilder(constructorBuilder);
    }

    CodeBlock.Builder codeBuilder = CodeBlock.builder();
    for (ClassMixin mixin : mixins) {
      mixin.addPreBuildingImplClassConstructorCode(codeBuilder);
    }
    for (PropertySpec propertySpec : propertySpecs) {
      codeBuilder.add(String.format("// %s\n", propertySpec.name));
      propertySpec.addImplClassContructorCode(codeBuilder);
    }
    for (ClassMixin mixin : mixins) {
      mixin.addPostBuildingImplClassConstructorCode(codeBuilder);
    }
    constructorBuilder.addCode(codeBuilder.build());

    implClassBuilder.addMethod(constructorBuilder.build());
  }

  private void addOtherImplClassConstructorCode(CodeBlock.Builder codeBuilder) {
    for (ClassMixin mixin : mixins) {
      mixin.addPreBuildingImplClassConstructorCode(codeBuilder);
    }
  }

  public String getImplBuilderClassName() {
    return getImplClassName() + "__Builder";
  }

  public ClassName getPoetImplBuilderClassName() {
    return ClassName.get(packageName, getImplBuilderClassName());
  }

  private String getImplAutoFactoryClassName() {
    return getImplClassName() + "Factory";
  }

  private ClassName getPoetImplAutoFactoryClassName() {
    return ClassName.get(packageName, getImplAutoFactoryClassName());
  }

  private void generateImplBuilderClass(Filer filer) {
    String selfTypeName = "T";
    TypeSpec.Builder implBuilderClassBuilder = TypeSpec.classBuilder(getPoetImplBuilderClassName())
        .addAnnotation(AnnotationSpec.builder(Generated.class)
            .addMember("value", "$S", HanaAnnotationProcessor.class)
            .build())
        .addTypeVariable(TypeVariableName.get(selfTypeName).withBounds(
            ParameterizedTypeName.get(getPoetImplBuilderClassName(), TypeVariableName.get(selfTypeName))))
        .addSuperinterface(ParameterizedTypeName.get(
            ClassName.bestGuess(FluentBuilder.class.getCanonicalName()),
            getPoetClassName()))
        .addModifiers(Modifier.PUBLIC);

    String rawDataBuilderName = "rawDataBuilder___";
    String factoryName = "factory___";
    implBuilderClassBuilder
        .addField(FieldSpec.builder(getPoetRawDataBuilderClassName(), rawDataBuilderName)
            .addModifiers(Modifier.PRIVATE)
            .build())
        .addField(FieldSpec.builder(getPoetImplAutoFactoryClassName(), factoryName)
            .addModifiers(Modifier.PRIVATE)
            .build())
        .addMethod(MethodSpec.methodBuilder("build")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .returns(getPoetClassName())
            .addStatement("return factory___.create(rawDataBuilder___.build())")
            .build());

    for (PropertySpec propertySpec : propertySpecs) {
      propertySpec.type.addImplBuilderMethods(
          implBuilderClassBuilder,
          propertySpec.name,
          rawDataBuilderName,
          selfTypeName);
    }

    MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
        .addParameter(getPoetImplAutoFactoryClassName(), "factory___")
        .addStatement("this.factory___ = factory___")
        .addStatement("this.rawDataBuilder___ = new $T.Builder()", getPoetRawDataClassName());

    implBuilderClassBuilder.addMethod(constructorBuilder.build());

    JavaFile javaFile = JavaFile.builder(packageName, implBuilderClassBuilder.build()).build();
    try {
      javaFile.writeTo(filer);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String getBuilderFactoryClassName() {
    return getClassName() + "__Builder__Factory";
  }

  public ClassName getPoetBuilderFactoryClassName() {
    return ClassName.get(packageName, getBuilderFactoryClassName());
  }

  private String getBuilderClassName() {
    if (userDefinedBuilderElement.isPresent()) {
      return getClassName() + ".Builder";
    } else {
      return getClassName() + "__Builder";
    }
  }

  public ClassName getPoetBuilderClassName() {
    if (userDefinedBuilderElement.isPresent()) {
      return ClassName.get(packageName, getClassName(), "Builder");
    } else {
      return ClassName.get(packageName, getBuilderClassName());
    }
  }

  private void generateBuilderClass(Filer filer) {
    if (userDefinedBuilderElement.isPresent()) {
      return;
    }

    TypeSpec.Builder builderClassBuilder = TypeSpec.classBuilder(getPoetBuilderClassName())
        .addAnnotation(AnnotationSpec.builder(Generated.class)
            .addMember("value", "$S", HanaAnnotationProcessor.class)
            .build())
        .addModifiers(Modifier.PUBLIC)
        .superclass(
            ParameterizedTypeName.get(
                getPoetImplBuilderClassName(),
                getPoetBuilderClassName()))
        .addSuperinterface(
            ParameterizedTypeName.get(
                ClassName.get(FluentBuilder.class),
                getPoetClassName()))
        .addMethod(MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ParameterSpec.builder(getPoetImplAutoFactoryClassName(), "factory").build())
            .addStatement("super(factory)")
            .build());

    JavaFile javaFile = JavaFile.builder(packageName, builderClassBuilder.build()).build();
    try {
      javaFile.writeTo(filer);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void generateBuilderFactoryClass(Filer filer) {
    TypeSpec.Builder builderFactoryClassBuilder = TypeSpec.classBuilder(getPoetBuilderFactoryClassName())
        .addAnnotation(AnnotationSpec.builder(Generated.class)
            .addMember("value", "$S", HanaAnnotationProcessor.class)
            .build())
        .addAnnotation(Singleton.class)
        .addModifiers(Modifier.PUBLIC)
        .addSuperinterface(
            ParameterizedTypeName.get(
                ClassName.get(FluentBuilderFactory.class),
                getPoetClassName(),
                getPoetBuilderClassName()));

    if (userDefinedBuilderElement.isPresent()) {
      throw new RuntimeException("Inner builder class is not allowed: " + element.toString());
      /*
      checkConstructor(userDefinedBuilderElement.get());

      TypeName builderProviderTypeName =
          ParameterizedTypeName.get(ClassName.get(Provider.class), getPoetBuilderClassName());

      builderFactoryClassBuilder.addField(FieldSpec.builder(builderProviderTypeName, "builderProvider").build());

      MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
          .addAnnotation(Inject.class)
          .addModifiers(Modifier.PUBLIC)
          .addParameter(ParameterSpec.builder(builderProviderTypeName, "builderProvider").build())
          .addStatement("this.builderProvider = builderProvider");
      builderFactoryClassBuilder.addMethod(constructorBuilder.build());

      MethodSpec.Builder createMethodBuilder = MethodSpec.methodBuilder("create")
          .addAnnotation(Override.class)
          .addModifiers(Modifier.PUBLIC)
          .addStatement("return builderProvider.get()")
          .returns(getPoetBuilderClassName());

      builderFactoryClassBuilder.addMethod(createMethodBuilder.build());
       */
    } else {
      builderFactoryClassBuilder.addField(FieldSpec.builder(getPoetImplAutoFactoryClassName(), "factory").build());

      MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
          .addAnnotation(Inject.class)
          .addModifiers(Modifier.PUBLIC)
          .addParameter(ParameterSpec.builder(getPoetImplAutoFactoryClassName(), "factory").build())
          .addStatement("this.factory = factory");
      builderFactoryClassBuilder.addMethod(constructorBuilder.build());

      MethodSpec.Builder createMethodBuilder = MethodSpec.methodBuilder("create")
          .addAnnotation(Override.class)
          .addModifiers(Modifier.PUBLIC)
          .addStatement("return new $T(factory)", getPoetBuilderClassName())
          .returns(getPoetBuilderClassName());
      builderFactoryClassBuilder.addMethod(createMethodBuilder.build());
    }

    JavaFile javaFile = JavaFile.builder(packageName, builderFactoryClassBuilder.build()).build();
    try {
      javaFile.writeTo(filer);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void checkConstructor(Element builderElement) {
    boolean foundConstructor = false;
    for (Element enclosedElement : builderElement.getEnclosedElements()) {
      if (!enclosedElement.getKind().equals(ElementKind.CONSTRUCTOR)) {
        continue;
      }
      if (enclosedElement.getAnnotation(Inject.class) == null) {
        continue;
      }
      foundConstructor = true;
      break;
    }
    if (!foundConstructor) {
      throw new RuntimeException("There must be a constructor annotated with @Inject in the user-defined builder.");
    }
  }

  @Override
  public List<BindingGenerator> getBindingGenerators() {
    ImmutableList.Builder<BindingGenerator> bindingGenerators = ImmutableList.builder();
    bindingGenerators.add(new BuilderFactoryBindingGenerator());
    for (ClassMixin mixin : mixins) {
      bindingGenerators.addAll(mixin.getBindingGenerators());
    }
    return bindingGenerators.build();
  }

  class BuilderFactoryBindingGenerator implements BindingGenerator {

    @Override
    public void generate(TypeSpec.Builder moduleClassBuilder) {
      moduleClassBuilder.addMethod(MethodSpec
          .methodBuilder("provide__" + getUniqueClassName() + "__builderFactory")
          .addModifiers(Modifier.PUBLIC)
          .addAnnotation(Binds.class)
          .addAnnotation(IntoMap.class)
          .addAnnotation(AnnotationSpec
              .builder(ClassKey.class)
              .addMember("value", CodeBlock.builder().add("$T.class", getPoetBuilderClassName()).build())
              .build())
          .addModifiers(Modifier.ABSTRACT)
          .returns(FluentBuilderFactory.class)
          .addParameter(ParameterSpec.builder(getPoetBuilderFactoryClassName(), "builderFactory").build())
          .build());
    }

    @Override
    public String getBindingLocationPackageName() {
      return packageName;
    }
  }
}
