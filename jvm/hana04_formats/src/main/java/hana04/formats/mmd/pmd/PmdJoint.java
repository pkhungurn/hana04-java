package hana04.formats.mmd.pmd;

import hana04.base.util.BinaryIo;
import hana04.gfxbase.gfxtype.BinaryIoUtil;
import org.apache.commons.io.input.SwappedDataInputStream;

import javax.vecmath.Vector3f;
import java.io.DataOutputStream;
import java.io.IOException;

public class PmdJoint {
    public String name;
    public final int[] rigidBodies = new int[2];
    public final Vector3f position = new Vector3f();
    public final Vector3f rotation = new Vector3f();
    public final Vector3f linearLowerLimit = new Vector3f();
    public final Vector3f linearUpperLimit = new Vector3f();
    public final Vector3f angularLowerLimit = new Vector3f();
    public final Vector3f angularUpperLimit = new Vector3f();
    public final float[] springLinearStiffness = new float[3];
    public final float[] springAngularStiffness = new float[3];

    public PmdJoint() {
        // NO-OP
    }

    public void read(SwappedDataInputStream fin) throws IOException {
        name = BinaryIo.readShiftJisString(fin, 20);
        rigidBodies[0] = fin.readInt();
        rigidBodies[1] = fin.readInt();

        BinaryIoUtil.readTuple3f(fin, position);
        //position.z = -position.z;
        BinaryIoUtil.readTuple3f(fin, rotation);
        //rotation.x = -rotation.x;
        //rotation.y = -rotation.y;

        BinaryIoUtil.readTuple3f(fin, linearLowerLimit);
        BinaryIoUtil.readTuple3f(fin, linearUpperLimit);
        
        /*
        float temp = linearLowerLimit.z;
        linearLowerLimit.z = -linearUpperLimit.z;
        linearUpperLimit.z = -temp;
        */

        BinaryIoUtil.readTuple3f(fin, angularLowerLimit);
        BinaryIoUtil.readTuple3f(fin, angularUpperLimit);
        
        /*
        temp = angularLowerLimit.x;
        angularLowerLimit.x = -angularLowerLimit.x;
        angularLowerLimit.x = -temp;
        
        temp = angularLowerLimit.y;
        angularLowerLimit.y = -angularLowerLimit.y;
        angularLowerLimit.y = -temp;
        */

        for (int i = 0; i < 3; i++) {
            springLinearStiffness[i] = fin.readFloat();
        }
        for (int i = 0; i < 3; i++) {
            springAngularStiffness[i] = fin.readFloat();
        }
    }

    public void write(DataOutputStream fout) throws IOException {
        BinaryIo.writeShiftJISString(fout, name, 20);
        BinaryIo.writeLittleEndianInt(fout, rigidBodies[0]);
        BinaryIo.writeLittleEndianInt(fout, rigidBodies[1]);
        BinaryIoUtil.writeLittleEndianTuple3f(fout, position);
        BinaryIoUtil.writeLittleEndianTuple3f(fout, rotation);
        BinaryIoUtil.writeLittleEndianTuple3f(fout, linearLowerLimit);
        BinaryIoUtil.writeLittleEndianTuple3f(fout, linearUpperLimit);
        BinaryIoUtil.writeLittleEndianTuple3f(fout, angularLowerLimit);
        BinaryIoUtil.writeLittleEndianTuple3f(fout, angularUpperLimit);
        for (int i = 0; i < 3; i++) {
            BinaryIo.writeLittleEndianFloat(fout, springLinearStiffness[i]);
        }
        for (int i = 0; i < 3; i++) {
            BinaryIo.writeLittleEndianFloat(fout, springAngularStiffness[i]);
        }
    }
}
