package hana04.formats.mmd.generic.api;

import hana04.formats.mmd.generic.api.ik.MmdIkChain;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.Optional;

public interface MmdBone {
  String japaneseName();

  String englishName();

  int index();

  int transformLevel();

  boolean transformsAfterPhysics();

  Optional<Integer> parentIndex();

  Vector3f translationFromParent();

  Point3f restPosition();

  Optional<? extends MmdBoneFuyoInfo> fuyoInfo();

  Optional<? extends MmdIkChain> ikChain();

  Optional<Vector3f> fixedAxis();

  boolean isKnee();

  static int compareTransformOrder(MmdBone a, MmdBone b) {
    if (a.transformsAfterPhysics() != b.transformsAfterPhysics()) {
      if (!a.transformsAfterPhysics()) {
        return -1;
      } else if (!b.transformsAfterPhysics()) {
        return 1;
      }
    }
    int levelComparison = Integer.compare(a.transformLevel(), b.transformLevel());
    if (levelComparison != 0) {
      return levelComparison;
    }
    return Integer.compare(a.index(), b.index());
  }

  default boolean isAfter(MmdBone other) {
    return compareTransformOrder(this, other) > 0;
  }

  default boolean isBefore(MmdBone other) {
    return compareTransformOrder(this, other) < 0;
  }

  default boolean isNotAfter(MmdBone other) {
    return !isAfter(other);
  }

  default boolean isNotBefore(MmdBone other) {
    return !isBefore(other);
  }
}
