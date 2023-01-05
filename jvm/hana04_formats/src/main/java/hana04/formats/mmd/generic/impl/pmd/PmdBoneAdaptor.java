package hana04.formats.mmd.generic.impl.pmd;

import hana04.formats.mmd.generic.api.MmdBone;
import hana04.formats.mmd.generic.api.MmdBoneFuyoInfo;
import hana04.formats.mmd.pmd.PmdBone;
import hana04.formats.mmd.pmd.PmdBoneType;
import hana04.formats.mmd.pmd.PmdModel;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.Optional;

public class PmdBoneAdaptor implements MmdBone {
  private final PmdBone pmdBone;
  private final int index;
  private final Vector3f translationFromParent;
  private final boolean isKnee;
  private final Optional<FuyoInfo> fuyoInfo;
  private final Optional<Vector3f> fixedAxis;
  private final Optional<PmdIkChainAdaptor> ikChain;

  PmdBoneAdaptor(PmdModel model, int index) {
    this.pmdBone = model.bones.get(index);
    this.index = index;
    this.translationFromParent = new Vector3f();
    pmdBone.getDisplacementFromParent(translationFromParent);
    this.isKnee = pmdBone.japaneseName.contains("ひざ");

    if (pmdBone.type.equals(PmdBoneType.CopyingRotation)) {
      fuyoInfo = Optional.of(new FuyoInfo(pmdBone.influenceInfo, 1.0f));
    } else if (pmdBone.type.equals(PmdBoneType.RotationInfluenced)) {
      fuyoInfo = Optional.of(new FuyoInfo(pmdBone.tailIndex, pmdBone.influenceInfo / 100.0f));
    } else {
      fuyoInfo = Optional.empty();
    }

    if (pmdBone.type.equals(PmdBoneType.RestrictedToAxis)) {
      var tailBone = model.bones.get(pmdBone.tailIndex);
      Vector3f axis = new Vector3f();
      tailBone.getDisplacementFromParent(axis);
      if (axis.length() < 1e-6) {
        fixedAxis = Optional.empty();
      } else {
        axis.normalize();
        fixedAxis = Optional.of(axis);
      }
    } else {
      fixedAxis = Optional.empty();
    }

    if (pmdBone.type.equals(PmdBoneType.Ik)) {
      ikChain =
          model.ikChains.stream()
              .filter(ikChain -> ikChain.boneIndex == index)
              .findFirst()
              .map(PmdIkChainAdaptor::new);
    } else {
      ikChain = Optional.empty();
    }
  }

  @Override
  public String japaneseName() {
    return pmdBone.japaneseName;
  }

  @Override
  public String englishName() {
    return pmdBone.englishName;
  }

  @Override
  public int index() {
    return index;
  }

  @Override
  public int transformLevel() {
    return pmdBone.type.equals(PmdBoneType.Ik) ? 1 : 0;
  }

  @Override
  public boolean transformsAfterPhysics() {
    return false;
  }

  @Override
  public Optional<Integer> parentIndex() {
    if (pmdBone.parentIndex == index || pmdBone.parentIndex < 0) {
      return Optional.empty();
    }
    return Optional.of((int) pmdBone.parentIndex);
  }

  @Override
  public Vector3f translationFromParent() {
    return translationFromParent;
  }

  @Override
  public Point3f restPosition() {
    return pmdBone.position;
  }

  @Override
  public Optional<FuyoInfo> fuyoInfo() {
    return fuyoInfo;
  }

  @Override
  public Optional<PmdIkChainAdaptor> ikChain() {
    return ikChain;
  }

  @Override
  public Optional<Vector3f> fixedAxis() {
    return fixedAxis;
  }

  @Override
  public boolean isKnee() {
    return isKnee;
  }

  static class FuyoInfo implements MmdBoneFuyoInfo {
    private final int sourceBoneIndex;
    private final float coefficient;

    FuyoInfo(int sourceBoneIndex, float coefficient) {
      this.sourceBoneIndex = sourceBoneIndex;
      this.coefficient = coefficient;
    }

    @Override
    public int getSourceBoneIndex() {
      return sourceBoneIndex;
    }

    @Override
    public float getCoefficient() {
      return coefficient;
    }

    @Override
    public boolean isCombiningRotation() {
      return true;
    }

    @Override
    public boolean isCombiningTranslation() {
      return false;
    }
  }
}
