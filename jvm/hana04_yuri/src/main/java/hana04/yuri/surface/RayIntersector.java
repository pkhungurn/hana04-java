package hana04.yuri.surface;

import hana04.base.changeprop.VersionedValue;
import hana04.gfxbase.gfxtype.Ray;

import java.util.Optional;

/**
 * Intersects rays with patches of a shape.
 */
public interface RayIntersector {
  Optional<PatchIntersection> rayIntersect(int index, Ray ray);

  Intersection createIntersectionRecord(PatchIntersection patchIntersection);

  class Proxy implements RayIntersector {
    private RayIntersector inner;

    public Proxy(RayIntersector inner) {
      this.inner = inner;
    }

    @Override
    public Optional<PatchIntersection> rayIntersect(int index, Ray ray) {
      return inner.rayIntersect(index, ray);
    }

    @Override
    public Intersection createIntersectionRecord(PatchIntersection patchIntersection) {
      return inner.createIntersectionRecord(patchIntersection);
    }
  }

  interface Vv extends VersionedValue<RayIntersector> {
    // NO-OP
  }

  class VvProxy implements RayIntersector {
    private final VersionedValue<RayIntersector> inner;

    public VvProxy(VersionedValue<RayIntersector> inner) {
      this.inner = inner;
    }

    @Override
    public Optional<PatchIntersection> rayIntersect(int index, Ray ray) {
      return inner.value().rayIntersect(index, ray);
    }

    @Override
    public Intersection createIntersectionRecord(PatchIntersection patchIntersection) {
      return inner.value().createIntersectionRecord(patchIntersection);
    }
  }
}
