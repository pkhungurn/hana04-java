package hana04.apt.codegen;

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
import dagger.Binds;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import hana04.apt.processor.HanaAnnotationProcessor;
import hana04.apt.util.Util;
import hana04.base.extension.FluentBuilderFactory;
import hana04.base.extension.annotation.HanaCustomizedBuilder;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.List;

public class HanaBuilderCodeGenerator implements CodeGenerator {
  private final Element element;
  private final String packageName;
  private final ClassName classToBeBuilt;

  public HanaBuilderCodeGenerator(
      Element element,
      String packageName,
      ClassName classToBeBuilt) {
    this.element = element;
    this.packageName = packageName;
    this.classToBeBuilt = classToBeBuilt;
  }

  private String getUniqueClassName() {
    return Util.getUniqueClassName(element);
  }

  public ClassName getPoetFactoryClassName() {
    return ClassName.get(packageName, Util.getPackageLevelClassName(element) + "__Factory");
  }

  public ClassName getPoetClassName() {
    return ClassName.bestGuess(element.asType().toString());
  }

  @Override
  public void generateCode(Filer filer) {
    TypeSpec.Builder builderFactoryClassBuilder = TypeSpec.classBuilder(getPoetFactoryClassName())
        .addAnnotation(AnnotationSpec.builder(Generated.class)
            .addMember("value", "$S", HanaAnnotationProcessor.class)
            .build())
        .addAnnotation(Singleton.class)
        .addModifiers(Modifier.PUBLIC)
        .addSuperinterface(
            ParameterizedTypeName.get(
                ClassName.get(FluentBuilderFactory.class),
                classToBeBuilt,
                getPoetClassName()));

    checkConstructor();

    TypeName builderProviderTypeName =
        ParameterizedTypeName.get(ClassName.get(Provider.class), getPoetClassName());

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
        .returns(getPoetClassName());

    builderFactoryClassBuilder.addMethod(createMethodBuilder.build());

    JavaFile javaFile = JavaFile.builder(packageName, builderFactoryClassBuilder.build()).build();
    try {
      javaFile.writeTo(filer);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void checkConstructor() {
    boolean foundConstructor = false;
    for (Element enclosedElement : element.getEnclosedElements()) {
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
    return ImmutableList.of(new BuilderFactoryBindingGenerator());
  }

  class BuilderFactoryBindingGenerator implements BindingGenerator {
    @Override
    public void generate(TypeSpec.Builder moduleClassBuilder) {
      moduleClassBuilder.addMethod(MethodSpec
          .methodBuilder("provide__" + getUniqueClassName())
          .addModifiers(Modifier.PUBLIC)
          .addAnnotation(Binds.class)
          .addAnnotation(IntoMap.class)
          .addAnnotation(AnnotationSpec
              .builder(ClassKey.class)
              .addMember("value", CodeBlock.builder().add("$T.class", getPoetClassName()).build())
              .build())
          .addModifiers(Modifier.ABSTRACT)
          .returns(FluentBuilderFactory.class)
          .addParameter(ParameterSpec.builder(getPoetFactoryClassName(), "builderFactory").build())
          .build());

      moduleClassBuilder.addMethod(MethodSpec
          .methodBuilder("provideCustomizedBuilder__" + getUniqueClassName())
          .addModifiers(Modifier.PUBLIC)
          .addAnnotation(Binds.class)
          .addAnnotation(IntoMap.class)
          .addAnnotation(HanaCustomizedBuilder.class)
          .addAnnotation(AnnotationSpec
              .builder(ClassKey.class)
              .addMember("value", CodeBlock.builder().add("$T.class", classToBeBuilt).build())
              .build())
          .addModifiers(Modifier.ABSTRACT)
          .returns(FluentBuilderFactory.class)
          .addParameter(ParameterSpec.builder(getPoetFactoryClassName(), "builderFactory").build())
          .build());
    }

    @Override
    public String getBindingLocationPackageName() {
      return packageName;
    }
  }
}
