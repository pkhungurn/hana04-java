package hana04.mikumikubake.mmd;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hana04.formats.mmd.common.MmdMorphPanel;
import hana04.formats.mmd.pmx.PmxBone;
import hana04.formats.mmd.pmx.PmxModel;
import hana04.formats.mmd.pmx.PmxMorph;
import hana04.formats.mmd.pmx.PmxVertex;
import hana04.shakuyaku.sbtm.converter.PmxToSbtmBaseMeshImplConverter;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class PmxModelPosingInfo implements MmdModelPosingInfo {
  private final PmxModel model;
  private final String leftEyelidMorphName;
  private final String rightEyelidMorphName;
  private final ImmutableMap<MmdMorphPanel, ImmutableList<String>> morphNamesByPanel;
  private final ImmutableList<String> morphNames;
  private final boolean hasMoeouExHideLogoMorph;
  private final String moeouExHideLogoMorphName;

  public PmxModelPosingInfo(PmxModel model) {
    this.model = model;
    this.leftEyelidMorphName = findFirstExistingMorphInList(LEFT_EYELID_MORPH_NAMES);
    this.rightEyelidMorphName = findFirstExistingMorphInList(RIGHT_EYELID_MORPH_NAMES);

    HashMap<MmdMorphPanel, ImmutableList.Builder<String>> tempMorphNameByPanel = new HashMap<>();
    ImmutableList.Builder<String> morphNamesBuilder = ImmutableList.builder();
    for (MmdMorphPanel panel : MmdMorphPanel.values()) {
      tempMorphNameByPanel.put(panel, ImmutableList.builder());
    }
    for (int i = 0; i < model.getMorphCount(); i++) {
      PmxMorph morph = model.getMorph(i);
      tempMorphNameByPanel.get(morph.panel).add(morph.japaneseName);
      morphNamesBuilder.add(morph.japaneseName);
    }
    morphNames = morphNamesBuilder.build();
    ImmutableMap.Builder<MmdMorphPanel, ImmutableList<String>> morphNamesByPanel = ImmutableMap.builder();

    for (MmdMorphPanel panel : MmdMorphPanel.values()) {
      morphNamesByPanel.put(panel, tempMorphNameByPanel.get(panel).build());
    }
    this.morphNamesByPanel = morphNamesByPanel.build();

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

  private String findFirstExistingMorphInList(List<String> morphNames) {
    for (String name : morphNames) {
      if (model.hasMorph(name)) {
        return name;
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
    PmxBone shoulderBone = model.getBone(getLeftArmBoneName());
    PmxBone elbowBone = model.getBone(getLeftElbowBoneName());
    Vector3f shoulderToElbow = new Vector3f();
    shoulderToElbow.sub(elbowBone.position, shoulderBone.position);
    shoulderToElbow.z = 0.0f;
    shoulderToElbow.normalize();
    return Math.acos(shoulderToElbow.dot(new Vector3f(0, -1, 0)));
  }

  @Override
  public double getEyesYCoordinate() {
    return model.getBone(getEyesBoneName()).position.y;
  }

  @Override
  public double getHeadYCoordinate() {
    return model.getBone(getHeadBoneName()).position.y;
  }

  @Override
  public double getHeadZCoordinate() {
    return model.getBone(getHeadBoneName()).position.z;
  }

  @Override
  public double getUpperBodyYCoodinate() {
    return model.getBone(getUpperBodyBoneName()).position.y;
  }

  @Override
  public double getNeckYCoordinate() {
    return model.getBone(getNeckBoneName()).position.y;
  }

  @Override
  public boolean hasBone(String name) {
    return model.hasBone(name);
  }

  @Override
  public boolean hasMorph(String name) {
    return model.hasMorph(name);
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
  public boolean isPosable() {
    if (!hasBone(getHeadBoneName())) {
      System.out.println("Missing " + getHeadBoneName());
      return false;
    }
    if (!hasBone(getNeckBoneName())) {
      System.out.println("Missing " + getNeckBoneName());
      return false;
    }
    if (!hasBone(getUpperBodyBoneName())) {
      logger.debug("Missing " + getUpperBodyBoneName());
      return false;
    }
    if (!hasBone(getLeftArmBoneName())) {
      logger.debug("Missing " + getLeftArmBoneName());
      return false;
    }
    if (!hasBone(getRightArmBoneName())) {
      logger.debug("Missing " + getRightArmBoneName());
      return false;
    }
    if (!hasBone(getLeftElbowBoneName())) {
      logger.debug("Missing " + getLeftElbowBoneName());
      return false;
    }
    if (!hasBone(getRightElbowBoneName())) {
      logger.debug("Missing " + getRightElbowBoneName());
      return false;
    }
    if (boneIsCopyingRotation(getHeadBoneName())) {
      logger.debug("Head bone is copying rotation.");
      return false;
    }
    if (boneIsCopyingRotation(getNeckBoneName())) {
      logger.debug("Neck bone is copying rotation.");
      return false;
    }
    if (boneIsCopyingRotation(getUpperBodyBoneName())) {
      logger.debug("Upper boby bone is copying rotation.");
      return false;
    }
    if (boneIsCopyingRotation(getEyesBoneName())) {
      logger.debug("Eyes bone is copying rotation.");
      return false;
    }
    return true;
  }

  private boolean boneIsCopyingRotation(String boneName) {
    PmxBone bone = model.getBone(boneName);
    return bone.isCopyingRotation();
  }

  public boolean isHanaCompatibleMorph(String morphName) {
    return morphNames.contains(morphName) &&
        PmxToSbtmBaseMeshImplConverter.isMorphConvertible(model, morphName);
  }

  @Override
  public Optional<Vector3d> getBoneRestPosition(String boneName) {
    if (!model.hasBone(boneName)) {
      return Optional.empty();
    }
    return Optional.of(new Vector3d(model.getBone(boneName).position));
  }

  @Override
  public boolean isDescendant(String descendantBoneName, String ancestorBoneName) {
    if (!hasBone(descendantBoneName)) {
      return false;
    }
    if (!hasBone(ancestorBoneName)) {
      return false;
    }
    PmxBone descendantBone = model.getBone(descendantBoneName);
    PmxBone ancestorBone = model.getBone(ancestorBoneName);
    while (descendantBone.parentIndex >= 0) {
      if (descendantBone.boneIndex == ancestorBone.boneIndex) {
        return true;
      }
      descendantBone = model.getBone(descendantBone.parentIndex);
    }
    return false;
  }

  @Override
  public List<String> getInfluencingBoneNames(int vertexIndex) {
    PmxVertex vertex = model.getVertex(vertexIndex);
    int boneCount = vertex.boneIndices.length;
    ImmutableList.Builder<String> boneNames = ImmutableList.builder();
    for (int i = 0; i < boneCount; i++) {
      float weight = vertex.boneWeights[i];
      if (weight <= 1e-4) {
        continue;
      }
      int boneIndex = vertex.boneIndices[i];
      PmxBone bone = model.getBone(boneIndex);
      boneNames.add(bone.japaneseName);
    }
    return boneNames.build();
  }

  @Override
  public int getVertexCount() {
    return model.getVertexCount();
  }
}
