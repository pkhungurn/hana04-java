package hana04.base.testing;

import hana04.base.util.StringSerializationUtil;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.Value;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Test {
  public static void main(String[] args) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);
    MessagePacker messagePacker = MessagePack.newDefaultPacker(outputStream);
    byte[] output = null;
    try {
      StringSerializationUtil.packString(messagePacker,"佐倉綾音");

      System.out.println("佐倉綾音");
      messagePacker.packLong(1L);
      messagePacker.packFloat(10.0f);
      messagePacker.packDouble(10.0);
      messagePacker.packBoolean(true);
      messagePacker.flush();
      output = byteArrayOutputStream.toByteArray();
      System.out.println(output.length);
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }

    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(output);
    DataInputStream inputStream = new DataInputStream(byteArrayInputStream);
    MessageUnpacker messageUnpakcer = MessagePack.newDefaultUnpacker(inputStream);
    try {
      Value value = messageUnpakcer.unpackValue();
      System.out.println(value.getValueType());
      System.out.println(value.asStringValue().asString());
      value = messageUnpakcer.unpackValue();
      System.out.println(value.asIntegerValue().isInLongRange());
      System.out.println(value.asIntegerValue().isInByteRange());
      value = messageUnpakcer.unpackValue();
      System.out.println(value.getValueType());
      value = messageUnpakcer.unpackValue();
      System.out.println(value.getValueType());
      System.out.println(value.asFloatValue().toDouble());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
