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

public class HanaProvidesExtensionCodeGenerator implements CodeGenerator {
  private final ClassName extensibleClass;
  private final ClassName extensionClass;
  private final ExecutableElement methodElement;
  private final String packageName;

  public HanaProvidesExtensionCodeGenerator(ExecutableElement methodElement,
                                            String packageName,
                                            ClassName extensibleClass,
                                            ClassName extensionClass) {
    this.extensibleClass = extensibleClass;
    this.extensionClass = extensionClass;
    this.methodElement = methodElement;
    this.packageName = packageName;
  }

  private ClassName getPoetExtensibleClassExtensibleFactoryClassName() {
    return HanaExtensionCodeGenerator.getPoetExtensibleClassExtensibleFactoryClassName(extensibleClass);
  }

  private ClassName getPoetEnclosingClassName() {
    ClassName className;
    try {
      className = ClassName.bestGuess(methodElement.getEnclosingElement().asType().toString());
    } catch (Exception e) {
      throw new RuntimeException(methodElement.getEnclosingElement().asType().toString());
    }
    return className;
  }

  private String getPackageLevelClassName() {
    return Util.getPackageLevelClassName(methodElement.getEnclosingElement());
  }

  private String getUniqueClassName() {
    return Util.getUniqueClassName(methodElement.getEnclosingElement());
  }

  private String getExtensionFactoryClassName() {
    return getPackageLevelClassName() + "__" + methodElement.getSimpleName() + "__ExtensionFactory";
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
    Preconditions.checkArgument(methodElement.getParameters().size() > 0,
      "The method that provides extension object must have at least one parameter.");
    for (int i = 1; i < methodElement.getParameters().size(); i++) {
      VariableElement variable = methodElement.getParameters().get(i);
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

    VariableElement firstParam = methodElement.getParameters().get(0);
    MethodSpec.Builder createMethod = MethodSpec.methodBuilder("create")
      .addAnnotation(Override.class)
      .addModifiers(Modifier.PUBLIC)
      .addParameter(ParameterSpec.builder(TypeName.get(firstParam.asType()),
        firstParam.getSimpleName().toString()).build())
      .returns(extensionClass);

    StringBuilder statement = new StringBuilder();
    statement.append("return $T.");
    statement.append(methodElement.getSimpleName());
    statement.append("(");
    statement.append(firstParam.getSimpleName().toString());
    for (int i = 1; i < methodElement.getParameters().size(); i++) {
      VariableElement variable = methodElement.getParameters().get(i);
      statement.append(", ");
      statement.append(variable.getSimpleName().toString());
    }
    statement.append(")");
    createMethod.addStatement(statement.toString(), getPoetEnclosingClassName());
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
        .methodBuilder("provide__" + getUniqueClassName() + methodElement.getSimpleName() + "__ExtensionFactory")
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
      return Util.getPackageName(methodElement.getEnclosingElement());
    }
  }

}
