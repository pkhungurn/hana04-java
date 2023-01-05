package hana04.apt.codegen.hanaclass.property;

import com.google.common.base.Preconditions;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.Variable;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PropertyType {
  String getSimpleName();

  TypeName getRawDataTypeName();

  TypeName getTypeName();

  void addReadableChildCode(CodeBlock.Builder builder, String name, String func);

  void addImplClassConstructorCode(CodeBlock.Builder builder, String name);

  void addReadableDeserializeCode(CodeBlock.Builder builder, String name);

  void addImplBuilderMethods(TypeSpec.Builder builder, String name, String rawDataBuilderName, String selfTypeName);

  void addBinarySerializationMapElementCountCode(CodeBlock.Builder builder, String name, String counterName);

  void addBinarySerializationCode(
      CodeBlock.Builder builder,
      String name,
      int tag,
      String packerName,
      String serializerName,
      boolean tagHasBeenWritten);

  void addBinaryDeserializationCode(
      CodeBlock.Builder builder,
      String name,
      int tag,
      String factoryName,
      String mapName,
      String deserializerName);

  static PropertyType parse(TypeMirror typeMirror, Types typeUtil) {
    Preconditions.checkArgument(typeMirror instanceof DeclaredType || typeMirror instanceof PrimitiveType);
    if (typeMirror instanceof PrimitiveType) {
      return new TerminalType(typeMirror);
    }
    DeclaredType declaredType = (DeclaredType) typeMirror;
    if (declaredType.getTypeArguments().size() == 0) {
      return new TerminalType(declaredType);
    }
    TypeMirror erased = typeUtil.erasure(typeMirror);
    String outerTypeName = erased.toString();
    if (outerTypeName.equals(Wrapped.class.getCanonicalName())) {
      PropertyType innerType = PropertyType.parse(declaredType.getTypeArguments().get(0), typeUtil);
      return new WrappedType(innerType);
    } else if (outerTypeName.equals(List.class.getCanonicalName())) {
      PropertyType innerType = PropertyType.parse(declaredType.getTypeArguments().get(0), typeUtil);
      return new ListType(innerType);
    } else if (outerTypeName.equals(Optional.class.getCanonicalName())) {
      PropertyType innerType = PropertyType.parse(declaredType.getTypeArguments().get(0), typeUtil);
      return new OptionalType(innerType);
    } else if (outerTypeName.equals(Variable.class.getCanonicalName())) {
      PropertyType innerType = PropertyType.parse(declaredType.getTypeArguments().get(0), typeUtil);
      return new VariableType(innerType);
    } else if (outerTypeName.equals(Map.class.getCanonicalName())) {
      PropertyType keyType = PropertyType.parse(declaredType.getTypeArguments().get(0), typeUtil);
      PropertyType valueType = PropertyType.parse(declaredType.getTypeArguments().get(1), typeUtil);
      return new MapType(keyType, valueType);
    } else {
      return new TerminalType(declaredType);
    }
  }
}
