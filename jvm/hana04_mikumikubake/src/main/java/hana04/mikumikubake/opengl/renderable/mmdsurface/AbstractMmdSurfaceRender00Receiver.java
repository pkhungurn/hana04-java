package hana04.mikumikubake.opengl.renderable.mmdsurface;

import hana04.base.caching.HanaUnwrapper;
import hana04.base.caching.Wrapped;
import hana04.botan.glasset.program.ProgramAsset;
import hana04.botan.glasset.provider.GlIndexProvider;
import hana04.botan.glasset.provider.GlProgramProvider;
import hana04.botan.glasset.provider.GlTexture2DProvider;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.spectrum.rgb.RgbSpace;
import hana04.mikumikubake.opengl.renderer00.Renderer00;
import hana04.mikumikubake.opengl.renderer00.extensions.GlBoneTransformTextureProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlMorphDisplacementTextureProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlMorphWeightTextureProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlSdefParamsTextureProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexBoneIndexProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexBoneWeightProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexIndexColorProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexMorphStartAndCountProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexNormalProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexPositionProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexSkinningInfoProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexTangentProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexTexCoordProvider;
import hana04.opengl.wrapper.GlConstants;
import hana04.opengl.wrapper.GlProgram;
import hana04.opengl.wrapper.GlTexture2D;
import hana04.opengl.wrapper.GlTextureUnit;
import hana04.opengl.wrapper.GlWrapper;
import hana04.shakuyaku.bsdf.Bsdf;
import hana04.shakuyaku.bsdf.classes.pmx.PmxBsdf;
import hana04.shakuyaku.surface.PatchInterval;
import hana04.shakuyaku.surface.SurfacePatchIntervalInfo;
import hana04.shakuyaku.surface.mmd.mmd.MmdSurface;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;
import hana04.shakuyaku.texture.twodim.image.ImageTexture;

import javax.vecmath.Tuple3d;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class AbstractMmdSurfaceRender00Receiver implements MmdSurfaceRenderer00 {
  protected final HanaUnwrapper unwrapper;
  protected final GlVertexPositionProvider vertexPositionProvider;
  protected final GlVertexNormalProvider vertexNormalProvider;
  protected final GlVertexTexCoordProvider vertexTexCoordProvider;
  protected final GlVertexTangentProvider vertexTangentProvider;
  protected final GlVertexBoneIndexProvider vertexBoneIndexProvider;
  protected final GlVertexBoneWeightProvider vertexBoneWeightProvider;
  protected final GlVertexMorphStartAndCountProvider vertexMorphStartAndCountProvider;
  protected final GlVertexSkinningInfoProvider vertexSkinningInfoProvider;
  protected final GlVertexIndexColorProvider vertexIndexColorProvider;
  protected final GlBoneTransformTextureProvider boneTransformTextureProvider;
  protected final GlMorphWeightTextureProvider morphWeightTextureProvider;
  protected final GlMorphDisplacementTextureProvider morphDisplacementTextureProvider;
  protected final GlSdefParamsTextureProvider sdefParamsTextureProvider;
  protected final GlIndexProvider indexProvider;
  protected final GlWrapper glWrapper;
  protected final SurfacePatchIntervalInfo.Vv patchIntervalInfo;

  public AbstractMmdSurfaceRender00Receiver(MmdSurface mesh, HanaUnwrapper unwrapper, GlWrapper glWrapper) {
    this.unwrapper = unwrapper;
    this.glWrapper = glWrapper;
    this.vertexPositionProvider = mesh.getExtension(GlVertexPositionProvider.class);
    this.vertexNormalProvider = mesh.getExtension(GlVertexNormalProvider.class);
    this.vertexTexCoordProvider = mesh.getExtension(GlVertexTexCoordProvider.class);
    this.vertexTangentProvider = mesh.getExtension(GlVertexTangentProvider.class);
    this.vertexBoneIndexProvider = mesh.getExtension(GlVertexBoneIndexProvider.class);
    this.vertexBoneWeightProvider = mesh.getExtension(GlVertexBoneWeightProvider.class);
    this.vertexMorphStartAndCountProvider = mesh.getExtension(GlVertexMorphStartAndCountProvider.class);
    this.vertexSkinningInfoProvider = mesh.getExtension(GlVertexSkinningInfoProvider.class);
    this.vertexIndexColorProvider = mesh.getExtension(GlVertexIndexColorProvider.class);
    this.boneTransformTextureProvider = mesh.getExtension(GlBoneTransformTextureProvider.class);
    this.morphWeightTextureProvider = mesh.getExtension(GlMorphWeightTextureProvider.class);
    this.morphDisplacementTextureProvider = mesh.getExtension(GlMorphDisplacementTextureProvider.class);
    this.sdefParamsTextureProvider = mesh.getExtension(GlSdefParamsTextureProvider.class);
    this.indexProvider = mesh.getExtension(GlIndexProvider.class);
    this.patchIntervalInfo = mesh.getExtension(SurfacePatchIntervalInfo.Vv.class);
  }

  protected abstract Wrapped<ProgramAsset> getProgramAsset();

  protected void useProgram(Consumer<GlProgram> code) {
    getProgramAsset().unwrap(unwrapper).getExtension(GlProgramProvider.class).getGlObject().use(code);
  }

  protected void setVertexAttributes(Renderer00 renderer00, GlProgram glProgram) {
    renderer00.vao.disableAllAttribute();
    glProgram.attribute("vert_position").ifPresent(attrib -> {
      vertexPositionProvider.getGlObject().use(vbo -> {
        attrib.setup(vertexPositionProvider.getAttributeSpec("vert_position"));
        attrib.setEnabled(true);
      });
    });
    glProgram.attribute("vert_normal").ifPresent(attrib -> {
      vertexNormalProvider.getGlObject().use(vbo -> {
        attrib.setup(vertexNormalProvider.getAttributeSpec("vert_normal"));
        attrib.setEnabled(true);
      });
    });
    glProgram.attribute("vert_texCoord").ifPresent(attrib -> {
      vertexTexCoordProvider.getGlObject().use(vbo -> {
        attrib.setup(vertexTexCoordProvider.getAttributeSpec("vert_texCoord"));
        attrib.setEnabled(true);
      });
    });
    glProgram.attribute("vert_tangent").ifPresent(attrib -> {
      vertexTangentProvider.getGlObject().use(vbo -> {
        attrib.setup(vertexTangentProvider.getAttributeSpec("vert_tangent"));
        attrib.setEnabled(true);
      });
    });
    glProgram.attribute("vert_boneWeight").ifPresent(attrib -> {
      vertexBoneWeightProvider.getGlObject().use(vbo -> {
        attrib.setup(vertexBoneWeightProvider.getAttributeSpec("vert_boneWeight"));
        attrib.setEnabled(true);
      });
    });
    glProgram.attribute("vert_boneIndex").ifPresent(attrib -> {
      vertexBoneIndexProvider.getGlObject().use(vbo -> {
        attrib.setup(vertexBoneIndexProvider.getAttributeSpec("vert_boneIndex"));
        attrib.setEnabled(true);
      });
    });
    glProgram.attribute("vert_morphStartAndCount").ifPresent(attrib -> {
      vertexMorphStartAndCountProvider.getGlObject().use(vbo -> {
        attrib.setup(vertexMorphStartAndCountProvider.getAttributeSpec("vert_morphStartAndCount"));
        attrib.setEnabled(true);
      });
    });
    glProgram.attribute("vert_skinningInfo").ifPresent(attrib -> {
      vertexSkinningInfoProvider.getGlObject().use(vbo -> {
        attrib.setup(vertexSkinningInfoProvider.getAttributeSpec("vert_skinningInfo"));
        attrib.setEnabled(true);
      });
    });
    glProgram.attribute("vert_indexColor").ifPresent(attrib -> {
      vertexIndexColorProvider.getGlObject().use(vbo -> {
        attrib.setup(vertexIndexColorProvider.getAttributeSpec("vert_indexColor"));
        attrib.setEnabled(true);
      });
    });
  }

  protected void setMeshUniforms(Renderer00 renderer00, GlProgram glProgram) {
    glProgram.uniform("mesh_boneTransform").ifPresent(uniform -> {
      Optional<GlTextureUnit> optionalTextureUnit = renderer00.popUnusedTextureUnit();
      optionalTextureUnit.ifPresent(textureUnit -> {
        boneTransformTextureProvider.getGlObject().bindTo(textureUnit);
        uniform.set1Int(textureUnit.getIndex());
      });
    });
    glProgram.uniform("mesh_morphDisplacement").ifPresent(uniform -> {
      Optional<GlTextureUnit> optionalTextureUnit = renderer00.popUnusedTextureUnit();
      optionalTextureUnit.ifPresent(textureUnit -> {
        morphDisplacementTextureProvider.getGlObject().bindTo(textureUnit);
        uniform.set1Int(textureUnit.getIndex());
      });
    });
    glProgram.uniform("mesh_morphWeight").ifPresent(uniform -> {
      Optional<GlTextureUnit> optionalTextureUnit = renderer00.popUnusedTextureUnit();
      optionalTextureUnit.ifPresent(textureUnit -> {
        morphWeightTextureProvider.getGlObject().bindTo(textureUnit);
        uniform.set1Int(textureUnit.getIndex());
      });
    });
    glProgram.uniform("mesh_sdefParams").ifPresent(uniform -> {
      Optional<GlTextureUnit> optionalTextureUnit = renderer00.popUnusedTextureUnit();
      optionalTextureUnit.ifPresent(textureUnit -> {
        sdefParamsTextureProvider.getGlObject().bindTo(textureUnit);
        uniform.set1Int(textureUnit.getIndex());
      });
    });
  }

  protected void setMatrixUniforms(Renderer00 renderer00, GlProgram glProgram) {
    glProgram.uniform(Renderer00.PROJECTION_MATRIX_VAR_NAME).ifPresent(uniform ->
        uniform.setMatrix4(renderer00.getBinding(Renderer00.PROJECTION_MATRIX_VAR_NAME)));
    glProgram.uniform(Renderer00.MODEL_MATRIX_VAR_NAME).ifPresent(uniform ->
        uniform.setMatrix4(renderer00.getBinding(Renderer00.MODEL_MATRIX_VAR_NAME)));
    glProgram.uniform(Renderer00.NORMAL_MATRIX_VAR_NAME).ifPresent(uniform ->
        uniform.setMatrix4(renderer00.getBinding(Renderer00.NORMAL_MATRIX_VAR_NAME)));
    glProgram.uniform(Renderer00.VIEW_MATRIX_VAR_NAME).ifPresent(uniform ->
        uniform.setMatrix4(renderer00.getBinding(Renderer00.VIEW_MATRIX_VAR_NAME)));
  }

  protected void renderPatch(PatchInterval patchInterval) {
    int startPatch = patchInterval.startPatchIndex();
    int endPatchIndex = patchInterval.endPatchIndex();
    glWrapper.drawElements(
        GlConstants.GL_TRIANGLES,
        (endPatchIndex - startPatch) * 3,
        startPatch * 3);
  }

  protected void renderPatchInterval(Renderer00 renderer00,
      GlProgram glProgram,
      PatchInterval patchInterval,
      PmxBsdf pmxBsdf) {
    if (pmxBsdf.displayBothSides().value()) {
      glWrapper.setFrontFace(GlConstants.GL_CW);
      renderPatch(patchInterval);
      glWrapper.setFrontFace(GlConstants.GL_CCW);
      renderPatch(patchInterval);
    } else {
      glWrapper.setFrontFace(GlConstants.GL_CCW);
      renderPatch(patchInterval);
    }
  }

  protected void unuseTextureUnits(Renderer00 renderer00, int numTextureUnitUsed) {
    for (int i = 0; i < numTextureUnitUsed; i++) {
      renderer00.pushUnusedTextureUnit();
    }
  }

  protected int preparePatchIntervalUniforms(Renderer00 renderer00, GlProgram glProgram, PmxBsdf pmxBsdf) {
    glProgram.uniform("mat_alpha").ifPresent(uniform -> {
      uniform.set1Float((float) pmxBsdf.alpha().value().doubleValue());
    });
    glProgram.uniform("mat_ambient").ifPresent(uniform -> {
      Rgb ambient = RgbSpace.I.convert(pmxBsdf.ambientReflectance().value());
      uniform.set3Float((float) ambient.x, (float) ambient.y, (float) ambient.z);
    });
    glProgram.uniform("mat_diffuse").ifPresent(uniform -> {
      Rgb diffuse = RgbSpace.I.convert(pmxBsdf.diffuseReflectance().value());
      uniform.set3Float((float) diffuse.x, (float) diffuse.y, (float) diffuse.z);
    });

    TextureTwoDim texture = unwrapper.unwrap(pmxBsdf.texture().value());
    int numTextureUnitUsed = 0;
    if (texture instanceof ImageTexture) {
      Optional<GlTextureUnit> optionalTextureUnit = renderer00.popUnusedTextureUnit();
      numTextureUnitUsed += optionalTextureUnit.isPresent() ? 1 : 0;
      if (optionalTextureUnit.isPresent()) {
        glProgram.uniform("mat_hasTexture").ifPresent(uniform ->
            uniform.set1Int(1));
        GlTexture2D texture2D = texture.getExtension(GlTexture2DProvider.class).getGlObject();
        texture2D.useWith(optionalTextureUnit.get());
        glProgram.uniform("mat_texture").ifPresent(uniform -> {
          uniform.set1Int(optionalTextureUnit.get().getIndex());
        });
      }
    } else {
      glProgram.uniform("mat_hasTexture").ifPresent(uniform ->
          uniform.set1Int(0));
    }
    return numTextureUnitUsed;
  }

  protected void setLightUniforms(Renderer00 renderer00, GlProgram glProgram) {
    glProgram.uniform("ambientLight_radiance").ifPresent(uniform -> {
      if (renderer00.hasBinding("ambientLight_radiance")) {
        Tuple3d ambientLightRadiance = renderer00.getBinding("ambientLight_radiance", Tuple3d.class);
        uniform.setTuple3(ambientLightRadiance);
      }
    });
    glProgram.uniform("dirLight_radiance").ifPresent(uniform -> {
      if (renderer00.hasBinding("dirLight_radiance")) {
        Tuple3d ambientLightRadiance = renderer00.getBinding("dirLight_radiance", Tuple3d.class);
        uniform.setTuple3(ambientLightRadiance);
      }
    });
    glProgram.uniform("dirLight_direction").ifPresent(uniform -> {
      if (renderer00.hasBinding("dirLight_direction")) {
        Tuple3d ambientLightRadiance = renderer00.getBinding("dirLight_direction", Tuple3d.class);
        uniform.setTuple3(ambientLightRadiance);
      }
    });
  }

  @Override
  public void render(Renderer00 renderer00, Supplier<Stream<Integer>> materialIndexStreamSupplier) {
    render(renderer00, materialIndexStreamSupplier.get());
  }

  public void render(Renderer00 renderer00, Stream<Integer> materialIndicesToRender) {
    this.patchIntervalInfo.update();
    useProgram(glProgram -> {
      setMatrixUniforms(renderer00, glProgram);
      setLightUniforms(renderer00, glProgram);
      setMeshUniforms(renderer00, glProgram);
      renderer00.vao.use(vao -> {
        setVertexAttributes(renderer00, glProgram);
        indexProvider.getGlObject().use(vbo -> {
          SurfacePatchIntervalInfo info = patchIntervalInfo.value();
          materialIndicesToRender.forEach(materialIndex -> {
            PatchInterval patchInterval = info.patchIntervals().get(materialIndex);
            Bsdf bsdf = unwrapper.unwrap(patchInterval.bsdf());
            if (!(bsdf instanceof PmxBsdf)) {
              return;
            }
            PmxBsdf pmxBsdf = (PmxBsdf) bsdf;
            int numTextureUnitUsed = preparePatchIntervalUniforms(renderer00, glProgram, pmxBsdf);
            renderPatchInterval(renderer00, glProgram, patchInterval, pmxBsdf);
            unuseTextureUnits(renderer00, numTextureUnitUsed);
          });
        });
        renderer00.unuseAllTextureUnits();
      });
    });
  }

  @Override
  public void render(Renderer00 renderer00) {
    this.patchIntervalInfo.update();
    render(renderer00, IntStream.range(0, patchIntervalInfo.value().getPatchIntervalCount()).boxed());
  }
}
