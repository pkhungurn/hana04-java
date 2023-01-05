package hana04.apt.codegen.hanaclass.property;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import hana04.base.util.TypeUtil;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.regex.Pattern;

public class TerminalType implements PropertyType {
  private static final ImmutableSet<TypeKind> DISALLOWED_TYPES = ImmutableSet.<TypeKind>builder()
      .add(TypeKind.BYTE)
      .add(TypeKind.BOOLEAN)
      .add(TypeKind.CHAR)
      .add(TypeKind.INT)
      .add(TypeKind.LONG)
      .add(TypeKind.DOUBLE)
      .add(TypeKind.FLOAT)
      .build();

  public final TypeMirror typeMirror;

  public TerminalType(TypeMirror typeMirror) {
    Preconditions.checkArgument(!DISALLOWED_TYPES.contains(typeMirror.getKind()));
    this.typeMirror = typeMirror;
  }

  @Override
  public String getSimpleName() {
    String[] comps = typeMirror.toString().split(Pattern.quote("."));
    return comps[comps.length - 1];
  }

  @Override
  public TypeName getRawDataTypeName() {
    return TypeName.get(typeMirror);
  }

  @Override
  public TypeName getTypeName() {
    return TypeName.get(typeMirror);
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
    /*
    if (typeMirror.getKind().equals(TypeKind.INT)) {
      builder.addStatement(String.format("factory.%s($T.cast(value, $T.class))", name), TypeUtil.class, Integer.class);
    } else if (typeMirror.getKind().equals(TypeKind.FLOAT)) {
      builder.addStatement(String.format("factory.%s($T.cast(value, $T.class))", name), TypeUtil.class, Float.class);
    } else if (typeMirror.getKind().equals(TypeKind.DOUBLE)) {
      builder.addStatement(String.format("factory.%s($T.cast(value, $T.class))", name), TypeUtil.class, Double.class);
    } else if (typeMirror.getKind().equals(TypeKind.BOOLEAN)) {
      builder.addStatement(String.format("factory.%s($T.cast(value, $T.class))", name), TypeUtil.class, Boolean.class);
    } else if (typeMirror.getKind().equals(TypeKind.LONG)) {
      builder.addStatement(String.format("factory.%s($T.cast(value, $T.class))", name), TypeUtil.class, Long.class);
    } else {
      builder.addStatement(String.format("factory.%s($T.cast(value, $T.class))", name), TypeUtil.class, getTypeName());
    }
     */
    builder.addStatement(String.format("factory.%s($T.cast(value, $T.class))", name), TypeUtil.class, getTypeName());
  }

  @Override
  public void addImplBuilderMethods(
      TypeSpec.Builder builder,
      String name,
      String rawDataBuilderName,
      String selfTypeName) {
    builder.addMethod(MethodSpec.methodBuilder(name)
        .addModifiers(Modifier.PUBLIC)
        .returns(TypeVariableName.get(selfTypeName))
        .addParameter(TypeName.get(typeMirror), name)
        .addStatement(String.format("%s.%s(%s)", rawDataBuilderName, name, name))
        .addStatement(String.format("return (%s) this", selfTypeName))
        .build());
  }

  @Override
  public void addBinarySerializationMapElementCountCode(
      CodeBlock.Builder builder, String name, String counterName) {
    builder.addStatement(String.format("%s += 1", counterName));
  }

  @Override
  public void addBinarySerializationCode(
      CodeBlock.Builder builder,
      String name,
      int tag,
      String packerName,
      String serializerName,
      boolean tagHasBeenWritten) {
    if (!tagHasBeenWritten) {
      builder.addStatement(String.format("%s.packInt(%d)", packerName, tag));
    }
    /*
    // PRIMITIVE_TYPE_HANDLING_CODEGEN
    // int
    if (typeMirror.getKind().equals(TypeKind.INT)
        || typeMirror.toString().equals(Integer.class.getCanonicalName())) {
      builder.addStatement(String.format("%s.packInt(%s)", packerName, name));
    }
    // float
    else if (typeMirror.getKind().equals(TypeKind.FLOAT)
        || typeMirror.toString().equals(Float.class.getCanonicalName())) {
      builder.addStatement(String.format("%s.packFloat(%s)", packerName, name));
    }
    // double
    else if (typeMirror.getKind().equals(TypeKind.DOUBLE)
        || typeMirror.toString().equals(Double.class.getCanonicalName())) {
      builder.addStatement(String.format("%s.packDouble(%s)", packerName, name));
    }
    // boolean
    else if (typeMirror.getKind().equals(TypeKind.BOOLEAN)
        || typeMirror.toString().equals(Boolean.class.getCanonicalName())) {
      builder.addStatement(String.format("%s.packBoolean(%s)", packerName, name));
    }
    // long
    else if (typeMirror.getKind().equals(TypeKind.LONG)
        || typeMirror.toString().equals(Long.class.getCanonicalName())) {
      builder.addStatement(String.format("%s.packLong(%s)", packerName, name));
    }
    // String
    else if (typeMirror.toString().equals(String.class.getCanonicalName())) {
      builder.addStatement(
          String.format("$T.packString(%s, %s)", packerName, name),
          StringSerializationUtil.class);
    }
    // Point2d
    else if (typeMirror.toString().equals(Point2d.class.getCanonicalName())) {
      builder.addStatement(String.format("$T.packPoint2d(%s, %s)", packerName, name), BinarySerializationUtil.class);
    }
    // Vector2d
    else if (typeMirror.toString().equals(Vector2d.class.getCanonicalName())) {
      builder.addStatement(String.format("$T.packVector2d(%s, %s)", packerName, name), BinarySerializationUtil.class);
    }
    // Point3d
    else if (typeMirror.toString().equals(Point3d.class.getCanonicalName())) {
      builder.addStatement(String.format("$T.packPoint3d(%s, %s)", packerName, name), BinarySerializationUtil.class);
    }
    // Vector3d
    else if (typeMirror.toString().equals(Vector3d.class.getCanonicalName())) {
      builder.addStatement(String.format("$T.packVector3d(%s, %s)", packerName, name), BinarySerializationUtil.class);
    }
    // Vector4d
    else if (typeMirror.toString().equals(Vector4d.class.getCanonicalName())) {
      builder.addStatement(String.format("$T.packVector4d(%s, %s)", packerName, name), BinarySerializationUtil.class);
    }
    // Quat4d
    else if (typeMirror.toString().equals(Quat4d.class.getCanonicalName())) {
      builder.addStatement(String.format("$T.packQuat4d(%s, %s)", packerName, name), BinarySerializationUtil.class);
    }
    // Matrix4d
    else if (typeMirror.toString().equals(Matrix4d.class.getCanonicalName())) {
      builder.addStatement(String.format("$T.packMatrix4d(%s, %s)", packerName, name), BinarySerializationUtil.class);
    }
    // Transform
    else if (typeMirror.toString().equals(Transform.class.getCanonicalName())) {
      builder.addStatement(String.format("$T.packTransform(%s, %s)", packerName, name), BinarySerializationUtil.class);
    }
    // Aabb2d
    else if (typeMirror.toString().equals(Aabb2d.class.getCanonicalName())) {
      builder.addStatement(String.format("$T.packAabb2d(%s, %s)", packerName, name), BinarySerializationUtil.class);
    }
    // Aabb3d
    else if (typeMirror.toString().equals(Aabb3d.class.getCanonicalName())) {
      builder.addStatement(String.format("$T.packAabb3d(%s, %s)", packerName, name), BinarySerializationUtil.class);
    }
    // Point3i
    else if (typeMirror.toString().equals(Point3i.class.getCanonicalName())) {
      builder.addStatement(String.format("$T.packPoint3i(%s, %s)", packerName, name), BinarySerializationUtil.class);
    }
    // Other
    else {
      builder.addStatement(String.format("%s.serialize(%s)", serializerName, name));
    }
     */
    builder.addStatement(String.format("%s.serialize(%s)", serializerName, name));
  }

  @Override
  public void addBinaryDeserializationCode(
      CodeBlock.Builder builder, String name, int tag, String factoryName, String mapName, String deserializerName) {
    builder.beginControlFlow(String.format("if (%s.containsKey(%d))", mapName, tag));
    /*
    // PRIMITIVE_TYPE_HANDLING_CODEGEN
    // int
    if (typeMirror.getKind().equals(TypeKind.INT)
        || typeMirror.toString().equals(Integer.class.getCanonicalName())) {
      builder.addStatement(String.format(
          "%s.%s(%s.get(%d).asIntegerValue().asInt())",
          factoryName,
          name,
          mapName,
          tag));
    }
    // float
    else if (typeMirror.getKind().equals(TypeKind.FLOAT)
        || typeMirror.toString().equals(Float.class.getCanonicalName())) {
      builder.addStatement(String.format(
          "%s.%s(%s.get(%d).asFloatValue().toFloat())",
          factoryName,
          name,
          mapName,
          tag));
    }
    // double
    else if (typeMirror.getKind().equals(TypeKind.DOUBLE)
        || typeMirror.toString().equals(Double.class.getCanonicalName())) {
      builder.addStatement(String.format(
          "%s.%s(%s.get(%d).asFloatValue().toDouble())",
          factoryName,
          name,
          mapName,
          tag));
    }
    // boolean
    else if (typeMirror.getKind().equals(TypeKind.BOOLEAN)
        || typeMirror.toString().equals(Boolean.class.getCanonicalName())) {
      builder.addStatement(String.format(
          "%s.%s(%s.get(%d).asBooleanValue().getBoolean())",
          factoryName,
          name,
          mapName,
          tag));
    }
    // long
    else if (typeMirror.getKind().equals(TypeKind.LONG)
        || typeMirror.toString().equals(Long.class.getCanonicalName())) {
      builder.addStatement(String.format(
          "%s.%s(%s.get(%d).asIntegerValue().asLong())",
          factoryName,
          name,
          mapName,
          tag));
    }
    // String
    else if (typeMirror.toString().equals(String.class.getCanonicalName())) {
      builder.addStatement(String.format(
          "%s.%s(%s.get(%d).asStringValue().asString())",
          factoryName,
          name,
          mapName,
          tag));
    }
    // Point2d
    else if (typeMirror.toString().equals(Point2d.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.%s($T.unpackPoint2d(%s.get(%d)))",
              factoryName,
              name,
              mapName,
              tag),
          BinarySerializationUtil.class);
    }
    // Vector2d
    else if (typeMirror.toString().equals(Vector2d.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.%s($T.unpackVector2d(%s.get(%d)))",
              factoryName,
              name,
              mapName,
              tag),
          BinarySerializationUtil.class);
    }
    // Point3d
    else if (typeMirror.toString().equals(Point3d.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.%s($T.unpackPoint3d(%s.get(%d)))",
              factoryName,
              name,
              mapName,
              tag),
          BinarySerializationUtil.class);
    }
    // Vector3d
    else if (typeMirror.toString().equals(Vector3d.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.%s($T.unpackVector3d(%s.get(%d)))",
              factoryName,
              name,
              mapName,
              tag),
          BinarySerializationUtil.class);
    }
    // Vector4d
    else if (typeMirror.toString().equals(Vector4d.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.%s($T.unpackVector4d(%s.get(%d)))",
              factoryName,
              name,
              mapName,
              tag),
          BinarySerializationUtil.class);
    }
    // Quat4d
    else if (typeMirror.toString().equals(Quat4d.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.%s($T.unpackQuat4d(%s.get(%d)))",
              factoryName,
              name,
              mapName,
              tag),
          BinarySerializationUtil.class);
    }
    // Matrix4d
    else if (typeMirror.toString().equals(Matrix4d.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.%s($T.unpackMatrix4d(%s.get(%d)))",
              factoryName,
              name,
              mapName,
              tag),
          BinarySerializationUtil.class);
    }
    // Transform
    else if (typeMirror.toString().equals(Transform.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.%s($T.unpackTransform(%s.get(%d)))",
              factoryName,
              name,
              mapName,
              tag),
          BinarySerializationUtil.class);
    }
    // Aabb2d
    else if (typeMirror.toString().equals(Aabb2d.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.%s($T.unpackAabb2d(%s.get(%d)))",
              factoryName,
              name,
              mapName,
              tag),
          BinarySerializationUtil.class);
    }
    // Aabb3d
    else if (typeMirror.toString().equals(Aabb3d.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.%s($T.unpackAabb3d(%s.get(%d)))",
              factoryName,
              name,
              mapName,
              tag),
          BinarySerializationUtil.class);
    }
    // Point3i
    else if (typeMirror.toString().equals(Point3i.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.%s($T.unpackPoint3i(%s.get(%d)))",
              factoryName,
              name,
              mapName,
              tag),
          BinarySerializationUtil.class);
    }
    // Other
    else {
      builder.addStatement(
          String.format(
              "%s.%s($T.cast(%s.deserialize(%s.get(%d).asMapValue()), $T.class))",
              factoryName,
              name,
              deserializerName,
              mapName,
              tag),
          TypeUtil.class,
          getTypeName());
    }
    */
    builder.addStatement(
        String.format(
            "%s.%s($T.cast(%s.deserialize(%s.get(%d).asMapValue()), $T.class))",
            factoryName,
            name,
            deserializerName,
            mapName,
            tag),
        TypeUtil.class,
        getTypeName());
    builder.endControlFlow();
  }

  void addBinaryDeserializeToListCode(
      CodeBlock.Builder builder,
      String name,
      String arrayName,
      String indexName,
      String factoryName,
      String deserializerName) {
    /*
    // PRIMITIVE_TYPE_HANDLING_CODEGEN
    // int
    if (typeMirror.getKind().equals(TypeKind.INT)
        || typeMirror.toString().equals(Integer.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.add%s(%s.get(%s).asIntegerValue().asInt())",
              factoryName,
              WordUtils.capitalize(name),
              arrayName,
              indexName));
    }
    // float
    else if (typeMirror.getKind().equals(TypeKind.FLOAT)
        || typeMirror.toString().equals(Float.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.add%s(%s.get(%s).asFloatValue().toFloat())",
              factoryName,
              WordUtils.capitalize(name),
              arrayName,
              indexName));
    }
    // double
    else if (typeMirror.getKind().equals(TypeKind.DOUBLE)
        || typeMirror.toString().equals(Double.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.add%s(%s.get(%s).asFloatValue().toDouble())",
              factoryName,
              WordUtils.capitalize(name),
              arrayName,
              indexName));
    }
    // boolean
    else if (typeMirror.getKind().equals(TypeKind.BOOLEAN)
        || typeMirror.toString().equals(Boolean.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.add%s(%s.get(%s).asBooleanValue().getBoolean())",
              factoryName,
              WordUtils.capitalize(name),
              arrayName,
              indexName));
    }
    // long
    else if (typeMirror.getKind().equals(TypeKind.LONG)
        || typeMirror.toString().equals(Long.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.add%s(%s.get(%s).asIntegerValue().asLong())",
              factoryName,
              WordUtils.capitalize(name),
              arrayName,
              indexName));
    }
    // String
    else if (typeMirror.toString().equals(String.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.add%s(%s.get(%s).asStringValue().asString())",
              factoryName,
              WordUtils.capitalize(name),
              arrayName,
              indexName));
    }
    // Point2d
    else if (typeMirror.toString().equals(Point2d.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.add%s($T.unpackPoint2d(%s.get(%s)))",
              factoryName,
              WordUtils.capitalize(name),
              arrayName,
              indexName),
          BinarySerializationUtil.class);
    }
    // Vector2d
    else if (typeMirror.toString().equals(Vector2d.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.add%s($T.unpackVector2d(%s.get(%s)))",
              factoryName,
              WordUtils.capitalize(name),
              arrayName,
              indexName),
          BinarySerializationUtil.class);
    }
    // Point3d
    else if (typeMirror.toString().equals(Point3d.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.add%s($T.unpackPoint3d(%s.get(%s)))",
              factoryName,
              WordUtils.capitalize(name),
              arrayName,
              indexName),
          BinarySerializationUtil.class);
    }
    // Vector3d
    else if (typeMirror.toString().equals(Vector3d.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.add%s($T.unpackVector3d(%s.get(%s)))",
              factoryName,
              WordUtils.capitalize(name),
              arrayName,
              indexName),
          BinarySerializationUtil.class);
    }
    // Vector4d
    else if (typeMirror.toString().equals(Vector4d.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.add%s($T.unpackVector4d(%s.get(%s)))",
              factoryName,
              WordUtils.capitalize(name),
              arrayName,
              indexName),
          BinarySerializationUtil.class);
    }
    // Quat4d
    else if (typeMirror.toString().equals(Quat4d.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.add%s($T.unpackQuat4d(%s.get(%s)))",
              factoryName,
              WordUtils.capitalize(name),
              arrayName,
              indexName),
          BinarySerializationUtil.class);
    }
    // Matrix4d
    else if (typeMirror.toString().equals(Matrix4d.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.add%s($T.unpackMatrix4d(%s.get(%s)))",
              factoryName,
              WordUtils.capitalize(name),
              arrayName,
              indexName),
          BinarySerializationUtil.class);
    }
    // Transform
    else if (typeMirror.toString().equals(Transform.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.add%s($T.unpackTransform(%s.get(%s)))",
              factoryName,
              WordUtils.capitalize(name),
              arrayName,
              indexName),
          BinarySerializationUtil.class);
    }
    // Aabb2d
    else if (typeMirror.toString().equals(Aabb2d.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.add%s($T.unpackAabb2d(%s.get(%s)))",
              factoryName,
              WordUtils.capitalize(name),
              arrayName,
              indexName),
          BinarySerializationUtil.class);
    }
    // Aabb3d
    else if (typeMirror.toString().equals(Aabb3d.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.add%s($T.unpackAabb3d(%s.get(%s)))",
              factoryName,
              WordUtils.capitalize(name),
              arrayName,
              indexName),
          BinarySerializationUtil.class);
    }
    // Point3i
    else if (typeMirror.toString().equals(Point3i.class.getCanonicalName())) {
      builder.addStatement(
          String.format(
              "%s.add%s($T.unpackPoint3i(%s.get(%s)))",
              factoryName,
              WordUtils.capitalize(name),
              arrayName,
              indexName),
          BinarySerializationUtil.class);
    }
    // Other
    else {
      builder.addStatement(
          String.format(
              "%s.add%s($T.cast(%s.deserialize(%s.get(%s).asMapValue()), $T.class))",
              factoryName,
              WordUtils.capitalize(name),
              deserializerName,
              arrayName,
              indexName),
          TypeUtil.class,
          getTypeName());
    }
     */
    builder.addStatement(
        String.format(
            "%s.add%s($T.cast(%s.deserialize(%s.get(%s).asMapValue()), $T.class))",
            factoryName,
            StringUtils.capitalize(name),
            deserializerName,
            arrayName,
            indexName),
        TypeUtil.class,
        getTypeName());
  }
}
