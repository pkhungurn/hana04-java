package hana04.shakuyaku.sbtm;

public enum SbtmSkinningType {
  // Normal linear blend skinning
  LINEAR_BLEND(0),
  // The spherical deformation algorithm (SDEF) invented by 佐々木優理
  SDEF(1),
  // The dual quaternion skining algorithm.
  DUAL_QUATERNION(2);

  public final int value;

  SbtmSkinningType(int value) {
    this.value = value;
  }

  public static SbtmSkinningType fromInt(int value) {
    switch (value) {
      case 0:
        return LINEAR_BLEND;
      case 1:
        return SDEF;
      case 2:
        return DUAL_QUATERNION;
      default:
        throw new RuntimeException("invalid enum value: " + value);
    }
  }
}
