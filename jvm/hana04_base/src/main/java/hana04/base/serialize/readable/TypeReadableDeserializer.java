package hana04.base.serialize.readable;

import java.util.Map;

public interface TypeReadableDeserializer<T> {
  T deserialize(Map json, ReadableDeserializer deserializer);
  Class<T> getSerializedClass();
}
