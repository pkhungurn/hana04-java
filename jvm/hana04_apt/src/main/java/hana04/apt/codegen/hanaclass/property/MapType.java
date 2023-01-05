package hana04.apt.codegen.hanaclass.property;

import com.google.common.base.Preconditions;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;
import hana04.base.caching.Direct;
import hana04.base.util.HanaMapEntry;
import hana04.base.util.TypeUtil;
import org.apache.commons.lang3.StringUtils;
import org.msgpack.value.ArrayValue;

import javax.lang.model.element.Modifier;
import java.util.HashMap;
import java.util.Map;

public class MapType implements PropertyType {
  private final PropertyType keyType;
  private final PropertyType valueType;

  public MapType(PropertyType keyType, PropertyType valueType) {
    this.keyType = keyType;
    this.valueType = valueType;
    Preconditions.checkArgument(keyType instanceof TerminalType);
    Preconditions.checkArgument(valueType instanceof WrappedType || valueType instanceof TerminalType);
  }

  @Override
  public String getSimpleName() {
    return String.format("Map<%s,%s>", keyType.getSimpleName(), valueType.getSimpleName());
  }

  @Override
  public TypeName getRawDataTypeName() {
    return ParameterizedTypeName.get(
        ClassName.get(Map.class),
        keyType.getRawDataTypeName(),
        valueType.getRawDataTypeName());
  }

  @Override
  public TypeName getTypeName() {
    return getRawDataTypeName();
  }

  public TypeName getMapEntryTypeName() {
    return ParameterizedTypeName.get(
        ClassName.get(Map.Entry.class),
        keyType.getRawDataTypeName(),
        valueType.getRawDataTypeName());
  }

  @Override
  public void addReadableChildCode(CodeBlock.Builder builder, String name, String func) {
    String entryName = name + "__entry_";
    builder.beginControlFlow(
        String.format("for($T %s : %s.entrySet())", entryName, name),
        getMapEntryTypeName());
    builder.addStatement(
        String.format("list_.add(serializer.serialize(new $T(%s.getKey(), %s.getValue()), $S))", entryName, entryName),
        HanaMapEntry.class,
        func);
    builder.endControlFlow();
  }

  @Override
  public void addImplClassConstructorCode(CodeBlock.Builder builder, String name) {
    builder.addStatement(String.format("this.%s = new $T<>(rawData.%s())", name, name), HashMap.class);
  }

  @Override
  public void addReadableDeserializeCode(CodeBlock.Builder builder, String name) {
    builder.addStatement("assert value instanceof $T", HanaMapEntry.class);
    String hanaMapEntryName = "hanaMapEntry__";
    builder.addStatement(String.format("$T %s = ($T) value", hanaMapEntryName), HanaMapEntry.class, HanaMapEntry.class);
    if (valueType instanceof TerminalType) {
      builder.addStatement(
          String.format(
              "factory.put%s($T.cast(%s.getKey(), $T.class), $T.cast(%s.getValue(), $T.class))",
              StringUtils.capitalize(name),
              hanaMapEntryName,
              hanaMapEntryName),
          TypeUtil.class, keyType.getTypeName(),
          TypeUtil.class, valueType.getTypeName());
    } else {
      WrappedType wrappedType = (WrappedType) valueType;
      builder.addStatement(
          String.format(
              "factory.put%s($T.cast(value.getKey(), $T.class), $T.castToWrapped(value.getValue(), $T.class))",
              StringUtils.capitalize(name)),
          TypeUtil.class, keyType.getTypeName(),
          TypeUtil.class, wrappedType.innerType.getTypeName());
    }
  }

  @Override
  public void addImplBuilderMethods(TypeSpec.Builder builder,
      String name,
      String rawDataBuilderName,
      String selfTypeName) {
    String keyParamName = name + "Key";
    String valueParamName = name + "Value";

    String putMethodName = "put" + StringUtils.capitalize(name);
    builder.addMethod(MethodSpec.methodBuilder(putMethodName)
        .addModifiers(Modifier.PUBLIC)
        .returns(TypeVariableName.get(selfTypeName))
        .addParameter(keyType.getTypeName(), keyParamName)
        .addParameter(valueType.getTypeName(), valueParamName)
        .addStatement(String.format("%s.%s(%s, %s)", rawDataBuilderName, putMethodName, keyParamName, valueParamName))
        .addStatement(String.format("return (%s) this", selfTypeName))
        .build());

    String putAllMethodName = "putAll" + StringUtils.capitalize(name);
    builder.addMethod(MethodSpec.methodBuilder(putAllMethodName)
        .addModifiers(Modifier.PUBLIC)
        .returns(TypeVariableName.get(selfTypeName))
        .addParameter(
            ParameterizedTypeName.get(
                ClassName.get(Map.class),
                WildcardTypeName.subtypeOf(keyType.getTypeName()),
                WildcardTypeName.subtypeOf(valueType.getTypeName())),
            name)
        .addStatement(String.format("%s.%s(%s)", rawDataBuilderName, putAllMethodName, name))
        .addStatement(String.format("return (%s) this", selfTypeName))
        .build());

    String clearMethodName = "clear" + StringUtils.capitalize(name);
    builder.addMethod(MethodSpec.methodBuilder(clearMethodName)
        .addModifiers(Modifier.PUBLIC)
        .returns(TypeVariableName.get(selfTypeName))
        .addStatement(String.format("%s.%s()", rawDataBuilderName, clearMethodName))
        .addStatement(String.format("return (%s) this", selfTypeName))
        .build());

    if (valueType instanceof WrappedType) {
      WrappedType wrappedType = (WrappedType) valueType;
      String pubMethodName = "put" + StringUtils.capitalize(name);
      builder.addMethod(MethodSpec.methodBuilder(pubMethodName)
          .addModifiers(Modifier.PUBLIC)
          .returns(TypeVariableName.get(selfTypeName))
          .addParameter(keyType.getTypeName(), keyParamName)
          .addParameter(wrappedType.innerType.getTypeName(), valueParamName)
          .addStatement(String.format(
              "%s.%s(%s, $T.of(%s))",
              rawDataBuilderName,
              pubMethodName,
              keyParamName,
              valueParamName), Direct.class)
          .addStatement(String.format("return (%s) this", selfTypeName))
          .build());
    }
  }

  @Override
  public void addBinarySerializationMapElementCountCode(CodeBlock.Builder builder, String name, String counterName) {
    builder.addStatement(String.format("%s += 1", counterName));
  }

  @Override
  public void addBinarySerializationCode(CodeBlock.Builder builder,
      String name,
      int tag,
      String packerName,
      String serializerName,
      boolean tagHasBeenWritten) {
    if (!tagHasBeenWritten) {
      builder.addStatement(String.format("%s.packInt(%d)", packerName, tag));
    }
    builder.addStatement(String.format("%s.packArrayHeader(%s.size())", packerName, name));
    String entryName = name + "Entry_";
    builder.beginControlFlow(
        String.format("for($T %s : %s.entrySet())", entryName, name),
        getMapEntryTypeName());
    builder.addStatement(
        String.format("%s.serialize(new $T(%s.getKey(), %s.getValue()))", serializerName, entryName, entryName),
        HanaMapEntry.class);
    builder.endControlFlow();
  }

  @Override
  public void addBinaryDeserializationCode(CodeBlock.Builder builder,
      String name,
      int tag,
      String factoryName,
      String mapName,
      String deserializerName) {
    builder.beginControlFlow(String.format("if (%s.containsKey(%d))", mapName, tag));
    builder.addStatement(String.format("$T arrayValue_ = %s.get(%d).asArrayValue()", mapName, tag), ArrayValue.class);
    builder.addStatement("int count_ = arrayValue_.size()");
    builder.beginControlFlow("for (int i=0;i<count_;i++)");
    builder.addStatement(
        String.format(
            "$T hanaMapEntry_ = $T.cast(%s.deserialize(arrayValue_.get(i).asMapValue()), $T.class)",
            deserializerName),
        HanaMapEntry.class,
        TypeUtil.class,
        HanaMapEntry.class);
    builder.addStatement(
        String.format(
            "%s.put%s(($T) hanaMapEntry_.getKey(), ($T) hanaMapEntry_.getValue())",
            factoryName,
            StringUtils.capitalize(name)),
        keyType.getTypeName(),
        valueType.getTypeName());
    builder.endControlFlow();
    builder.endControlFlow();
  }
}
