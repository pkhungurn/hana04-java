package hana04.formats.mmd.generic.api.physics;

import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import java.util.List;

public interface MmdPhysicsWithDebugFeatures {
  int getNumSubSteps();

  void setNumSubSteps(int value);

  List<btRigidBody> getBtRigidBodies();
}
