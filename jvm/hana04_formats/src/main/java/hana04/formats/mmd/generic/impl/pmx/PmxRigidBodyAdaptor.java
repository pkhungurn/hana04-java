package hana04.formats.mmd.generic.impl.pmx;

import hana04.formats.mmd.common.MmdRigidBodyShapeType;
import hana04.formats.mmd.common.MmdRigidBodyType;
import hana04.formats.mmd.generic.api.physics.MmdRigidBody;
import hana04.formats.mmd.pmx.PmxRigidBody;

import javax.vecmath.Vector3f;

public class PmxRigidBodyAdaptor implements MmdRigidBody {
  private final PmxRigidBody rigidBody;

  PmxRigidBodyAdaptor(PmxRigidBody rigidBody) {
    this.rigidBody = rigidBody;
  }

  @Override
  public String japaneseName() {
    return rigidBody.japaneseName;
  }

  @Override
  public String englishName() {
    return rigidBody.englishName;
  }

  @Override
  public int boneIndex() {
    return rigidBody.boneIndex;
  }

  @Override
  public int groupIndex() {
    return rigidBody.groupIndex;
  }

  @Override
  public int hitWithGroupFlags() {
    return rigidBody.hitWithGroupFlags;
  }

  @Override
  public MmdRigidBodyShapeType shape() {
    return rigidBody.shape;
  }

  @Override
  public float width() {
    return rigidBody.width;
  }

  @Override
  public float height() {
    return rigidBody.height;
  }

  @Override
  public float depth() {
    return rigidBody.depth;
  }

  @Override
  public Vector3f position() {
    return rigidBody.position;
  }

  @Override
  public Vector3f rotation() {
    return rigidBody.rotation;
  }

  @Override
  public float mass() {
    return rigidBody.mass;
  }

  @Override
  public float positionDamping() {
    return rigidBody.positionDamping;
  }

  @Override
  public float rotationDamping() {
    return rigidBody.rotationDamping;
  }

  @Override
  public float restitution() {
    return rigidBody.restitution;
  }

  @Override
  public float friction() {
    return rigidBody.friction;
  }

  @Override
  public MmdRigidBodyType type() {
    return rigidBody.type;
  }
}
