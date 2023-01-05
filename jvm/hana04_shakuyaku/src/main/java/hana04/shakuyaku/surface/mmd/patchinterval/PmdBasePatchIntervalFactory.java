package hana04.shakuyaku.surface.mmd.patchinterval;

import hana04.base.filesystem.FilePath;
import hana04.formats.mmd.pmd.PmdMaterial;
import hana04.formats.mmd.pmd.PmdModel;
import hana04.gfxbase.spectrum.rgb.Rgb;
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
import javax.inject.Singleton;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Singleton
public class PmdBasePatchIntervalFactory {
  private final Provider<PmxBsdfs.PmxBsdfBuilder> pmxBsdfBuilder;
  private final Provider<ImageTextures.ImageTextureBuilder> imageTextureBuilder;
  private final Provider<ConstantTwoDimTextures.ConstantTwoDimTextureBuilder> constantTwoDimTextureBuilder;
  private final Provider<ConcretePatchIntervalBuilder> concretePatchIntervalBuilder;
  private final FileSystem fileSystem;

  @Inject
  public PmdBasePatchIntervalFactory(
    Provider<PmxBsdfs.PmxBsdfBuilder> pmxBsdfBuilder,
    Provider<ImageTextures.ImageTextureBuilder> imageTextureBuilder,
    Provider<ConstantTwoDimTextures.ConstantTwoDimTextureBuilder> constantTwoDimTextureBuilder,
    Provider<ConcretePatchIntervalBuilder> concretePatchIntervalBuilder,
    FileSystem fileSystem) {
    this.pmxBsdfBuilder = pmxBsdfBuilder;
    this.imageTextureBuilder = imageTextureBuilder;
    this.constantTwoDimTextureBuilder = constantTwoDimTextureBuilder;
    this.concretePatchIntervalBuilder = concretePatchIntervalBuilder;
    this.fileSystem = fileSystem;
  }

  public List<? extends PatchInterval> create(PmdModel pmdModel,
                                              Surface surface,
                                              MaterialAmbientUsage ambientUsage) {
    ArrayList<Integer> intervalBoundaries = new ArrayList<>();
    int start = 0;
    intervalBoundaries.add(start);
    for (int i = 0; i < pmdModel.materials.size(); i++) {
      PmdMaterial material = pmdModel.materials.get(i);
      int end = start + material.vertexCount / 3;
      intervalBoundaries.add(end);
      start = end;
    }

    HashMap<String, TextureTwoDim> textureByName = new HashMap<>();
    ArrayList<PatchInterval> result = new ArrayList<>();
    for (int i = 0; i < pmdModel.materials.size(); i++) {
      final int index = i;

      PmxBsdfs.PmxBsdfBuilder bsdfFactory = pmxBsdfBuilder.get();
      PmdMaterial material = pmdModel.materials.get(index);

      bsdfFactory.alpha((double) material.alpha);
      if (material.alpha <= 0.99) {
        bsdfFactory.displayBothSides(true);
      } else {
        bsdfFactory.displayBothSides(false);
      }
      boolean textureIsOpaque = true;
      if (!material.textureFileName.isEmpty()) {
        String textureFileName = material.textureFileName;
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
      if (ambientUsage.shouldMaterialUseAmbient(Integer.toString(i))) {
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
      bsdfFactory.isOpaque(material.alpha >= 1.0 && textureIsOpaque);
      bsdfFactory.ambientReflectance(new Rgb(material.ambient.x, material.ambient.y, material.ambient.z));

      if (material.edgeFlag != 0) {
        bsdfFactory.drawEdge(true);
        bsdfFactory.edgeThickness(1.0);
        bsdfFactory.edgeColor(new Rgb(0, 0, 0));
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

  private TextureTwoDim getTexture(String textureFileName, HashMap<String, TextureTwoDim> textureByName) {
    textureFileName = textureFileName.trim();
    if (textureByName.containsKey(textureFileName)) {
      return textureByName.get(textureFileName);
    } else {
      TextureTwoDim texture;
      if (ImageTextures.supports(textureFileName) && Files.exists(fileSystem.getPath(textureFileName))) {
        texture = imageTextureBuilder.get()
          .filePath(FilePath.relative(textureFileName))
          .build();
      } else {
        texture = createWhiteTexture();
      }
      textureByName.put(textureFileName, texture);
      return texture;
    }
  }

  private TextureTwoDim createWhiteTexture() {
    return constantTwoDimTextureBuilder.get()
      .spectrum(new Rgb(1, 1, 1))
      .alpha(1.0)
      .build();
  }
}
