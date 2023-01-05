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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.msgpack.value.ArrayValue;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ListType implements PropertyType {
  public final PropertyType innerType;

  public ListType(PropertyType innerType) {
    Preconditions.checkArgument((innerType instanceof TerminalType)
      || (innerType instanceof WrappedType));
    this.innerType = innerType;
  }


  @Override
  public String getSimpleName() {
    return String.format("List<%s>", innerType.getSimpleName());
  }

  @Override
  public TypeName getRawDataTypeName() {
    return ParameterizedTypeName.get(ClassName.get(List.class), innerType.getRawDataTypeName());
  }

  @Override
  public TypeName getTypeName() {
    return ParameterizedTypeName.get(ClassName.get(List.class), innerType.getRawDataTypeName());
  }

  @Override
  public void addReadableChildCode(CodeBlock.Builder builder, String name, String func) {
    builder.beginControlFlow(String.format("for($T %s : %s)", name + "_", name), innerType.getTypeName());
    innerType.addReadableChildCode(builder, name + "_", func);
    builder.endControlFlow();
  }

  @Override
  public void addImplClassConstructorCode(CodeBlock.Builder builder, String name) {
    builder.addStatement(String.format("this.%s = new $T<>(rawData.%s())", name, name), ArrayList.class);
  }

  @Override
  public void addReadableDeserializeCode(CodeBlock.Builder builder, String name) {
    if (innerType instanceof TerminalType) {
      builder.addStatement(String.format("factory.add%s($T.cast(value, $T.class))", WordUtils.capitalize(name)),
        TypeUtil.class, innerType.getTypeName());
    } else {
      WrappedType wrappedType = (WrappedType) innerType;
      builder.addStatement(String.format(
        "factory.add%s($T.castToWrapped(value, $T.class))",
        WordUtils.capitalize(name)),
        TypeUtil.class, wrappedType.innerType.getTypeName());
    }
  }

  @Override
  public void addImplBuilderMethods(
    TypeSpec.Builder builder, String name, String rawDataBuilderName,
    String selfTypeName) {
    addListMethods(builder, name, rawDataBuilderName, selfTypeName);
    if (innerType instanceof WrappedType) {
      WrappedType wrappedType = (WrappedType) innerType;
      String addMethodName = "add" + StringUtils.capitalize(name);
      builder.addMethod(MethodSpec.methodBuilder(addMethodName)
        .addModifiers(Modifier.PUBLIC)
        .returns(TypeVariableName.get(selfTypeName))
        .addParameter(wrappedType.innerType.getTypeName(), name)
        .addStatement(String.format("%s.%s($T.of(%s))", rawDataBuilderName, addMethodName, name), Direct.class)
        .addStatement(String.format("return (%s) this", selfTypeName))
        .build());
    }
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
    builder.addStatement(String.format("%s.packArrayHeader(%s.size())", packerName, name));
    builder.beginControlFlow(String.format("for($T %s : %s)", name + "_", name), innerType.getTypeName());
    innerType.addBinarySerializationCode(builder, name + "_", tag, packerName, serializerName, true);
    builder.endControlFlow();
  }

  @Override
  public void addBinaryDeserializationCode(
    CodeBlock.Builder builder, String name, int tag, String factoryName, String mapName, String deserializerName) {
    builder.beginControlFlow(String.format("if (%s.containsKey(%d))", mapName, tag));
    builder.addStatement(String.format("$T arrayValue_ = %s.get(%d).asArrayValue()", mapName, tag), ArrayValue.class);
    builder.addStatement("int count_ = arrayValue_.size()");
    builder.beginControlFlow("for (int i=0;i<count_;i++)");
    if (innerType instanceof TerminalType) {
      TerminalType terminalType = (TerminalType) innerType;
      terminalType.addBinaryDeserializeToListCode(builder, name, "arrayValue_", "i", factoryName, deserializerName);
    } else {
      builder.addStatement(
        String.format(
          "%s.add%s($T.cast(%s.deserialize(%s.get(%s).asMapValue()), $T.class))",
          factoryName,
          WordUtils.capitalize(name),
          deserializerName,
          "arrayValue_",
          "i"),
        TypeUtil.class,
        Wrapped.class);
    }
    builder.endControlFlow();
    builder.endControlFlow();
  }

  public void addListMethods(
    TypeSpec.Builder builder, String name, String rawDataBuilderName,
    String selfTypeName) {
    String addMethodName = "add" + StringUtils.capitalize(name);
    builder.addMethod(MethodSpec.methodBuilder(addMethodName)
      .addModifiers(Modifier.PUBLIC)
      .returns(TypeVariableName.get(selfTypeName))
      .addParameter(innerType.getTypeName(), name)
      .addStatement(String.format("%s.%s(%s)", rawDataBuilderName, addMethodName, name))
      .addStatement(String.format("return (%s) this", selfTypeName))
      .build());

    String addAllMethodName = "addAll" + StringUtils.capitalize(name);
    builder.addMethod(MethodSpec.methodBuilder(addAllMethodName)
      .addModifiers(Modifier.PUBLIC)
      .returns(TypeVariableName.get(selfTypeName))
      .addParameter(ParameterizedTypeName.get(ClassName.get(Iterable.class), innerType.getTypeName()), name)
      .addStatement(String.format("%s.%s(%s)", rawDataBuilderName, addAllMethodName, name))
      .addStatement(String.format("return (%s) this", selfTypeName))
      .build());

    String clearMethodName = "clear" + StringUtils.capitalize(name);
    builder.addMethod(MethodSpec.methodBuilder(clearMethodName)
      .addModifiers(Modifier.PUBLIC)
      .returns(TypeVariableName.get(selfTypeName))
      .addStatement(String.format("%s.%s()", rawDataBuilderName, clearMethodName))
      .addStatement(String.format("return (%s) this", selfTypeName))
      .build());
  }
}
