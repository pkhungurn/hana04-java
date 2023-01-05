package hana04.mikumikubake.mmd;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import hana04.formats.mmd.common.MmdMorphPanel;
import hana04.formats.mmd.generic.api.MmdModel;
import hana04.formats.mmd.generic.api.MmdMorph;
import hana04.shakuyaku.sbtm.converter.PmxToSbtmBaseMeshImplConverter;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableListMultimap.toImmutableListMultimap;

public class MmdModelPosingInfoImpl implements MmdModelPosingInfo {
  private final MmdModel mmdModel;
  private final String leftEyelidMorphName;
  private final String rightEyelidMorphName;
  private final ImmutableListMultimap<MmdMorphPanel, String> morphNamesByPanel;
  private final ImmutableList<String> morphNames;
  private final boolean hasMoeouExHideLogoMorph;
  private final String moeouExHideLogoMorphName;

  public MmdModelPosingInfoImpl(MmdModel mmdModel) {
    this.mmdModel = mmdModel;

    this.leftEyelidMorphName = findFirstExistingMorphInList(LEFT_EYELID_MORPH_NAMES);
    this.rightEyelidMorphName = findFirstExistingMorphInList(RIGHT_EYELID_MORPH_NAMES);

    this.morphNames = mmdModel.morphs().stream().map(MmdMorph::japaneseName).collect(toImmutableList());
    this.morphNamesByPanel = mmdModel.morphs().stream()
        .collect(toImmutableListMultimap(
            MmdMorph::panel,
            MmdMorph::japaneseName
        ));

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
    return morphNames
        .stream()
        .filter(name -> mmdModel.morphIndex(name).isPresent())
        .findFirst()
        .orElse(morphNames.get(morphNames.size() - 1));
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
    var shoulderBone = mmdModel.getBone(getLeftArmBoneName()).get();
    var elbowBone = mmdModel.getBone(getLeftElbowBoneName()).get();
    Vector3f shoulderToElbow = new Vector3f();
    shoulderToElbow.sub(elbowBone.restPosition(), shoulderBone.restPosition());
    shoulderToElbow.z = 0.0f;
    shoulderToElbow.normalize();
    return Math.acos(shoulderToElbow.dot(new Vector3f(0, -1, 0)));
  }

  @Override
  public double getEyesYCoordinate() {
    return mmdModel.getBone(getEyesBoneName()).get().restPosition().y;
  }

  @Override
  public double getHeadYCoordinate() {
    return mmdModel.getBone(getHeadBoneName()).get().restPosition().y;
  }

  @Override
  public double getHeadZCoordinate() {
    return mmdModel.getBone(getHeadBoneName()).get().restPosition().z;
  }

  @Override
  public double getUpperBodyYCoodinate() {
    return mmdModel.getBone(getUpperBodyBoneName()).get().restPosition().y;
  }

  @Override
  public double getNeckYCoordinate() {
    return mmdModel.getBone(getNeckBoneName()).get().restPosition().y;
  }

  @Override
  public boolean hasBone(String name) {
    return mmdModel.boneIndex(name).isPresent();
  }

  @Override
  public boolean hasMorph(String name) {
    return mmdModel.morphIndex(name).isPresent();
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
    return mmdModel
        .getMorph(morphName)
        .map(morph_ -> PmxToSbtmBaseMeshImplConverter.isMorphConvertible(mmdModel, morph_))
        .orElse(false);
  }

  @Override
  public Optional<Vector3d> getBoneRestPosition(String boneName) {
    return mmdModel.getBone(boneName).map(bone -> new Vector3d(bone.restPosition()));
  }

  @Override
  public boolean isDescendant(String descendantBoneName, String ancestorBoneName) {
    if (!hasBone(descendantBoneName)) {
      return false;
    }
    if (!hasBone(ancestorBoneName)) {
      return false;
    }
    var descendantBone = mmdModel.getBone(descendantBoneName).get();
    var ancestorBone = mmdModel.getBone(ancestorBoneName).get();
    while (true) {
      if (descendantBone.index() == ancestorBone.index()) {
        return true;
      }
      if (descendantBone.parentIndex().isEmpty()) {
        break;
      }
      descendantBone = mmdModel.bones().get(descendantBone.parentIndex().get());
    }
    return false;
  }

  @Override
  public List<String> getInfluencingBoneNames(int vertexIndex) {
    ImmutableList.Builder<String> outputs = ImmutableList.builder();
    var vertex = mmdModel.vertices().get(vertexIndex);
    for (int i = 0; i < vertex.boneIndices().size(); i++) {
      int boneIndex = vertex.boneIndices().get(i);
      float weight = vertex.boneWeights().get(i);
      if (weight <= 1e-4) {
        continue;
      }
      outputs.add(mmdModel.bones().get(boneIndex).japaneseName());
    }
    return outputs.build();
  }

  @Override
  public int getVertexCount() {
    return mmdModel.vertices().size();
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
    var bone = mmdModel.getBone(boneName).get();
    return bone.fuyoInfo().isPresent();
  }
}
