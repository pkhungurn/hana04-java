package hana04.base.util;

import hana04.base.caching.Direct;
import hana04.base.caching.Wrapped;

public class TypeUtil {
  public static <T> T cast(Object obj, Class<T> klass, String errorMessage) {
    if (!klass.isAssignableFrom(obj.getClass())) {
      throw new IllegalArgumentException(errorMessage);
    }
    return (T) obj;
  }

  public static <T> T cast(Object obj, Class<T> klass) {
    return cast(obj, klass, "Invalid cast from " + obj.getClass() + " to " + klass);
  }

  public static <T> Wrapped<T> castToWrapped(Object obj, Class<T> klass) {
    if (obj instanceof Wrapped) {
      return (Wrapped) obj;
    } else if (klass.isAssignableFrom(obj.getClass())) {
      return Direct.of((T) obj);
    } else {
      throw new IllegalArgumentException("Invalid cast from " + obj.getClass() + " to Wrapped< " + klass + ">");
    }
  }
}
