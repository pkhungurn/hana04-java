package hana04.mikumikubake.mmd;

import com.google.common.collect.ImmutableList;
import hana04.formats.mmd.common.MmdMorphPanel;
import hana04.formats.mmd.pmd.PmdModel;
import hana04.formats.mmd.pmx.PmxModel;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.vecmath.Vector3d;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface MmdModelPosingInfo {
  Logger logger = LoggerFactory.getLogger(MmdModelPosingInfo.class);

  String GLOBAL_PARENT_BONE_NAME = "全ての親";
  String CENTER_BONE_NAME = "センター";
  String HEAD_BONE_NAME = "頭";
  String NECK_BONE_NAME = "首";
  String UPPER_BODY_BONE_NAME = "上半身";
  String UPPER_BODY_2_BONE_NAME = "上半身2";
  String LOWER_BODY_BONE_NAME = "下半身";
  String GROOVE_BONE_NAME = "グルーブ";

  String LEFT_SHOULDER_BONE_NAME = "左肩";
  String LEFT_ARM_BONE_NAME = "左腕";
  String LEFT_ELBOW_BONE_NAME = "左ひじ";

  String RIGHT_SHOULDER_BONE_NAME = "右肩";
  String RIGHT_ARM_BONE_NAME = "右腕";
  String RIGHT_ELBOW_BONE_NAME = "右ひじ";

  String EYES_BONE_NAME = "両目";
  String LEFT_EYE_BONE_NAME = "左目";
  String RIGHT_EYE_BONE_NAME = "右目";

  String MOEOU_EX_HIDE_LOGO_MORPH_NAME_1 = "LOGO消";
  String MOEOU_EX_HIDE_LOGO_MORPH_NAME_2 = "logo消";

  List<String> LEFT_EYELID_MORPH_NAMES = ImmutableList.of(
      "左ウインク",
      "左ウィンク",
      "ウインク左",
      "ウィンク左",
      "ウインク",
      "ウィンク");

  List<String> RIGHT_EYELID_MORPH_NAMES = ImmutableList.of(
      "右ウインク",
      "右ウィンク",
      "ウインク右",
      "ウィンク右",
      "ウインク",
      "ウィンク");

  String getLeftEyelidMorphName();

  String getRightEyeLidMorphName();

  default String getHeadBoneName() {
    return HEAD_BONE_NAME;
  }

  default String getNeckBoneName() {
    return NECK_BONE_NAME;
  }

  default String getEyesBoneName() {
    return EYES_BONE_NAME;
  }

  default String getMouthMorphName() {
    return "あ";
  }

  default String getUpperBodyBoneName() {
    return UPPER_BODY_BONE_NAME;
  }

  default String getUpperBody2BoneName() {
    return UPPER_BODY_2_BONE_NAME;
  }

  double getArmVerticalAngleRad();

  default String getLeftShoulderBoneName() {
    return LEFT_SHOULDER_BONE_NAME;
  }

  default String getLeftArmBoneName() {
    return LEFT_ARM_BONE_NAME;
  }

  default String getLeftElbowBoneName() {
    return LEFT_ELBOW_BONE_NAME;
  }

  default String getRightShoulderBoneName() {
    return RIGHT_SHOULDER_BONE_NAME;
  }

  default String getRightArmBoneName() {
    return RIGHT_ARM_BONE_NAME;
  }

  default String getRightElbowBoneName() {
    return RIGHT_ELBOW_BONE_NAME;
  }

  double getEyesYCoordinate();

  double getHeadYCoordinate();

  double getHeadZCoordinate();

  double getUpperBodyYCoodinate();

  double getNeckYCoordinate();

  boolean hasBone(String name);

  boolean hasMorph(String name);

  default boolean isPosable() {
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
    return true;
  }

  default boolean canWinkLeftEye() {
    return hasMorph(getLeftEyelidMorphName());
  }

  default boolean canWinkRightEye() {
    return hasMorph(getRightEyeLidMorphName());
  }

  default boolean canWink() {
    return hasMorph(getLeftEyelidMorphName())
        && hasMorph(getRightEyeLidMorphName());
  }

  default boolean canMouthMove() {
    return hasMorph(getMouthMorphName());
  }

  default boolean canEyesMove() {
    return hasBone(getEyesBoneName());
  }

  List<String> getMorphNamesInPanel(MmdMorphPanel panel);

  List<String> getMorphNames();

  boolean hasMoeouExHideLogoMorph();

  String getMoeouExHideLogoMorphName();

  boolean isHanaCompatibleMorph(String morphName);

  Optional<Vector3d> getBoneRestPosition(String boneName);

  boolean isDescendant(String descendantBoneName, String ancestorBoneName);

  List<String> getInfluencingBoneNames(int vertexIndex);

  int getVertexCount();

  static MmdModelPosingInfo load(Path path) {
    try {
      String extension = FilenameUtils.getExtension(path.toString()).toLowerCase();
      MmdModelPosingInfo posingInfo = null;
      logger.info(String.format("Loading %s ...", path.toString()));
      if (extension.equals("pmx")) {
        PmxModel pmxModel = PmxModel.load(path);
        posingInfo = new PmxModelPosingInfo(pmxModel);
      } else if (extension.equals("pmd")) {
        PmdModel pmdModel = PmdModel.load(path);
        posingInfo = new PmdModelPosingInfo(pmdModel);
      } else {
        throw new RuntimeException("Invalid extension: " + extension);
      }
      return posingInfo;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
