package hana04.formats.mmd.pmd;

import hana04.base.util.BinaryIo;
import hana04.formats.mmd.common.MmdRigidBodyShapeType;
import hana04.formats.mmd.common.MmdRigidBodyType;
import hana04.gfxbase.gfxtype.BinaryIoUtil;
import org.apache.commons.io.input.SwappedDataInputStream;

import javax.vecmath.Vector3f;
import java.io.DataOutputStream;
import java.io.IOException;

public class PmdRigidBody {
  public String name;
  public int boneIndex;
  public byte groupIndex;
  public int hitWithGroupFlags;
  public MmdRigidBodyShapeType shape;
  public float width;
  public float height;
  public float depth;
  public final Vector3f position = new Vector3f();
  public final Vector3f rotation = new Vector3f();
  public float mass;
  public float positionDamping;
  public float rotationDamping;
  public float restitution;
  public float friction;
  public MmdRigidBodyType type;

  public PmdRigidBody() {
    // NO-OP
  }

  public void read(SwappedDataInputStream fin) throws IOException {
    name = BinaryIo.readShiftJisString(fin, 20);
    boneIndex = fin.readShort();
    groupIndex = fin.readByte();
    hitWithGroupFlags = fin.readUnsignedShort();
    shape = MmdRigidBodyShapeType.fromInt(fin.readByte());
    width = fin.readFloat();
    height = fin.readFloat();
    depth = fin.readFloat();

    BinaryIoUtil.readTuple3f(fin, position);
    BinaryIoUtil.readTuple3f(fin, rotation);

    mass = fin.readFloat();
    positionDamping = fin.readFloat();
    rotationDamping = fin.readFloat();
    restitution = fin.readFloat();
    friction = fin.readFloat();
    type = MmdRigidBodyType.fromInt(fin.readByte());
  }

  void write(DataOutputStream fout) throws IOException {
    BinaryIo.writeShiftJISString(fout, name, 20);
    BinaryIo.writeLittleEndianShort(fout, (short) boneIndex);
    fout.write(groupIndex);
    BinaryIo.writeLittleEndianShort(fout, (short) (hitWithGroupFlags & 0xffff));
    fout.write(shape.getValue());
    BinaryIo.writeLittleEndianFloat(fout, width);
    BinaryIo.writeLittleEndianFloat(fout, height);
    BinaryIo.writeLittleEndianFloat(fout, depth);
    BinaryIoUtil.writeLittleEndianTuple3f(fout, position);
    BinaryIoUtil.writeLittleEndianTuple3f(fout, rotation);
    BinaryIo.writeLittleEndianFloat(fout, mass);
    BinaryIo.writeLittleEndianFloat(fout, positionDamping);
    BinaryIo.writeLittleEndianFloat(fout, rotationDamping);
    BinaryIo.writeLittleEndianFloat(fout, restitution);
    BinaryIo.writeLittleEndianFloat(fout, friction);
    fout.write(type.getValue());
  }
}
