package hana04.yuri.surface.geometry.trimesh;

import com.google.common.base.Preconditions;
import hana04.gfxbase.gfxtype.Measure;
import hana04.gfxbase.gfxtype.Transform;
import hana04.gfxbase.gfxtype.VecMathDUtil;
import hana04.shakuyaku.surface.geometry.trimesh.TriangleMeshInfo;
import hana04.yuri.surface.PatchIntervalPointSampler;
import hana04.yuri.surface.PatchIntervalPointSamplingOutput;
import hana04.yuri.sampler.RandomNumberGenerator;
import hana04.yuri.util.DiscretePdf;

import javax.vecmath.Point3d;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3d;

public class XformedTriMeshIntervalPointSampler implements PatchIntervalPointSampler {
  private final TriangleMeshInfo triMesh;
  private final Transform toWorld;
  private final DiscretePdf discretePdf;

  public XformedTriMeshIntervalPointSampler(TriangleMeshInfo triMesh, Transform toWorld,
                                            int startIndex, int endIndex) {
    Preconditions.checkArgument(startIndex <= endIndex);
    this.triMesh = triMesh;
    this.toWorld = toWorld;

    Point3d A = new Point3d();
    Point3d B = new Point3d();
    Point3d C = new Point3d();
    DiscretePdf.Builder builder = new DiscretePdf.Builder();
    for (int i = startIndex; i < endIndex; i++) {
      Point3i tri = triMesh.getTriangle(i);
      toWorld.m.transform(triMesh.getPosition(tri.x), A);
      toWorld.m.transform(triMesh.getPosition(tri.y), B);
      toWorld.m.transform(triMesh.getPosition(tri.z), C);
      builder.add(triangleArea(A,B,C));
    }
    discretePdf = builder.build();
  }

  @Override
  public PatchIntervalPointSamplingOutput sample(RandomNumberGenerator rng) {
    int triangleIndex = discretePdf.sample(rng.next1D());
    Point3i tri = triMesh.getTriangle(triangleIndex);

    Point3d p0 = new Point3d();
    Point3d p1 = new Point3d();
    Point3d p2 = new Point3d();
    toWorld.m.transform(triMesh.getPosition(tri.x), p0);
    toWorld.m.transform(triMesh.getPosition(tri.y), p1);
    toWorld.m.transform(triMesh.getPosition(tri.z), p2);

    double mu1 = rng.next1D();
    double mu2 = rng.next1D();
    double sqrtMu1 = Math.sqrt(mu1);

    double cA = (1 - sqrtMu1);
    double cB = sqrtMu1 * (1 - mu2);
    double cC = sqrtMu1 * mu2;

    Point3d position = new Point3d();
    position.set(0, 0, 0);
    position.scaleAdd(cA, p0, position);
    position.scaleAdd(cB, p1, position);
    position.scaleAdd(cC, p2, position);

    Vector3d normal = triMesh.getBaryNormal(triangleIndex, new Vector3d(cA, cB, cC));
    toWorld.mit.transform(normal);
    normal.normalize();

    PatchIntervalPointSamplingOutput result = new PatchIntervalPointSamplingOutput();
    result.q.set(position);
    result.nq.set(normal);
    result.measure = Measure.Area;
    return result;
  }

  @Override
  public double pdf(PatchIntervalPointSamplingOutput record) {
    return discretePdf.getNormalization();
  }

  private static double triangleArea(Point3d A, Point3d B, Point3d C) {
    Vector3d AB = VecMathDUtil.sub(B, A);
    Vector3d AC = VecMathDUtil.sub(C, A);
    Vector3d cp = VecMathDUtil.cross(AB, AC);
    return cp.length() / 2.0;
  }
}
