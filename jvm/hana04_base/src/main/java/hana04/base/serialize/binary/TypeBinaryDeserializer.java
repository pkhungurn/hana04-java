package hana04.base.serialize.binary;

import org.msgpack.value.Value;

public interface TypeBinaryDeserializer<T> {
  T deserialize(Value value, BinaryDeserializer deserializer);

  Class<?> getSerializedClass();
}
