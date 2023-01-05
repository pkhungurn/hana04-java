package hana04.base.serialize;

import hana04.base.extension.HanaExtensible;
import hana04.base.serialize.binary.BinarySerializer;
import hana04.base.serialize.readable.ReadableSerializer;
import org.msgpack.core.MessagePacker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class ExtensionSerialization {
  private ExtensionSerialization() {
    // NO-OP
  }

  static List serializeExtensions(HanaExtensible extensible, ReadableSerializer serializer) {
    List extensions = new ArrayList();
    for (Object extension : extensible.extensions()) {
      if (extension instanceof HanaLateDeserializable) {
        extensions.add(serializer.serialize(extension));
      }
    }
    return extensions;
  }

  public static void serializeExtensions(
    HanaExtensible extensible,
    MessagePacker messagePacker,
    BinarySerializer serializer) {
    int serializableExtensionCount = 0;
    for (Object extension : extensible.extensions()) {
      if (extension instanceof HanaLateDeserializable) {
        serializableExtensionCount++;
      }
    }
    try {
      messagePacker.packArrayHeader(serializableExtensionCount);
      for (Object extension : extensible.extensions()) {
        if (extension instanceof HanaLateDeserializable) {
          serializer.serialize(extension);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
