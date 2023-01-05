package hana04.formats.mmd.generic.impl.pmx;

import hana04.formats.mmd.generic.api.MmdMaterial;
import hana04.formats.mmd.pmx.PmxMaterial;
import hana04.formats.mmd.pmx.PmxModel;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.Optional;

public class PmxMaterialAdaptor implements MmdMaterial {
  private final PmxMaterial pmxMaterial;
  private final Optional<EdgeInfo> edgeInfo;
  private final Optional<String> textureFileName;
  private final int vertexStart;

  public PmxMaterialAdaptor(PmxModel pmxModel, int materialIndex) {
    this.pmxMaterial = pmxModel.getMaterial(materialIndex);
    this.edgeInfo = (pmxMaterial.renderFlag & PmxMaterial.DRAW_EDGE) != 0
        ? Optional.of(new EdgeInfo())
        : Optional.empty();
    if (pmxMaterial.textureIndex >= 0) {
      String pmxTextureFileName = pmxModel.getRelativeTextureFileName(pmxMaterial.textureIndex);
      String textureFileName = pmxModel.getDirectory() + "/" + pmxTextureFileName;
      this.textureFileName = Optional.of(textureFileName);
    } else {
      this.textureFileName = Optional.empty();
    }

    int start = 0;
    for (int i = 0; i < materialIndex - 1; i++) {
      start += pmxModel.getMaterial(i).vertexCount;
    }
    vertexStart = start;
  }

  @Override
  public String japaneseName() {
    return pmxMaterial.japaneseName;
  }

  @Override
  public String englishName() {
    return pmxMaterial.englishName;
  }

  @Override
  public Vector4f diffuse() {
    return pmxMaterial.diffuse;
  }

  @Override
  public Vector3f specular() {
    return pmxMaterial.specular;
  }

  @Override
  public float shininess() {
    return pmxMaterial.shininess;
  }

  @Override
  public Vector3f ambient() {
    return pmxMaterial.ambient;
  }

  @Override
  public Optional<? extends EdgeInfo> edgeInfo() {
    return edgeInfo;
  }

  @Override
  public Optional<String> getTextureFileName() {
    return textureFileName;
  }

  @Override
  public int vertexStart() {
    return vertexStart;
  }

  @Override
  public int vertexCount() {
    return pmxMaterial.vertexCount;
  }

  class EdgeInfo implements MmdMaterial.EdgeInfo {

    @Override
    public Vector4f edgeColor() {
      return pmxMaterial.edgeColor;
    }

    @Override
    public float edgeSize() {
      return pmxMaterial.edgeSize;
    }
  }
}
