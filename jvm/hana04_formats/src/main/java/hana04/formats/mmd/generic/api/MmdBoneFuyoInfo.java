package hana04.formats.mmd.generic.api;

public interface MmdBoneFuyoInfo {
  int getSourceBoneIndex();

  float getCoefficient();

  boolean isCombiningRotation();

  boolean isCombiningTranslation();
}
