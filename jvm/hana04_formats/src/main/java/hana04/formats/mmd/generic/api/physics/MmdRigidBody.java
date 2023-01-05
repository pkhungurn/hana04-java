package hana04.formats.mmd.generic.api.physics;

import hana04.formats.mmd.common.MmdRigidBodyShapeType;
import hana04.formats.mmd.common.MmdRigidBodyType;

import javax.vecmath.Vector3f;

public interface MmdRigidBody {
  String japaneseName();

  String englishName();

  int boneIndex();

  int groupIndex();

  int hitWithGroupFlags();

  MmdRigidBodyShapeType shape();

  float width();

  float height();

  float depth();

  Vector3f position();

  Vector3f rotation();

  float mass();

  float positionDamping();

  float rotationDamping();

  float restitution();

  float friction();

  MmdRigidBodyType type();
}
