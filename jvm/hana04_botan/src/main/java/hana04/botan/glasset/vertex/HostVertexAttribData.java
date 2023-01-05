package hana04.botan.glasset.vertex;

import hana04.base.changeprop.VersionedSubject;
import hana04.botan.glasset.provider.HostBufferProvider;
import hana04.opengl.wrapper.GlAttributeSpec;

public interface HostVertexAttribData extends HostBufferProvider, VersionedSubject {
  GlAttributeSpec getAttributeSpec(String name);

  boolean hasAttribute(String name);

  int getNumBytesPerVertex();

  int getVertexCount();

  default int getBufferSizeInByte() {
    return getVertexCount() * getNumBytesPerVertex();
  }
}
