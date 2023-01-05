package hana04.botan.glasset.provider;

import java.nio.Buffer;

public interface HostBufferProvider {
  Buffer getBuffer();
  int getBufferSizeInByte();
}
