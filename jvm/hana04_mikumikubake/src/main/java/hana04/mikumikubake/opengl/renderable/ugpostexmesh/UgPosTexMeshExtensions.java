package hana04.mikumikubake.opengl.renderable.ugpostexmesh;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.DirtinessObserver;
import hana04.base.changeprop.DirtinessObserverManager;
import hana04.base.changeprop.VersionManager;
import hana04.base.changeprop.VersionedSubject;
import hana04.botan.cache.GlObjectCache;
import hana04.botan.glasset.index.HostIndexData;
import hana04.botan.glasset.provider.GlIndexProvider;
import hana04.botan.glasset.vertex.HostVertexAttribData;
import hana04.mikumikubake.opengl.renderable.adaptors.HostIndexDataToGlIndexProviderAdaptor;
import hana04.mikumikubake.opengl.renderable.adaptors.HostVertexAttribDataToGlVertexAttribProviderAdaptor;
import hana04.mikumikubake.opengl.renderer00.extensions.GlPrimitiveTypeProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexPositionProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexTexCoordProvider;
import hana04.opengl.wrapper.GlAttributeSpec;
import hana04.opengl.wrapper.GlConstants;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector2d;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class UgPosTexMeshExtensions {
  public static class HostData implements VersionedSubject {
    private static final GlAttributeSpec POSITION = new GlAttributeSpec(
      "position", 3, GlConstants.GL_FLOAT, false, 4 * 3, 0);
    private static final GlAttributeSpec TEX_COORD = new GlAttributeSpec(
      "texCoord", 2, GlConstants.GL_FLOAT, false, 4 * 2, 0);

    private static final HashMap<String, GlAttributeSpec> vertexPositionAttributes = new HashMap<>();
    private static final HashMap<String, GlAttributeSpec> vertexTexCoordAttributes = new HashMap<>();

    static {
      vertexPositionAttributes.put("position", POSITION);
      vertexPositionAttributes.put("vert_position", POSITION);
      vertexPositionAttributes.put("vertex_position", POSITION);

      vertexTexCoordAttributes.put("texCoord", TEX_COORD);
      vertexTexCoordAttributes.put("vert_texCoord", TEX_COORD);
      vertexTexCoordAttributes.put("vertex_texCoord", TEX_COORD);
    }

    ArrayList<Point3d> positions = new ArrayList<>();
    ArrayList<Vector2d> texCoords = new ArrayList<>();
    ArrayList<Integer> indices = new ArrayList<>();
    int primitiveType = GlConstants.GL_TRIANGLES;

    private final VersionManager versionManager = new VersionManager();
    private final DirtinessObserverManager dirtinessObserverManager = new DirtinessObserverManager(this);
    private final HostIndexData indexData;
    private final HostVertexAttribData positionData;
    private final HostVertexAttribData texCoordData;

    @HanaDeclareExtension(
      extensionClass = HostData.class,
      extensibleClass = UgPosTexMesh.class)
    public HostData(UgPosTexMesh ugPosTexMesh) {
      this.indexData = new IndexData();
      this.positionData = new PositionData();
      this.texCoordData = new TexCoordData();
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

    public Point3d getPosition(int index) {
      return new Point3d(positions.get(index));
    }

    @Override
    public boolean isDirty() {
      return false;
    }

    public class Builder {
      private Vector2d currentTexCoord = new Vector2d();
      private ArrayList<Point3d> builderPositions = new ArrayList<>();
      private ArrayList<Vector2d> builderTexCoords = new ArrayList<>();
      private ArrayList<Integer> builderIndices = new ArrayList<>();
      private int builderPrimitiveType = GlConstants.GL_TRIANGLES;

      public Builder() {
        currentTexCoord.set(0.0, 0.0);
      }

      public Builder addPosition(double x, double y, double z) {
        builderPositions.add(new Point3d(x, y, z));
        builderTexCoords.add(new Vector2d(currentTexCoord));
        return this;
      }

      public Builder addPosition(Tuple3d p) {
        return addPosition(p.x, p.y, p.z);
      }

      public Builder setTexCoord(double u, double v) {
        currentTexCoord.set(u, v);
        return this;
      }

      public Builder setTexCoord(Tuple2d c) {
        return setTexCoord(c.x, c.y);
      }

      public Builder addIndex(int index) {
        builderIndices.add(index);
        return this;
      }

      public Builder setPrimitiveType(int primitiveType) {
        this.builderPrimitiveType = primitiveType;
        return this;
      }

      public HostData endBuild() {
        synchronized (HostData.this) {
          HostData.this.positions = builderPositions;
          HostData.this.texCoords = builderTexCoords;
          HostData.this.indices = builderIndices;
          HostData.this.primitiveType = builderPrimitiveType;
        }
        versionManager.bumpVersion();
        dirtinessObserverManager.notifyObservers();
        return HostData.this;
      }
    }

    public Builder startBuild() {
      return new Builder();
    }

    public synchronized Buffer getIndexBuffer() {
      IntBuffer buffer = ByteBuffer.allocateDirect(indices.size() * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
      for (int i = 0; i < indices.size(); i++) {
        buffer.put(i, indices.get(i));
      }
      return buffer;
    }

    public synchronized int getIndexCount() {
      return indices.size();
    }

    public class IndexData implements HostIndexData {

      @Override
      public int getIndexCount() {
        return HostData.this.getIndexCount();
      }

      @Override
      public void addObserver(DirtinessObserver observer) {
        HostData.this.addObserver(observer);
      }

      @Override
      public void removeObserver(DirtinessObserver observer) {
        HostData.this.removeObserver(observer);
      }

      @Override
      public long version() {
        return HostData.this.version();
      }

      @Override
      public void update() {
        HostData.this.update();
      }

      @Override
      public boolean isDirty() {
        return HostData.this.isDirty();
      }

      @Override
      public Buffer getBuffer() {
        return getIndexBuffer();
      }

      @Override
      public int getBufferSizeInByte() {
        return getIndexCount() * 4;
      }
    }

    public HostIndexData getIndexData() {
      return indexData;
    }

    public synchronized int getVertexCount() {
      return positions.size();
    }

    public synchronized Buffer getPositionBuffer() {
      FloatBuffer buffer = ByteBuffer
        .allocateDirect(positions.size() * 3 * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer();
      for (int i = 0; i < positions.size(); i++) {
        buffer.put(3 * i + 0, (float) positions.get(i).x);
        buffer.put(3 * i + 1, (float) positions.get(i).y);
        buffer.put(3 * i + 2, (float) positions.get(i).z);
      }
      return buffer;
    }

    public synchronized Buffer getTexCoordBuffer() {
      FloatBuffer buffer = ByteBuffer
        .allocateDirect(texCoords.size() * 2 * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer();
      for (int i = 0; i < texCoords.size(); i++) {
        buffer.put(2 * i + 0, (float) texCoords.get(i).x);
        buffer.put(2 * i + 1, (float) texCoords.get(i).y);
      }
      return buffer;
    }

    class PositionData implements HostVertexAttribData {
      @Override
      public GlAttributeSpec getAttributeSpec(String name) {
        return vertexPositionAttributes.get(name);
      }

      @Override
      public boolean hasAttribute(String name) {
        return vertexTexCoordAttributes.containsKey(name);
      }

      @Override
      public int getNumBytesPerVertex() {
        return 4 * 3;
      }

      @Override
      public int getVertexCount() {
        return HostData.this.getVertexCount();
      }

      @Override
      public void addObserver(DirtinessObserver observer) {
        HostData.this.addObserver(observer);
      }

      @Override
      public void removeObserver(DirtinessObserver observer) {
        HostData.this.removeObserver(observer);
      }

      @Override
      public long version() {
        return HostData.this.version();
      }

      @Override
      public void update() {
        HostData.this.update();
      }

      @Override
      public boolean isDirty() {
        return HostData.this.isDirty();
      }

      @Override
      public Buffer getBuffer() {
        return HostData.this.getPositionBuffer();
      }

      @Override
      public int getBufferSizeInByte() {
        return getVertexCount() * 3 * 4;
      }
    }

    public HostVertexAttribData getPositionData() {
      return positionData;
    }

    class TexCoordData implements HostVertexAttribData {
      @Override
      public GlAttributeSpec getAttributeSpec(String name) {
        return vertexTexCoordAttributes.get(name);
      }

      @Override
      public boolean hasAttribute(String name) {
        return vertexTexCoordAttributes.containsKey(name);
      }

      @Override
      public int getNumBytesPerVertex() {
        return 4 * 3;
      }

      @Override
      public int getVertexCount() {
        return HostData.this.getVertexCount();
      }

      @Override
      public void addObserver(DirtinessObserver observer) {
        HostData.this.addObserver(observer);
      }

      @Override
      public void removeObserver(DirtinessObserver observer) {
        HostData.this.removeObserver(observer);
      }

      @Override
      public long version() {
        return HostData.this.version();
      }

      @Override
      public void update() {
        HostData.this.update();
      }

      @Override
      public boolean isDirty() {
        return HostData.this.isDirty();
      }

      @Override
      public Buffer getBuffer() {
        return HostData.this.getTexCoordBuffer();
      }

      @Override
      public int getBufferSizeInByte() {
        return getVertexCount() * 3 * 4;
      }
    }

    public HostVertexAttribData getTexCoordData() {
      return texCoordData;
    }

    public int getPrimitiveType() {
      return primitiveType;
    }
  }

  public static class GlPrimitiveTypeProvider_ implements GlPrimitiveTypeProvider {
    private final HostData hostData;

    @HanaDeclareExtension(
      extensionClass = GlPrimitiveTypeProvider.class,
      extensibleClass = UgPosTexMesh.class)
    GlPrimitiveTypeProvider_(UgPosTexMesh ugPosColMesh) {
      this.hostData = ugPosColMesh.getExtension(HostData.class);
    }

    @Override
    public int getPrimitiveType() {
      return hostData.getPrimitiveType();
    }
  }

  public static class GlIndexProvider_ extends HostIndexDataToGlIndexProviderAdaptor {
    @HanaDeclareExtension(
      extensionClass = GlIndexProvider.class,
      extensibleClass = UgPosTexMesh.class)
    GlIndexProvider_(UgPosTexMesh ugPosColMesh, GlObjectCache glObjectCache) {
      super(ugPosColMesh.getExtension(HostData.class).getIndexData(), glObjectCache);
    }
  }

  public static class GlVertexPositionProvider_
    extends HostVertexAttribDataToGlVertexAttribProviderAdaptor
    implements GlVertexPositionProvider {
    @HanaDeclareExtension(
      extensionClass = GlVertexPositionProvider.class,
      extensibleClass = UgPosTexMesh.class)
    GlVertexPositionProvider_(UgPosTexMesh ugPosTexMesh, GlObjectCache glObjectCache) {
      super(ugPosTexMesh.getExtension(HostData.class).getPositionData(), glObjectCache);
    }
  }

  public static class GlVertexTexCoordProvider_
    extends HostVertexAttribDataToGlVertexAttribProviderAdaptor
    implements GlVertexTexCoordProvider {

    @HanaDeclareExtension(
      extensionClass = GlVertexTexCoordProvider.class,
      extensibleClass = UgPosTexMesh.class)
    GlVertexTexCoordProvider_(UgPosTexMesh ugPosColMesh, GlObjectCache glObjectCache) {
      super(ugPosColMesh.getExtension(HostData.class).getTexCoordData(), glObjectCache);
    }
  }
}
