package hana04.mikumikubake.mmd;

import com.google.common.collect.ImmutableList;
import hana04.formats.mmd.common.MmdMorphPanel;
import hana04.formats.mmd.pmd.PmdBone;
import hana04.formats.mmd.pmd.PmdModel;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.ImmutableList.toImmutableList;

public class PmdModelPosingInfo implements MmdModelPosingInfo {
  private final PmdModel model;

  private final String leftEyelidMorphName;
  private final String rightEyelidMorphName;
  private final HashMap<MmdMorphPanel, ImmutableList<String>> morphNamesByPanel;
  private final ImmutableList<String> morphNames;
  private final boolean hasMoeouExHideLogoMorph;
  private final String moeouExHideLogoMorphName;

  public PmdModelPosingInfo(PmdModel model) {
    this.model = model;
    this.leftEyelidMorphName = findFirstExistingMorphNameFromList(LEFT_EYELID_MORPH_NAMES);
    this.rightEyelidMorphName = findFirstExistingMorphNameFromList(RIGHT_EYELID_MORPH_NAMES);

    morphNamesByPanel = new HashMap<>();
    for (MmdMorphPanel panel : MmdMorphPanel.values()) {
      ImmutableList<String> morphNames = model.morphs.stream()
          .filter(morph -> morph.panel.equals(panel))
          .map(morph -> morph.japaneseName)
          .collect(toImmutableList());
      morphNamesByPanel.put(panel, morphNames);
    }
    morphNames = model.morphs.stream().map(morph -> morph.japaneseName).collect(toImmutableList());

    if (morphNames.contains(MOEOU_EX_HIDE_LOGO_MORPH_NAME_1)) {
      this.hasMoeouExHideLogoMorph = true;
      this.moeouExHideLogoMorphName = MOEOU_EX_HIDE_LOGO_MORPH_NAME_1;
    } else if (morphNames.contains(MOEOU_EX_HIDE_LOGO_MORPH_NAME_2)) {
      this.hasMoeouExHideLogoMorph = true;
      this.moeouExHideLogoMorphName = MOEOU_EX_HIDE_LOGO_MORPH_NAME_2;
    } else {
      this.hasMoeouExHideLogoMorph = false;
      this.moeouExHideLogoMorphName = "";
    }
  }

  private String findFirstExistingMorphNameFromList(List<String> morphNames) {
    for (String morphName : morphNames) {
      if (model.getMorphIndex(morphName) >= 0) {
        return morphName;
      }
    }
    return morphNames.get(morphNames.size() - 1);
  }

  @Override
  public String getLeftEyelidMorphName() {
    return leftEyelidMorphName;
  }

  @Override
  public String getRightEyeLidMorphName() {
    return rightEyelidMorphName;
  }

  @Override
  public double getArmVerticalAngleRad() {
    int shoulderBoneIndex = model.getBoneIndex(getLeftArmBoneName());
    int elbowBoneIndex = model.getBoneIndex(getLeftElbowBoneName());
    PmdBone shoulderBone = model.bones.get(shoulderBoneIndex);
    PmdBone elbowBone = model.bones.get(elbowBoneIndex);
    Vector3f shoulderToElbow = new Vector3f();
    shoulderToElbow.sub(elbowBone.position, shoulderBone.position);
    shoulderToElbow.normalize();
    return Math.acos(shoulderToElbow.dot(new Vector3f(0, -1, 0)));
  }

  @Override
  public double getEyesYCoordinate() {
    int eyesBoneIndex = model.getBoneIndex(getEyesBoneName());
    return model.bones.get(eyesBoneIndex).position.y;
  }

  @Override
  public double getHeadYCoordinate() {
    int headBoneIndex = model.getBoneIndex(getHeadBoneName());
    return model.bones.get(headBoneIndex).position.y;
  }

  @Override
  public double getHeadZCoordinate() {
    int headBoneIndex = model.getBoneIndex(getHeadBoneName());
    return model.bones.get(headBoneIndex).position.z;
  }

  @Override
  public double getUpperBodyYCoodinate() {
    int upperBodyBoneIndex = model.getBoneIndex(getUpperBodyBoneName());
    return model.bones.get(upperBodyBoneIndex).position.y;
  }

  @Override
  public double getNeckYCoordinate() {
    int neckBoneIndex = model.getBoneIndex(getNeckBoneName());
    return model.bones.get(neckBoneIndex).position.y;
  }

  @Override
  public boolean hasBone(String name) {
    return model.getBoneIndex(name) >= 0;
  }

  @Override
  public boolean hasMorph(String name) {
    return model.getMorphIndex(name) >= 0;
  }

  @Override
  public List<String> getMorphNamesInPanel(MmdMorphPanel panel) {
    return morphNamesByPanel.get(panel);
  }

  @Override
  public List<String> getMorphNames() {
    return morphNames;
  }

  @Override
  public boolean hasMoeouExHideLogoMorph() {
    return hasMoeouExHideLogoMorph;
  }

  @Override
  public String getMoeouExHideLogoMorphName() {
    return moeouExHideLogoMorphName;
  }

  @Override
  public boolean isHanaCompatibleMorph(String morphName) {
    return morphNames.contains(morphName);
  }

  @Override
  public Optional<Vector3d> getBoneRestPosition(String boneName) {
    int boneIndex = model.getBoneIndex(boneName);
    if (boneIndex < 0) {
      return Optional.empty();
    }
    PmdBone bone = model.bones.get(boneIndex);
    return Optional.of(new Vector3d(bone.position));
  }

  @Override
  public boolean isDescendant(String descendantBoneName, String ancestorBoneName) {
    if (!hasBone(descendantBoneName)) {
      return false;
    }
    if (!hasBone(ancestorBoneName)) {
      return false;
    }
    int descendantBoneIndex = model.getBoneIndex(descendantBoneName);
    int ancestorBoneIndex = model.getBoneIndex(ancestorBoneName);
    while (descendantBoneIndex >= 0) {
      if (ancestorBoneIndex == descendantBoneIndex) {
        return true;
      }
      PmdBone descendantBone = model.bones.get(descendantBoneIndex);
      descendantBoneIndex = descendantBone.parentIndex;
    }
    return false;
  }

  @Override
  public List<String> getInfluencingBoneNames(int vertexIndex) {
    float weight = model.vertexBoneBlendWeights.get(2 * vertexIndex);
    int boneIndex0 = model.vertexBoneIndices.get(2 * vertexIndex);
    int boneIndex1 = model.vertexBoneIndices.get(2 * vertexIndex + 1);
    ImmutableList.Builder<String> boneNames = ImmutableList.builder();
    if (weight > 1e-4f) {
      boneNames.add(model.bones.get(boneIndex0).japaneseName);
    }
    if (weight < 1.0f - 1e-4f) {
      boneNames.add(model.bones.get(boneIndex1).japaneseName);
    }
    return boneNames.build();
  }

  @Override
  public int getVertexCount() {
    return model.getVertexCount();
  }
}
