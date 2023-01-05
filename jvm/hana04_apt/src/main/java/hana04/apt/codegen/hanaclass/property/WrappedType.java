package hana04.apt.codegen.hanaclass.property;

import com.google.common.base.Preconditions;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import hana04.base.caching.Direct;
import hana04.base.caching.Wrapped;
import hana04.base.util.TypeUtil;

import javax.lang.model.element.Modifier;

public class WrappedType implements PropertyType {
  public final PropertyType innerType;

  public WrappedType(PropertyType innerType) {
    Preconditions.checkArgument(innerType instanceof TerminalType);
    this.innerType = innerType;
  }

  @Override
  public String getSimpleName() {
    return String.format("Wrapped<%s>", innerType.getSimpleName());
  }

  @Override
  public TypeName getRawDataTypeName() {
    return ParameterizedTypeName.get(ClassName.get(Wrapped.class), innerType.getRawDataTypeName());
  }

  @Override
  public TypeName getTypeName() {
    return ParameterizedTypeName.get(ClassName.get(Wrapped.class), innerType.getRawDataTypeName());
  }

  @Override
  public void addReadableChildCode(CodeBlock.Builder builder, String name, String func) {
    builder.addStatement(String.format("list_.add(serializer.serialize(%s, $S))", name), func);
  }

  @Override
  public void addImplClassConstructorCode(CodeBlock.Builder builder, String name) {
    builder.addStatement(String.format("this.%s = rawData.%s()", name, name));
  }

  @Override
  public void addReadableDeserializeCode(CodeBlock.Builder builder, String name) {
    builder.addStatement(String.format("factory.%s($T.castToWrapped(value, $T.class))", name),
      TypeUtil.class, innerType.getTypeName());
  }

  @Override
  public void addImplBuilderMethods(
    TypeSpec.Builder builder, String name, String rawDataBuilderName,
    String selfTypeName) {
    addInnerTypeBuilderMethod(builder, name, rawDataBuilderName, selfTypeName);
    addWrappedBuilderMethod(builder, name, rawDataBuilderName, selfTypeName);
  }

  @Override
  public void addBinarySerializationMapElementCountCode(
    CodeBlock.Builder builder, String name, String counterName) {
    builder.addStatement(String.format("%s += 1", counterName));
  }

  @Override
  public void addBinarySerializationCode(
    CodeBlock.Builder builder, String name, int tag,
    String packerName, String serializerName,
    boolean tagHasBeenWritten) {
    if (!tagHasBeenWritten) {
      builder.addStatement(String.format("%s.packInt(%d)", packerName, tag));
    }
    builder.addStatement(String.format("%s.serialize(%s)", serializerName, name));
  }

  @Override
  public void addBinaryDeserializationCode(
    CodeBlock.Builder builder, String name, int tag, String factoryName, String mapName, String deserializerName) {
    builder.beginControlFlow(String.format("if (%s.containsKey(%d))", mapName, tag));
    builder.addStatement(String.format(
      "%s.%s($T.cast(%s.deserialize(%s.get(%d).asMapValue()), $T.class))",
      factoryName,
      name,
      deserializerName,
      mapName,
      tag),
      TypeUtil.class, Wrapped.class);
    builder.endControlFlow();
  }

  public void addInnerTypeBuilderMethod(
    TypeSpec.Builder builder, String name, String rawDataBuilderName,
    String selfTypeName) {
    builder.addMethod(MethodSpec.methodBuilder(name)
      .addModifiers(Modifier.PUBLIC)
      .returns(TypeVariableName.get(selfTypeName))
      .addParameter(innerType.getTypeName(), name)
      .addStatement(String.format("%s.%s($T.of(%s))", rawDataBuilderName, name, name), Direct.class)
      .addStatement(String.format("return (%s) this", selfTypeName))
      .build());
  }

  public void addWrappedBuilderMethod(
    TypeSpec.Builder builder, String name, String rawDataBuilderName,
    String selfTypeName) {
    builder.addMethod(MethodSpec.methodBuilder(name)
      .addModifiers(Modifier.PUBLIC)
      .returns(TypeVariableName.get(selfTypeName))
      .addParameter(ParameterizedTypeName.get(ClassName.get(Wrapped.class), innerType.getTypeName()), name)
      .addStatement(String.format("%s.%s(%s)", rawDataBuilderName, name, name))
      .addStatement(String.format("return (%s) this", selfTypeName))
      .build());
  }
}
