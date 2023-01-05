package hana04.apt.codegen.hanaclass.property;

import com.google.common.base.Preconditions;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import javax.lang.model.element.Modifier;
import java.util.Optional;

public class OptionalType implements PropertyType {
  public final PropertyType innerType;

  public OptionalType(PropertyType innerType) {
    Preconditions.checkArgument((innerType instanceof TerminalType)
      || (innerType instanceof WrappedType));
    this.innerType = innerType;
  }

  @Override
  public String getSimpleName() {
    return String.format("Optional<%s>", innerType.getSimpleName());
  }

  @Override
  public TypeName getRawDataTypeName() {
    return ParameterizedTypeName.get(ClassName.get(Optional.class), innerType.getRawDataTypeName());
  }

  @Override
  public TypeName getTypeName() {
    return ParameterizedTypeName.get(ClassName.get(Optional.class), innerType.getRawDataTypeName());
  }

  @Override
  public void addReadableChildCode(CodeBlock.Builder builder, String name, String func) {
    builder.beginControlFlow(String.format("if (%s.isPresent())", name));
    builder.addStatement(String.format("list_.add(serializer.serialize(%s.get(), $S))", name), func);
    builder.endControlFlow();
  }

  @Override
  public void addImplClassConstructorCode(CodeBlock.Builder builder, String name) {
    builder.addStatement(String.format("this.%s = rawData.%s()", name, name));
  }

  @Override
  public void addReadableDeserializeCode(CodeBlock.Builder builder, String name) {
    innerType.addReadableDeserializeCode(builder, name);
  }

  @Override
  public void addImplBuilderMethods(
    TypeSpec.Builder builder, String name, String rawDataBuilderName,
    String selfTypeName) {
    innerType.addImplBuilderMethods(builder, name, rawDataBuilderName, selfTypeName);
    addOptionalBuilderMethod(builder, name, rawDataBuilderName, selfTypeName);
  }

  @Override
  public void addBinarySerializationMapElementCountCode(
    CodeBlock.Builder builder, String name, String counterName) {
    builder.beginControlFlow(String.format("if (%s.isPresent())", name));
    builder.addStatement(String.format("%s += 1", counterName));
    builder.endControlFlow();
  }

  @Override
  public void addBinarySerializationCode(
    CodeBlock.Builder builder, String name, int tag,
    String packerName, String serializerName,
    boolean tagHasBeenWritten) {
    builder.beginControlFlow(String.format("if (%s.isPresent())", name));
    builder.addStatement(String.format("$T %s = %s.get()", name + "_", name), innerType.getTypeName());
    innerType.addBinarySerializationCode(builder, name + "_", tag, packerName, serializerName, tagHasBeenWritten);
    builder.endControlFlow();
  }

  @Override
  public void addBinaryDeserializationCode(
    CodeBlock.Builder builder, String name, int tag, String factoryName, String mapName, String deserializerName) {
    innerType.addBinaryDeserializationCode(builder, name, tag, factoryName, mapName, deserializerName);
  }

  public void addOptionalBuilderMethod(
    TypeSpec.Builder builder, String name, String rawDataBuilderName,
    String selfTypeName) {
    builder.addMethod(MethodSpec.methodBuilder(name)
      .addModifiers(Modifier.PUBLIC)
      .returns(TypeVariableName.get(selfTypeName))
      .addParameter(ParameterizedTypeName.get(ClassName.get(Optional.class), innerType.getTypeName()), name)
      .addStatement(String.format("%s.%s(%s)", rawDataBuilderName, name, name))
      .addStatement(String.format("return (%s) this", selfTypeName))
      .build());
  }
}
