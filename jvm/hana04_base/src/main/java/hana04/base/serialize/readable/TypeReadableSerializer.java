package hana04.base.serialize.readable;

import java.util.Map;

public interface TypeReadableSerializer<T> {
  Map serialize(T obj, ReadableSerializer serializer);
}
