package hana04.base.serialize.readable;

import com.google.common.base.Preconditions;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class TypeNameToReadableSerializerMap {
  private final Map<String, TypeReadableSerializer<?>> typeNameToSerializer;

  @Inject
  public TypeNameToReadableSerializerMap(Map<String, TypeReadableSerializer<?>> typeNameToSerializer) {
    this.typeNameToSerializer = typeNameToSerializer;
  }

  public TypeReadableSerializer get(String typeName) {
    return Preconditions.checkNotNull(typeNameToSerializer.get(typeName),
      "TypeReadableSerializer for type " + typeName + " does not exist");
  }
}
