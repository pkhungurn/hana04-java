package hana04.base.serialize.readable;

import com.google.common.base.Preconditions;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class TypeNameToReadableDeserializerMap {
  private final Map<String, TypeReadableDeserializer<?>> typeNameToDeserializer;

  @Inject
  public TypeNameToReadableDeserializerMap(Map<String, TypeReadableDeserializer<?>> typeNameToDeserializer) {
    this.typeNameToDeserializer = typeNameToDeserializer;
  }

  public TypeReadableDeserializer<?> get(String typeName) {
    return Preconditions.checkNotNull(typeNameToDeserializer.get(typeName),
      "TypeReadableDeserializer for type " + typeName + " does not exist");
  }
}
