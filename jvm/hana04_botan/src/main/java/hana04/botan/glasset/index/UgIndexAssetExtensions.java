package hana04.botan.glasset.index;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.DirtinessObserver;
import hana04.base.changeprop.DirtinessObserverManager;
import hana04.base.changeprop.VersionManager;
import hana04.botan.cache.GlObjectCache;
import hana04.botan.cache.GlObjectRecord;
import hana04.botan.glasset.provider.GlIndexProvider;
import hana04.opengl.wrapper.GlConstants;
import hana04.opengl.wrapper.GlVbo;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class UgIndexAssetExtensions {
  public static class HostData implements HostIndexData {
    ArrayList<Integer> indices = new ArrayList<>();
    private final VersionManager versionManager = new VersionManager();
    private final DirtinessObserverManager dirtinessObserverManager = new DirtinessObserverManager(this);

    @HanaDeclareExtension(
      extensibleClass = UgIndexAsset.class,
      extensionClass = HostIndexData.class)
    public HostData(UgIndexAsset ugIndexAsset) {
      // NO-OP
    }

    @Override
    public synchronized Buffer getBuffer() {
      IntBuffer buffer = ByteBuffer.allocateDirect(indices.size() * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
      for (int i = 0; i < indices.size(); i++) {
        buffer.put(i, indices.get(i));
      }
      return buffer;
    }

    @Override
    public synchronized int getBufferSizeInByte() {
      return indices.size() * 4;
    }

    @Override
    public synchronized int getIndexCount() {
      return indices.size();
    }

    @Override
    public void addObserver(DirtinessObserver observer) {
      dirtinessObserverManager.addObserver(observer);
    }

    @Override
    public void removeObserver(DirtinessObserver observer) {
      dirtinessObserverManager.removeObserver(observer);
    }

    @Override
    public long version() {
      return versionManager.getVersion();
    }

    @Override
    public void update() {
      // NO-OP
    }

    @Override
    public boolean isDirty() {
      return false;
    }

    public class Builder {
      private ArrayList<Integer> builderIndices = new ArrayList<>();

      Builder() {
        builderIndices.clear();
      }

      public Builder add(int index) {
        builderIndices.add(index);
        return this;
      }

      public HostData endBuild() {
        synchronized (HostData.this) {
          HostData.this.indices = builderIndices;
        }
        versionManager.bumpVersion();
        dirtinessObserverManager.notifyObservers();
        return HostData.this;
      }
    }

    public Builder startBuild() {
      return new Builder();
    }
  }

  public static class HostDataBuilder {
    private final HostData hostData;

    @HanaDeclareExtension(
      extensionClass = HostDataBuilder.class,
      extensibleClass = UgIndexAsset.class)
    public HostDataBuilder(UgIndexAsset ugIndexAsset) {
      hostData = (HostData) ugIndexAsset.getExtension(HostIndexData.class);
    }

    public HostData.Builder startBuild() {
      return hostData.startBuild();
    }
  }

  public static class GlProvider implements GlIndexProvider {
    private final HostIndexData hostIndexData;
    private final GlObjectCache glObjectCache;

    @HanaDeclareExtension(
      extensibleClass = UgIndexAsset.class,
      extensionClass = GlIndexProvider.class)
    public GlProvider(UgIndexAsset ugIndexAsset, GlObjectCache glObjectCache) {
      hostIndexData = ugIndexAsset.getExtension(HostIndexData.class);
      this.glObjectCache = glObjectCache;
    }

    @Override
    public void updateGlResource(GlObjectRecord record) {
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
}
