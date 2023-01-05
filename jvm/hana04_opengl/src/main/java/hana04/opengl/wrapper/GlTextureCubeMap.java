package hana04.opengl.wrapper;

import java.nio.Buffer;

public interface GlTextureCubeMap extends GlTexture {
  int getSize();

  void allocate(int size, int format, int type);

  void allocate(int size);

  void setData(int size, int format, int type, Buffer[] buffers);
}
