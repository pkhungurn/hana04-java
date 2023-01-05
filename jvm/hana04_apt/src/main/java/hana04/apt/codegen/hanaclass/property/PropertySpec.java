package hana04.apt.codegen.hanaclass.property;

import com.squareup.javapoet.CodeBlock;

public class PropertySpec {
  public final String name;
  public final PropertyType type;
  public final int tag;

  public PropertySpec(String name, PropertyType type, int tag) {
    this.name = name;
    this.type = type;
    this.tag = tag;
  }

  public void addReadableChildrenCode(CodeBlock.Builder builder) {
    type.addReadableChildCode(builder, name, name);
  }

  public void addImplClassContructorCode(CodeBlock.Builder builder) {
    type.addImplClassConstructorCode(builder, name);
  }

  public void addReadableDeserializeCode(CodeBlock.Builder builder) {
    type.addReadableDeserializeCode(builder, name);
  }

  public void addBinarySerializationMapElementCountCode(CodeBlock.Builder builder, String counterName) {
    type.addBinarySerializationMapElementCountCode(builder, name, counterName);
  }

  public void addBinarySerializationCode(CodeBlock.Builder builder, String packerName, String serializerName) {
    type.addBinarySerializationCode(builder, name, tag, packerName, serializerName, false);
  }

  public void addBinaryDeserializationCode(
    CodeBlock.Builder builder, String factoryName, String mapName, String deserializerName) {
    type.addBinaryDeserializationCode(builder, name, tag, factoryName, mapName, deserializerName);
  }
}
