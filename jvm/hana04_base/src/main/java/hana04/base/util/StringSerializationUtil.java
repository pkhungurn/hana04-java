package hana04.base.util;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;

import java.io.IOException;

public class StringSerializationUtil {
  public static void packString(MessagePacker packer, String s) throws IOException {
    byte[] bytes = s.getBytes(MessagePack.UTF8);
    packer.packRawStringHeader(bytes.length);
    packer.addPayload(bytes);
  }
}
