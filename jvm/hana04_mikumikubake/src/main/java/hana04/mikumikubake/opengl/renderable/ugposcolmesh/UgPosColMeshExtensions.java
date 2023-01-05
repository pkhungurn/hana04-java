package hana04.mikumikubake.opengl.renderable.ugposcolmesh;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.caching.CacheKey;
import hana04.base.caching.Cached;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.DirtinessObserver;
import hana04.base.changeprop.DirtinessObserverManager;
import hana04.base.changeprop.VersionManager;
import hana04.base.changeprop.VersionedSubject;
import hana04.botan.cache.GlObjectCache;
import hana04.botan.glasset.index.HostIndexData;
import hana04.botan.glasset.program.ProgramAsset;
import hana04.botan.glasset.program.ResourceProgramAssetExtensions;
import hana04.botan.glasset.provider.GlIndexProvider;
import hana04.botan.glasset.provider.GlProgramProvider;
import hana04.botan.glasset.vertex.HostVertexAttribData;
import hana04.mikumikubake.opengl.renderable.adaptors.HostIndexDataToGlIndexProviderAdaptor;
import hana04.mikumikubake.opengl.renderable.adaptors.HostVertexAttribDataToGlVertexAttribProviderAdaptor;
import hana04.mikumikubake.opengl.renderable.shaders.Constants;
import hana04.mikumikubake.opengl.renderer00.Renderer00;
import hana04.mikumikubake.opengl.renderer00.Renderer00Receiver;
import hana04.mikumikubake.opengl.renderer00.extensions.GlPrimitiveTypeProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexColorProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexPositionProvider;
import hana04.opengl.wrapper.GlAttributeSpec;
import hana04.opengl.wrapper.GlConstants;
import hana04.opengl.wrapper.GlProgram;
import hana04.opengl.wrapper.GlVbo;
import hana04.opengl.wrapper.GlWrapper;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple4d;
import javax.vecmath.Vector4d;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class UgPosColMeshExtensions {
  public static class HostData implements VersionedSubject {
    private static final GlAttributeSpec POSITION = new GlAttributeSpec(
      "restPosition", 3, GlConstants.GL_FLOAT, false, 4 * 3, 0);
    private static final GlAttributeSpec COLOR = new GlAttributeSpec(
      "color", 3, GlConstants.GL_FLOAT, false, 4 * 3, 0);

    private static final HashMap<String, GlAttributeSpec> vertexPositionAttributes = new HashMap<>();
    private static final HashMap<String, GlAttributeSpec> vertexColorAttributes = new HashMap<>();

    static {
      vertexPositionAttributes.put("restPosition", POSITION);
      vertexPositionAttributes.put("vert_position", POSITION);
      vertexPositionAttributes.put("vertex_position", POSITION);

      vertexColorAttributes.put("color", COLOR);
      vertexColorAttributes.put("vert_color", COLOR);
      vertexColorAttributes.put("vertex_color", COLOR);
    }

    ArrayList<Point3d> positions = new ArrayList<>();
    ArrayList<Vector4d> colors = new ArrayList<>();
    ArrayList<Integer> indices = new ArrayList<>();
    int primitiveType = GlConstants.GL_TRIANGLES;

    private final VersionManager versionManager = new VersionManager();
    private final DirtinessObserverManager dirtinessObserverManager = new DirtinessObserverManager(this);
    private final HostIndexData indexData;
    private final HostVertexAttribData positionData;
    private final HostVertexAttribData colorData;

    @HanaDeclareExtension(
      extensionClass = HostData.class,
      extensibleClass = UgPosColMesh.class)
    public HostData(UgPosColMesh ugPosColMesh) {
      this.indexData = new IndexData();
      this.positionData = new PositionData();
      this.colorData = new ColorData();
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

    public Point3d getPosition(int vertexIndex) {
      return new Point3d(positions.get(vertexIndex));
    }

    @Override
    public boolean isDirty() {
      return false;
    }

    public class Builder {
      private Vector4d currentColor = new Vector4d();
      private ArrayList<Point3d> builderPositions = new ArrayList<>();
      private ArrayList<Vector4d> builderColors = new ArrayList<>();
      private ArrayList<Integer> builderIndices = new ArrayList<>();
      private int builderPrimitiveType = GlConstants.GL_TRIANGLES;

      public Builder() {
        currentColor.set(1.0, 1.0, 1.0, 1.0);
      }

      public Builder addPosition(double x, double y, double z) {
        builderPositions.add(new Point3d(x, y, z));
        builderColors.add(new Vector4d(currentColor));
        return this;
      }

      public Builder addPosition(Tuple3d p) {
        return addPosition(p.x, p.y, p.z);
      }

      public Builder setColor(double r, double g, double b, double a) {
        currentColor.set(r, g, b, a);
        return this;
      }

      public Builder setColor(Tuple4d c) {
        return setColor(c.x, c.y, c.z, c.w);
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
          HostData.this.colors = builderColors;
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

    public synchronized Buffer getColorBuffer() {
      FloatBuffer buffer = ByteBuffer
        .allocateDirect(colors.size() * 3 * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer();
      for (int i = 0; i < colors.size(); i++) {
        buffer.put(3 * i + 0, (float) colors.get(i).x);
        buffer.put(3 * i + 1, (float) colors.get(i).y);
        buffer.put(3 * i + 2, (float) colors.get(i).z);
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
        return vertexColorAttributes.containsKey(name);
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

    class ColorData implements HostVertexAttribData {
      @Override
      public GlAttributeSpec getAttributeSpec(String name) {
        return vertexColorAttributes.get(name);
      }

      @Override
      public boolean hasAttribute(String name) {
        return vertexColorAttributes.containsKey(name);
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
        return HostData.this.getColorBuffer();
      }

      @Override
      public int getBufferSizeInByte() {
        return getVertexCount() * 3 * 4;
      }
    }

    public HostVertexAttribData getColorData() {
      return colorData;
    }

    public int getPrimitiveType() {
      return primitiveType;
    }
  }

  public static class GlPrimitiveTypeProvider_ implements GlPrimitiveTypeProvider {
    private final HostData hostData;

    @HanaDeclareExtension(
      extensionClass = GlPrimitiveTypeProvider.class,
      extensibleClass = UgPosColMesh.class)
    public GlPrimitiveTypeProvider_(UgPosColMesh ugPosColMesh) {
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
      extensibleClass = UgPosColMesh.class)
    public GlIndexProvider_(UgPosColMesh ugPosColMesh, GlObjectCache glObjectCache) {
      super(ugPosColMesh.getExtension(HostData.class).getIndexData(), glObjectCache);
    }
  }

  public static class GlVertexPositionProvider_
    extends HostVertexAttribDataToGlVertexAttribProviderAdaptor
    implements GlVertexPositionProvider {
    @HanaDeclareExtension(
      extensionClass = GlVertexPositionProvider.class,
      extensibleClass = UgPosColMesh.class)
    public GlVertexPositionProvider_(UgPosColMesh ugPosColMesh, GlObjectCache glObjectCache) {
      super(ugPosColMesh.getExtension(HostData.class).getPositionData(), glObjectCache);
    }
  }

  public static class GlVertexColorProvider_
    extends HostVertexAttribDataToGlVertexAttribProviderAdaptor
    implements GlVertexColorProvider {

    @HanaDeclareExtension(
      extensionClass = GlVertexColorProvider.class,
      extensibleClass = UgPosColMesh.class)
    public GlVertexColorProvider_(UgPosColMesh ugPosColMesh, GlObjectCache glObjectCache) {
      super(ugPosColMesh.getExtension(HostData.class).getColorData(), glObjectCache);
    }
  }

  public static class Renderer00Receiver_ implements Renderer00Receiver {
    private final GlPrimitiveTypeProvider primitiveTypeProvider;
    private final GlVertexPositionProvider vertexPositionProvider;
    private final GlVertexColorProvider vertexColorProvider;
    private final GlIndexProvider indexProvider;
    private final HanaUnwrapper unwrapper;
    private final GlWrapper glWrapper;
    final Wrapped<ProgramAsset> programAsset;

    @HanaDeclareExtension(
      extensibleClass = UgPosColMesh.class,
      extensionClass = Renderer00Receiver.class)
    public Renderer00Receiver_(UgPosColMesh ugPosColMesh, HanaUnwrapper unwrapper, GlWrapper glWrapper) {
      this.primitiveTypeProvider = ugPosColMesh.getExtension(GlPrimitiveTypeProvider.class);
      this.vertexPositionProvider = ugPosColMesh.getExtension(GlVertexPositionProvider.class);
      this.vertexColorProvider = ugPosColMesh.getExtension(GlVertexColorProvider.class);
      this.indexProvider = ugPosColMesh.getExtension(GlIndexProvider.class);
      this.unwrapper = unwrapper;
      this.glWrapper = glWrapper;
      programAsset = new Cached<>(CacheKey.builder()
        .protocol(ResourceProgramAssetExtensions.PROTOCOL)
        .addStringPart(Constants.VERT_COLOR_VERT_RESOURCE_NAME)
        .addStringPart(Constants.COLOR_FRAG_RESOURCE_NAME)
        .build());
    }

    @Override
    public void render(Renderer00 renderer00) {
      GlProgram glProgram = programAsset.unwrap(unwrapper).getExtension(GlProgramProvider.class).getGlObject();
      glProgram.use();

      glProgram.getUniform(Renderer00.PROJECTION_MATRIX_VAR_NAME).setMatrix4(renderer00.getBinding(Renderer00.PROJECTION_MATRIX_VAR_NAME));
      glProgram.getUniform(Renderer00.VIEW_MATRIX_VAR_NAME).setMatrix4(renderer00.getBinding(Renderer00.VIEW_MATRIX_VAR_NAME));
      glProgram.getUniform(Renderer00.MODEL_MATRIX_VAR_NAME).setMatrix4(renderer00.getBinding(Renderer00.MODEL_MATRIX_VAR_NAME));

      renderer00.vao.bind();
      {
        renderer00.vao.disableAllAttribute();
        {
          GlVbo vertexPositionVbo = vertexPositionProvider.getGlObject();
          vertexPositionVbo.bind();
          glProgram.getAttribute("vert_position").setup(vertexPositionProvider.getAttributeSpec("vert_position"));
          glProgram.getAttribute("vert_position").setEnabled(true);
          vertexPositionVbo.unbind();
        }
        {
          GlVbo vertexColorVbo = vertexColorProvider.getGlObject();
          vertexColorVbo.bind();
          glProgram.getAttribute("vert_color").setup(vertexColorProvider.getAttributeSpec("vert_color"));
          glProgram.getAttribute("vert_color").setEnabled(true);
          vertexColorVbo.unbind();
        }
      }
      {
        GlVbo indexVbo = indexProvider.getGlObject();
        indexVbo.bind();
        glWrapper.drawElements(primitiveTypeProvider.getPrimitiveType(), indexProvider.getIndexCount(), 0);
        indexVbo.unbind();
      }
      renderer00.vao.unbind();

      glProgram.unuse();
    }
  }
}
