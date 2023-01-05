package hana04.apt.codegen;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import dagger.Binds;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import hana04.apt.processor.HanaAnnotationProcessor;
import hana04.apt.util.Util;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.util.List;

public class HanaExtensionCodeGenerator implements CodeGenerator {
  private final ClassName extensibleClass;
  private final ClassName extensionClass;
  private final ClassName implClass;
  private final ExecutableElement constructorElement;
  private final String packageName;

  public HanaExtensionCodeGenerator(
      ExecutableElement constructorElement,
      String packageName,
      ClassName extensibleClass,
      ClassName extensionClass,
      ClassName implClass) {
    this.extensibleClass = extensibleClass;
    this.extensionClass = extensionClass;
    this.implClass = implClass;
    this.constructorElement = constructorElement;
    this.packageName = packageName;
  }

  private String getClassName() {
    return constructorElement.getEnclosingElement().getSimpleName().toString();
  }

  static ClassName getPoetExtensibleClassExtensibleFactoryClassName(ClassName extensibleClass) {
    return ClassName.get(extensibleClass.packageName(), extensibleClass.simpleName() + "__ExtensionFactory");
  }

  private ClassName getPoetExtensibleClassExtensibleFactoryClassName() {
    return getPoetExtensibleClassExtensibleFactoryClassName(extensibleClass);
  }

  private String getPackageLevelClassName() {
    return Util.getPackageLevelClassName(constructorElement.getEnclosingElement());
  }

  private String getUniqueClassName() {
    return Util.getUniqueClassName(constructorElement.getEnclosingElement());
  }

  private String getExtensionFactoryClassName() {
    return getPackageLevelClassName() + "__ExtensionFactory";
  }

  private ClassName getPoetExtensionFactoryClassName() {
    return ClassName.get(packageName, getExtensionFactoryClassName());
  }

  @Override
  public void generateCode(Filer filer) {
    TypeSpec.Builder klass = TypeSpec.classBuilder(getExtensionFactoryClassName())
        .addAnnotation(AnnotationSpec.builder(Generated.class)
            .addMember("value", "$S", HanaAnnotationProcessor.class)
            .build())
        .addAnnotation(Singleton.class)
        .addModifiers(Modifier.PUBLIC)
        .addSuperinterface(
            getPoetExtensibleClassExtensibleFactoryClassName());

    MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
        .addAnnotation(Inject.class);
    Preconditions.checkArgument(
        constructorElement.getParameters().size() > 0,
        "The annotated constructor of an extension object must have at least one parameter.");
    Preconditions.checkArgument(
        constructorElement.getParameters().get(0).asType().toString().equals(extensibleClass.toString()),
        "The annotated constructor of an extension object " + getClassName() + " must have type " + extensibleClass);
    for (int i = 1; i < constructorElement.getParameters().size(); i++) {
      VariableElement variable = constructorElement.getParameters().get(i);
      String variableName = variable.getSimpleName().toString();
      TypeName typeName = TypeName.get(variable.asType());
      klass.addField(FieldSpec.builder(typeName, variableName)
          .addModifiers(Modifier.PRIVATE)
          .addModifiers(Modifier.FINAL)
          .build());
      constructor.addParameter(ParameterSpec.builder(typeName, variableName).build());
      constructor.addStatement(String.format("this.%s = %s", variable, variable));
    }
    klass.addMethod(constructor.build());

    VariableElement firstParam = constructorElement.getParameters().get(0);
    MethodSpec.Builder createMethod = MethodSpec.methodBuilder("create")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .addParameter(ParameterSpec.builder(
            TypeName.get(firstParam.asType()),
            firstParam.getSimpleName().toString()).build())
        .returns(extensionClass);
    StringBuilder statement = new StringBuilder();
    statement.append("return new $T(");
    statement.append(firstParam.getSimpleName().toString());
    for (int i = 1; i < constructorElement.getParameters().size(); i++) {
      VariableElement variable = constructorElement.getParameters().get(i);
      statement.append(", ");
      statement.append(variable.getSimpleName().toString());
    }
    statement.append(")");
    createMethod.addStatement(statement.toString(), implClass);
    klass.addMethod(createMethod.build());

    Util.writeJavaFile(filer, packageName, klass.build());
  }

  @Override
  public List<BindingGenerator> getBindingGenerators() {
    return ImmutableList.of(new ExtensionBindingGenerator());
  }

  public class ExtensionBindingGenerator implements BindingGenerator {

    @Override
    public void generate(TypeSpec.Builder moduleClassBuilder) {
      moduleClassBuilder.addMethod(MethodSpec
          .methodBuilder("provide__" + getUniqueClassName() + "__ExtensionFactory")
          .addAnnotation(Binds.class)
          .addAnnotation(IntoMap.class)
          .addAnnotation(AnnotationSpec.builder(ClassKey.class)
              .addMember("value", CodeBlock.builder().add("$T.class", extensionClass).build())
              .build())
          .addModifiers(Modifier.ABSTRACT)
          .addModifiers(Modifier.PUBLIC)
          .returns(getPoetExtensibleClassExtensibleFactoryClassName())
          .addParameter(ParameterSpec.builder(getPoetExtensionFactoryClassName(), "factory").build())
          .build());
    }

    @Override
    public String getBindingLocationPackageName() {
      return Util.getPackageName(constructorElement.getEnclosingElement());
    }
  }
}
