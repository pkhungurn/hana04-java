package hana04.formats.mmd.generic.api.ik;

import javax.vecmath.Vector3f;

public interface MmdIkLink {
  int boneIndex();

  boolean isLimitingAngle();

  Vector3f angleLowerBoundRad();

  Vector3f angleUpperBoundRad();
}
