package hana04.base.serialize;

import hana04.base.extension.HanaObject;
import hana04.base.serialize.binary.BinaryDeserializer;
import hana04.base.serialize.readable.ReadableDeserializer;
import org.msgpack.value.MapValue;

import java.util.Map;

/**
 * A HanaObject with the following properties:
 * (1) An instance can be created without specifying property values
 * (i.e., all properties have default values).
 * (2) All properties are {@code Variable}s.
 * This allows the object to be deserialized after it is created.
 */
public interface HanaLateDeserializable extends HanaObject {
  void deserialize(Map content, ReadableDeserializer deserializer);

  void deserialize(MapValue value, BinaryDeserializer deserializer);
}
