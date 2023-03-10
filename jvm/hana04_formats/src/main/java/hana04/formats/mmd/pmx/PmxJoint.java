
package hana04.formats.mmd.pmx;

import hana04.base.util.BinaryIo;
import hana04.gfxbase.gfxtype.BinaryIoUtil;
import org.apache.commons.io.input.SwappedDataInputStream;

import javax.vecmath.Vector3f;
import java.io.IOException;
import java.nio.charset.Charset;

public class PmxJoint {
    public String japaneseName;
    public String englishName;
    public int type;
    public final int[] rigidBodies = new int[2];
    public final Vector3f position = new Vector3f();
    public final Vector3f rotation = new Vector3f();
    public final Vector3f linearLowerLimit = new Vector3f();
    public final Vector3f linearUpperLimit = new Vector3f();
    public final Vector3f angularLowerLimit = new Vector3f();
    public final Vector3f angularUpperLimit = new Vector3f();
    public final float[] springLinearStiffness = new float[3];
    public final float[] springAngularStiffness = new float[3];

    public void read(SwappedDataInputStream fin, Charset charset, int rigidBodyIndexSize) throws IOException {
        japaneseName = BinaryIo.readVariableLengthString(fin, charset);
        englishName = BinaryIo.readVariableLengthString(fin, charset);

        type = fin.readByte();

        rigidBodies[0] = BinaryIo.readIntGivenSizeInBytes(fin, rigidBodyIndexSize, false);
        rigidBodies[1] = BinaryIo.readIntGivenSizeInBytes(fin, rigidBodyIndexSize, false);

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
}
