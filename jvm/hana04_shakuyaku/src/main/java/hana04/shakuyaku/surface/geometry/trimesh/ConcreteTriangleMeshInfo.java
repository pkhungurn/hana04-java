package hana04.shakuyaku.surface.geometry.trimesh;

import com.google.common.base.Preconditions;
import hana04.gfxbase.gfxtype.Aabb3d;
import hana04.gfxbase.gfxtype.Matrix4dUtil;
import hana04.gfxbase.gfxtype.Transform;
import hana04.gfxbase.gfxtype.WireSerializationUtil;

import javax.vecmath.Point3d;
import javax.vecmath.Point3i;
import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3i;
import javax.vecmath.Tuple4d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

public class ConcreteTriangleMeshInfo implements TriangleMeshInfo {
  /**
   * Vertex positions.
   */
  private final ArrayList<Point3d> positions = new ArrayList<>();
  /**
   * Vertex normals
   */
  private final ArrayList<Vector3d> normals = new ArrayList<>();
  /**
   * Vertex texture coordinates.
   */
  private final ArrayList<Vector2d> texCoords = new ArrayList<>();
  /**
   * Vertex tangent vector.
   */
  private final ArrayList<Vector4d> tangents = new ArrayList<>();
  /**
   * Faces
   */
  private final ArrayList<Point3i> triangles = new ArrayList<>();
  /**
   * Axis-aligned bounding box.
   */
  private final Aabb3d aabb = new Aabb3d();
  /**
   * Total surface area.
   */
  private double surfaceArea;

  public int getTriangleCount() {
    return triangles.size();
  }

  public int getVertexCount() {
    return positions.size();
  }

  @Override
  public void getPosition(int index, Tuple3d output) {
    output.set(positions.get(index));
  }

  @Override
  public void getTexCoord(int index, Tuple2d output) {
    output.set(texCoords.get(index));
  }

  @Override
  public void getNormal(int index, Tuple3d output) {
    output.set(normals.get(index));
  }

  @Override
  public void getTangent(int index, Tuple4d output) {
    output.set(tangents.get(index));
  }

  @Override
  public void getTriangle(int index, Tuple3i output) {
    output.set(triangles.get(index));
  }

  @Override
  public void getAabb(Aabb3d aabb) {
    aabb.set(this.aabb);
  }

  @Override
  public void getTriangleAabb(int index, Aabb3d aabb) {
    TriangleMeshUtil.getTriangleAabb(index, aabb, positions, triangles);
  }

  @Override
  public void getTriangleCentroid(int index, Tuple3d centroid) {
    TriangleMeshUtil.getTriangleCentroid(index, centroid, positions, triangles);
  }

  public double getTriangleSurfaceArea(int index) {
    return TriangleMeshUtil.getTriangleArea(index, positions, triangles);
  }

  public double getSurfaceArea() {
    return surfaceArea;
  }

  public void computeAggregateSurfaceArea() {
    surfaceArea = TriangleMeshUtil.computeAggregateSurfaceArea(positions, triangles);
  }

  void serialize(DataOutputStream stream) {
    try {
      stream.writeInt(positions.size());
      for (int i = 0; i < positions.size(); i++) {
        WireSerializationUtil.writeTuple3d(stream, positions.get(i));
      }
      stream.writeInt(normals.size());
      for (int i = 0; i < normals.size(); i++) {
        WireSerializationUtil.writeTuple3d(stream, normals.get(i));
      }
      stream.writeInt(texCoords.size());
      for (int i = 0; i < texCoords.size(); i++) {
        WireSerializationUtil.writeTuple2d(stream, texCoords.get(i));
      }
      stream.writeInt(tangents.size());
      for (int i = 0; i < tangents.size(); i++) {
        WireSerializationUtil.writeTuple4d(stream, tangents.get(i));
      }
      stream.writeInt(triangles.size());
      for (int i = 0; i < triangles.size(); i++) {
        WireSerializationUtil.writeTuple3i(stream, triangles.get(i));
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  void deserialize(DataInputStream stream) {
    try {
      // Positions
      aabb.reset();
      int positionCount = stream.readInt();
      for (int i = 0; i < positionCount; i++) {
        Point3d p = new Point3d();
        WireSerializationUtil.readTuple3d(stream, p);
        aabb.expandBy(p);
        positions.add(p);
      }
      // Normals
      int normalCount = stream.readInt();
      for (int i = 0; i < normalCount; i++) {
        Vector3d n = new Vector3d();
        WireSerializationUtil.readTuple3d(stream, n);
        normals.add(n);
      }
      // TexCoords
      int texCoordCount = stream.readInt();
      for (int i = 0; i < texCoordCount; i++) {
        Vector2d t = new Vector2d();
        WireSerializationUtil.readTuple2d(stream, t);
        texCoords.add(t);
      }
      // Tangents
      int tangentCount = stream.readInt();
      for (int i = 0; i < tangentCount; i++) {
        Vector4d t = new Vector4d();
        WireSerializationUtil.readTuple4d(stream, t);
        tangents.add(t);
      }
      // Triangles
      int triangleCount = stream.readInt();
      for (int i = 0; i < triangleCount; i++) {
        Point3i tri = new Point3i();
        WireSerializationUtil.readTuple3i(stream, tri);
        triangles.add(tri);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void computeNormals() {
    TriangleMeshUtil.computeNormals(positions, triangles, normals);
  }

  private void computeAabb() {
    TriangleMeshUtil.computeAabb(positions, aabb);
  }

  private void computeTangents() {
    TriangleMeshUtil.computeTangents(positions, texCoords, normals, triangles, tangents);
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

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private ConcreteTriangleMeshInfo data;

    public Builder() {
      this(new Transform(Matrix4dUtil.IDENTITY_MATRIX));
    }

    public Builder(Transform transform) {
      data = new ConcreteTriangleMeshInfo();
    }

    public Builder addPosition(double x, double y, double z) {
      data.positions.add(new Point3d(x, y, z));
      return this;
    }

    public Builder addPosition(Tuple3d p) {
      return addPosition(p.x, p.y, p.z);
    }

    public Builder addNormal(double x, double y, double z) {
      data.normals.add(new Vector3d(x, y, z));
      return this;
    }

    public Builder addNormal(Tuple3d p) {
      return addNormal(p.x, p.y, p.z);
    }

    public Builder addTexCoord(double x, double y) {
      data.texCoords.add(new Vector2d(x, y));
      return this;
    }

    public Builder addTexCoord(Tuple2d p) {
      addTexCoord(p.x, p.y);
      return this;
    }

    public Builder addTangent(double x, double y, double z, double w) {
      Preconditions.checkArgument(Math.abs(Math.abs(w) - 1) < 1e-8);
      data.tangents.add(new Vector4d(x, y, z, w));
      return this;
    }

    public Builder addTangent(Tuple4d p) {
      return addTangent(p.x, p.y, p.z, p.w);
    }

    public Builder addTriangle(int v0, int v1, int v2) {
      data.triangles.add(new Point3i(v0, v1, v2));
      return this;
    }

    public Builder addTriangle(Tuple3i p) {
      return addTriangle(p.x, p.y, p.z);
    }

    public ConcreteTriangleMeshInfo build() {
      boolean hasTexCoord = !data.texCoords.isEmpty();
      boolean hasNormal = !data.normals.isEmpty();
      boolean hasTangents = !data.tangents.isEmpty();
      int vertexCount = data.positions.size();

      if (hasTexCoord) {
        Preconditions.checkState(vertexCount == data.texCoords.size(),
          "#texCoords != #vertices");
      } else {
        for (int i = 0; i < vertexCount; i++) {
          addTexCoord(0.0, 0.0);
        }
      }
      if (hasNormal) {
        Preconditions.checkArgument(vertexCount == data.normals.size(),
          "#normals != #vertices");
      } else {
        data.computeNormals();
      }
      if (hasTangents) {
        Preconditions.checkArgument(vertexCount == data.tangents.size(),
          "#tangents != #vertices");
      } else {
        data.computeTangents();
      }
      data.computeAabb();
      data.computeAggregateSurfaceArea();

      return data;
    }
  }
}
