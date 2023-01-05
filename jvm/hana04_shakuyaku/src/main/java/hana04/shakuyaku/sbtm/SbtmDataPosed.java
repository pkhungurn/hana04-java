package hana04.shakuyaku.sbtm;

import hana04.gfxbase.gfxtype.Aabb3d;
import hana04.shakuyaku.surface.geometry.trimesh.TriangleMeshInfo;
import hana04.shakuyaku.surface.geometry.trimesh.TriangleMeshUtil;

import javax.vecmath.Point3d;
import javax.vecmath.Point3i;
import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3i;
import javax.vecmath.Tuple4d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.util.ArrayList;

public class SbtmDataPosed implements TriangleMeshInfo {
  final ArrayList<Point3d> positions = new ArrayList<>();
  final ArrayList<Vector3d> normals = new ArrayList<>();
  final ArrayList<Vector4d> tangents = new ArrayList<>();
  final ArrayList<Vector2d> texCoords;
  final ArrayList<Point3i> triangles;
  final Aabb3d aabb = new Aabb3d();

  SbtmDataPosed(ArrayList<Vector2d> texCoords, ArrayList<Point3i> triangles) {
    this.texCoords = texCoords;
    this.triangles = triangles;
    int vertexCount = texCoords.size();
    for (int i = 0; i < vertexCount; i++) {
      positions.add(new Point3d(0,0,0));
      //normals.add(new Vector3d(0,0,0));
      //tangents.add(new Vector4d(0,0,0,0));
    }
  }

  @Override
  public int getTriangleCount() {
    return triangles.size();
  }

  @Override
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

  void computeAabb() {
    TriangleMeshUtil.computeAabb(positions, aabb);
  }

  void computeNormals() {
    TriangleMeshUtil.computeNormals(positions, triangles, normals);
  }

  void computeTangents() {
    TriangleMeshUtil.computeTangents(positions, texCoords, normals, triangles, tangents);
  }
}
