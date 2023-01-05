package hana04.shakuyaku.sbtm;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Booleans;
import hana04.base.util.BinaryIo;
import hana04.gfxbase.gfxtype.Aabb3d;
import hana04.gfxbase.gfxtype.BinaryIoUtil;
import hana04.gfxbase.gfxtype.DualNumber2d;
import hana04.gfxbase.gfxtype.DualQuat8d;
import hana04.gfxbase.gfxtype.Matrix4dUtil;
import hana04.gfxbase.gfxtype.PointMathUtil;
import hana04.gfxbase.gfxtype.TupleUtil;
import hana04.gfxbase.gfxtype.VecMathDUtil;
import hana04.gfxbase.util.MathUtil;
import hana04.shakuyaku.surface.geometry.trimesh.TriangleMeshUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.SwappedDataInputStream;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Point3i;
import javax.vecmath.Point4d;
import javax.vecmath.Point4i;
import javax.vecmath.Quat4d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3i;
import javax.vecmath.Tuple4d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class SbtmBaseMeshImpl implements SbtmBaseMesh {
  private static final String MAGIC = "SBTM";
  private final ArrayList<Point3d> positions = new ArrayList<>();
  private final ArrayList<Vector2d> texCoords = new ArrayList<>();
  private final ArrayList<Vector3d> normals = new ArrayList<>();
  private final ArrayList<Vector4d> tangents = new ArrayList<>();
  private final ArrayList<Point3i> triangles = new ArrayList<>();
  private final ArrayList<Point4i> boneIndices = new ArrayList<>();
  private final ArrayList<Point4d> boneWeights = new ArrayList<>();
  private final ArrayList<SbtmSkinningType> skinningTypes = new ArrayList<>();
  private final ArrayList<SbtmBoneImpl> bones = new ArrayList<>();
  private final ArrayList<SbtmMorphImpl> morphs = new ArrayList<>();
  private boolean originallyHasTexCoord;
  private boolean originallyHasNormals;
  private boolean orignallyHasTangents;
  private final ArrayList<Matrix4d> boneRestXform = new ArrayList<>();
  private final ArrayList<Matrix4d> boneRestXformInverse = new ArrayList<>();
  private final Aabb3d aabb = new Aabb3d();
  private final HashMap<Integer, ArrayList<Integer>> vertexToMorphIndices = new HashMap<>();
  private final HashMap<Integer, ArrayList<Vector3d>> vertexToMorphDisplacements = new HashMap<>();
  private final HashMap<Integer, SbtmSdefParams> vertexToSdefParams = new HashMap<>();

  private SbtmBaseMeshImpl() {
    // NO-OP
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private final SbtmBaseMeshImpl data;

    public Builder() {
      data = new SbtmBaseMeshImpl();
    }

    public Builder addPosition(double x, double y, double z) {
      data.positions.add(new Point3d(x, y, z));
      return this;
    }

    public Builder addPosition(Tuple3d p) {
      addPosition(p.x, p.y, p.z);
      return this;
    }

    public Builder addNormal(double x, double y, double z) {
      data.normals.add(new Vector3d(x, y, z));
      return this;
    }

    public Builder addNormal(Tuple3d n) {
      addNormal(n.x, n.y, n.z);
      return this;
    }

    public Builder addTangent(double x, double y, double z, double w) {
      data.tangents.add(new Vector4d(x, y, z, w));
      return this;
    }

    public Builder addTangent(Tuple4d t) {
      addTangent(t.x, t.y, t.z, t.w);
      return this;
    }

    public Builder addTexCoord(double x, double y) {
      data.texCoords.add(new Vector2d(x, y));
      return this;
    }

    public Builder addTexCoord(Tuple2d t) {
      addTexCoord(t.x, t.y);
      return this;
    }

    public Builder addTriangle(int v0, int v1, int v2) {
      data.triangles.add(new Point3i(v0, v1, v2));
      return this;
    }

    public Builder addTriangle(Tuple3i tri) {
      addTriangle(tri.x, tri.y, tri.z);
      return this;
    }

    public Builder addVertexSkinningRecord(SbtmSkinningType sbtmSkinningType) {
      Preconditions.checkState(
          sbtmSkinningType.equals(SbtmSkinningType.LINEAR_BLEND)
              || sbtmSkinningType.equals(SbtmSkinningType.DUAL_QUATERNION),
          "addVertexSkinningRecord with only one argument can be called only with sbtmSkinningType equal to " +
              "LINEAR_BLEND " +
              "or DUAL_QUATERNION.");
      data.skinningTypes.add(sbtmSkinningType);
      data.boneIndices.add(new Point4i(-1, -1, -1, -1));
      data.boneWeights.add(new Point4d(0, 0, 0, 0));
      return this;
    }

    public Builder addVertexSdefSkinningRecord(SbtmSdefParams params) {
      data.skinningTypes.add(SbtmSkinningType.SDEF);
      data.boneIndices.add(new Point4i(-1, -1, -1, -1));
      data.boneWeights.add(new Point4d(0, 0, 0, 0));
      int vertexIndex = data.positions.size() - 1;
      data.vertexToSdefParams.put(vertexIndex, new SbtmSdefParams(params));
      return this;
    }

    public Builder addVertexBoneWeight(int boneIndex, double weight) {
      if (boneIndex < 0) {
        throw new RuntimeException("a bone index cannot be less than 0");
      }
      if (data.boneIndices.isEmpty()) {
        throw new RuntimeException("Add a new vertex bone record first");
      } else {
        Point4i boneIndexRecord = data.boneIndices.get(data.boneIndices.size() - 1);
        Point4d boneWeightRecord = data.boneWeights.get(data.boneIndices.size() - 1);
        boolean found = false;
        for (int i = 0; i < 4; i++) {
          if (TupleUtil.getComponent(boneIndexRecord, i) == -1) {
            found = true;
            TupleUtil.setComponent(boneIndexRecord, i, boneIndex);
            TupleUtil.setComponent(boneWeightRecord, i, weight);
            break;
          }
        }
        if (!found) {
          throw new RuntimeException("a vertex cannot be influenced by more than 4 bones");
        }
      }
      return this;
    }

    public Builder addBone(String name, Point3d relPosition, Quat4d relOrientation, String parentName) {
      SbtmBoneImpl bone = new SbtmBoneImpl();
      if (name == null || name.equals("")) {
        throw new RuntimeException("Name cannot be null or an empty string.");
      }
      bone.name = name;
      bone.translationToParent.set(relPosition);
      bone.rotationToParent.set(relOrientation);
      bone.parentName = parentName;
      data.bones.add(bone);
      return this;
    }

    public Builder addNewMorph(String name) {
      SbtmMorphImpl morph = new SbtmMorphImpl(name);
      data.morphs.add(morph);
      return this;
    }

    public Builder addVertexMorph(int vertexIndex, Vector3d displacement) {
      if (data.morphs.size() == 0) {
        throw new RuntimeException("Please add a new morph first");
      } else {
        SbtmMorphImpl morph = data.morphs.get(data.morphs.size() - 1);
        morph.addVertexMorph(vertexIndex, new Vector3d(displacement.x, displacement.y, displacement.z));
      }
      return this;
    }

    public SbtmBaseMeshImpl build() {
      checkConsistency();

      int vertexCount = data.positions.size();
      data.originallyHasTexCoord = !data.texCoords.isEmpty();
      if (!data.originallyHasTexCoord) {
        for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
          data.texCoords.add(new Vector2d(0, 0));
        }
      }

      data.originallyHasNormals = !data.normals.isEmpty();
      if (data.normals.isEmpty()) {
        data.computeNormals();
      }

      data.orignallyHasTangents = !data.tangents.isEmpty();
      if (data.tangents.isEmpty()) {
        data.computeTangents();
      }

      data.computeBoneData();
      data.computeMorphData();
      data.computeAabb();

      return data;
    }

    private void checkConsistency() {
      int vertexCount = data.positions.size();
      if (!data.texCoords.isEmpty() && data.texCoords.size() != vertexCount) {
        throw new RuntimeException("invalid number of texture coordinates (either 0 or equal to the number of " +
            "vertices");
      }
      if (!data.normals.isEmpty() && data.normals.size() != vertexCount) {
        throw new RuntimeException("invalid number of normals (either 0 or equal to the number of vertices");
      }
      if (!data.tangents.isEmpty() && data.tangents.size() != vertexCount) {
        throw new RuntimeException("invalid number of tangents (either 0 or equal to the number of vertices");
      }
      if (data.boneIndices.size() != vertexCount) {
        throw new RuntimeException("invalid number of bone index record (must be equal to the number of vertices)");
      }
      if (data.boneWeights.size() != vertexCount) {
        throw new RuntimeException("invalid number of bone weight record (must be equal to the number of vertices)");
      }
      if (data.bones.isEmpty()) {
        throw new RuntimeException("there must be at least one bone");
      }

      // Check consistency of bone indices and weights.
      for (int i = 0; i < data.boneIndices.size(); i++) {
        Point4i boneIndexRecord = data.boneIndices.get(i);
        Point4d boneWeightRecord = data.boneWeights.get(i);
        double weightSum = 0;
        boolean found = false;
        for (int j = 0; j < 4; j++) {
          int boneIndex = TupleUtil.getComponent(boneIndexRecord, j);
          double boneWeight = TupleUtil.getComponent(boneWeightRecord, j);
          if (boneIndex >= data.bones.size()) {
            throw new RuntimeException(String.format("invalid bone index: vertex index = %d, bone index = %d", i,
                boneIndex));
          } else if (boneIndex >= 0 && boneIndex < data.bones.size()) {
            weightSum += boneWeight;
            found = true;
          }
        }
        if (!found) {
          throw new RuntimeException("each vertex must be influenced by at least one bone");
        }
        /*
        if (Math.abs(weightSum - 1) > 1e-5) {
          throw new RuntimeException("the sum of influence weights of each vertex must sum up to 1");
        }
        */
      }

      // Check consistency of morphs.
      for (SbtmMorphImpl morph : data.morphs) {
        for (int i = 0; i < morph.getRecordCount(); i++) {
          int vertexIndex = morph.getVertexIndex(i);
          if (vertexIndex < 0 || vertexIndex >= vertexCount) {
            throw new RuntimeException("invalid vertex index in a morph");
          }
        }
      }
    }

    public void deserialize(SwappedDataInputStream stream) {
      try {
        int vertexCount = stream.readInt();
        for (int i = 0; i < vertexCount; i++) {
          addPosition(stream.readDouble(), stream.readDouble(), stream.readDouble());
          addNormal(stream.readDouble(), stream.readDouble(), stream.readDouble());
          addTexCoord(stream.readDouble(), stream.readDouble());
          addTangent(stream.readDouble(), stream.readDouble(), stream.readDouble(), stream.readDouble());

          SbtmSkinningType skinningType = SbtmSkinningType.fromInt(stream.readInt());
          if (skinningType.equals(SbtmSkinningType.SDEF)) {
            Point3d C = new Point3d();
            Point3d R0 = new Point3d();
            Point3d R1 = new Point3d();
            BinaryIoUtil.readTuple3d(stream, C);
            BinaryIoUtil.readTuple3d(stream, R0);
            BinaryIoUtil.readTuple3d(stream, R1);
            addVertexSdefSkinningRecord(new SbtmSdefParams(C, R0, R1));
          } else {
            addVertexSkinningRecord(skinningType);
          }

          Point4i indices = new Point4i();
          BinaryIoUtil.readTuple4i(stream, indices);
          Vector4d weights = new Vector4d();
          BinaryIoUtil.readTuple4d(stream, weights);
          for (int j = 0; j < 4; j++) {
            int index = TupleUtil.getComponent(indices, j);
            if (index != -1) {
              addVertexBoneWeight(TupleUtil.getComponent(indices, j), TupleUtil.getComponent(weights, j));
            }
          }
        }

        int triangleCount = stream.readInt();
        for (int i = 0; i < triangleCount; i++) {
          addTriangle(stream.readInt(), stream.readInt(), stream.readInt());
        }

        int boneCount = stream.readInt();
        for (int i = 0; i < boneCount; i++) {
          String name = BinaryIo.readVariableLengthUtfString(stream);
          Point3d translation = new Point3d();
          BinaryIoUtil.readTuple3d(stream, translation);
          Quat4d rotation = new Quat4d();
          BinaryIoUtil.readTuple4d(stream, rotation);
          String parentName = BinaryIo.readVariableLengthUtfString(stream);
          addBone(name, translation, rotation, parentName);
        }

        int morphCount = stream.readInt();
        for (int i = 0; i < morphCount; i++) {
          String name = BinaryIo.readVariableLengthUtfString(stream);
          addNewMorph(name);
          int count = stream.readInt();
          for (int j = 0; j < count; j++) {
            int vertexIndex = stream.readInt();
            Vector3d displacement = new Vector3d(stream.readDouble(), stream.readDouble(), stream.readDouble());
            addVertexMorph(vertexIndex, displacement);
          }
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  public void getTriangle(int triangleIndex, Tuple3i output) {
    output.set(triangles.get(triangleIndex));
  }

  @Override
  public void getAabb(Aabb3d output) {
    output.set(aabb);
  }

  @Override
  public void getTriangleAabb(int index, Aabb3d output) {
    TriangleMeshUtil.getTriangleAabb(index, output, positions, triangles);
  }

  @Override
  public void getTriangleCentroid(int index, Tuple3d output) {
    TriangleMeshUtil.getTriangleCentroid(index, output, positions, triangles);
  }

  @Override
  public void getBaryPosition(int triangleIndex, Tuple3d bary, Tuple3d output) {
    TriangleMeshUtil.getBaryPosition(triangleIndex, bary, output, positions, triangles);
  }

  @Override
  public void getBaryTexCoord(int triangleIndex, Tuple3d bary, Tuple2d output) {
    TriangleMeshUtil.getBaryTexCoord(triangleIndex, bary, output, texCoords, triangles);
  }

  @Override
  public void getTriangleNormal(int triangleIndex, Tuple3d output) {
    TriangleMeshUtil.getTriangleNormal(triangleIndex, output, positions, triangles);
  }

  @Override
  public void getBaryNormal(int triangleIndex, Tuple3d bary, Vector3d output) {
    TriangleMeshUtil.getBaryNormal(triangleIndex, bary, output, normals, triangles);
  }

  @Override
  public void getBaryTangent(int triangleIndex, Tuple3d bary, Vector3d normal, Vector4d output) {
    TriangleMeshUtil.getBaryTangent(triangleIndex, bary, normal, output, tangents, triangles);
  }

  @Override
  public void getTriangleVertexIndices(int triangleIndex, Tuple3i output) {
    output.set(triangles.get(triangleIndex));
  }

  @Override
  public double getTriangleArea(int triangleIndex) {
    return TriangleMeshUtil.getTriangleArea(triangleIndex, positions, triangles);
  }

  @Override
  public int getTriangleCount() {
    return triangles.size();
  }

  @Override
  public int getVertexCount() {
    return positions.size();
  }

  public void getPosition(int vertexIndex, Tuple3d output) {
    output.set(positions.get(vertexIndex));
  }

  public void getNormal(int vertexIndex, Tuple3d output) {
    output.set(normals.get(vertexIndex));
  }

  @Override
  public void getTangent(int index, Tuple4d output) {
    output.set(tangents.get(index));
  }

  private void setNormal(int vertexIndex, Tuple3d input) {
    normals.get(vertexIndex).set(input);
  }

  public void getTexCoord(int vertexIndex, Tuple2d output) {
    output.set(texCoords.get(vertexIndex));
  }

  private void setTangent(int vertexIndex, double x, double y, double z, double w) {
    tangents.get(vertexIndex).set(x, y, z, w);
  }

  private void computeNormals() {
    TriangleMeshUtil.computeNormals(positions, triangles, normals);
  }

  private void computeTangents() {
    TriangleMeshUtil.computeTangents(positions, texCoords, normals, triangles, tangents);
  }

  private void computeAabb() {
    TriangleMeshUtil.computeAabb(positions, aabb);
  }

  private void computeBoneData() {
    for (int i = 0; i < bones.size(); i++) {
      SbtmBoneImpl bone = bones.get(i);
      if (bone.parentName.equals("")) {
        bone.parentIndex = -1;
      } else {
        boolean found = false;
        for (int j = 0; j < bones.size(); j++) {
          if (bones.get(j).name.equals(bone.parentName)) {
            bone.parentIndex = j;
            found = true;
            break;
          }
        }
        if (!found) {
          bone.parentIndex = -1;
        }
      }
    }

    boneRestXformInverse.clear();
    boneRestXform.clear();

    for (int i = 0; i < bones.size(); i++) {
      Matrix4d m = new Matrix4d();
      m.setIdentity();
      boneRestXformInverse.add(m);
      m = new Matrix4d();
      m.setIdentity();
      boneRestXform.add(m);
    }

    for (int i = 0; i < bones.size(); i++) {
      int current = i;
      Matrix4d restXform = boneRestXform.get(i);
      while (current >= 0) {
        Matrix4d xform = new Matrix4d();
        xform.setIdentity();
        SbtmBoneImpl bone = bones.get(current);
        xform.setRotation(bone.rotationToParent);
        xform.setTranslation(bone.translationToParent);
        restXform.mul(xform, restXform);
        current = bone.parentIndex;
      }
      boneRestXformInverse.get(i).invert(restXform);
    }
  }

  private void computeMorphData() {
    vertexToMorphIndices.clear();
    vertexToMorphDisplacements.clear();
    for (int morphIndex = 0; morphIndex < getMorphCount(); morphIndex++) {
      SbtmMorphImpl morph = morphs.get(morphIndex);
      for (int i = 0; i < morph.getRecordCount(); i++) {
        int vertexIndex = morph.getVertexIndex(i);
        if (!vertexToMorphIndices.containsKey(vertexIndex)) {
          vertexToMorphIndices.put(vertexIndex, new ArrayList<>());
          vertexToMorphDisplacements.put(vertexIndex, new ArrayList<>());
        }
        vertexToMorphIndices.get(vertexIndex).add(morphIndex);
        Vector3d disp = new Vector3d();
        morph.getDisplacement(i, disp);
        vertexToMorphDisplacements.get(vertexIndex).add(disp);
      }
    }
  }

  public void serialize(DataOutputStream stream) {
    try {
      BinaryIo.writeLittleEndianInt(stream, getVertexCount());
      for (int i = 0; i < getVertexCount(); i++) {
        BinaryIoUtil.writeLittleEndianTuple3d(stream, positions.get(i));
        BinaryIoUtil.writeLittleEndianTuple3d(stream, normals.get(i));
        BinaryIoUtil.writeLittleEndianTuple2d(stream, texCoords.get(i));
        BinaryIoUtil.writeLittleEndianTuple4d(stream, tangents.get(i));
        BinaryIo.writeLittleEndianInt(stream, skinningTypes.get(i).value);
        if (skinningTypes.get(i).equals(SbtmSkinningType.SDEF)) {
          SbtmSdefParams params = vertexToSdefParams.get(i);
          BinaryIoUtil.writeLittleEndianTuple3d(stream, params.C);
          BinaryIoUtil.writeLittleEndianTuple3d(stream, params.R0);
          BinaryIoUtil.writeLittleEndianTuple3d(stream, params.R1);
        }
        BinaryIoUtil.writeLittleEndianTuple4i(stream, boneIndices.get(i));
        BinaryIoUtil.writeLittleEndianTuple4d(stream, boneWeights.get(i));
      }
      BinaryIo.writeLittleEndianInt(stream, triangles.size());
      for (int i = 0; i < getTriangleCount(); i++) {
        BinaryIoUtil.writeLittleEndianTuple3i(stream, triangles.get(i));
      }
      BinaryIo.writeLittleEndianInt(stream, bones.size());
      for (SbtmBoneImpl bone : bones) {
        BinaryIo.writeLittleEndianVaryingLengthUtfString(stream, bone.name);
        BinaryIoUtil.writeLittleEndianTuple3d(stream, bone.translationToParent);
        BinaryIoUtil.writeLittleEndianTuple4d(stream, bone.rotationToParent);
        if (bone.parentIndex < 0) {
          BinaryIo.writeLittleEndianVaryingLengthUtfString(stream, "");
        } else {
          BinaryIo.writeLittleEndianVaryingLengthUtfString(stream, bones.get(bone.parentIndex).name);
        }
      }
      BinaryIo.writeLittleEndianInt(stream, morphs.size());
      Vector3d disp = new Vector3d();
      for (SbtmMorphImpl morph : morphs) {
        BinaryIo.writeLittleEndianVaryingLengthUtfString(stream, morph.getName());
        BinaryIo.writeLittleEndianInt(stream, morph.getRecordCount());
        for (int i = 0; i < morph.getRecordCount(); i++) {
          BinaryIo.writeLittleEndianInt(stream, morph.getVertexIndex(i));
          morph.getDisplacement(i, disp);
          BinaryIoUtil.writeLittleEndianTuple3d(stream, disp);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void save(Path path) {
    try {
      ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      DataOutputStream fout = new DataOutputStream(outStream);
      BinaryIo.writeLittleEndianVaryingLengthUtfString(fout, MAGIC);
      serialize(fout);
      fout.close();
      outStream.close();

      byte[] data = outStream.toByteArray();
      FileUtils.writeByteArrayToFile(path.toFile(), data);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static SbtmBaseMeshImpl load(Path path) {
    try {
      long fileSize = Files.size(path);
      ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
      SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.READ);
      channel.read(buffer);
      channel.close();

      ByteArrayInputStream stream = new ByteArrayInputStream(buffer.array());
      SwappedDataInputStream swappedStream = new SwappedDataInputStream(stream);
      String magic = BinaryIo.readVariableLengthUtfString(swappedStream);
      Preconditions.checkArgument(magic.equals(MAGIC));
      SbtmBaseMeshImpl.Builder sbtm = SbtmBaseMeshImpl.builder();
      sbtm.deserialize(swappedStream);
      swappedStream.close();
      return sbtm.build();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public int getBoneCount() {
    return bones.size();
  }

  public int getMorphCount() {
    return morphs.size();
  }

  public SbtmBoneImpl getBone(int index) {
    return bones.get(index);
  }

  public SbtmMorphImpl getMorph(int index) {
    return morphs.get(index);
  }

  public SbtmDataPosed pose(SbtmPose pose) {
    SbtmDataPosed result = new SbtmDataPosed(texCoords, triangles);
    List<Matrix4d> skinningXforms = getPosedBoneMatricesForBlending(pose);
    for (int vertexIndex = 0; vertexIndex < getVertexCount(); vertexIndex++) {
      Point3d worldPosition = getPosedVertexPosition(pose, skinningXforms, vertexIndex);
      result.positions.get(vertexIndex).set(worldPosition);
    }
    result.computeNormals();
    result.computeTangents();
    result.computeAabb();
    return result;
  }

  private Point3d getPosedVertexPosition(SbtmPose pose, List<Matrix4d> skinningXforms, int vertexIndex) {
    Point3d morphedPosition = new Point3d(positions.get(vertexIndex));
    if (vertexToMorphIndices.containsKey(vertexIndex)) {
      for (int i = 0; i < vertexToMorphIndices.get(vertexIndex).size(); i++) {
        int morphIndex = vertexToMorphIndices.get(vertexIndex).get(i);
        String morphName = morphs.get(morphIndex).getName();
        if (!pose.morphPoses.containsKey(morphName)) {
          continue;
        }
        double morphWeight = pose.morphPoses.get(morphName);
        morphedPosition.scaleAdd(morphWeight, vertexToMorphDisplacements.get(vertexIndex).get(i),
            morphedPosition);
      }
    }

    Point3d worldPosition;
    SbtmSkinningType skinningType = skinningTypes.get(vertexIndex);
    if (skinningType.equals(SbtmSkinningType.LINEAR_BLEND)) {
      worldPosition = skinWithLinearBlendSkinning(vertexIndex, morphedPosition, skinningXforms);
    } else if (skinningType.equals(SbtmSkinningType.SDEF)) {
      worldPosition = skinWithSdef(vertexIndex, morphedPosition, skinningXforms);
    } else {
      worldPosition = skinWithDualQuaternion(vertexIndex, morphedPosition, skinningXforms);
    }
    return worldPosition;
  }

  public Point3d getPosedVertexPosition(SbtmPose pose, int vertexIndex) {
    List<Matrix4d> skinningXforms = getPosedBoneMatricesForBlending(pose);
    return getPosedVertexPosition(pose, skinningXforms, vertexIndex);
  }

  private Point3d skinWithLinearBlendSkinning(int vertexIndex, Point3d morphedPosition, List<Matrix4d> skinningXforms) {
    Point3d outputPosition = new Point3d(0, 0, 0);
    Point4i vertexBoneIndices = boneIndices.get(vertexIndex);
    Point4d vertexBoneWeights = boneWeights.get(vertexIndex);
    for (int i = 0; i < 4; i++) {
      int boneIndex = TupleUtil.getComponent(vertexBoneIndices, i);
      if (boneIndex < 0) {
        continue;
      }
      double boneWeight = TupleUtil.getComponent(vertexBoneWeights, i);

      Point3d bonePosition = new Point3d(0, 0, 0);
      skinningXforms.get(boneIndex).transform(morphedPosition, bonePosition);
      outputPosition.scaleAdd(boneWeight, bonePosition, outputPosition);
    }
    return outputPosition;
  }

  private Point3d skinWithSdef(int vertexIndex, Point3d morphedPosition, List<Matrix4d> skinningXforms) {
    // This implementation is based on MikuMikuFlex.
    // See: https://github.com/DTXMania/MikuMikuFlex/blob/2da1dfe337825bd7ec4bd6e3e8b39d4046eb844d/MikuMikuFlex
    // /Resource/Shader/DefaultShader.fx

    int boneIndex0 = boneIndices.get(vertexIndex).x;
    int boneIndex1 = boneIndices.get(vertexIndex).y;
    Preconditions.checkState(boneIndex0 >= 0);
    Preconditions.checkState(boneIndex1 >= 0);
    Point4d bw = boneWeights.get(vertexIndex);
    double bw0 = bw.x;
    double bw1 = bw.y;
    Matrix4d worldM0 = skinningXforms.get(boneIndex0);
    Matrix4d worldM1 = skinningXforms.get(boneIndex1);
    Matrix4d blendedM = Matrix4dUtil.add(Matrix4dUtil.scale(worldM0, bw0), Matrix4dUtil.scale(worldM1, bw1));

    SbtmSdefParams params = vertexToSdefParams.get(vertexIndex);
    Point3d C = params.C;
    Point3d R0 = params.R0;
    Point3d R1 = params.R1;

    Point3d bonePos = getRestBonePosition(boneIndex1);
    double l0 = bonePos.distance(R0);
    double l1 = bonePos.distance(R1);

    double w0;
    if (Math.abs(l0 - l1) < 1e-4) {
      w0 = 0.5;
    } else {
      w0 = MathUtil.clamp(l0 / (l0 + l1), 0, 1);
    }
    double w1 = 1 - w0;

    Point3d worldC = Matrix4dUtil.transform(blendedM, C);

    Quat4d qM0 = Matrix4dUtil.rotationPartToQuaternion(worldM0);
    Quat4d qM1 = Matrix4dUtil.rotationPartToQuaternion(worldM1);
    Quat4d qBlended = new Quat4d();
    qBlended.interpolate(qM0, qM1, 1 - bw0);

    Point3d R0pos = PointMathUtil.add(
        PointMathUtil.scale(Matrix4dUtil.transform(worldM0, R0), bw0),
        PointMathUtil.scale(Matrix4dUtil.transform(blendedM, R0), -bw0));
    Point3d R1pos = PointMathUtil.add(
        PointMathUtil.scale(Matrix4dUtil.transform(worldM1, R1), bw1),
        PointMathUtil.scale(Matrix4dUtil.transform(blendedM, R1), -bw1));
    Point3d Cpos = PointMathUtil.add(worldC, PointMathUtil.add(
        PointMathUtil.scale(R0pos, w0),
        PointMathUtil.scale(R1pos, w1)));
    Matrix4d blendedMInverse = new Matrix4d(blendedM);
    blendedMInverse.invert();
    Cpos = Matrix4dUtil.transform(blendedMInverse, Cpos);

    return PointMathUtil.add(
        Matrix4dUtil.transform(blendedM, Cpos),
        VecMathDUtil.rotate(qBlended, PointMathUtil.sub(morphedPosition, Cpos)));
  }

  private Point3d skinWithDualQuaternion(int vertexIndex, Point3d morphedPosition, List<Matrix4d> posedXforms) {
    DualQuat8d blendedQ = new DualQuat8d(0, 0, 0, 0, 0, 0, 0, 0);
    Point4i vertexBoneIndices = boneIndices.get(vertexIndex);
    Point4d vertexBoneWeights = boneWeights.get(vertexIndex);
    for (int i = 0; i < 4; i++) {
      int boneIndex = TupleUtil.getComponent(vertexBoneIndices, i);
      if (boneIndex < 0) {
        continue;
      }
      double boneWeight = TupleUtil.getComponent(vertexBoneWeights, i);

      Matrix4d M = posedXforms.get(boneIndex);
      DualQuat8d Q = DualQuat8d.fromMatrix(M);

      if (blendedQ.q0.dot(Q.q0) < 0) {
        Q.scaleI(-boneWeight);
      } else {
        Q.scaleI(boneWeight);
      }
      blendedQ.addI(Q);
    }
    DualNumber2d normInv = blendedQ.norm();
    normInv.invertI();
    blendedQ.mulI(DualQuat8d.fromDualNumber(normInv));
    Matrix4d blendedM = blendedQ.toMatrix();
    Point3d outputPosition = new Point3d(0, 0, 0);
    blendedM.transform(morphedPosition, outputPosition);
    return outputPosition;
  }

  @Override
  public int getVertexMorphCount(int vertexIndex) {
    if (!vertexToMorphIndices.containsKey(vertexIndex)) {
      return 0;
    }
    return vertexToMorphIndices.get(vertexIndex).size();
  }

  @Override
  public void getVertexMorphDisplacement(int vertexIndex, int vertexMorphOrder, Tuple3d output) {
    if (!vertexToMorphIndices.containsKey(vertexIndex)) {
      output.set(0, 0, 0);
      return;
    }
    output.set(vertexToMorphDisplacements.get(vertexIndex).get(vertexMorphOrder));
  }

  @Override
  public int getMorphIndex(int vertexIndex, int vertexMorphOrder) {
    return vertexToMorphIndices.get(vertexIndex).get(vertexMorphOrder);
  }

  @Override
  public SbtmSkinningType getVertexSkinningType(int vertexIndex) {
    return skinningTypes.get(vertexIndex);
  }

  @Override
  public int getVertexBoneCount(int vertexIndex) {
    int output = Booleans.countTrue(
        boneIndices.get(vertexIndex).x >= 0,
        boneIndices.get(vertexIndex).y >= 0,
        boneIndices.get(vertexIndex).z >= 0,
        boneIndices.get(vertexIndex).w >= 0);
    return output;
  }

  @Override
  public int getVertexBoneIndex(int vertexIndex, int vertexBoneIndex) {
    return TupleUtil.getComponent(boneIndices.get(vertexIndex), vertexBoneIndex);
  }

  @Override
  public double getVertexBoneWeight(int vertexIndex, int vertexBoneIndex) {
    return TupleUtil.getComponent(boneWeights.get(vertexIndex), vertexBoneIndex);
  }

  @Override
  public List<Matrix4d> getPosedBoneToWorldMatrices(SbtmPose pose) {
    ArrayList<Matrix4d> result = new ArrayList<Matrix4d>();
    for (int i = 0; i < bones.size(); i++) {
      result.add(Matrix4dUtil.createIdentity());
    }
    for (int i = 0; i < bones.size(); i++) {
      Matrix4d toWorld = Matrix4dUtil.createIdentity();
      int current = i;
      while (current >= 0) {
        SbtmBoneImpl bone = bones.get(current);

        Vector3d poseTranslation = new Vector3d(0, 0, 0);
        Quat4d poseRotation = new Quat4d(0, 0, 0, 1);
        pose.getBonePose(bone.name, poseTranslation, poseRotation);
        poseTranslation.add(bone.translationToParent);
        poseRotation.mul(poseRotation, bone.rotationToParent);

        Matrix4d xform = Matrix4dUtil.createIdentity();
        xform.setRotation(poseRotation);
        xform.setTranslation(poseTranslation);
        toWorld.mul(xform, toWorld);

        current = bone.parentIndex;
      }
      result.get(i).set(toWorld);
    }

    return result;
  }

  @Override
  public List<Matrix4d> getPosedBoneMatricesForBlending(SbtmPose pose) {
    ArrayList<Matrix4d> result = new ArrayList<Matrix4d>();
    for (int i = 0; i < bones.size(); i++) {
      result.add(Matrix4dUtil.createIdentity());
    }
    for (int i = 0; i < bones.size(); i++) {
      Matrix4d toWorld = Matrix4dUtil.createIdentity();
      int current = i;
      while (current >= 0) {
        SbtmBoneImpl bone = bones.get(current);

        Vector3d poseTranslation = new Vector3d(0, 0, 0);
        Quat4d poseRotation = new Quat4d(0, 0, 0, 1);
        pose.getBonePose(bone.name, poseTranslation, poseRotation);
        poseTranslation.add(bone.translationToParent);
        poseRotation.mul(poseRotation, bone.rotationToParent);

        Matrix4d xform = Matrix4dUtil.createIdentity();
        xform.setRotation(poseRotation);
        xform.setTranslation(poseTranslation);
        toWorld.mul(xform, toWorld);

        current = bone.parentIndex;
      }
      result.get(i).set(toWorld);
    }
    for (int i = 0; i < bones.size(); i++) {
      result.get(i).mul(boneRestXformInverse.get(i));
    }
    return result;
  }

  @Override
  public List<Matrix4d> getInverseRestBoneMatrices() {
    ArrayList<Matrix4d> result = new ArrayList<Matrix4d>();
    for (int i = 0; i < bones.size(); i++) {
      result.add(new Matrix4d(boneRestXformInverse.get(i)));
    }
    return result;
  }

  @Override
  public Optional<SbtmSdefParams> getSdefParams(int vertexIndex) {
    return Optional.ofNullable(vertexToSdefParams.get(vertexIndex));
  }

  @Override
  public Point3d getRestBonePosition(int boneIndex) {
    Point3d output = new Point3d();
    SbtmBoneImpl bone = bones.get(boneIndex);
    while (true) {
      Matrix4d toParent = new Matrix4d();
      toParent.set(bone.rotationToParent);
      toParent.setTranslation(bone.translationToParent);
      toParent.transform(output);
      if (bone.parentIndex < 0) {
        break;
      } else {
        bone = bones.get(bone.parentIndex);
      }
    }
    return output;
  }
}
