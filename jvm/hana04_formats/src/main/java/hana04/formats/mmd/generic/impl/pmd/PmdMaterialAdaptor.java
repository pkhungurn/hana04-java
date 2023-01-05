package hana04.formats.mmd.generic.impl.pmd;

import hana04.formats.mmd.generic.api.MmdMaterial;
import hana04.formats.mmd.pmd.PmdMaterial;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.Optional;

public class PmdMaterialAdaptor implements MmdMaterial {
  private final PmdMaterial pmdMaterial;
  private final String japaneseName;
  private final String englishName;
  private final Vector4f diffuse;
  private final Optional<EdgeInfo> edgeInfo;
  private final Optional<String> textureFileName;

  PmdMaterialAdaptor(PmdMaterial pmdMaterial, int index) {
    this.pmdMaterial = pmdMaterial;
    this.japaneseName = "素材" + index;
    this.englishName = "Material" + index;
    this.diffuse = new Vector4f(pmdMaterial.diffuse.x, pmdMaterial.diffuse.y, pmdMaterial.diffuse.z, pmdMaterial.alpha);

    if (pmdMaterial.edgeFlag != 0) {
      this.edgeInfo = Optional.of(new EdgeInfo());
    } else {
      this.edgeInfo = Optional.empty();
    }

    this.textureFileName =
        pmdMaterial.textureFileName.isEmpty() ? Optional.empty() : Optional.of(pmdMaterial.textureFileName);
  }

  @Override
  public String japaneseName() {
    return japaneseName;
  }

  @Override
  public String englishName() {
    return englishName;
  }

  @Override
  public Vector4f diffuse() {
    return diffuse;
  }

  @Override
  public Vector3f specular() {
    return pmdMaterial.specular;
  }

  @Override
  public float shininess() {
    return pmdMaterial.shininess;
  }

  @Override
  public Vector3f ambient() {
    return pmdMaterial.ambient;
  }

  @Override
  public Optional<EdgeInfo> edgeInfo() {
    return edgeInfo;
  }

  @Override
  public Optional<String> getTextureFileName() {
    return textureFileName;
  }

  @Override
  public int vertexStart() {
    return pmdMaterial.vertexStart;
  }

  @Override
  public int vertexCount() {
    return pmdMaterial.vertexCount;
  }

  static class EdgeInfo implements MmdMaterial.EdgeInfo {
    private final Vector4f edgeColor = new Vector4f(0, 0, 0, 1);

    @Override
    public Vector4f edgeColor() {
      return edgeColor;
    }

    @Override
    public float edgeSize() {
      return 1.0f;
    }
  }
}
