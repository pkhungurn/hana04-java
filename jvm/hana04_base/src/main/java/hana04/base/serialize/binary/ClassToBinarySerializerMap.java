package hana04.base.serialize.binary;

import com.google.common.base.Preconditions;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class ClassToBinarySerializerMap {
  private final Map<Class<?>, TypeBinarySerializer<?>> classToBinarySerializerMap;

  @Inject
  public ClassToBinarySerializerMap(Map<Class<?>, TypeBinarySerializer<?>> classToBinarySerializerMap) {
    this.classToBinarySerializerMap = classToBinarySerializerMap;
  }

  public TypeBinarySerializer<?> get(Class<?> klass) {
    return Preconditions.checkNotNull(
      classToBinarySerializerMap.get(klass),
      "TypeBinarySerialzier does not exist for class " + klass.getCanonicalName());
  }
}
