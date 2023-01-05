
package hana04.formats.unreal;

import hana04.gfxbase.gfxtype.Aabb3f;
import org.apache.commons.io.input.SwappedDataInputStream;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class PskMesh {
  public String directory = "";
  public String name = "";
  public VChunkHeader header = new VChunkHeader();
  public VChunkHeader pointHeader = new VChunkHeader();
  public ArrayList<Point3f> points = new ArrayList<Point3f>();
  public VChunkHeader vertexHeader = new VChunkHeader();
  public ArrayList<VVertex> vertices = new ArrayList<VVertex>();
  public VChunkHeader faceHeader = new VChunkHeader();
  public ArrayList<VTriangle> faces = new ArrayList<VTriangle>();
  public VChunkHeader materialHeader = new VChunkHeader();
  public ArrayList<VMaterial> materials = new ArrayList<VMaterial>();
  public VChunkHeader boneHeader = new VChunkHeader();
  public ArrayList<VBone> bones = new ArrayList<VBone>();
  public VChunkHeader influencesHeader = new VChunkHeader();
  public ArrayList<VRawBoneInfluence> influences = new ArrayList<VRawBoneInfluence>();

  public PskMesh() {
    // NO-OP
  }

  public int getPointCount() {
    return pointHeader.dataCount;
  }

  public int getVertexCount() {
    return vertexHeader.dataCount;
  }

  public int getMaterialCount() {
    return materialHeader.dataCount;
  }

  public int getBoneCount() {
    return boneHeader.dataCount;
  }

  public void read(SwappedDataInputStream fin) throws IOException {
    readHeader(fin);
    readPoints(fin);
    readVertices(fin);
    readFaces(fin);
    readMaterials(fin);
    readBones(fin);
    readInfluences(fin);
  }

  private void readHeader(SwappedDataInputStream fin) throws IOException {
    header.read(fin);
  }

  private void readPoints(SwappedDataInputStream fin) throws IOException {
    pointHeader.read(fin);

    int vertexCount = pointHeader.dataCount;
    for (int i = 0; i < vertexCount; i++) {
      float x = fin.readFloat();
      float y = fin.readFloat();
      float z = fin.readFloat();
      points.add(new Point3f(x, y, z));
    }
  }

  private void readVertices(SwappedDataInputStream fin) throws IOException {
    vertexHeader.read(fin);

    for (int i = 0; i < vertexHeader.dataCount; i++) {
      VVertex v = new VVertex();
      v.read(fin);
      vertices.add(v);
      fin.readInt();
    }
  }

  private void readFaces(SwappedDataInputStream fin) throws IOException {
    faceHeader.read(fin);

    for (int i = 0; i < faceHeader.dataCount; i++) {
      VTriangle tri = new VTriangle();
      tri.read(fin);
      faces.add(tri);
    }
  }

  private void readMaterials(SwappedDataInputStream fin) throws IOException {
    materialHeader.read(fin);

    for (int i = 0; i < materialHeader.dataCount; i++) {
      VMaterial material = new VMaterial();
      material.read(fin, directory);
      materials.add(material);
    }
  }

  private void readBones(SwappedDataInputStream fin) throws IOException {
    boneHeader.read(fin);

    for (int i = 0; i < boneHeader.dataCount; i++) {
      VBone bone = new VBone();
      bone.read(fin);
      bones.add(bone);
    }

    bones.get(0).parentIndex = -1;
    //bones.get(0).bonePose.orientation.y *= -1;
    for (int i = 1; i < boneHeader.dataCount; i++) {
      VJointPos pos = bones.get(i).bonePose;
      pos.orientation.x *= -1;
      pos.orientation.y *= -1;
      pos.orientation.z *= -1;
    }
  }

  private void readInfluences(SwappedDataInputStream fin) throws IOException {
    influencesHeader.read(fin);

    for (int i = 0; i < influencesHeader.dataCount; i++) {
      VRawBoneInfluence influence = new VRawBoneInfluence();
      influence.read(fin);
      influences.add(influence);
    }
  }

  public static PskMesh load(String fileName) throws IOException {
    PskMesh psk = new PskMesh();

    File theFile = new File(fileName);
    File absoluteFile = theFile.getAbsoluteFile();
    psk.directory = absoluteFile.getParent();
    psk.name = absoluteFile.getName();
    String[] comps = psk.name.split("\\.(?=[^\\.]+$)");
    psk.name = comps[0];

    long fileSize = theFile.length();
    if (fileSize > Integer.MAX_VALUE) {
      throw new RuntimeException("file to large to load");
    }
    ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
    FileInputStream fileInputStream = new FileInputStream(theFile);
    FileChannel channel = fileInputStream.getChannel();
    channel.read(buffer);
    channel.close();
    fileInputStream.close();
    buffer.rewind();
    ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer.array());

    SwappedDataInputStream fin = new SwappedDataInputStream(inputStream);
    psk.read(fin);
    fin.close();

    return psk;
  }

  public void getBoneWorldPosition(int boneIndex, Point3f output) {
    Matrix4f xform = new Matrix4f();

    output.set(0, 0, 0);
    int current = boneIndex;
    while (current >= 0) {
      current = getBoneWorldPositionHelper(current, xform, output);
    }
  }

  private int getBoneWorldPositionHelper(int current, Matrix4f xform, Point3f output) {
    VBone bone = bones.get(current);
    xform.setIdentity();
    VJointPos jointPos = bone.bonePose;
    xform.setRotation(jointPos.orientation);
    xform.transform(output);
    output.add(jointPos.position);
    current = bone.parentIndex;
    return current;
  }

  public int getBoneIndex(String boneName) {
    for (int i = 0; i < bones.size(); i++) {
      VBone bone = bones.get(i);
      if (bone.name.equals(boneName)) {
        return i;
      }
    }
    return -1;
  }

  public void getBoneTransform(int boneIndex, Matrix4f output) {
    Quat4f rotation = new Quat4f();
    Matrix4f xform = new Matrix4f();

    output.setIdentity();
    int current = boneIndex;
    while (current >= 0) {
      VBone bone = bones.get(current);
      VJointPos jointPos = bone.bonePose;
      xform.setIdentity();
      xform.setRotation(jointPos.orientation);
      xform.setTranslation(jointPos.position);
      output.mul(xform, output);

      current = bone.parentIndex;
    }
  }

  public void getAabb(Aabb3f aabb) {
    aabb.reset();
    for (Point3f p : points) {
      aabb.expandBy(p);
    }
  }
}
