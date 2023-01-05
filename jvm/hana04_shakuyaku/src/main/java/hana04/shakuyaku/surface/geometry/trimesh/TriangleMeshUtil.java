package hana04.shakuyaku.surface.geometry.trimesh;

import hana04.base.filesystem.FilePath;
import hana04.gfxbase.gfxtype.Aabb3d;
import hana04.gfxbase.gfxtype.VecMathDUtil;
import hana04.shakuyaku.surface.mmd.pmxbase.PmxBaseGeometryLoader;
import org.apache.commons.io.FilenameUtils;

import javax.vecmath.Point3d;
import javax.vecmath.Point3i;
import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3i;
import javax.vecmath.Tuple4d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.nio.file.FileSystem;
import java.util.List;

public class TriangleMeshUtil {
  public static void getTriangleAabb(int index, Aabb3d aabb,
                                     List<? extends Tuple3d> positions,
                                     List<? extends Tuple3i> triangles) {
    aabb.reset();
    Tuple3i tri = triangles.get(index);
    aabb.expandBy(positions.get(tri.x));
    aabb.expandBy(positions.get(tri.y));
    aabb.expandBy(positions.get(tri.z));
  }

  public static void getTriangleCentroid(int index, Tuple3d centroid,
                                         List<? extends Tuple3d> positions,
                                         List<? extends Tuple3i> triangles) {
    centroid.set(0, 0, 0);
    Tuple3i tri = triangles.get(index);
    centroid.add(positions.get(tri.x));
    centroid.add(positions.get(tri.y));
    centroid.add(positions.get(tri.z));
    centroid.scale(1.0 / 3);
  }

  public static double getTriangleArea(int index,
                                       List<? extends Tuple3d> positions,
                                       List<? extends Tuple3i> triangles) {
    Tuple3i tri = triangles.get(index);
    Tuple3d p0 = positions.get(tri.x);
    Tuple3d p1 = positions.get(tri.y);
    Tuple3d p2 = positions.get(tri.z);
    Vector3d v0 = new Vector3d();
    Vector3d v1 = new Vector3d();
    v0.sub(p1, p0);
    v1.sub(p2, p0);
    Vector3d cross = new Vector3d();
    cross.cross(v0, v1);
    return cross.length() * 0.5;
  }

  public static double computeAggregateSurfaceArea(
    List<? extends Tuple3d> positions,
    List<? extends Tuple3i> triangles) {
    double surfaceArea = 0;
    int triangleCount = triangles.size();
    for (int i = 0; i < triangleCount; i++) {
      surfaceArea += getTriangleArea(i, positions, triangles);
    }
    return surfaceArea;
  }

  public static void computeNormals(List<? extends Tuple3d> positions,
                                    List<? extends Tuple3i> triangles,
                                    List<Vector3d> normals) {
    int vertexCount = positions.size();
    normals.clear();
    for (int i = 0; i < vertexCount; i++) {
      normals.add(new Vector3d());
    }

    Vector3d p01 = new Vector3d();
    Vector3d p02 = new Vector3d();
    Vector3d n = new Vector3d();
    Point3d p0 = new Point3d();
    Point3d p1 = new Point3d();
    Point3d p2 = new Point3d();
    Vector3d nn = new Vector3d();
    int[] v = new int[3];

    int triangleCount = triangles.size();
    for (int i = 0; i < triangleCount; i++) {
      Tuple3i tri = triangles.get(i);
      v[0] = tri.x;
      v[1] = tri.y;
      v[2] = tri.z;

      p0.set(positions.get(v[0]));
      p1.set(positions.get(v[1]));
      p2.set(positions.get(v[2]));

      p01.sub(p1, p0);
      p02.sub(p2, p0);
      n.cross(p01, p02);
      n.normalize();

      for (int j = 0; j < 3; j++) {
        normals.get(v[j]).add(n);
      }
    }
    for (int i = 0; i < vertexCount; i++) {
      normals.get(i).normalize();
    }
  }

  public static void computeAabb(List<? extends Tuple3d> positions,
                                 Aabb3d aabb) {
    aabb.reset();
    for (Tuple3d p : positions) {
      aabb.expandBy(p);
    }
  }

  public static void computeTangents(List<? extends Tuple3d> positions,
                                     List<? extends Tuple2d> texCoords,
                                     List<? extends Tuple3d> normals,
                                     List<? extends Tuple3i> triangles,
                                     List<Vector4d> tangents) {
    int vertexCount = positions.size();
    int triangleCount = triangles.size();
    tangents.clear();

    Vector3d[] ts = new Vector3d[vertexCount];
    Vector3d[] bs = new Vector3d[vertexCount];
    for (int i = 0; i < vertexCount; i++) {
      ts[i] = new Vector3d(0, 0, 0);
      bs[i] = new Vector3d(0, 0, 0);
    }

    Point3i tri = new Point3i();
    Point3d v1 = new Point3d();
    Point3d v2 = new Point3d();
    Point3d v3 = new Point3d();
    Vector2d w1 = new Vector2d();
    Vector2d w2 = new Vector2d();
    Vector2d w3 = new Vector2d();
    Vector3d sdir = new Vector3d();
    Vector3d tdir = new Vector3d();
    Vector3d n = new Vector3d();
    Vector3d t = new Vector3d();
    Vector3d b = new Vector3d();
    Vector3d v12 = new Vector3d();
    Vector3d v13 = new Vector3d();

    for (int triIndex = 0; triIndex < triangleCount; triIndex++) {
      tri.x = triangles.get(triIndex).x;
      tri.y = triangles.get(triIndex).y;
      tri.z = triangles.get(triIndex).z;

      v1.set(positions.get(tri.x));
      v2.set(positions.get(tri.y));
      v3.set(positions.get(tri.z));

      w1.set(texCoords.get(tri.x));
      w2.set(texCoords.get(tri.y));
      w3.set(texCoords.get(tri.z));

      double x1 = v2.x - v1.x;
      double x2 = v3.x - v1.x;
      double y1 = v2.y - v1.y;
      double y2 = v3.y - v1.y;
      double z1 = v2.z - v1.z;
      double z2 = v3.z - v1.z;

      double s1 = w2.x - w1.x;
      double s2 = w3.x - w1.x;
      double t1 = w2.y - w1.y;
      double t2 = w3.y - w1.y;

      double det = s1 * t2 - s2 * t1;
      if (Math.abs(det) < 1e-9) {
        v12.sub(v2, v1);
        v13.sub(v3, v1);
        n.cross(v12, v13);
        VecMathDUtil.coordinateSystem(n, sdir, tdir);
      } else {
        double r = 1.0f / det;
        sdir.set((t2 * x1 - t1 * x2) * r,
          (t2 * y1 - t1 * y2) * r,
          (t2 * z1 - t1 * z2) * r);
        tdir.set((s1 * x2 - s2 * x1) * r,
          (s1 * y2 - s2 * y1) * r,
          (s1 * z2 - s2 * z1) * r);
      }

      ts[tri.x].add(sdir);
      ts[tri.y].add(sdir);
      ts[tri.z].add(sdir);

      bs[tri.x].add(tdir);
      bs[tri.y].add(tdir);
      bs[tri.z].add(tdir);
    }

    for (int i = 0; i < vertexCount; i++) {
      n.set(normals.get(i));
      t.set(ts[i]);

      tdir.scaleAdd(-n.dot(t), n, t);
      tdir.normalize();

      b.cross(n, tdir);
      double dot = b.dot(bs[i]);
      double w = (dot < 0) ? -1 : 1;

      tangents.add(new Vector4d(tdir.x, tdir.y, tdir.z, w));
    }
  }

  public static void getBaryPosition(int triangleIndex, Tuple3d bary, Tuple3d output,
                                     List<? extends Tuple3d> positions,
                                     List<? extends Tuple3i> triangles) {
    Tuple3i tri = triangles.get(triangleIndex);
    output.set(0, 0, 0);
    output.scaleAdd(bary.x, positions.get(tri.x), output);
    output.scaleAdd(bary.y, positions.get(tri.y), output);
    output.scaleAdd(bary.z, positions.get(tri.z), output);
  }

  public static void getBaryTexCoord(int triangleIndex, Tuple3d bary, Tuple2d output,
                                     List<? extends Tuple2d> texCoords,
                                     List<? extends Tuple3i> triangles) {
    Tuple3i tri = triangles.get(triangleIndex);
    output.set(0, 0);
    output.scaleAdd(bary.x, texCoords.get(tri.x), output);
    output.scaleAdd(bary.y, texCoords.get(tri.y), output);
    output.scaleAdd(bary.z, texCoords.get(tri.z), output);
  }

  public static void getTriangleNormal(int triangleIndex, Tuple3d output,
                                       List<? extends Tuple3d> positions,
                                       List<? extends Tuple3i> triangles) {
    Tuple3i tri = triangles.get(triangleIndex);
    Tuple3d p0 = positions.get(tri.x);
    Tuple3d p1 = positions.get(tri.y);
    Tuple3d p2 = positions.get(tri.z);
    Vector3d v1 = new Vector3d();
    v1.sub(p1, p0);
    Vector3d v2 = new Vector3d();
    v2.sub(p2, p0);
    Vector3d output_ = new Vector3d();
    output_.cross(v1, v2);
    output_.normalize();
    output.set(output_);
  }

  public static void getBaryNormal(int triangleIndex, Tuple3d bary, Vector3d output,
                                   List<? extends Tuple3d> normals,
                                   List<? extends Tuple3i> triangles) {
    Tuple3i tri = triangles.get(triangleIndex);
    output.set(0, 0, 0);
    output.scaleAdd(bary.x, normals.get(tri.x), output);
    output.scaleAdd(bary.y, normals.get(tri.y), output);
    output.scaleAdd(bary.z, normals.get(tri.z), output);
    output.normalize();
  }

  static Vector3d toVector3d(Tuple4d v) {
    return new Vector3d(v.x, v.y, v.z);
  }

  public static void getBaryTangent(
    int triangleIndex, Tuple3d bary, Vector3d normal, Vector4d output,
    List<? extends Tuple4d> tangents,
    List<? extends Tuple3i> triangles
  ) {
    Tuple3i tri = triangles.get(triangleIndex);
    Vector3d tangent = new Vector3d();
    tangent.set(0, 0, 0);
    tangent.scaleAdd(bary.x, toVector3d(tangents.get(tri.x)), tangent);
    tangent.scaleAdd(bary.y, toVector3d(tangents.get(tri.y)), tangent);
    tangent.scaleAdd(bary.z, toVector3d(tangents.get(tri.z)), tangent);
    tangent.normalize();
    Vector3d bitangent = VecMathDUtil.cross(normal, tangent);
    tangent.cross(bitangent, normal);
    tangent.normalize();
    double newW = bary.x * tangents.get(tri.x).w
      + bary.y * tangents.get(tri.y).w
      + bary.z * tangents.get(tri.z).w;
    output.set(tangent.x, tangent.y, tangent.z, newW > 0 ? 1 : -1);
  }

  public static TriangleMeshInfo load(FilePath filePath, FileSystem fileSystem) {
    String extension = FilenameUtils.getExtension(filePath.storedPath).toLowerCase();
    switch (extension) {
      case "obj":
        return ObjMeshGeometryLoader.load(filePath, fileSystem);
      case "pmx":
        return PmxBaseGeometryLoader.load(filePath, fileSystem);
      default:
        throw new RuntimeException("File of extension '" + extension + "' is not supports!");
    }
  }
}
