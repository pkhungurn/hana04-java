package hana04.formats.mmd.generic.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.Map;
import java.util.Optional;

public interface MmdVertex {
  Point3f position();

  Vector3f normal();

  Vector2f texCoords();

  ImmutableList<Vector4f> additionalTexCoords();

  SkinningType skinningType();

  ImmutableList<Integer> boneIndices();

  ImmutableList<Float> boneWeights();

  float edgeScale();

  Optional<? extends SdefInfo> sdefInfo();

  enum SkinningType {
    BDEF(0),
    SDEF(1),
    QDEF(2);

    private final int value;

    SkinningType(int value) {
      this.value = value;
    }

    private static final Map<Integer, SkinningType> INT_TO_SKINNING_TYPE = ImmutableMap.of(
        0, BDEF,
        1, SDEF,
        2, QDEF);

    public static SkinningType fromInt(int value) {
      if (!INT_TO_SKINNING_TYPE.containsKey(value)) {
        throw new RuntimeException("Invalid value.");
      }
      return INT_TO_SKINNING_TYPE.get(value);
    }

    public int getValue() {
      return value;
    }
  }

  interface SdefInfo {
    Vector3f C();

    Vector3f R0();

    Vector3f R1();
  }
}
