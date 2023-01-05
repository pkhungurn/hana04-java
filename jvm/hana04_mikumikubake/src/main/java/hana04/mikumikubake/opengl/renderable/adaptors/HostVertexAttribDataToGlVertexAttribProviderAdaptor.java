package hana04.mikumikubake.opengl.renderable.adaptors;

import hana04.botan.cache.GlObjectCache;
import hana04.botan.cache.GlObjectRecord;
import hana04.botan.glasset.provider.GlVertexAttribProvider;
import hana04.botan.glasset.vertex.HostVertexAttribData;
import hana04.opengl.wrapper.GlAttributeSpec;
import hana04.opengl.wrapper.GlConstants;
import hana04.opengl.wrapper.GlVbo;

import java.nio.Buffer;

public class HostVertexAttribDataToGlVertexAttribProviderAdaptor implements GlVertexAttribProvider {
  private final HostVertexAttribData hostVertexAttribData;
  private final GlObjectCache glObjectCache;

  public HostVertexAttribDataToGlVertexAttribProviderAdaptor(
    HostVertexAttribData hostVertexAttribData, GlObjectCache glObjectCache) {
    this.hostVertexAttribData = hostVertexAttribData;
    this.glObjectCache = glObjectCache;
  }

  @Override
  public GlAttributeSpec getAttributeSpec(String name) {
    return hostVertexAttribData.getAttributeSpec(name);
  }

  @Override
  public boolean hasAttribute(String name) {
    return hostVertexAttribData.hasAttribute(name);
  }

  @Override
  public int getNumBytesPerVertex() {
    return hostVertexAttribData.getNumBytesPerVertex();
  }

  @Override
  public int getSizeInBytes() {
    return hostVertexAttribData.getBufferSizeInByte();
  }

  @Override
  public int getVertexCount() {
    return hostVertexAttribData.getVertexCount();
  }

  @Override
  public void updateGlResource(GlObjectRecord record) {
    hostVertexAttribData.update();
    GlVbo vbo = null;
    boolean needUpdate = false;
    if (record.resource == null) {
      vbo = glObjectCache.getGlWrapper().createVbo(GlConstants.GL_ARRAY_BUFFER);
      record.resource = vbo;
      needUpdate = true;
    } else if (record.version != hostVertexAttribData.version()) {
      vbo = (GlVbo) record.resource;
      needUpdate = true;
    }
    if (needUpdate) {
      Buffer buffer = hostVertexAttribData.getBuffer();
      vbo.setData(buffer);
      record.version = hostVertexAttribData.version();
      record.sizeInBytes = hostVertexAttribData.getBufferSizeInByte();
    }
  }

  @Override
  public GlVbo getGlObject() {
    return (GlVbo) glObjectCache.getGLResource(this);
  }
}
