package hana04.mikumikubake.opengl.renderable.adaptors;

import hana04.botan.cache.GlObjectCache;
import hana04.botan.cache.GlObjectRecord;
import hana04.botan.glasset.index.HostIndexData;
import hana04.botan.glasset.provider.GlIndexProvider;
import hana04.opengl.wrapper.GlConstants;
import hana04.opengl.wrapper.GlVbo;

import java.nio.Buffer;

public class HostIndexDataToGlIndexProviderAdaptor implements GlIndexProvider {
  private final HostIndexData hostIndexData;
  private final GlObjectCache glObjectCache;

  public HostIndexDataToGlIndexProviderAdaptor(HostIndexData hostIndexData, GlObjectCache glObjectCache) {
    this.hostIndexData = hostIndexData;
    this.glObjectCache = glObjectCache;
  }

  @Override
  public void updateGlResource(GlObjectRecord record) {
    hostIndexData.update();
    GlVbo vbo = null;
    boolean needUpdate = false;
    if (record.resource == null) {
      vbo = glObjectCache.getGlWrapper().createVbo(GlConstants.GL_ELEMENT_ARRAY_BUFFER);
      record.resource = vbo;
      needUpdate = true;
    } else if (record.version != hostIndexData.version()) {
      vbo = (GlVbo) record.resource;
      needUpdate = true;
    }
    if (needUpdate) {
      Buffer buffer = hostIndexData.getBuffer();
      vbo.setData(buffer);
      record.version = hostIndexData.version();
      record.sizeInBytes = hostIndexData.getBufferSizeInByte();
    }
  }

  @Override
  public GlVbo getGlObject() {
    return (GlVbo) glObjectCache.getGLResource(this);
  }

  @Override
  public int getIndexCount() {
    return hostIndexData.getIndexCount();
  }
}
