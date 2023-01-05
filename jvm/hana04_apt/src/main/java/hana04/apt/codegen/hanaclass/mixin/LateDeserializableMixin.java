package hana04.apt.codegen.hanaclass.mixin;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import hana04.apt.codegen.BindingGenerator;
import hana04.apt.codegen.hanaclass.HanaSerializableInterfaceCodeGenerator;
import hana04.apt.codegen.hanaclass.property.PropertySpec;
import hana04.apt.codegen.hanaclass.property.VariableType;
import hana04.base.extension.validator.Validator;
import hana04.base.serialize.binary.BinaryDeserializer;
import hana04.base.serialize.readable.ReadableDeserializer;
import org.msgpack.value.MapValue;
import org.msgpack.value.Value;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Map;

public class LateDeserializableMixin implements ClassMixin {
  private final HanaSerializableInterfaceCodeGenerator classSpec;

  public LateDeserializableMixin(HanaSerializableInterfaceCodeGenerator classSpec) {
    this.classSpec = classSpec;
    classSpec.propertySpecs.forEach(propertySpec -> {
      Preconditions.checkArgument(
          propertySpec.type instanceof VariableType,
          "Property " + propertySpec.name + " is not a Variable.");
    });
  }

  @Override
  public void generateCode(Filer filer) {
    // NO-OP
  }

  @Override
  public List<BindingGenerator> getBindingGenerators() {
    return ImmutableList.of();
  }

  @Override
  public void modifyImplClassBuilder(TypeSpec.Builder implClassBuilder) {
    addReadableDeserializeMethod(implClassBuilder);
    addBinaryDeserializeMethod(implClassBuilder);
  }

  private void addReadableDeserializeMethod(TypeSpec.Builder implClassBuilder) {
    MethodSpec.Builder builder = MethodSpec.methodBuilder("deserialize")
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Override.class)
        .addParameter(ParameterSpec.builder(Map.class, "json").build())
        .addParameter(ParameterSpec.builder(ReadableDeserializer.class, "deserializer").build());

    builder.addStatement(
        "$T.Builder factory = new $T.Builder()",
        classSpec.getPoetRawDataClassName(),
        classSpec.getPoetRawDataClassName());
    builder.addCode(SerializableMixin.createReadableDeserializationCodeBlock(classSpec));
    builder.addStatement("$T rawData = factory.build()", classSpec.getPoetRawDataClassName());
    builder.addCode(createSetPropertiesAndValidateCodeblock());

    implClassBuilder.addMethod(builder.build());
  }

  private CodeBlock createSetPropertiesAndValidateCodeblock() {
    CodeBlock.Builder builder = CodeBlock.builder();
    for (PropertySpec propertySpec : classSpec.propertySpecs) {
      builder.addStatement(String.format("this.%s.set(rawData.%s())", propertySpec.name, propertySpec.name));
    }
    builder.beginControlFlow("if (supportsExtension($T.class))", Validator.class)
        .addStatement("getExtension($T.class).validate()", Validator.class)
        .endControlFlow();
    return builder.build();
  }

  private static final String MAP_VALUE_VAR_NAME = "mapValue__";
  private static final String DESERIALIZER_VAR_NAME = "deserializer__";
  private static final String FACTORY_VAR_NAME = "factory__";
  private static final String RAW_DATA_VAR_NAME = "rawData";
  private static final String MAP_VAR_NAME = "map__";

  private void addBinaryDeserializeMethod(TypeSpec.Builder implClassBuilder) {
    MethodSpec.Builder builder = MethodSpec.methodBuilder("deserialize")
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Override.class)
        .addParameter(ParameterSpec.builder(MapValue.class, MAP_VALUE_VAR_NAME).build())
        .addParameter(ParameterSpec.builder(BinaryDeserializer.class, DESERIALIZER_VAR_NAME).build());

    builder
        .addStatement(
            String.format("$T<$T, $T> %s = $T.convertToIntMap(%s)", MAP_VAR_NAME, MAP_VALUE_VAR_NAME),
            Map.class,
            Integer.class,
            Value.class,
            BinaryDeserializer.class)
        .addStatement(
            String.format("$T.Builder %s = new $T.Builder()", FACTORY_VAR_NAME),
            classSpec.getPoetRawDataClassName(),
            classSpec.getPoetRawDataClassName())
        .addCode(SerializableMixin.createBinaryDeserializationCodeBlock(
            classSpec,
            FACTORY_VAR_NAME,
            MAP_VAR_NAME,
            DESERIALIZER_VAR_NAME))
        .addStatement(
            String.format("$T %s = %s.build()", RAW_DATA_VAR_NAME, FACTORY_VAR_NAME),
            classSpec.getPoetRawDataClassName())
        .addCode(createSetPropertiesAndValidateCodeblock());

    implClassBuilder.addMethod(builder.build());
  }

}
