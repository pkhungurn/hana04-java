package hana04.base.serialize.readable;

import com.google.common.base.Preconditions;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class ClassToReadableSerializerMap {
  private final Map<Class<?>, TypeReadableSerializer<?>> classToSerializer;

  @Inject
  public ClassToReadableSerializerMap(Map<Class<?>, TypeReadableSerializer<?>> classToSerializer) {
    this.classToSerializer = classToSerializer;
  }

  public TypeReadableSerializer<?> get(Class<?> klass) {
    return Preconditions.checkNotNull(classToSerializer.get(klass),
      "TypeReadableDeserializer for type " + klass.getCanonicalName() + " does not exist");
  }
}
