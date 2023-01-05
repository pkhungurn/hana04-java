package hana04.formats.mmd.generic.impl.pmx;

import com.google.common.collect.ImmutableList;
import hana04.formats.mmd.generic.api.MmdVertex;
import hana04.formats.mmd.pmx.PmxVertex;

import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.Optional;

public class PmxVertexAdaptor implements MmdVertex {
  private final PmxVertex pmxVertex;
  private final ImmutableList<Vector4f> additionalTexCoords;
  private final SkinningType skinningType;
  private final ImmutableList<Integer> boneIndices;
  private final ImmutableList<Float> boneWeights;
  private final Optional<SdefInfo> sdefInfo;

  public PmxVertexAdaptor(PmxVertex pmxVertex) {
    this.pmxVertex = pmxVertex;
    additionalTexCoords = ImmutableList.copyOf(pmxVertex.additionalTexCoords);

    if (pmxVertex.boneDataType == PmxVertex.BDEF1
        || pmxVertex.boneDataType == PmxVertex.BDEF2
        || pmxVertex.boneDataType == PmxVertex.BDEF4) {
      skinningType = SkinningType.BDEF;
    } else if (pmxVertex.boneDataType == PmxVertex.SDEF) {
      skinningType = SkinningType.QDEF;
    } else {
      throw new RuntimeException("Invalid bone skinning type.");
    }

    ImmutableList.Builder<Integer> boneIndicesBuilder = ImmutableList.builder();
    ImmutableList.Builder<Float> boneWeightsBuilder = ImmutableList.builder();
    for (int i = 0; i < 4; i++) {
      if (pmxVertex.boneIndices[i] < 0) {
        break;
      }
      boneIndicesBuilder.add(pmxVertex.boneIndices[i]);
      boneWeightsBuilder.add(pmxVertex.boneWeights[i]);
    }
    boneIndices = boneIndicesBuilder.build();
    boneWeights = boneWeightsBuilder.build();

    if (pmxVertex.boneDataType == PmxVertex.SDEF) {
      sdefInfo = Optional.of(new SdefInfo());
    } else {
      sdefInfo = Optional.empty();
    }
  }

  @Override
  public Point3f position() {
    return pmxVertex.position;
  }

  @Override
  public Vector3f normal() {
    return pmxVertex.normal;
  }

  @Override
  public Vector2f texCoords() {
    return pmxVertex.texCoords;
  }

  @Override
  public ImmutableList<Vector4f> additionalTexCoords() {
    return additionalTexCoords;
  }

  @Override
  public SkinningType skinningType() {
    return skinningType;
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
    return pmxVertex.edgeScale;
  }

  @Override
  public Optional<SdefInfo> sdefInfo() {
    return sdefInfo;
  }

  class SdefInfo implements MmdVertex.SdefInfo {

    @Override
    public Vector3f C() {
      return pmxVertex.C;
    }

    @Override
    public Vector3f R0() {
      return pmxVertex.R0;
    }

    @Override
    public Vector3f R1() {
      return pmxVertex.R1;
    }
  }
}
