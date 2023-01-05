package hana04.formats.mmd.generic.api.physics;

import hana04.formats.mmd.generic.MmdModelPose;
import hana04.formats.mmd.generic.api.MmdModel;

import javax.vecmath.Vector3f;

public interface MmdPhysics {
  void update(MmdModelPose pose, float elapsedTime, boolean forcePhysicsToFollowPose);

  void setGravity(Vector3f gravity);

  void setGravity(float x, float y, float z);

  void setNumSubSteps(int substepCount);

  void dispose();

  interface Factory {
    MmdPhysics create(MmdModel model);
  }
}
