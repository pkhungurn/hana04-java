package hana04.base.serialize.binary;

import org.msgpack.core.MessagePacker;

public interface TypeBinarySerializer<T> {
  void serialize(T obj, MessagePacker messagePacker, BinarySerializer serializer);

  int typeId();
}
