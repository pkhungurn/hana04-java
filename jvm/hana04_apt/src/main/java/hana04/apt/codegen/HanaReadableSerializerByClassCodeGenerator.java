package hana04.apt.codegen;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import dagger.Binds;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import hana04.apt.util.Util;
import hana04.base.serialize.readable.TypeReadableSerializer;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.util.List;

public class HanaReadableSerializerByClassCodeGenerator implements CodeGenerator {
  private final Element serializer;
  private final TypeName serialized;

  public HanaReadableSerializerByClassCodeGenerator(Element serializer, TypeName serialized) {
    this.serializer = serializer;
    this.serialized = serialized;
    Util.checkInjectedConstructor(serializer,
      "A readable serializer implementation must have a constructor annotated with @Inject. "
        + "(" + serializer.asType().toString() + ")");
  }

  @Override
  public void generateCode(Filer filer) {
    // NOP
  }

  private String getUniqueClassName() {
    return Util.getUniqueClassName(serializer);
  }

  @Override
  public List<BindingGenerator> getBindingGenerators() {
    return ImmutableList.of(new Generator());
  }

  class Generator implements BindingGenerator {

    @Override
    public void generate(TypeSpec.Builder moduleClassBuilder) {
      moduleClassBuilder.addMethod(MethodSpec
        .methodBuilder("provide__" + getUniqueClassName() + "__ReadableSerializerByClass")
        .addAnnotation(Binds.class)
        .addAnnotation(IntoMap.class)
        .addAnnotation(AnnotationSpec.builder(ClassKey.class)
          .addMember("value", CodeBlock.builder().add("$T.class", serialized).build())
          .build())
        .addModifiers(Modifier.PUBLIC)
        .addModifiers(Modifier.ABSTRACT)
        .returns(ParameterizedTypeName.get(ClassName.get(TypeReadableSerializer.class), TypeVariableName.get("?")))
        .addParameter(ParameterSpec.builder(ClassName.get(serializer.asType()), "serializer").build())
        .build());
    }

    @Override
    public String getBindingLocationPackageName() {
      return Util.getPackageName(serializer);
    }
  }

}
