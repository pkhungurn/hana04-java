package hana04.mikumikubake.opengl.renderer00.flow;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.caching.CacheKey;
import hana04.base.caching.Cached;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.caching.Wrapped;
import hana04.botan.glasset.program.ProgramAsset;
import hana04.botan.glasset.program.ResourceProgramAssetExtensions;
import hana04.mikumikubake.opengl.renderable.mmdsurface.AbstractMmdSurfaceRender00Receiver;
import hana04.mikumikubake.opengl.renderer00.Renderer00;
import hana04.mikumikubake.opengl.renderer00.extensions.GlBoneTransformTextureProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlMorphWeightTextureProvider;
import hana04.mikumikubake.opengl.renderer00.visibility.shaders.Constants;
import hana04.opengl.wrapper.GlConstants;
import hana04.opengl.wrapper.GlTextureRect;
import hana04.opengl.wrapper.GlTextureUnit;
import hana04.opengl.wrapper.GlWrapper;
import hana04.shakuyaku.bsdf.Bsdf;
import hana04.shakuyaku.bsdf.classes.pmx.PmxBsdf;
import hana04.shakuyaku.surface.PatchInterval;
import hana04.shakuyaku.surface.SurfacePatchIntervalInfo;
import hana04.shakuyaku.surface.mmd.mmd.MmdSurface;

import java.util.Optional;

public class MmdSurfaceExtension {
  public static class FlowMapRenderer_ extends AbstractMmdSurfaceRender00Receiver implements FlowMapRenderer {
    private final Wrapped<ProgramAsset> programAsset;

    @HanaDeclareExtension(
      extensibleClass = MmdSurface.class,
      extensionClass = FlowMapRenderer.class)
    public FlowMapRenderer_(MmdSurface mesh, HanaUnwrapper unwrapper, GlWrapper glWrapper) {
      super(mesh, unwrapper, glWrapper);
      this.programAsset = new Cached<>(CacheKey.builder()
        .protocol(ResourceProgramAssetExtensions.PROTOCOL)
        .addStringPart(Constants.MMD_POSE_MESH_START_TO_END_VERT_RESOURCE_NAME)
        .addStringPart(hana04.mikumikubake.opengl.renderer00.flow.shaders.Constants.FLOW_FRAG_RESOURCE_NAME)
        .build());
    }

    @Override
    protected Wrapped<ProgramAsset> getProgramAsset() {
      return programAsset;
    }

    @Override
    public void render(Renderer00 renderer00,
                       MmdSurface startSurface,
                       GlTextureRect positionMapTexture,
                       double positionEpsilon,
                       int outputWidth,
                       int outputHeight) {
      this.patchIntervalInfo.update();
      useProgram(glProgram -> {
        glProgram.uniform("output_width").ifPresent(uniform -> {
          uniform.set1Int(outputWidth);
        });
        glProgram.uniform("output_height").ifPresent(uniform -> {
          uniform.set1Int(outputHeight);
        });

        positionMapTexture.setMagFilter(GlConstants.GL_NEAREST);
        positionMapTexture.setMinFilter(GlConstants.GL_NEAREST);
        glProgram.uniform("positionMap_width").ifPresent(uniform -> {
          uniform.set1Int(positionMapTexture.getWidth());
        });
        glProgram.uniform("positionMap_height").ifPresent(uniform -> {
          uniform.set1Int(positionMapTexture.getHeight());
        });
        glProgram.uniform("positionMap_epsilon").ifPresent(uniform -> {
          uniform.set1Float((float) positionEpsilon);
        });
        glProgram.uniform("positionMap_texture").ifPresent(uniform -> {
          Optional<GlTextureUnit> optionalTextureUnit = renderer00.popUnusedTextureUnit();
          optionalTextureUnit.ifPresent(textureUnit -> {
            positionMapTexture.bindTo(textureUnit);
            uniform.set1Int(textureUnit.getIndex());
          });
        });

        GlBoneTransformTextureProvider startBoneTransformTextureProvider =
          startSurface.getExtension(GlBoneTransformTextureProvider.class);
        glProgram.uniform("mesh_boneTransform_start").ifPresent(uniform -> {
          Optional<GlTextureUnit> optionalTextureUnit = renderer00.popUnusedTextureUnit();
          optionalTextureUnit.ifPresent(textureUnit -> {
            startBoneTransformTextureProvider.getGlObject().bindTo(textureUnit);
            uniform.set1Int(textureUnit.getIndex());
          });
        });

        GlMorphWeightTextureProvider startMorphWightTextureProvider =
          startSurface.getExtension(GlMorphWeightTextureProvider.class);
        glProgram.uniform("mesh_morphWeight_start").ifPresent(uniform -> {
          Optional<GlTextureUnit> optionalTextureUnit = renderer00.popUnusedTextureUnit();
          optionalTextureUnit.ifPresent(textureUnit -> {
            startMorphWightTextureProvider.getGlObject().bindTo(textureUnit);
            uniform.set1Int(textureUnit.getIndex());
          });
        });

        setMatrixUniforms(renderer00, glProgram);
        setMeshUniforms(renderer00, glProgram);
        renderer00.vao.use(vao -> {
          setVertexAttributes(renderer00, glProgram);
          indexProvider.getGlObject().use(vbo -> {
            SurfacePatchIntervalInfo info = patchIntervalInfo.value();
            for (PatchInterval patchInterval : info.patchIntervals()) {
              Bsdf bsdf = unwrapper.unwrap(patchInterval.bsdf());
              if (!(bsdf instanceof PmxBsdf)) {
                continue;
              }
              PmxBsdf pmxBsdf = (PmxBsdf) bsdf;
              int numTextureUnitUsed = preparePatchIntervalUniforms(renderer00, glProgram, pmxBsdf);
              renderPatchInterval(renderer00, glProgram, patchInterval, pmxBsdf);
              unuseTextureUnits(renderer00, numTextureUnitUsed);
            }
          });
          renderer00.unuseAllTextureUnits();
        });
      });
    }
  }
}
