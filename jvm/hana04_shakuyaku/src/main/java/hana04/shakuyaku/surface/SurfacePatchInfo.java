package hana04.shakuyaku.surface;

import hana04.base.changeprop.VersionedValue;
import hana04.gfxbase.gfxtype.Aabb3d;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;

/**
 * Basic information about the geometry of a surface. A surface consists of "patches," which are parts of the surface
 * that can be individually intersect with a ray. With this extension, it is sufficient to build an acceleration
 * structure of the surface. The SurfacePatchInfo is supposed to represent surface located in world space.
 */
public interface SurfacePatchInfo {
  /**
   * The axis-aligned bounding box of the whole surface.
   */
  void getAabb(Aabb3d output);

  /**
   * The number of patches this surface has.
   */
  int getPatchCount();

  /**
   * The AABB of the {@code i}th patch.
   */
  void getPatchAabb(int i, Aabb3d output);

  /**
   * The centroid of the {@code i}th patch.
   */
  void getPatchCentroid(int i, Tuple3d output);

  default Aabb3d getAabb() {
    Aabb3d aabb = new Aabb3d();
    getAabb(aabb);
    return aabb;
  }

  default Aabb3d getPatchAabb(int index) {
    Aabb3d aabb = new Aabb3d();
    getPatchAabb(index, aabb);
    return aabb;
  }

  default Point3d getPatchCentroid(int index) {
    Point3d output = new Point3d();
    getPatchCentroid(index, output);
    return output;
  }

  interface Vv extends VersionedValue<SurfacePatchInfo> {
    // NO-OP
  }

  class Proxy implements SurfacePatchInfo {
    private SurfacePatchInfo inner;

    public Proxy(SurfacePatchInfo inner) {
      this.inner = inner;
    }

    @Override
    public void getAabb(Aabb3d output) {
      inner.getAabb(output);
    }

    @Override
    public int getPatchCount() {
      return inner.getPatchCount();
    }

    @Override
    public void getPatchAabb(int i, Aabb3d output) {
      inner.getPatchAabb(i, output);
    }

    @Override
    public void getPatchCentroid(int i, Tuple3d output) {
      inner.getPatchCentroid(i, output);
    }
  }

  class VvProxy implements SurfacePatchInfo {
    private final VersionedValue<SurfacePatchInfo> inner;

    public VvProxy(VersionedValue<SurfacePatchInfo> inner) {
      this.inner = inner;
    }

    @Override
    public void getAabb(Aabb3d output) {
      inner.value().getAabb(output);
    }

    @Override
    public int getPatchCount() {
      return inner.value().getPatchCount();
    }

    @Override
    public void getPatchAabb(int i, Aabb3d output) {
      inner.value().getPatchAabb(i, output);
    }

    @Override
    public void getPatchCentroid(int i, Tuple3d output) {
      inner.value().getPatchCentroid(i, output);
    }
  }
}
