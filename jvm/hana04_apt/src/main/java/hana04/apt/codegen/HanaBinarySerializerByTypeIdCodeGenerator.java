package hana04.apt.codegen;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import dagger.Binds;
import dagger.multibindings.IntKey;
import dagger.multibindings.IntoMap;
import hana04.apt.util.Util;
import hana04.base.serialize.binary.TypeBinarySerializer;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.util.List;

public class HanaBinarySerializerByTypeIdCodeGenerator implements CodeGenerator {
  private final Element serializer;
  private final int typeId;

  public HanaBinarySerializerByTypeIdCodeGenerator(Element serializer, int typeId) {
    this.serializer = serializer;
    this.typeId = typeId;
  }

  @Override
  public void generateCode(Filer filer) {
    // NO-OP
  }

  @Override
  public List<BindingGenerator> getBindingGenerators() {
    return ImmutableList.of(new Generator());
  }

  private String getPackageLevelClassName() {
    return Util.getPackageLevelClassName(serializer);
  }

  private String getUniqueClassName() {
    return Util.getUniqueClassName(serializer);
  }

  class Generator implements BindingGenerator {

    @Override
    public void generate(TypeSpec.Builder moduleClassBuilder) {
      moduleClassBuilder.addMethod(MethodSpec
        .methodBuilder("provide__" + getUniqueClassName() + "__BinarySerializerByTypeName")
        .addAnnotation(Binds.class)
        .addAnnotation(IntoMap.class)
        .addAnnotation(AnnotationSpec.builder(IntKey.class)
          .addMember("value", CodeBlock.builder().add(String.format("%d", typeId)).build())
          .build())
        .addModifiers(Modifier.PUBLIC)
        .addModifiers(Modifier.ABSTRACT)
        .returns(ParameterizedTypeName.get(ClassName.get(TypeBinarySerializer.class), TypeVariableName.get("?")))
        .addParameter(ParameterSpec.builder(ClassName.get(serializer.asType()), "serializer").build())
        .build());
    }

    @Override
    public String getBindingLocationPackageName() {
      return Util.getPackageName(serializer);
    }
  }
}
