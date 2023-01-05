package hana04.apt.codegen.hanaclass.mixin;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import dagger.Binds;
import dagger.Provides;
import dagger.multibindings.IntKey;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import hana04.apt.codegen.BindingGenerator;
import hana04.apt.codegen.hanaclass.HanaConcreteClassCodeGenerator;
import hana04.apt.codegen.hanaclass.HanaSerializableInterfaceCodeGenerator;
import hana04.apt.codegen.hanaclass.property.PropertySpec;
import hana04.apt.processor.HanaAnnotationProcessor;
import hana04.apt.util.Util;
import hana04.base.extension.FluentBuilderFactory;
import hana04.base.extension.HanaExtensible;
import hana04.base.extension.annotation.HanaCustomizedBuilder;
import hana04.base.serialize.ExtensionSerialization;
import hana04.base.serialize.HanaSerializable;
import hana04.base.serialize.binary.BinaryDeserializer;
import hana04.base.serialize.binary.BinarySerializer;
import hana04.base.serialize.binary.TypeBinaryDeserializer;
import hana04.base.serialize.binary.TypeBinarySerializer;
import hana04.base.serialize.readable.JsonParsingUtil;
import hana04.base.serialize.readable.ReadableDeserializer;
import hana04.base.serialize.readable.ReadableSerializer;
import hana04.base.serialize.readable.TypeReadableDeserializer;
import hana04.base.serialize.readable.TypeReadableSerializer;
import org.msgpack.core.MessagePacker;
import org.msgpack.value.Value;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SerializableMixin implements ClassMixin {
  private final HanaSerializableInterfaceCodeGenerator classSpec;

  public SerializableMixin(HanaSerializableInterfaceCodeGenerator classSpec) {
    this.classSpec = classSpec;
  }

  public void generateCode(Filer filer) {
    generateReadableDeserializerClassFile(filer);
    generateBinaryDeserializerClassFile(filer);
  }

  @Override
  public List<BindingGenerator> getBindingGenerators() {
    return ImmutableList.of(
        new ReadableDeserializerBindingGenerator(),
        new ReadableSerializerBindingGenerator(),
        new BinaryDeserializerBindingGenerator(),
        new BinarySerializerBindingGenerator());
  }

  private String getClassName() {
    return classSpec.getClassName();
  }

  private String getUniqueClassName() {
    return classSpec.getUniqueClassName();
  }

  @Override
  public void addPreBuildingImplClassConstructorCode(CodeBlock.Builder codeBuilder) {
    // NO-OP
  }

  @Override
  public void modifyImplClassBuilder(TypeSpec.Builder implClassBuilder) {
    addSerializableMethods(implClassBuilder);
  }

  private String getPrimaryTypeName() {
    return classSpec.getSerializedTypeName();
  }

  private List<String> getTypeNames() {
    return classSpec.getTypeNames();
  }

  private void addSerializableMethods(TypeSpec.Builder implClassBuilder) {
    implClassBuilder.addMethod(MethodSpec.methodBuilder("getSerializedTypeId")
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Override.class)
        .returns(TypeName.INT)
        .addStatement("return " + classSpec.getTypeId())
        .build());
    implClassBuilder.addMethod(MethodSpec.methodBuilder("getSerializedTypeName")
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Override.class)
        .returns(String.class)
        .addStatement("return $S", getPrimaryTypeName())
        .build());
    addBinarySerializeContentMethod(implClassBuilder);
    addGetReadableChildrenList(implClassBuilder);
  }

  private static final String MAP_ELEMENT_COUNTER_VAR_NAME = "mapElementCount__";
  private static final String PACKER_VAR_NAME = "packer__";
  private static final String SERIALIZER_VAR_NAME = "serializer__";

  private void addBinarySerializeContentMethod(TypeSpec.Builder implClassBuilder) {
    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("binarySerializeContent")
        .addAnnotation(Override.class)
        .addParameter(ParameterSpec.builder(MessagePacker.class, PACKER_VAR_NAME).build())
        .addParameter(ParameterSpec.builder(BinarySerializer.class, SERIALIZER_VAR_NAME).build())
        .addModifiers(Modifier.PUBLIC);

    CodeBlock.Builder codeBuilder = CodeBlock.builder();
    {
      codeBuilder.addStatement(String.format("int %s = 0", MAP_ELEMENT_COUNTER_VAR_NAME));
      for (PropertySpec propertySpec : classSpec.propertySpecs) {
        codeBuilder.add(String.format("// %s\n", propertySpec.name));
        propertySpec.addBinarySerializationMapElementCountCode(codeBuilder, MAP_ELEMENT_COUNTER_VAR_NAME);
      }
      codeBuilder.add("// Extensions\n");
      codeBuilder.beginControlFlow("if (this instanceof $T)", HanaExtensible.class);
      codeBuilder.addStatement(String.format("%s += 1", MAP_ELEMENT_COUNTER_VAR_NAME));
      codeBuilder.endControlFlow();
    }
    {
      codeBuilder.beginControlFlow("try");
      codeBuilder.addStatement(String.format("%s.packMapHeader(%s)", PACKER_VAR_NAME, MAP_ELEMENT_COUNTER_VAR_NAME));
      for (PropertySpec propertySpec : classSpec.propertySpecs) {
        codeBuilder.add(String.format("// %s\n", propertySpec.name));
        propertySpec.addBinarySerializationCode(codeBuilder, PACKER_VAR_NAME, SERIALIZER_VAR_NAME);
      }
      codeBuilder.beginControlFlow("if (this instanceof $T)", HanaExtensible.class);
      codeBuilder.addStatement(String.format("%s.packInt($T.EXTENSION_TAG)", PACKER_VAR_NAME), BinarySerializer.class);
      codeBuilder.addStatement(
          String.format(
              "$T.serializeExtensions(($T)this, %s, %s)",
              PACKER_VAR_NAME,
              SERIALIZER_VAR_NAME),
          ExtensionSerialization.class,
          HanaExtensible.class);
      codeBuilder.endControlFlow();
      codeBuilder.endControlFlow();
      codeBuilder.beginControlFlow("catch ($T e)", Exception.class);
      codeBuilder.addStatement("throw new $T(e)", RuntimeException.class);
      codeBuilder.endControlFlow();
    }
    methodBuilder.addCode(codeBuilder.build());

    implClassBuilder.addMethod(methodBuilder.build());
  }

  private void addGetReadableChildrenList(TypeSpec.Builder implClassBuilder) {
    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getReadableChildrenList")
        .addAnnotation(Override.class)
        .addParameter(ParameterSpec.builder(ReadableSerializer.class, "serializer").build())
        .returns(List.class)
        .addModifiers(Modifier.PUBLIC);

    CodeBlock.Builder codeBuilder = CodeBlock.builder();
    codeBuilder.addStatement("$T list_ = new $T()", List.class, ArrayList.class);
    for (PropertySpec propertySpec : classSpec.propertySpecs) {
      codeBuilder.add(String.format("// %s\n", propertySpec.name));
      propertySpec.addReadableChildrenCode(codeBuilder);
    }
    codeBuilder.addStatement("return list_");
    methodBuilder.addCode(codeBuilder.build());

    implClassBuilder.addMethod(methodBuilder.build());
  }

  private String getPackageName() {
    return classSpec.packageName;
  }

  private ClassName getPoetClassName() {
    return classSpec.getPoetClassName();
  }

  private String getReadableDeserializerClassName() {
    return getClassName() + "__ReadableDeserializer";
  }

  private ClassName getPoetReadableDeserializerClassName() {
    return ClassName.get(getPackageName(), getReadableDeserializerClassName());
  }

  private TypeName getCustomizedBuilderFactoryMapTypeName() {
    return ParameterizedTypeName.get(
        ClassName.get(Map.class),
        ParameterizedTypeName.get(ClassName.get(Class.class), TypeVariableName.get("?")),
        ClassName.get(FluentBuilderFactory.class));
  }

  private void generateReadableDeserializerClassFile(Filer filer) {
    TypeSpec.Builder readableDeserializerClassBuilder = TypeSpec.classBuilder(getReadableDeserializerClassName())
        .addAnnotation(AnnotationSpec.builder(Generated.class)
            .addMember("value", "$S", HanaAnnotationProcessor.class)
            .build())
        .addAnnotation(Singleton.class)
        .addModifiers(Modifier.PUBLIC)
        .addSuperinterface(ParameterizedTypeName.get(
            ClassName.get(TypeReadableDeserializer.class),
            getPoetClassName()));

    String defaultBuilderFactory = "defaultBuilderFactory";
    readableDeserializerClassBuilder.addField(
        FieldSpec.builder(classSpec.getPoetBuilderFactoryClassName(), defaultBuilderFactory)
            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
            .build());
    TypeName customizedBuilderFactoryMapTypeName = getCustomizedBuilderFactoryMapTypeName();
    String customizedBuilderFactoryMap = "customizedBuilderFactoryMap";
    readableDeserializerClassBuilder.addField(
        FieldSpec
            .builder(
                customizedBuilderFactoryMapTypeName,
                customizedBuilderFactoryMap)
            .addModifiers(Modifier.PRIVATE)
            .build());

    readableDeserializerClassBuilder.addMethod(MethodSpec.constructorBuilder()
        .addAnnotation(Inject.class)
        .addParameter(ParameterSpec.builder(classSpec.getPoetBuilderFactoryClassName(), defaultBuilderFactory).build())
        .addParameter(ParameterSpec
            .builder(
                customizedBuilderFactoryMapTypeName.annotated(AnnotationSpec
                    .builder(HanaCustomizedBuilder.class)
                    .build()),
                customizedBuilderFactoryMap)
            .build())
        .addModifiers(Modifier.PUBLIC)
        .addStatement(String.format("this.%s = %s", defaultBuilderFactory, defaultBuilderFactory))
        .addStatement(String.format("this.%s = %s", customizedBuilderFactoryMap, customizedBuilderFactoryMap))
        .build());

    MethodSpec.Builder deserializerBuilder = MethodSpec.methodBuilder("deserialize")
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Override.class)
        .addParameter(ParameterSpec.builder(Map.class, "json").build())
        .addParameter(ParameterSpec.builder(ReadableDeserializer.class, "deserializer").build())
        .returns(getPoetClassName());

    deserializerBuilder.addStatement("$T factory", classSpec.getPoetImplBuilderClassName());
    deserializerBuilder.beginControlFlow(
        String.format("if (%s.containsKey($T.class))", customizedBuilderFactoryMap),
        getPoetClassName());
    deserializerBuilder.addStatement(
        String.format("factory = ($T) %s.get($T.class).create()", customizedBuilderFactoryMap),
        classSpec.getPoetImplBuilderClassName(),
        getPoetClassName());
    deserializerBuilder.endControlFlow();
    deserializerBuilder.beginControlFlow("else");
    deserializerBuilder.addStatement(
        String.format("factory = %s.create()", defaultBuilderFactory));
    deserializerBuilder.endControlFlow();

    deserializerBuilder.addCode(createReadableDeserializationCodeBlock(classSpec));
    deserializerBuilder.addStatement("$T instance = factory.build()", getPoetClassName());
    deserializerBuilder.addCode(CodeBlock.builder()
        .beginControlFlow("if (instance instanceof $T)", HanaExtensible.class)
        .addStatement("deserializer.deserializeExtensions(json, ($T)instance)", HanaExtensible.class)
        .endControlFlow()
        .build());
    deserializerBuilder.addStatement("return instance");

    readableDeserializerClassBuilder.addMethod(MethodSpec.methodBuilder("getSerializedClass")
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Override.class)
        .returns(ParameterizedTypeName.get(ClassName.get(Class.class), getPoetClassName()))
        .addStatement("return $T.class", getPoetClassName())
        .build());

    readableDeserializerClassBuilder.addMethod(deserializerBuilder.build());

    Util.writeJavaFile(filer, getPackageName(), readableDeserializerClassBuilder.build());
  }

  static CodeBlock createReadableDeserializationCodeBlock(HanaConcreteClassCodeGenerator classSpec) {
    CodeBlock.Builder codeBuilder = CodeBlock.builder();
    codeBuilder.beginControlFlow(
        "$T.forEachChildren(json, deserializer, (func, value) ->",
        JsonParsingUtil.class);
    codeBuilder.beginControlFlow("switch(func)");
    for (PropertySpec propertySpec : classSpec.propertySpecs) {
      codeBuilder.add("case $S:\n", propertySpec.name);
      propertySpec.addReadableDeserializeCode(codeBuilder);
      codeBuilder.addStatement("break");
    }
    codeBuilder.endControlFlow();
    codeBuilder.endControlFlow(")");
    return codeBuilder.build();
  }

  private String getBinaryDeserializerClassName() {
    return getClassName() + "__BinaryDeserializer";
  }

  private ClassName getPoetBinaryDeserializerClassName() {
    return ClassName.get(getPackageName(), getBinaryDeserializerClassName());
  }

  private static final String VALUE_VAR_NAME = "value__";
  private static final String DESERIALIZER_VAR_NAME = "deserializer__";
  private static final String DEFAULT_BUILDER_FACTORY_VAR_NAME = "defaultBuilderFactory__";
  private static final String FACTORY_VAR_NAME = "factory__";
  private static final String INSTANCE_VAR_NAME = "instance__";
  private static final String MAP_VAR_NAME = "map__";
  private static final String CUSTOMIZED_BUILDER_FACTORY_MAP_VAR_NAME = "customizedBuilderFactoryMap__";

  private void generateBinaryDeserializerClassFile(Filer filer) {
    TypeSpec.Builder binaryDeserializerClassBuilder = TypeSpec.classBuilder(getBinaryDeserializerClassName())
        .addAnnotation(AnnotationSpec.builder(Generated.class)
            .addMember("value", "$S", HanaAnnotationProcessor.class)
            .build())
        .addAnnotation(Singleton.class)
        .addModifiers(Modifier.PUBLIC)
        .addSuperinterface(ParameterizedTypeName.get(ClassName.get(TypeBinaryDeserializer.class), getPoetClassName()));

    binaryDeserializerClassBuilder.addField(FieldSpec.builder(
            classSpec.getPoetBuilderFactoryClassName(),
            DEFAULT_BUILDER_FACTORY_VAR_NAME)
        .addModifiers(Modifier.PRIVATE).build());
    TypeName customizedBuilderFactoryMapTypeName = getCustomizedBuilderFactoryMapTypeName();
    binaryDeserializerClassBuilder.addField(
        FieldSpec
            .builder(
                customizedBuilderFactoryMapTypeName,
                CUSTOMIZED_BUILDER_FACTORY_MAP_VAR_NAME)
            .addModifiers(Modifier.PRIVATE)
            .build());

    binaryDeserializerClassBuilder.addMethod(MethodSpec.constructorBuilder()
        .addAnnotation(Inject.class)
        .addModifiers(Modifier.PUBLIC)
        .addParameter(ParameterSpec
            .builder(classSpec.getPoetBuilderFactoryClassName(), DEFAULT_BUILDER_FACTORY_VAR_NAME)
            .build())
        .addParameter(ParameterSpec
            .builder(
                customizedBuilderFactoryMapTypeName.annotated(AnnotationSpec
                    .builder(HanaCustomizedBuilder.class)
                    .build()),
                CUSTOMIZED_BUILDER_FACTORY_MAP_VAR_NAME)
            .build())
        .addStatement(String.format("this.%s = %s", DEFAULT_BUILDER_FACTORY_VAR_NAME, DEFAULT_BUILDER_FACTORY_VAR_NAME))
        .addStatement(String.format(
            "this.%s = %s",
            CUSTOMIZED_BUILDER_FACTORY_MAP_VAR_NAME,
            CUSTOMIZED_BUILDER_FACTORY_MAP_VAR_NAME))
        .build());

    MethodSpec.Builder deserializeBuilder = MethodSpec.methodBuilder("deserialize")
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Override.class)
        .returns(getPoetClassName())
        .addParameter(ParameterSpec.builder(Value.class, VALUE_VAR_NAME).build())
        .addParameter(ParameterSpec.builder(BinaryDeserializer.class, DESERIALIZER_VAR_NAME).build())
        .addStatement(
            String.format("$T<$T, $T> %s = $T.convertToIntMap(%s.asMapValue())", MAP_VAR_NAME, VALUE_VAR_NAME),
            Map.class,
            Integer.class,
            Value.class,
            BinaryDeserializer.class)
        .addStatement(String.format("$T %s", FACTORY_VAR_NAME), classSpec.getPoetImplBuilderClassName())
        .beginControlFlow(
            String.format("if (%s.containsKey($T.class))", CUSTOMIZED_BUILDER_FACTORY_MAP_VAR_NAME),
            getPoetClassName())
        .addStatement(
            String.format(
                "%s = ($T) %s.get($T.class).create()",
                FACTORY_VAR_NAME,
                CUSTOMIZED_BUILDER_FACTORY_MAP_VAR_NAME),
            classSpec.getPoetImplBuilderClassName(),
            getPoetClassName())
        .endControlFlow()
        .beginControlFlow("else")
        .addStatement(
            String.format("%s = %s.create()", FACTORY_VAR_NAME, DEFAULT_BUILDER_FACTORY_VAR_NAME))
        .endControlFlow()
        .addCode(createBinaryDeserializationCodeBlock(classSpec, FACTORY_VAR_NAME, MAP_VAR_NAME, DESERIALIZER_VAR_NAME))
        .addStatement(String.format("$T %s = %s.build()", INSTANCE_VAR_NAME, FACTORY_VAR_NAME), getPoetClassName())
        .addCode(CodeBlock.builder()
            .beginControlFlow(
                String.format(
                    "if (%s instanceof $T && %s.containsKey($T.EXTENSION_TAG))",
                    INSTANCE_VAR_NAME,
                    MAP_VAR_NAME),
                HanaExtensible.class,
                BinarySerializer.class)
            .addStatement(
                String.format(
                    "%s.deserializeExtensions(%s.get($T.EXTENSION_TAG).asArrayValue(), ($T)%s)",
                    DESERIALIZER_VAR_NAME,
                    MAP_VAR_NAME,
                    INSTANCE_VAR_NAME),
                BinarySerializer.class,
                HanaExtensible.class)
            .endControlFlow()
            .build())
        .addStatement(String.format("return %s", INSTANCE_VAR_NAME));
    binaryDeserializerClassBuilder.addMethod(deserializeBuilder.build());

    binaryDeserializerClassBuilder.addMethod(MethodSpec.methodBuilder("getSerializedClass")
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Override.class)
        .returns(ParameterizedTypeName.get(ClassName.get(Class.class), getPoetClassName()))
        .addStatement("return $T.class", getPoetClassName())
        .build());

    Util.writeJavaFile(filer, getPackageName(), binaryDeserializerClassBuilder.build());
  }

  static CodeBlock createBinaryDeserializationCodeBlock(
      HanaSerializableInterfaceCodeGenerator classSpec,
      String factoryVarName,
      String mapVarName,
      String deserializerVarName) {
    CodeBlock.Builder codeBuilder = CodeBlock.builder();
    codeBuilder.beginControlFlow("try");
    for (PropertySpec propertySpec : classSpec.propertySpecs) {
      codeBuilder.add(String.format("// %s\n", propertySpec.name));
      propertySpec.addBinaryDeserializationCode(codeBuilder, factoryVarName, mapVarName, deserializerVarName);
    }
    codeBuilder.endControlFlow();
    codeBuilder.beginControlFlow("catch ($T e)", Exception.class);
    codeBuilder.addStatement("throw new $T(e)", RuntimeException.class);
    codeBuilder.endControlFlow();
    return codeBuilder.build();
  }

  class BinaryDeserializerBindingGenerator implements BindingGenerator {

    @Override
    public void generate(TypeSpec.Builder moduleClassBuilder) {
      moduleClassBuilder.addMethod(MethodSpec
          .methodBuilder("provide__" + getUniqueClassName() + "__typeBinaryDeserializer")
          .addModifiers(Modifier.ABSTRACT)
          .addModifiers(Modifier.PUBLIC)
          .addAnnotation(Binds.class)
          .addAnnotation(IntoMap.class)
          .addAnnotation(AnnotationSpec.builder(IntKey.class)
              .addMember("value", CodeBlock.builder().add(String.format("%d", classSpec.getTypeId())).build())
              .build())
          .returns(ParameterizedTypeName.get(ClassName.get(TypeBinaryDeserializer.class), TypeVariableName.get("?")))
          .addParameter(ParameterSpec.builder(getPoetBinaryDeserializerClassName(), "deserializer").build())
          .build());
    }

    @Override
    public String getBindingLocationPackageName() {
      return getPackageName();
    }
  }

  class BinarySerializerBindingGenerator implements BindingGenerator {

    @Override
    public void generate(TypeSpec.Builder moduleClassBuilder) {
      moduleClassBuilder.addMethod(MethodSpec
          .methodBuilder("provide__" + getUniqueClassName() + "__typeBinarySerializer")
          .addModifiers(Modifier.PUBLIC)
          .addModifiers(Modifier.STATIC)
          .addAnnotation(Provides.class)
          .addAnnotation(IntoMap.class)
          .addAnnotation(AnnotationSpec.builder(IntKey.class)
              .addMember("value", CodeBlock.builder().add(String.format("%d", classSpec.getTypeId())).build())
              .build())
          .returns(ParameterizedTypeName.get(ClassName.get(TypeBinarySerializer.class), TypeVariableName.get("?")))
          .addCode(CodeBlock.builder()
              .addStatement(
                  String.format("return new $T(%d)", classSpec.getTypeId()),
                  ParameterizedTypeName.get(
                      ClassName.get(HanaSerializable.BinarySerializer_.class),
                      getPoetClassName()))
              .build())
          .build());
    }

    @Override
    public String getBindingLocationPackageName() {
      return getPackageName();
    }
  }

  class ReadableDeserializerBindingGenerator implements BindingGenerator {

    @Override
    public void generate(TypeSpec.Builder moduleClassBuilder) {
      List<String> typeNames = getTypeNames();
      for (int i = 0; i < typeNames.size(); i++) {
        moduleClassBuilder.addMethod(MethodSpec
            .methodBuilder("provide__" + getUniqueClassName() + "__typeReadableDeserializer_" + i)
            .addModifiers(Modifier.ABSTRACT)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Binds.class)
            .addAnnotation(IntoMap.class)
            .addAnnotation(AnnotationSpec.builder(StringKey.class)
                .addMember("value", CodeBlock.builder().add("$S", typeNames.get(i)).build())
                .build())
            .returns(ParameterizedTypeName.get(
                ClassName.get(TypeReadableDeserializer.class),
                TypeVariableName.get("?")))
            .addParameter(ParameterSpec.builder(getPoetReadableDeserializerClassName(), "deserializer").build())
            .build());
      }
    }

    @Override
    public String getBindingLocationPackageName() {
      return getPackageName();
    }
  }

  class ReadableSerializerBindingGenerator implements BindingGenerator {

    @Override
    public void generate(TypeSpec.Builder moduleClassBuilder) {
      List<String> typeNames = getTypeNames();
      for (int i = 0; i < typeNames.size(); i++) {
        moduleClassBuilder.addMethod(MethodSpec
            .methodBuilder("provide__" + getUniqueClassName() + "__typeReadableSerializer_" + i)
            .addModifiers(Modifier.PUBLIC)
            .addModifiers(Modifier.STATIC)
            .addAnnotation(Provides.class)
            .addAnnotation(IntoMap.class)
            .addAnnotation(AnnotationSpec.builder(StringKey.class)
                .addMember("value", CodeBlock.builder().add("$S", typeNames.get(i)).build())
                .build())
            .returns(ParameterizedTypeName.get(ClassName.get(TypeReadableSerializer.class), TypeVariableName.get("?")))
            .addCode(CodeBlock.builder()
                .addStatement(
                    "return new $T()",
                    ParameterizedTypeName.get(
                        ClassName.get(HanaSerializable.ReadableSerializer_.class),
                        getPoetClassName()))
                .build())
            .build());
      }
    }

    @Override
    public String getBindingLocationPackageName() {
      return getPackageName();
    }
  }
}
