package hana04.formats.mmd.generic.impl.pmx;

import com.google.common.collect.ImmutableList;
import hana04.formats.mmd.generic.api.MmdBone;
import hana04.formats.mmd.generic.api.MmdBoneFuyoInfo;
import hana04.formats.mmd.generic.api.ik.MmdIkChain;
import hana04.formats.mmd.generic.api.ik.MmdIkLink;
import hana04.formats.mmd.pmx.PmxBone;
import hana04.formats.mmd.pmx.PmxIkLink;
import hana04.formats.mmd.pmx.PmxModel;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.Optional;

import static com.google.common.collect.ImmutableList.toImmutableList;

public class PmxBoneAdaptor implements MmdBone {
  private final PmxModel pmxModel;
  private final PmxBone pmxBone;
  private final Optional<MmdBoneFuyoInfo> fuyoInfo;
  private final Optional<MmdIkChain> ikChain;
  private final boolean isKnee;

  PmxBoneAdaptor(PmxBone pmxBone, PmxModel pmxModel) {
    this.pmxBone = pmxBone;
    this.pmxModel = pmxModel;

    if (pmxBone.isCopyingRotation() || pmxBone.isCopyingTranslation()) {
      fuyoInfo = Optional.of(new FuyoInfo());
    } else {
      fuyoInfo = Optional.empty();
    }

    if (pmxBone.isIk()) {
      ikChain = Optional.of(new IkChain());
    } else {
      ikChain = Optional.empty();
    }

    isKnee = pmxBone.japaneseName.contains("ひざ");
  }

  @Override
  public String japaneseName() {
    return pmxBone.japaneseName;
  }

  @Override
  public String englishName() {
    return pmxBone.englishName;
  }

  @Override
  public int index() {
    return pmxBone.boneIndex;
  }

  @Override
  public int transformLevel() {
    return pmxBone.transformLevel;
  }

  @Override
  public boolean transformsAfterPhysics() {
    return pmxBone.transformAfterPhysics();
  }

  @Override
  public Optional<Integer> parentIndex() {
    if (pmxBone.parentIndex == pmxBone.boneIndex || pmxBone.parentIndex < 0) {
      return Optional.empty();
    } else {
      return Optional.of(pmxBone.parentIndex);
    }
  }

  @Override
  public Vector3f translationFromParent() {
    return pmxBone.displacementFromParent;
  }

  @Override
  public Point3f restPosition() {
    return pmxBone.position;
  }

  @Override
  public Optional<MmdBoneFuyoInfo> fuyoInfo() {
    return fuyoInfo;
  }

  @Override
  public Optional<MmdIkChain> ikChain() {
    return ikChain;
  }

  @Override
  public Optional<Vector3f> fixedAxis() {
    if (pmxBone.usesFixedAxis()) {
      return Optional.of(pmxBone.axis);
    } else {
      return Optional.empty();
    }
  }

  @Override
  public boolean isKnee() {
    return isKnee;
  }

  class FuyoInfo implements MmdBoneFuyoInfo {
    @Override
    public int getSourceBoneIndex() {
      return pmxBone.copySourceBoneIndex;
    }

    @Override
    public float getCoefficient() {
      return pmxBone.copyRate;
    }

    @Override
    public boolean isCombiningRotation() {
      return pmxBone.isCopyingRotation();
    }

    @Override
    public boolean isCombiningTranslation() {
      return pmxBone.isCopyingTranslation();
    }
  }

  static class IkLink implements MmdIkLink {
    private final PmxIkLink pmxIkLink;
    private final boolean isLimitingAngle;
    private final Vector3f angleLowerBoundRad = new Vector3f();
    private final Vector3f angleUpperBoundRad = new Vector3f();

    public IkLink(PmxIkLink pmxIkLink) {
      this.pmxIkLink = pmxIkLink;
      if (pmxIkLink.angleLimited) {
        isLimitingAngle = true;
        angleLowerBoundRad.set(pmxIkLink.angleLowerBound);
        angleUpperBoundRad.set(pmxIkLink.angleUpperBound);
      } else {
        isLimitingAngle = false;
        angleLowerBoundRad.set((float) (-2 * Math.PI), (float) (-2 * Math.PI), (float) (-2 * Math.PI));
        angleUpperBoundRad.set((float) (2 * Math.PI), (float) (2 * Math.PI), (float) (2 * Math.PI));
      }
    }

    @Override
    public int boneIndex() {
      return pmxIkLink.boneIndex;
    }

    @Override
    public boolean isLimitingAngle() {
      return isLimitingAngle;
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

  class IkChain implements MmdIkChain {
    private final ImmutableList<MmdIkLink> ikLinks;

    IkChain() {
      ikLinks = pmxBone.ikLinks.stream().map(IkLink::new).collect(toImmutableList());
    }

    @Override
    public int boneIndex() {
      return pmxBone.boneIndex;
    }

    @Override
    public int effectorBoneIndex() {
      return pmxBone.ikTargetBoneIndex;
    }

    @Override
    public int iterationCount() {
      return pmxBone.ikLoopCount;
    }

    @Override
    public float iterationAngleLimitRad() {
      return pmxBone.ikAngleLimit;
    }

    @Override
    public ImmutableList<MmdIkLink> ikLinks() {
      return ikLinks;
    }
  }
}
