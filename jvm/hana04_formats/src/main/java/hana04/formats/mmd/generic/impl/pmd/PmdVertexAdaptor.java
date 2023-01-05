package hana04.formats.mmd.generic.impl.pmd;

import com.google.common.collect.ImmutableList;
import hana04.formats.mmd.generic.api.MmdVertex;
import hana04.formats.mmd.pmd.PmdModel;

import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.Optional;

public class PmdVertexAdaptor implements MmdVertex {
  private static final ImmutableList<Vector4f> EMPTY_TEX_COORDS_LIST = ImmutableList.of();

  private final PmdModel pmdModel;
  private final int vertexIndex;
  private final Point3f position;
  private final Vector3f normal;
  private final Vector2f texCoords;
  private final ImmutableList<Integer> boneIndices;
  private final ImmutableList<Float> boneWeights;

  public PmdVertexAdaptor(PmdModel pmdModel, int vertexIndex) {
    this.pmdModel = pmdModel;
    this.vertexIndex = vertexIndex;

    this.position = new Point3f(
        pmdModel.positions.get(3*vertexIndex),
        pmdModel.positions.get(3*vertexIndex+1),
        pmdModel.positions.get(3*vertexIndex+2));
    this.normal = new Vector3f(
        pmdModel.normals.get(3*vertexIndex),
        pmdModel.normals.get(3*vertexIndex+1),
        pmdModel.normals.get(3*vertexIndex+2));
    this.texCoords = new Vector2f(
        pmdModel.texCoords.get(2*vertexIndex),
        pmdModel.texCoords.get(2*vertexIndex+1));

    ImmutableList.Builder<Integer> boneIndicesBuilder = ImmutableList.builder();
    ImmutableList.Builder<Float> boneWeightsBuilders = ImmutableList.builder();
    for (int i = 0; i < 2; i++) {
      int boneIndex = pmdModel.vertexBoneIndices.get(2*vertexIndex + i);
      float boneWeight = pmdModel.vertexBoneBlendWeights.get(2*vertexIndex + i);
      if (boneIndex < 0) {
        break;
      }
      boneIndicesBuilder.add(boneIndex);
      boneWeightsBuilders.add(boneWeight);
    }
    boneIndices = boneIndicesBuilder.build();
    boneWeights = boneWeightsBuilders.build();
  }

  @Override
  public Point3f position() {
    return position;
  }

  @Override
  public Vector3f normal() {
    return normal;
  }

  @Override
  public Vector2f texCoords() {
    return texCoords;
  }

  @Override
  public ImmutableList<Vector4f> additionalTexCoords() {
    return EMPTY_TEX_COORDS_LIST;
  }

  @Override
  public SkinningType skinningType() {
    return SkinningType.BDEF;
  }

  @Override
  public ImmutableList<Integer> boneIndices() {
    return boneIndices;
  }

  @Override
  public ImmutableList<Float> boneWeights() {
    return boneWeights;
  }

  @Override
  public float edgeScale() {
    return 0;
  }

  @Override
  public Optional<? extends SdefInfo> sdefInfo() {
    return Optional.empty();
  }
}
