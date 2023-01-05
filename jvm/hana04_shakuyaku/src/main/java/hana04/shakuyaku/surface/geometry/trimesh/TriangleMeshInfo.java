package hana04.shakuyaku.surface.geometry.trimesh;

import hana04.base.changeprop.VersionedValue;
import hana04.gfxbase.gfxtype.Aabb3d;
import hana04.shakuyaku.surface.SurfacePatchInfo;

import javax.vecmath.Point3d;
import javax.vecmath.Point3i;
import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3i;
import javax.vecmath.Tuple4d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

public interface TriangleMeshInfo extends SurfacePatchInfo {
  int getTriangleCount();

  int getVertexCount();

  void getPosition(int index, Tuple3d output);

  void getTexCoord(int index, Tuple2d output);

  void getNormal(int index, Tuple3d output);

  void getTangent(int index, Tuple4d output);

  void getTriangle(int index, Tuple3i output);

  void getAabb(Aabb3d output);

  void getTriangleAabb(int index, Aabb3d output);

  void getTriangleCentroid(int index, Tuple3d output);

  void getBaryPosition(int triangleIndex, Tuple3d bary, Tuple3d output);

  void getBaryTexCoord(int triangleIndex, Tuple3d bary, Tuple2d output);

  void getTriangleNormal(int triangleIndex, Tuple3d output);

  void getBaryNormal(int triangleIndex, Tuple3d bary, Vector3d output);

  void getBaryTangent(int triangleIndex, Tuple3d bary, Vector3d normal, Vector4d output);

  void getTriangleVertexIndices(int triangleIndex, Tuple3i output);

  double getTriangleArea(int triangleIndex);

  default int getPatchCount() {
    return getTriangleCount();
  }

  default void getPatchAabb(int index, Aabb3d aabb) {
    getTriangleAabb(index, aabb);
  }

  default void getPatchCentroid(int index, Tuple3d centroid) {
    getTriangleCentroid(index, centroid);
  }

  default Point3d getPosition(int index) {
    Point3d output = new Point3d();
    getPosition(index, output);
    return output;
  }

  default Vector2d getTexCoord(int index) {
    Vector2d output = new Vector2d();
    getTexCoord(index, output);
    return output;
  }

  default Vector3d getNormal(int index) {
    Vector3d output = new Vector3d();
    getNormal(index, output);
    return output;
  }

  default Vector4d getTangent(int index) {
    Vector4d output = new Vector4d();
    getTangent(index, output);
    return output;
  }

  default Aabb3d getTriangleAabb(int index) {
    Aabb3d aabb = new Aabb3d();
    getTriangleAabb(index, aabb);
    return aabb;
  }

  default Point3d getTriangleCentroid(int index) {
    Point3d result = new Point3d();
    getTriangleCentroid(index, result);
    return result;
  }

  default Point3i getTriangle(int index) {
    Point3i output = new Point3i();
    getTriangle(index, output);
    return output;
  }

  default Point3d getBaryPosition(int triangleIndex, Tuple3d bary) {
    Point3d result = new Point3d();
    getBaryPosition(triangleIndex, bary, result);
    return result;
  }

  default Vector2d getBaryTexCoord(int triangleIndex, Tuple3d bary) {
    Vector2d result = new Vector2d();
    getBaryTexCoord(triangleIndex, bary, result);
    return result;
  }

  default Vector3d getBaryNormal(int triangleIndex, Tuple3d bary) {
    Vector3d result = new Vector3d();
    getBaryNormal(triangleIndex, bary, result);
    return result;
  }

  default Vector4d getBaryTangent(int triangleIndex, Tuple3d bary, Vector3d normal) {
    Vector4d result = new Vector4d();
    getBaryTangent(triangleIndex, bary, normal, result);
    return result;
  }

  default Vector3d getTriangleNormal(int triangleIndex) {
    Vector3d result = new Vector3d();
    getTriangleNormal(triangleIndex, result);
    return result;
  }

  default Point3i getTriangleVertexIndices(int triangleIndex) {
    Point3i result = new Point3i();
    getTriangleVertexIndices(triangleIndex, result);
    return result;
  }

  class Proxy implements TriangleMeshInfo {
    private TriangleMeshInfo inner;

    public Proxy(TriangleMeshInfo inner) {
      this.inner = inner;
    }

    @Override
    public int getTriangleCount() {
      return inner.getTriangleCount();
    }

    @Override
    public int getVertexCount() {
      return inner.getVertexCount();
    }

    @Override
    public void getPosition(int index, Tuple3d output) {
      inner.getPosition(index, output);
    }

    @Override
    public void getTexCoord(int index, Tuple2d output) {
      inner.getTexCoord(index, output);
    }

    @Override
    public void getNormal(int index, Tuple3d output) {
      inner.getNormal(index, output);
    }

    @Override
    public void getTangent(int index, Tuple4d output) {
      inner.getTangent(index, output);
    }

    @Override
    public void getTriangle(int index, Tuple3i output) {
      inner.getTriangle(index, output);
    }

    @Override
    public void getAabb(Aabb3d output) {
      inner.getAabb(output);
    }

    @Override
    public void getTriangleAabb(int index, Aabb3d output) {
      inner.getTriangleAabb(index, output);
    }

    @Override
    public void getTriangleCentroid(int index, Tuple3d output) {
      inner.getTriangleCentroid(index, output);
    }

    @Override
    public void getBaryPosition(int triangleIndex, Tuple3d bary, Tuple3d output) {
      inner.getBaryPosition(triangleIndex, bary, output);
    }

    @Override
    public void getBaryTexCoord(int triangleIndex, Tuple3d bary, Tuple2d output) {
      inner.getBaryTexCoord(triangleIndex, bary, output);
    }

    @Override
    public void getTriangleNormal(int triangleIndex, Tuple3d output) {
      inner.getTriangleNormal(triangleIndex, output);
    }

    @Override
    public void getBaryNormal(int triangleIndex, Tuple3d bary, Vector3d output) {
      inner.getBaryNormal(triangleIndex, bary, output);
    }

    @Override
    public void getBaryTangent(int triangleIndex, Tuple3d bary, Vector3d normal, Vector4d output) {
      inner.getBaryTangent(triangleIndex, bary, normal, output);
    }

    @Override
    public void getTriangleVertexIndices(int triangleIndex, Tuple3i output) {
      inner.getTriangleVertexIndices(triangleIndex, output);
    }

    @Override
    public double getTriangleArea(int triangleIndex) {
      return inner.getTriangleArea(triangleIndex);
    }
  }

  /**
   * A class token, meant to be used as an extension identifier, for the versioned value of
   * {@link TriangleMeshInfo}.
   */
  interface Vv extends VersionedValue<TriangleMeshInfo> {
    // NO-OP
  }

  /**
   * A convenience class for convertering a versioned {@link TriangleMeshInfo} value into plain
   * {@link TriangleMeshInfo}.
   */
  class VvProxy implements TriangleMeshInfo {
    private final VersionedValue<TriangleMeshInfo> inner;

    public VvProxy(VersionedValue<TriangleMeshInfo> inner) {
      this.inner = inner;
    }

    @Override
    public int getTriangleCount() {
      return inner.value().getTriangleCount();
    }

    @Override
    public int getVertexCount() {
      return inner.value().getVertexCount();
    }

    @Override
    public void getPosition(int index, Tuple3d output) {
      inner.value().getPosition(index, output);
    }

    @Override
    public void getTexCoord(int index, Tuple2d output) {
      inner.value().getTexCoord(index, output);
    }

    @Override
    public void getNormal(int index, Tuple3d output) {
      inner.value().getNormal(index, output);
    }

    @Override
    public void getTangent(int index, Tuple4d output) {
      inner.value().getTangent(index, output);
    }

    @Override
    public void getTriangle(int index, Tuple3i output) {
      inner.value().getTriangle(index, output);
    }

    @Override
    public void getAabb(Aabb3d output) {
      inner.value().getAabb(output);
    }

    @Override
    public void getTriangleAabb(int index, Aabb3d output) {
      inner.value().getTriangleAabb(index, output);
    }

    @Override
    public void getTriangleCentroid(int index, Tuple3d output) {
      inner.value().getTriangleCentroid(index, output);
    }

    @Override
    public void getBaryPosition(int triangleIndex, Tuple3d bary, Tuple3d output) {
      inner.value().getBaryPosition(triangleIndex, bary, output);
    }

    @Override
    public void getBaryTexCoord(int triangleIndex, Tuple3d bary, Tuple2d output) {
      inner.value().getBaryTexCoord(triangleIndex, bary, output);
    }

    @Override
    public void getTriangleNormal(int triangleIndex, Tuple3d output) {
      inner.value().getTriangleNormal(triangleIndex, output);
    }

    @Override
    public void getBaryNormal(int triangleIndex, Tuple3d bary, Vector3d output) {
      inner.value().getBaryNormal(triangleIndex, bary, output);
    }

    @Override
    public void getBaryTangent(int triangleIndex, Tuple3d bary, Vector3d normal, Vector4d output) {
      inner.value().getBaryTangent(triangleIndex, bary, normal, output);
    }

    @Override
    public void getTriangleVertexIndices(int triangleIndex, Tuple3i output) {
      inner.value().getTriangleVertexIndices(triangleIndex, output);
    }

    @Override
    public double getTriangleArea(int triangleIndex) {
      return inner.value().getTriangleArea(triangleIndex);
    }
  }

  /**
   * A class token, meant to be used as an extension, for {@link TriangleMeshInfo} that is in
   * object space.
   */
  interface ObjectSpace extends TriangleMeshInfo {
    /**
     * A class token, meant to be used as an extension, for the versioned {@link TriangleMeshInfo} value
     * in object space.
     */
    interface Vv extends VersionedValue<TriangleMeshInfo> {
      // NO-OP
    }
  }
}
