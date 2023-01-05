package hana04.formats.mmd.generic.impl.pmd;

import com.google.common.collect.ImmutableList;
import hana04.formats.mmd.generic.api.ik.MmdIkChain;
import hana04.formats.mmd.generic.api.ik.MmdIkLink;
import hana04.formats.mmd.pmd.PmdIkChain;

import javax.vecmath.Vector3f;
import java.util.Arrays;

import static com.google.common.collect.ImmutableList.toImmutableList;

public class PmdIkChainAdaptor implements MmdIkChain {
  private final PmdIkChain pmdIkChain;
  private final ImmutableList<IkLink> ikLinks;

  public PmdIkChainAdaptor(PmdIkChain pmdIkChain) {
    this.pmdIkChain = pmdIkChain;
    ikLinks = Arrays.stream(pmdIkChain.chainBoneIndices).mapToObj(IkLink::new).collect(toImmutableList());
  }

  @Override
  public int boneIndex() {
    return pmdIkChain.boneIndex;
  }

  @Override
  public int effectorBoneIndex() {
    return pmdIkChain.targetBoneIndex;
  }

  @Override
  public int iterationCount() {
    return pmdIkChain.iterationCount;
  }

  @Override
  public float iterationAngleLimitRad() {
    return pmdIkChain.quarterIterationAngleLimitRad * 4;
  }

  @Override
  public ImmutableList<IkLink> ikLinks() {
    return ikLinks;
  }

  static class IkLink implements MmdIkLink {
    private final int boneIndex;
    private final Vector3f angleLowerBoundRad;
    private final Vector3f angleUpperBoundRad;

    IkLink(int boneIndex) {
      this.boneIndex = boneIndex;
      angleLowerBoundRad = new Vector3f((float) (-2 * Math.PI), (float) (-2 * Math.PI), (float) (-2 * Math.PI));
      angleUpperBoundRad = new Vector3f((float) (2 * Math.PI), (float) (2 * Math.PI), (float) (2 * Math.PI));
    }

    @Override
    public int boneIndex() {
      return boneIndex;
    }

    @Override
    public boolean isLimitingAngle() {
      return false;
    }

    @Override
    public Vector3f angleLowerBoundRad() {
      return angleLowerBoundRad;
    }

    @Override
    public Vector3f angleUpperBoundRad() {
      return angleUpperBoundRad;
    }
  }
}
