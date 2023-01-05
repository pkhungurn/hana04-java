package hana04.mikumikubake.bake.adaptor.pmx;

import hana04.formats.mmd.pmx.PmxBone;
import hana04.formats.mmd.pmx.PmxModel;
import hana04.formats.mmd.pmx.PmxMorph;
import hana04.formats.mmd.pmx.PmxPose;
import hana04.formats.mmd.vmd.VmdBoneMotion;
import hana04.formats.mmd.vmd.VmdMorphMotion;
import hana04.formats.mmd.vmd.VmdMotion;
import hana04.mikumikubake.bake.adaptor.MmdAnimatedInstanceAdaptor;
import hana04.mikumikubake.bake.adaptor.MmdModelAdaptor;
import hana04.mikumikubake.bake.adaptor.MmdPoseAdaptor;

import javax.vecmath.Vector3f;

public class PmxModelAdaptor implements MmdModelAdaptor<PmxModel, PmxPose> {
  private final PmxModel pmxModel;

  public PmxModelAdaptor(PmxModel pmxModel) {
    this.pmxModel = pmxModel;
  }

  @Override
  public MmdPoseAdaptor<PmxPose> createPose() {
    return new PmxPoseAdaptor(new PmxPose(pmxModel));
  }

  @Override
  public MmdAnimatedInstanceAdaptor<PmxModel, PmxPose> createAnimatedInstance() {
    return new PmxAnimatedInstanceAdaptor(pmxModel);
  }

  @Override
  public PmxModel getModel() {
    return pmxModel;
  }

  @Override
  public void populateBoneAndMorphNames(VmdMotion vmdMotion) {
    for (int i = 0; i < pmxModel.getBoneCount(); i++) {
      PmxBone bone = pmxModel.getBone(i);
      VmdBoneMotion boneMotion = new VmdBoneMotion();
      boneMotion.boneName = bone.japaneseName;
      vmdMotion.boneMotions.put(bone.japaneseName, boneMotion);
    }
    for (int i = 0; i < pmxModel.getMorphCount(); i++) {
      PmxMorph morph = pmxModel.getMorph(i);
      VmdMorphMotion morphMotion = new VmdMorphMotion();
      morphMotion.morphName = morph.japaneseName;
      vmdMotion.morphMotions.put(morph.japaneseName, morphMotion);
    }
  }

  @Override
  public String getJapaneseName() {
    return pmxModel.getJapaneseName();
  }

  @Override
  public void getBoneDisplacement(String boneName, Vector3f displacement) {
    displacement.set(pmxModel.getBone(boneName).displacementFromParent);
  }
}
