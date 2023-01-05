package hana04.botan.glasset.provider;

import hana04.botan.cache.GlObjectProvider;
import hana04.opengl.wrapper.GlAttributeSpec;
import hana04.opengl.wrapper.GlVbo;

public interface GlVertexAttribProvider extends GlObjectProvider<GlVbo> {
  GlAttributeSpec getAttributeSpec(String name);

  boolean hasAttribute(String name);

  int getNumBytesPerVertex();

  int getSizeInBytes();

  int getVertexCount();
}
