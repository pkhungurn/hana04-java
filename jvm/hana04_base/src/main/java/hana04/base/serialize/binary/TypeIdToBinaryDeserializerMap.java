package hana04.base.serialize.binary;

import dagger.internal.Preconditions;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class TypeIdToBinaryDeserializerMap {
  private final Map<Integer, TypeBinaryDeserializer<?>> typeIdToBinaryDeserializerMap;

  @Inject
  public TypeIdToBinaryDeserializerMap(Map<Integer, TypeBinaryDeserializer<?>> typeIdToBinaryDeserializerMap) {
    this.typeIdToBinaryDeserializerMap = typeIdToBinaryDeserializerMap;
  }

  public TypeBinaryDeserializer<?> get(int typeId) {
    return Preconditions.checkNotNull(typeIdToBinaryDeserializerMap.get(typeId),
      "TypeBinaryDeserializer for id=" + typeId + " does not exist");
  }

}
