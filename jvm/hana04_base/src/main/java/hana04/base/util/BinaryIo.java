package hana04.base.util;

import org.apache.commons.io.input.SwappedDataInputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.function.Consumer;

/**
 * Utility class for writing binary values to streams.
 */
public class BinaryIo {

  public static void writeByteString(DataOutputStream fout, byte[] b, int length) throws IOException {
    fout.write(b);
    for (int i = 0; i < length - b.length; i++) {
      fout.write('\0');
    }
  }

  public static void writeUtfString(DataOutputStream fout, String s, int length) throws IOException {
    byte[] b = s.getBytes(StandardCharsets.UTF_8);
    writeByteString(fout, b, length);
  }

  public static void writeUtfString(DataOutputStream fout, String s) throws IOException {
    byte[] b = s.getBytes(StandardCharsets.UTF_8);
    writeUtfString(fout, s, b.length);
  }

  public static void writeLittleEndianVaryingLengthUtfString(DataOutputStream fout, String s) throws IOException {
    byte[] b = s.getBytes(StandardCharsets.UTF_8);
    writeLittleEndianInt(fout, b.length);
    writeUtfString(fout, s, b.length);
  }

  public static void writeShiftJISString(DataOutputStream fout, String s, int length) throws IOException {
    byte[] b = s.getBytes("Shift-JIS");
    writeByteString(fout, b, length);
  }

  public static String readUtfString(SwappedDataInputStream fin, int length) throws IOException {
    byte[] data = new byte[length];
    int read = fin.read(data, 0, length);
    if (read == -1) {
      throw new EOFException("end of file reached");
    } else {
      int l = 0;
      while (l < data.length && data[l] != '\0') {
        l++;
      }
      byte[] s = new byte[l];
      for (int i = 0; i < l; i++) {
        s[i] = data[i];
      }
      return new String(s, StandardCharsets.UTF_8);
    }
  }

  public static String readShiftJisString(SwappedDataInputStream fin, int length) throws IOException {
    byte[] data = new byte[length];
    int read = fin.read(data, 0, length);
    if (read == -1) {
      throw new EOFException("end of file reached");
    } else {
      int l = 0;
      while (l < data.length && data[l] != '\0') {
        l++;
      }
      byte[] s = new byte[l];
      for (int i = 0; i < l; i++) {
        s[i] = data[i];
      }
      return new String(s, "Shift-JIS");
    }
  }

  public static String readUTF(DataInputStream fin) {
    try {
      return fin.readUTF();
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String readVariableLengthString(SwappedDataInputStream fin, Charset charset) throws IOException {
    int length = fin.readInt();
    byte[] data = new byte[length];
    fin.read(data);
    return new String(data, charset);
  }

  public static String readVariableLengthUtfString(SwappedDataInputStream fin) throws IOException {
    int length = fin.readInt();
    String result = readUtfString(fin, length);
    return result;
  }

  public static void writeLittleEndianShort(DataOutputStream out, short value) throws IOException {
    out.writeByte(value & 0xFF);
    out.writeByte((value >> 8) & 0xFF);
  }

  public static void writeLittleEndianInt(DataOutputStream out, int value) throws IOException {
    out.writeByte(value & 0xFF);
    out.writeByte((value >> 8) & 0xFF);
    out.writeByte((value >> 16) & 0xFF);
    out.writeByte((value >> 24) & 0xFF);
  }

  public static void writeLittleEndianLong(DataOutputStream out, long value) throws IOException {
    out.writeByte((int) (value & 0xFF));
    out.writeByte((int) ((value >> 8) & 0xFF));
    out.writeByte((int) ((value >> 16) & 0xFF));
    out.writeByte((int) ((value >> 24) & 0xFF));
    out.writeByte((int) ((value >> 32) & 0xFF));
    out.writeByte((int) ((value >> 40) & 0xFF));
    out.writeByte((int) ((value >> 48) & 0xFF));
    out.writeByte((int) ((value >> 56) & 0xFF));
  }

  public static void writeLittleEndianFloat(DataOutputStream out, float value) throws IOException {
    writeLittleEndianInt(out, Float.floatToIntBits(value));
  }

  public static void writeLittleEndianDouble(DataOutputStream out, double value) throws IOException {
    writeLittleEndianLong(out, Double.doubleToLongBits(value));
  }

  public static void readBinaryFile(String fileName, Consumer<DataInputStream> reader) {
    try {
      File file = new File(fileName);
      int bufferSize = (int) file.length();
      ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
      FileInputStream fileStream = new FileInputStream(new File(fileName));
      FileChannel channel = fileStream.getChannel();
      channel.read(buffer);
      channel.close();
      fileStream.close();
      buffer.rewind();

      DataInputStream stream = new DataInputStream(new ByteBufferBackedInputStream(buffer));
      reader.accept(stream);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void writeBinaryFile(String fileName, int fileSize, Consumer<DataOutputStream> writer) {
    try {
      ByteBuffer buffer = ByteBuffer.allocateDirect(fileSize);
      ByteBufferBackedOutputStream outStream = new ByteBufferBackedOutputStream(buffer);
      DataOutputStream stream = new DataOutputStream(outStream);
      writer.accept(stream);
      stream.close();

      File file = new File(fileName);
      FileOutputStream fileOutputStream = new FileOutputStream(file);
      FileChannel channel = fileOutputStream.getChannel();
      buffer.rewind();
      channel.write(buffer);
      channel.close();
      fileOutputStream.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void writeByteArrayToFile(Path path, byte[] bytes) {
    try {
      if (path.getParent() != null) {
        Files.createDirectories(path.getParent());
      }
      if (!Files.exists(path)) {
        Files.createFile(path);
      }
      ByteChannel byteChannel = Files.newByteChannel(
        path,
        EnumSet.of(
          StandardOpenOption.WRITE,
          StandardOpenOption.TRUNCATE_EXISTING,
          StandardOpenOption.CREATE));
      byteChannel.write(ByteBuffer.wrap(bytes));
      byteChannel.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static byte[] readByteArrayFromFile(Path path) {
    try {
      long fileSize = Files.size(path);
      ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
      SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.READ);
      channel.read(buffer);
      channel.close();
      return buffer.array();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static int readIntGivenSizeInBytes(SwappedDataInputStream fin, int size, boolean unsigned) throws IOException {
    if (size == 1) {
      byte b = fin.readByte();
      if (unsigned) {
        return (b & 0xff);
      } else {
        return b;
      }
    } else if (size == 2) {
      short s = fin.readShort();
      if (unsigned) {
        return (s & 0xff00) | (s & 0xff);
      } else {
        return s;
      }
    } else if (size == 4) {
      return fin.readInt();
    } else {
      throw new RuntimeException("invalid size");
    }
  }
}
