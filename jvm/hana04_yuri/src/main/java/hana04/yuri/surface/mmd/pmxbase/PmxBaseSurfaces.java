package hana04.yuri.surface.mmd.pmxbase;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.shakuyaku.surface.mmd.pmxbase.PmxBaseSurface;
import hana04.yuri.surface.RayIntersector;
import hana04.yuri.surface.geometry.trimesh.XformedTriMeshRayIntersector;

public class PmxBaseSurfaces {
  public static RayIntersector createRayIntersection(PmxBaseSurface pmxBaseSurface) {
    hana04.shakuyaku.surface.mmd.pmxbase.PmxBaseSurfaces.TriangleMeshInfoProvider triangleMeshInfoProvider =
        pmxBaseSurface.getExtension(hana04.shakuyaku.surface.mmd.pmxbase.PmxBaseSurfaces.TriangleMeshInfoProvider.class);
    return new XformedTriMeshRayIntersector(
        triangleMeshInfoProvider.getTriangleMeshInfo(), pmxBaseSurface.toWorld().value());
  }

  public static class RayIntersector_ extends RayIntersector.Proxy {
    @HanaDeclareExtension(
        extensibleClass = PmxBaseSurface.class,
        extensionClass = RayIntersector.class)
    public RayIntersector_(PmxBaseSurface pmxBaseSurface) {
      super(createRayIntersection(pmxBaseSurface));
    }
  }
}
