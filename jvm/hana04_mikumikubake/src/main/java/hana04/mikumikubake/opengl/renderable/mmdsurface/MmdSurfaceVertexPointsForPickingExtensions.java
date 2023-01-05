package hana04.mikumikubake.opengl.renderable.mmdsurface;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.caching.CacheKey;
import hana04.base.caching.Cached;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.caching.Wrapped;
import hana04.botan.glasset.program.ProgramAsset;
import hana04.botan.glasset.program.ResourceProgramAssetExtensions;
import hana04.mikumikubake.opengl.renderable.shaders.Constants;
import hana04.mikumikubake.opengl.renderer00.Renderer00;
import hana04.opengl.wrapper.GlConstants;
import hana04.opengl.wrapper.GlWrapper;
import hana04.shakuyaku.surface.PatchInterval;
import hana04.shakuyaku.surface.SurfacePatchIntervalInfo;

import java.util.stream.Stream;

public class MmdSurfaceVertexPointsForPickingExtensions {
  public static class MmdSurfaceRenderer00_ extends AbstractMmdSurfaceRender00Receiver implements MmdSurfaceRenderer00 {
    private final Wrapped<ProgramAsset> program;

    @HanaDeclareExtension(
        extensibleClass = MmdSurfaceVertexPointsForPicking.class,
        extensionClass = MmdSurfaceRenderer00.class)
    public MmdSurfaceRenderer00_(MmdSurfaceVertexPointsForPicking mmdSurfaceVertexPointsForPicking,
        HanaUnwrapper unwrapper,
        GlWrapper glWrapper) {
      super(mmdSurfaceVertexPointsForPicking.mmdSurface(), unwrapper, glWrapper);
      program = new Cached<>(CacheKey.builder()
          .protocol(ResourceProgramAssetExtensions.PROTOCOL)
          .addStringPart(Constants.MMD_POSED_MESH_VERTEX_INDEX_COLOR_VERT_RESOURCE_NAME)
          .addStringPart(Constants.COLOR_FRAG_RESOURCE_NAME)
          .build());
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
        renderer00.vao.use(vao -> {
          setVertexAttributes(renderer00, glProgram);
          indexProvider.getGlObject().use(vbo -> {
            SurfacePatchIntervalInfo info = patchIntervalInfo.value();
            materialIndicesToRender.forEach(materialIndex -> {
              PatchInterval patchInterval = info.patchIntervals().get(materialIndex);
              int startPatch = patchInterval.startPatchIndex();
              int endPatchIndex = patchInterval.endPatchIndex();
              glWrapper.drawElements(
                  GlConstants.GL_POINTS,
                  (endPatchIndex - startPatch) * 3,
                  startPatch * 3);
            });
          });
          renderer00.unuseAllTextureUnits();
        });
      });
    }
  }
}
