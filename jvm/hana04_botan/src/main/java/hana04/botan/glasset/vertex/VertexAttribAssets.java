package hana04.botan.glasset.vertex;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.botan.cache.GlObjectCache;
import hana04.botan.cache.GlObjectRecord;
import hana04.botan.glasset.provider.GlVertexAttribProvider;
import hana04.opengl.wrapper.GlAttributeSpec;
import hana04.opengl.wrapper.GlConstants;
import hana04.opengl.wrapper.GlVbo;

import java.nio.Buffer;

public class VertexAttribAssets {
  public static class GlProvider implements GlVertexAttribProvider {
    private final HostVertexAttribData hostVertexData;
    private final GlObjectCache glObjectCache;

    @HanaDeclareExtension(
      extensibleClass = VertexAttribAsset.class,
      extensionClass = GlVertexAttribProvider.class)
    public GlProvider(VertexAttribAsset vertexAsset, GlObjectCache glObjectCache) {
      hostVertexData = vertexAsset.getExtension(HostVertexAttribData.class);
      this.glObjectCache = glObjectCache;
    }

    @Override
    public GlAttributeSpec getAttributeSpec(String name) {
      return hostVertexData.getAttributeSpec(name);
    }

    @Override
    public boolean hasAttribute(String name) {
      return hostVertexData.hasAttribute(name);
    }

    @Override
    public int getNumBytesPerVertex() {
      return hostVertexData.getNumBytesPerVertex();
    }

    @Override
    public int getSizeInBytes() {
      return hostVertexData.getBufferSizeInByte();
    }

    @Override
    public int getVertexCount() {
      return hostVertexData.getVertexCount();
    }

    @Override
    public void updateGlResource(GlObjectRecord record) {
      GlVbo vbo = null;
      boolean needUpdate = false;
      if (record.resource == null) {
        vbo = glObjectCache.getGlWrapper().createVbo(GlConstants.GL_ARRAY_BUFFER);
        record.resource = vbo;
        needUpdate = true;
      } else if (record.version != hostVertexData.version()) {
        vbo = (GlVbo) record.resource;
        needUpdate = true;
      }
      if (needUpdate) {
        Buffer buffer = hostVertexData.getBuffer();
        vbo.setData(buffer);
        record.version = hostVertexData.version();
        record.sizeInBytes = hostVertexData.getBufferSizeInByte();
      }
    }

    @Override
    public GlVbo getGlObject() {
      return (GlVbo) glObjectCache.getGLResource(this);
    }
  }
}
