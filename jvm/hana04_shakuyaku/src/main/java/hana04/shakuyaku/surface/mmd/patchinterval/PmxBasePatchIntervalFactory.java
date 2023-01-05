package hana04.shakuyaku.surface.mmd.patchinterval;

import hana04.base.filesystem.FilePath;
import hana04.formats.mmd.pmx.PmxMaterial;
import hana04.formats.mmd.pmx.PmxModel;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.spectrum.util.SrgbUtil;
import hana04.shakuyaku.bsdf.classes.pmx.PmxBsdfs;
import hana04.shakuyaku.surface.ConcretePatchIntervalBuilder;
import hana04.shakuyaku.surface.PatchInterval;
import hana04.shakuyaku.surface.Surface;
import hana04.shakuyaku.surface.mmd.util.MaterialAmbientUsage;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;
import hana04.shakuyaku.texture.twodim.constant.ConstantTwoDimTextures;
import hana04.shakuyaku.texture.twodim.image.ImageTextures;
import org.apache.commons.io.FilenameUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class PmxBasePatchIntervalFactory {
  private final Provider<PmxBsdfs.PmxBsdfBuilder> pmxBsdfBuilder;
  private final Provider<ImageTextures.ImageTextureBuilder> imageTextureBuilder;
  private final Provider<ConstantTwoDimTextures.ConstantTwoDimTextureBuilder> constantWithAlphaTwoDimTextureBuilder;
  private final Provider<ConcretePatchIntervalBuilder> concretePatchIntervalBuilder;
  private final FileSystem fileSystem;

  @Inject
  public PmxBasePatchIntervalFactory(
    Provider<PmxBsdfs.PmxBsdfBuilder> pmxBsdfBuilder,
    Provider<ImageTextures.ImageTextureBuilder> imageTextureBuilder,
    Provider<ConstantTwoDimTextures.ConstantTwoDimTextureBuilder> constantTwoDimTextureBuilder,
    Provider<ConcretePatchIntervalBuilder> concretePatchIntervalBuilder,
    FileSystem fileSystem) {
    this.pmxBsdfBuilder = pmxBsdfBuilder;
    this.imageTextureBuilder = imageTextureBuilder;
    this.constantWithAlphaTwoDimTextureBuilder = constantTwoDimTextureBuilder;
    this.concretePatchIntervalBuilder = concretePatchIntervalBuilder;
    this.fileSystem = fileSystem;
  }

  public List<? extends PatchInterval> create(
    PmxModel pmxModel,
    Surface surface,
    MaterialAmbientUsage ambientUsage) {
    ArrayList<Integer> intervalBoundaries = new ArrayList<>();
    int start = 0;
    intervalBoundaries.add(start);
    for (int i = 0; i < pmxModel.getMaterialCount(); i++) {
      PmxMaterial material = pmxModel.getMaterial(i);
      int end = start + material.vertexCount / 3;
      intervalBoundaries.add(end);
      start = end;
    }

    HashMap<String, TextureTwoDim> textureByName = new HashMap<>();
    ArrayList<PatchInterval> result = new ArrayList<>();
    for (int i = 0; i < pmxModel.getMaterialCount(); i++) {
      final int index = i;

      PmxBsdfs.PmxBsdfBuilder bsdfFactory = pmxBsdfBuilder.get();
      PmxMaterial material = pmxModel.getMaterial(index);
      bsdfFactory.alpha((double) material.diffuse.w);
      boolean textureIsOpaque = true;
      if (material.textureIndex >= 0) {
        String pmxTextureFileName = pmxModel.getRelativeTextureFileName(material.textureIndex);
        String textureFileName = pmxModel.getDirectory() + "/" + pmxTextureFileName;
        TextureTwoDim texture = getTexture(textureFileName, textureByName);
        bsdfFactory.texture(texture);
        String textureExtension = FilenameUtils.getExtension(textureFileName).toLowerCase();
        textureIsOpaque =
          textureExtension.equals("bmp") || textureExtension.equals("jpg") || textureExtension.equals("gif");
      } else {
        bsdfFactory.texture(createWhiteTexture());
      }
      /*
      bsdfFactory.diffuseReflectance(new Rgb(
        SrgbUtil.srgbToLinear(material.diffuse.x),
        SrgbUtil.srgbToLinear(material.diffuse.y),
        SrgbUtil.srgbToLinear(material.diffuse.z)));
       */
      bsdfFactory.diffuseReflectance(new Rgb(
        material.diffuse.x,
        material.diffuse.y,
        material.diffuse.z));
      if (ambientUsage.shouldMaterialUseAmbient(material.japaneseName)) {
        /*
        bsdfFactory.ambientReflectance(new Rgb(
          SrgbUtil.srgbToLinear(material.ambient.x),
          SrgbUtil.srgbToLinear(material.ambient.y),
          SrgbUtil.srgbToLinear(material.ambient.z)));
         */
        bsdfFactory.ambientReflectance(new Rgb(
          material.ambient.x,
          material.ambient.y,
          material.ambient.z));
      } else {
        bsdfFactory.ambientReflectance(new Rgb(0, 0, 0));
      }

      bsdfFactory.displayBothSides(material.hasRenderBothSidesFlag());
      bsdfFactory.isOpaque(material.diffuse.w >= 1.0 && textureIsOpaque);
      if ((material.renderFlag & PmxMaterial.DRAW_EDGE) != 0) {
        bsdfFactory.drawEdge(true);
        bsdfFactory.edgeColor(new Rgb(
          SrgbUtil.srgbToLinear(material.edgeColor.x),
          SrgbUtil.srgbToLinear(material.edgeColor.y),
          SrgbUtil.srgbToLinear(material.edgeColor.z)));
        bsdfFactory.edgeThickness((double) material.edgeSize);
      }

      int startIndex_ = intervalBoundaries.get(index);
      int endIndex_ = intervalBoundaries.get(index + 1);
      PatchInterval patchInterval = concretePatchIntervalBuilder.get()
        .bsdf(bsdfFactory.build())
        .startPatchIndex(startIndex_)
        .endPatchIndex(endIndex_)
        .emitter(Optional.empty())
        .surface(surface)
        .build();
      result.add(patchInterval);
    }
    return result;
  }

  TextureTwoDim getTexture(String textureFileName, HashMap<String, TextureTwoDim> textureByName) {
    textureFileName = textureFileName.trim();
    if (textureByName.containsKey(textureFileName)) {
      return textureByName.get(textureFileName);
    } else {
      TextureTwoDim texture;
      try {
        Path texturePath = fileSystem.getPath(textureFileName);
        if (ImageTextures.supports(textureFileName) && Files.exists(texturePath)) {
          texture = imageTextureBuilder.get()
            .filePath(FilePath.relative(textureFileName))
            .build();
        } else {
          texture = createWhiteTexture();
        }
      } catch (Exception e) {
        texture = createWhiteTexture();
      }
      textureByName.put(textureFileName, texture);
      return texture;
    }
  }

  private TextureTwoDim createWhiteTexture() {
    return constantWithAlphaTwoDimTextureBuilder.get()
      .spectrum(new Rgb(1, 1, 1))
      .alpha(1.0)
      .build();
  }
}