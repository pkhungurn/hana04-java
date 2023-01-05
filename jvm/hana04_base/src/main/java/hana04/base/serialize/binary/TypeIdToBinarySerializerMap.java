package hana04.base.serialize.binary;

import com.google.common.base.Preconditions;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class TypeIdToBinarySerializerMap {
  private final Map<Integer, TypeBinarySerializer<?>> typeIdToBinarySerializerMap;

  @Inject
  public TypeIdToBinarySerializerMap(Map<Integer, TypeBinarySerializer<?>> typeIdToBinarySerializerMap) {
    this.typeIdToBinarySerializerMap = typeIdToBinarySerializerMap;
  }

  public TypeBinarySerializer<?> get(int typeId) {
    return Preconditions.checkNotNull(typeIdToBinarySerializerMap.get(typeId),
      "TypeBinarySerializer for id=" + typeId + " does not exist");
  }
}
