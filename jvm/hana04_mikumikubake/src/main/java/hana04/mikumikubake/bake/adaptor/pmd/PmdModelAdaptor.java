package hana04.mikumikubake.bake.adaptor.pmd;

import hana04.formats.mmd.pmd.PmdBone;
import hana04.formats.mmd.pmd.PmdModel;
import hana04.formats.mmd.pmd.PmdMorph;
import hana04.formats.mmd.pmd.PmdPose;
import hana04.formats.mmd.vmd.VmdBoneMotion;
import hana04.formats.mmd.vmd.VmdMorphMotion;
import hana04.formats.mmd.vmd.VmdMotion;
import hana04.mikumikubake.bake.adaptor.MmdAnimatedInstanceAdaptor;
import hana04.mikumikubake.bake.adaptor.MmdModelAdaptor;
import hana04.mikumikubake.bake.adaptor.MmdPoseAdaptor;

import javax.vecmath.Vector3f;

public class PmdModelAdaptor implements MmdModelAdaptor<PmdModel, PmdPose> {
  final PmdModel pmdModel;

  public PmdModelAdaptor(PmdModel pmdModel) {
    this.pmdModel = pmdModel;
  }

  @Override
  public MmdPoseAdaptor<PmdPose> createPose() {
    return new PmdPoseAdaptor(new PmdPose(pmdModel));
  }

  @Override
  public MmdAnimatedInstanceAdaptor<PmdModel, PmdPose> createAnimatedInstance() {
    return new PmdAnimatedInstanceAdaptor(pmdModel);
  }

  @Override
  public PmdModel getModel() {
    return pmdModel;
  }

  @Override
  public void populateBoneAndMorphNames(VmdMotion vmdMotion) {
    for (PmdBone bone : pmdModel.bones) {
      VmdBoneMotion boneMotion = new VmdBoneMotion();
      boneMotion.boneName = bone.japaneseName;
      vmdMotion.boneMotions.put(bone.japaneseName, boneMotion);
    }
    for (PmdMorph morph : pmdModel.morphs) {
      VmdMorphMotion morphMotion = new VmdMorphMotion();
      morphMotion.morphName = morph.japaneseName;
      vmdMotion.morphMotions.put(morph.japaneseName, morphMotion);
    }
  }

  @Override
  public String getJapaneseName() {
    return pmdModel.japaneseName;
  }

  @Override
  public void getBoneDisplacement(String boneName, Vector3f displacement) {
    int boneIndex = pmdModel.getBoneIndex(boneName);
    PmdBone bone = pmdModel.bones.get(boneIndex);
    bone.getDisplacementFromParent(displacement);
  }
}
