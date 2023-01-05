package hana04.shakuyaku.surface.geometry.trimesh;

import hana04.gfxbase.gfxtype.Aabb3d;
import hana04.gfxbase.gfxtype.Transform;
import hana04.shakuyaku.surface.SurfacePatchInfo;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Point3i;
import javax.vecmath.Tuple3d;

public class XformedTriMeshPatchInfo implements SurfacePatchInfo {
  private final TriangleMeshInfo triMesh;
  private final Transform xform;
  private final Aabb3d aabb = new Aabb3d();

  public XformedTriMeshPatchInfo(TriangleMeshInfo triMesh, Transform xform) {
    this.triMesh = triMesh;
    this.xform = xform;

    aabb.reset();
    Point3d objPoint = new Point3d();
    Point3d worldPoint = new Point3d();
    for (int i = 0; i < triMesh.getVertexCount(); i++) {
      triMesh.getPosition(i, objPoint);
      xform.m.transform(objPoint, worldPoint);
      aabb.expandBy(worldPoint);
    }
  }

  @Override
  public void getAabb(Aabb3d output) {
    output.set(aabb);
  }

  @Override
  public int getPatchCount() {
    return triMesh.getTriangleCount();
  }

  @Override
  public void getPatchAabb(int i, Aabb3d output) {
    Point3i tri = triMesh.getTriangle(i);
    Point3d A = triMesh.getPosition(tri.x);
    Point3d B = triMesh.getPosition(tri.y);
    Point3d C = triMesh.getPosition(tri.z);
    Matrix4d m = xform.m;
    m.transform(A);
    m.transform(B);
    m.transform(C);
    output.reset();
    output.expandBy(A);
    output.expandBy(B);
    output.expandBy(C);
  }

  @Override
  public void getPatchCentroid(int i, Tuple3d output) {
    Point3i tri = triMesh.getTriangle(i);
    Point3d A = triMesh.getPosition(tri.x);
    Point3d B = triMesh.getPosition(tri.y);
    Point3d C = triMesh.getPosition(tri.z);
    Matrix4d m = xform.m;
    m.transform(A);
    m.transform(B);
    m.transform(C);
    output.set(0,0,0);
    output.add(A);
    output.add(B);
    output.add(C);
    output.scale(1.0 / 3);
  }
}
