package hana04.formats.mmd.pmd;

import hana04.base.util.BinaryIo;
import hana04.gfxbase.gfxtype.BinaryIoUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.input.SwappedDataInputStream;

import javax.vecmath.Vector3f;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class PmdMaterial {
    public static int NO_SPHERE_MAP = 0;
    public static int MULTIPLY_SPHERE_MAP = 1;
    public static int ADD_SPHERE_MAP = 2;

    public Vector3f diffuse = new Vector3f();
    public float alpha;
    public float shininess;
    public Vector3f specular = new Vector3f();
    public Vector3f ambient = new Vector3f();
    public byte toonIndex;
    public byte edgeFlag;
    public int vertexStart;
    public int vertexCount;
    public String textureFileName;
    public String sphereMapFileName;
    public int sphereMapMode;

    public PmdMaterial() {
        // NO-OP
    }

    public void read(SwappedDataInputStream fin, String directory) throws IOException {
        BinaryIoUtil.readTuple3f(fin, diffuse);
        alpha = fin.readFloat();
        shininess = fin.readFloat();
        BinaryIoUtil.readTuple3f(fin, specular);
        BinaryIoUtil.readTuple3f(fin, ambient);
        toonIndex = fin.readByte();
        edgeFlag = fin.readByte();
        vertexCount = fin.readInt();

        String rawFileName = BinaryIo.readShiftJisString(fin, 20);
        if (rawFileName.length() > 0) {
            int starIndex = rawFileName.lastIndexOf("*");
            if (starIndex < 0) {
                String extension = FilenameUtils.getExtension(rawFileName).toLowerCase();
                if (extension.equals("sph") || extension.equals("spa")) {
                    textureFileName = "";
                    sphereMapFileName = directory + File.separator + rawFileName;
                    if (extension.equals("sph"))
                        sphereMapMode = MULTIPLY_SPHERE_MAP;
                    else
                        sphereMapMode = ADD_SPHERE_MAP;
                } else {
                    textureFileName = directory + File.separator + rawFileName;
                    sphereMapFileName = "";
                    sphereMapMode = NO_SPHERE_MAP;
                }
            } else {
                String[] fileNames = rawFileName.split("\\*");
                textureFileName = directory + File.separator + fileNames[0];
                sphereMapFileName = directory + File.separator + fileNames[1];
                String extension = FilenameUtils.getExtension(sphereMapFileName);
                if (extension.equals("sph"))
                    sphereMapMode = MULTIPLY_SPHERE_MAP;
                else
                    sphereMapMode = ADD_SPHERE_MAP;
            }
        } else {
            textureFileName = "";
            sphereMapFileName = "";
            sphereMapMode = NO_SPHERE_MAP;
        }
    }

    public void write(DataOutputStream fout, File outDir) throws IOException {
        BinaryIoUtil.writeLittleEndianTuple3f(fout, diffuse);
        BinaryIo.writeLittleEndianFloat(fout, alpha);
        BinaryIo.writeLittleEndianFloat(fout, shininess);
        BinaryIoUtil.writeLittleEndianTuple3f(fout, specular);
        BinaryIoUtil.writeLittleEndianTuple3f(fout, ambient);
        fout.write(toonIndex);
        fout.write(edgeFlag);
        BinaryIo.writeLittleEndianInt(fout, vertexCount);

        File textureFile = new File(textureFileName);
        String toSaveTextureFileName = textureFile.getName();
        byte[] textureFileByteArray = toSaveTextureFileName.getBytes("Shift-JIS");
        BinaryIo.writeByteString(fout, textureFileByteArray, 20);

        if (toSaveTextureFileName.length() > 0) {
            File destFile = new File(outDir.getAbsolutePath() + "/" + toSaveTextureFileName);
            if (!destFile.equals(textureFile)) {
                FileUtils.copyFile(textureFile, destFile);
            }
        }
    }
}
