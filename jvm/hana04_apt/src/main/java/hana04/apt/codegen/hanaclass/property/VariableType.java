package hana04.apt.codegen.hanaclass.property;

import com.google.common.base.Preconditions;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import hana04.base.changeprop.Variable;

import java.util.ArrayList;
import java.util.HashMap;

public class VariableType implements PropertyType {
  public final PropertyType innerType;

  public VariableType(PropertyType innerType) {
    Preconditions.checkArgument((innerType instanceof OptionalType)
        || (innerType instanceof TerminalType)
        || (innerType instanceof WrappedType)
        || (innerType instanceof ListType)
        || (innerType instanceof MapType));
    this.innerType = innerType;
  }

  @Override
  public String getSimpleName() {
    return String.format("Variable<%s>", innerType.getSimpleName());
  }

  @Override
  public TypeName getRawDataTypeName() {
    return innerType.getRawDataTypeName();
  }

  @Override
  public TypeName getTypeName() {
    return ParameterizedTypeName.get(ClassName.get(Variable.class), innerType.getRawDataTypeName());
  }

  @Override
  public void addReadableChildCode(CodeBlock.Builder builder, String name, String func) {
    builder.beginControlFlow("");
    builder.addStatement(String.format("$T %s = %s.value()", name + "_", name), innerType.getTypeName());
    innerType.addReadableChildCode(builder, name + "_", func);
    builder.endControlFlow();
  }

  @Override
  public void addImplClassConstructorCode(CodeBlock.Builder builder, String name) {
    builder.beginControlFlow("");
    builder.addStatement(String.format("$T %s = rawData.%s()", name + "_", name), innerType.getTypeName());
    if (innerType instanceof ListType) {
      builder.addStatement(
          String.format("this.%s = new $T<>(new $T<>(rawData.%s()))", name, name),
          Variable.class,
          ArrayList.class);
    } else if (innerType instanceof MapType) {
      builder.addStatement(
          String.format("this.%s = new $T<>(new $T<>(rawData.%s()))", name, name),
          Variable.class,
          HashMap.class);
    } else {
      builder.addStatement(String.format("this.%s = new $T<>(rawData.%s())", name, name), Variable.class);
    }
    builder.endControlFlow();
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
  }

  @Override
  public void addBinarySerializationMapElementCountCode(
      CodeBlock.Builder builder, String name, String counterName) {
    if (innerType instanceof OptionalType) {
      builder.beginControlFlow("");
      builder.addStatement(String.format("$T %s = %s.value()", name + "_", name), innerType.getTypeName());
      innerType.addBinarySerializationMapElementCountCode(builder, name + "_", counterName);
      builder.endControlFlow();
    } else {
      builder.addStatement(String.format("%s += 1", counterName));
    }
  }

  @Override
  public void addBinarySerializationCode(
      CodeBlock.Builder builder, String name, int tag, String packerName, String serializerName,
      boolean tagHasBeenWritten) {
    builder.beginControlFlow("");
    builder.addStatement(String.format("$T %s = %s.value()", name + "_", name), innerType.getTypeName());
    innerType.addBinarySerializationCode(builder, name + "_", tag, packerName, serializerName, tagHasBeenWritten);
    builder.endControlFlow();
  }

  @Override
  public void addBinaryDeserializationCode(
      CodeBlock.Builder builder, String name, int tag, String factoryName, String mapName, String deserializerName) {
    innerType.addBinaryDeserializationCode(builder, name, tag, factoryName, mapName, deserializerName);
  }
}
