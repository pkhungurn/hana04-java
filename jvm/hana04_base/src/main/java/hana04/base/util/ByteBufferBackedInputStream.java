package hana04.base.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferBackedInputStream extends InputStream {
  private ByteBuffer buf;

  public ByteBufferBackedInputStream(ByteBuffer buf) {
    this.buf = buf;
  }

  public synchronized int read() throws IOException {
    if (!buf.hasRemaining()) {
      return -1;
    }
    int result = buf.get() & 0xFF;
    return (0 | result);
  }

  public synchronized int read(byte[] bytes, int off, int len) throws IOException {
    len = Math.min(len, buf.remaining());
    buf.get(bytes, off, len);
    return len;
  }
}

