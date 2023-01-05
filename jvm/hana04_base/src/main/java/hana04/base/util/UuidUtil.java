package hana04.base.util;

import org.msgpack.core.MessagePacker;
import org.msgpack.core.Preconditions;
import org.msgpack.value.Value;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Lifted from https://gist.github.com/jeffjohnson9046/c663dd22bbe6bb0b3f5e on 2020/02/29
 */
public class UuidUtil {
  public static byte[] getBytesFromUUID(UUID uuid) {
    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
    bb.putLong(uuid.getMostSignificantBits());
    bb.putLong(uuid.getLeastSignificantBits());
    return bb.array();
  }

  public static UUID getUUIDFromBytes(byte[] bytes) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
    long high = byteBuffer.getLong();
    long low = byteBuffer.getLong();
    return new UUID(high, low);
  }

  public static void pack(MessagePacker messagePacker, UUID uuid) throws IOException {
    messagePacker.packBinaryHeader(16);
    messagePacker.writePayload(getBytesFromUUID(uuid));
  }

  public static UUID unpack(Value value) {
    Preconditions.checkArgument(value.isBinaryValue());
    byte[] bytes = value.asBinaryValue().asByteArray();
    Preconditions.checkArgument(bytes.length == 16);
    return getUUIDFromBytes(bytes);
  }
}
