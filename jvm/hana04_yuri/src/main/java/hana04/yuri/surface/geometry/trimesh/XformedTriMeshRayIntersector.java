package hana04.yuri.surface.geometry.trimesh;

import hana04.base.util.TypeUtil;
import hana04.gfxbase.gfxtype.Frame;
import hana04.gfxbase.gfxtype.Ray;
import hana04.gfxbase.gfxtype.Transform;
import hana04.gfxbase.gfxtype.VecMathDUtil;
import hana04.shakuyaku.surface.geometry.trimesh.TriangleMeshInfo;
import hana04.yuri.surface.Intersection;
import hana04.yuri.surface.PatchIntersection;
import hana04.yuri.surface.RayIntersector;

import javax.vecmath.Point3d;
import javax.vecmath.Point3i;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.util.Optional;
import java.util.OptionalDouble;

public class XformedTriMeshRayIntersector implements RayIntersector {
  private final Transform toWorld;
  private final TriangleMeshInfo triMesh;

  public XformedTriMeshRayIntersector(TriangleMeshInfo triMesh, Transform toWorld) {
    this.triMesh = triMesh;
    this.toWorld = toWorld;
  }

  @Override
  public Optional<PatchIntersection> rayIntersect(int index, Ray worldRay) {
    Ray objectRay = new Ray(worldRay);
    objectRay.transform(toWorld.mi);

    Point3i tri = triMesh.getTriangle(index);
    Point3d p0 = triMesh.getPosition(tri.x);
    Point3d p1 = triMesh.getPosition(tri.y);
    Point3d p2 = triMesh.getPosition(tri.z);

    Vector3d bary = new Vector3d();
    OptionalDouble t = Triangle.rayIntersect(p0, p1, p2, objectRay, bary);
    if (!t.isPresent()) {
      return Optional.empty();
    }
    worldRay.maxt = t.getAsDouble();
    TriangleIntersection result = new TriangleIntersection();
    result.t = t.getAsDouble();
    result.bary.set(bary);
    result.patchIndex = index;
    return Optional.of(result);
  }

  @Override
  public Intersection createIntersectionRecord(PatchIntersection patchIntersection) {
    TriangleIntersection triIts = TypeUtil.cast(patchIntersection, TriangleIntersection.class);

    Point3d p = triMesh.getBaryPosition(triIts.patchIndex, triIts.bary);
    toWorld.m.transform(p);

    Vector2d uv = triMesh.getBaryTexCoord(triIts.patchIndex, triIts.bary);

    Vector3d geoNormalLocal = triMesh.getTriangleNormal(triIts.patchIndex);
    Vector3d geoNormal = new Vector3d();
    toWorld.mit.transform(geoNormalLocal, geoNormal);
    geoNormal.normalize();

    Vector3d shNormalLocal = triMesh.getBaryNormal(triIts.patchIndex, triIts.bary);
    Vector3d shNormal = new Vector3d();
    toWorld.mit.transform(shNormalLocal, shNormal);
    shNormal.normalize();

    Vector4d geoBaryTangent = triMesh.getBaryTangent(triIts.patchIndex, triIts.bary, geoNormalLocal);
    Vector3d geoTangent = VecMathDUtil.toVector3d(geoBaryTangent);
    toWorld.m.transform(geoTangent);
    geoTangent.normalize();

    Vector4d shBaryTangent = triMesh.getBaryTangent(triIts.patchIndex, triIts.bary, shNormalLocal);
    Vector3d shTangent = VecMathDUtil.toVector3d(shBaryTangent);
    toWorld.m.transform(shTangent);
    shTangent.normalize();

    double directionFlippingFactor = toWorld.isPreservingOrientation() ? 1 : -1;

    Frame geoFrame = new Frame(geoTangent,
      VecMathDUtil.scale(directionFlippingFactor, VecMathDUtil.cross(geoNormal, geoTangent)), geoNormal);

    Frame shFrame = new Frame(shTangent,
      VecMathDUtil.scale(directionFlippingFactor * shBaryTangent.w, VecMathDUtil.cross(shNormal, shTangent)),
      shNormal);

    Intersection intersection = new Intersection();
    intersection.p.set(p);
    intersection.uv.set(uv);
    intersection.geoFrame.set(geoFrame);
    intersection.shFrame.set(shFrame);
    intersection.patchIntersection = patchIntersection;
    return intersection;
  }
}
