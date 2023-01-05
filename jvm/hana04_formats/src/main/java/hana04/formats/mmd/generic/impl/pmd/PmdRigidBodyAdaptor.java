package hana04.formats.mmd.generic.impl.pmd;

import hana04.formats.mmd.common.MmdRigidBodyShapeType;
import hana04.formats.mmd.common.MmdRigidBodyType;
import hana04.formats.mmd.generic.api.physics.MmdRigidBody;
import hana04.formats.mmd.pmd.PmdModel;
import hana04.formats.mmd.pmd.PmdRigidBody;
import hana04.gfxbase.gfxtype.VecMathFUtil;

import javax.vecmath.Vector3f;

public class PmdRigidBodyAdaptor implements MmdRigidBody {
  private final PmdRigidBody pmdRigidBody;
  private final Vector3f position;

  public PmdRigidBodyAdaptor(PmdRigidBody pmdRigidBody, PmdModel model) {
    this.pmdRigidBody = pmdRigidBody;
    if (pmdRigidBody.boneIndex == -1) {
      position = VecMathFUtil.add(new Vector3f(0,0,0), new Vector3f(pmdRigidBody.position));
    } else {
      var bone = model.bones.get(pmdRigidBody.boneIndex);
      position = VecMathFUtil.add(new Vector3f(bone.position), new Vector3f(pmdRigidBody.position));
    }
  }

  @Override
  public String japaneseName() {
    return pmdRigidBody.name;
  }

  @Override
  public String englishName() {
    return "";
  }

  @Override
  public int boneIndex() {
    return pmdRigidBody.boneIndex;
  }

  @Override
  public int groupIndex() {
    return pmdRigidBody.groupIndex;
  }

  @Override
  public int hitWithGroupFlags() {
    return pmdRigidBody.hitWithGroupFlags;
  }

  @Override
  public MmdRigidBodyShapeType shape() {
    return pmdRigidBody.shape;
  }

  @Override
  public float width() {
    return pmdRigidBody.width;
  }

  @Override
  public float height() {
    return pmdRigidBody.height;
  }

  @Override
  public float depth() {
    return pmdRigidBody.depth;
  }

  @Override
  public Vector3f position() {
    return position;
  }

  @Override
  public Vector3f rotation() {
    return pmdRigidBody.rotation;
  }

  @Override
  public float mass() {
    return pmdRigidBody.mass;
  }

  @Override
  public float positionDamping() {
    return pmdRigidBody.positionDamping;
  }

  @Override
  public float rotationDamping() {
    return pmdRigidBody.rotationDamping;
  }

  @Override
  public float restitution() {
    return pmdRigidBody.restitution;
  }

  @Override
  public float friction() {
    return pmdRigidBody.friction;
  }

  @Override
  public MmdRigidBodyType type() {
    return pmdRigidBody.type;
  }
}
