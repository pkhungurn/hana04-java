package hana04.mikumikubake.opengl.renderable.mmdsurface;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.caching.CacheKey;
import hana04.base.caching.Cached;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.AbstractDerivedSubject;
import hana04.base.changeprop.VersionedValue;
import hana04.botan.cache.GlObjectCache;
import hana04.botan.glasset.index.HostIndexData;
import hana04.botan.glasset.program.ProgramAsset;
import hana04.botan.glasset.program.ResourceProgramAssetExtensions;
import hana04.botan.glasset.provider.GlIndexProvider;
import hana04.mikumikubake.opengl.renderable.adaptors.HostIndexDataToGlIndexProviderAdaptor;
import hana04.mikumikubake.opengl.renderable.shaders.Constants;
import hana04.mikumikubake.opengl.renderer00.Renderer00;
import hana04.opengl.wrapper.GlConstants;
import hana04.opengl.wrapper.GlWrapper;
import hana04.shakuyaku.sbtm.SbtmBaseMesh;
import hana04.shakuyaku.surface.PatchInterval;
import hana04.shakuyaku.surface.SurfacePatchIntervalInfo;
import hana04.shakuyaku.surface.geometry.trimesh.TriangleMeshInfo;

import javax.vecmath.Point3i;
import javax.vecmath.Tuple3d;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.stream.Stream;

public class MmdSurfaceWireframeExtensions {
  public static class HostIndexData_ extends AbstractDerivedSubject implements HostIndexData {
    private final VersionedValue<SbtmBaseMesh> baseMeshVv;

    @HanaDeclareExtension(
        extensibleClass = MmdSurfaceWireframe.class,
        extensionClass = HostIndexData.class)
    public HostIndexData_(MmdSurfaceWireframe mmdSurfaceWireframe) {
      this.baseMeshVv = mmdSurfaceWireframe.mmdSurface().getExtension(SbtmBaseMesh.Vv.class);
      this.baseMeshVv.addObserver(this);
    }

    @Override
    protected long updateInternal() {
      baseMeshVv.update();
      return Math.max(version() + 1, baseMeshVv.version());
    }

    @Override
    public int getIndexCount() {
      return baseMeshVv.value().getTriangleCount() * 6;
    }

    @Override
    public Buffer getBuffer() {
      TriangleMeshInfo triMesh = baseMeshVv.value();
      int triangelCount = triMesh.getTriangleCount();
      IntBuffer buffer = ByteBuffer.allocateDirect(triangelCount * 6 * 4)
          .order(ByteOrder.nativeOrder()).asIntBuffer();
      Point3i tri = new Point3i();
      for (int i = 0; i < triangelCount; i++) {
        triMesh.getTriangleVertexIndices(i, tri);
        buffer.put(6 * i + 0, tri.z);
        buffer.put(6 * i + 1, tri.y);
        buffer.put(6 * i + 2, tri.y);
        buffer.put(6 * i + 3, tri.x);
        buffer.put(6 * i + 4, tri.x);
        buffer.put(6 * i + 5, tri.z);
      }
      return buffer;
    }

    @Override
    public int getBufferSizeInByte() {
      return getIndexCount() * 4;
    }
  }

  public static class GlIndexProvider_ extends HostIndexDataToGlIndexProviderAdaptor {
    @HanaDeclareExtension(
        extensionClass = GlIndexProvider.class,
        extensibleClass = MmdSurfaceWireframe.class)
    public GlIndexProvider_(MmdSurfaceWireframe mmdSurfaceWireframe, GlObjectCache glObjectCache) {
      super(mmdSurfaceWireframe.getExtension(HostIndexData.class), glObjectCache);
    }
  }

  public static class MmdSurfaceRenderer00_ extends AbstractMmdSurfaceRender00Receiver implements MmdSurfaceRenderer00 {
    private final MmdSurfaceWireframe mmdSurfaceWireframe;
    private final Wrapped<ProgramAsset> program;
    private final GlIndexProvider wireframeIndexProvider;

    @HanaDeclareExtension(
        extensibleClass = MmdSurfaceWireframe.class,
        extensionClass = MmdSurfaceRenderer00.class)
    public MmdSurfaceRenderer00_(MmdSurfaceWireframe mmdSurfaceWireframe, HanaUnwrapper unwrapper, GlWrapper glWrapper) {
      super(mmdSurfaceWireframe.mmdSurface(), unwrapper, glWrapper);
      this.mmdSurfaceWireframe = mmdSurfaceWireframe;
      program = new Cached<>(CacheKey.builder()
          .protocol(ResourceProgramAssetExtensions.PROTOCOL)
          .addStringPart(Constants.MMD_POSED_MESH_VERT_RESOURCE_NAME)
          .addStringPart(Constants.MAT_COLOR_FRAG_RESOURCE_NAME)
          .build());
      wireframeIndexProvider = mmdSurfaceWireframe.getExtension(GlIndexProvider.class);
    }

    @Override
    protected Wrapped<ProgramAsset> getProgramAsset() {
      return program;
    }

    @Override
    public void render(Renderer00 renderer00, Stream<Integer> materialIndicesToRender) {
      this.patchIntervalInfo.update();
      useProgram(glProgram -> {
        setMatrixUniforms(renderer00, glProgram);
        setMeshUniforms(renderer00, glProgram);
        glProgram.uniform("mat_color").ifPresent(glUniform -> {
          if (renderer00.hasBinding("mat_color")) {
            Tuple3d color = renderer00.getBinding("mat_color", Tuple3d.class);
            glUniform.set4Float(color.x, color.y, color.z, 1.0);
          } else {
            glUniform.set4Float(0.0, 0.0, 0.0, 1.0);
          }
        });
        renderer00.vao.use(vao -> {
          setVertexAttributes(renderer00, glProgram);
          wireframeIndexProvider.getGlObject().use(vbo -> {
            SurfacePatchIntervalInfo info = patchIntervalInfo.value();
            materialIndicesToRender.forEach(materialIndex -> {
              PatchInterval patchInterval = info.patchIntervals().get(materialIndex);
              int startPatch = patchInterval.startPatchIndex();
              int endPatchIndex = patchInterval.endPatchIndex();
              glWrapper.drawElements(
                  GlConstants.GL_LINES,
                  (endPatchIndex - startPatch) * 6,
                  startPatch * 6);
            });
          });
          renderer00.unuseAllTextureUnits();
        });
      });
    }
  }
}
