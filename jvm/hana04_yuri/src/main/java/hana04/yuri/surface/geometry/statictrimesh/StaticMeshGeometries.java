package hana04.yuri.surface.geometry.statictrimesh;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.shakuyaku.surface.geometry.statictrimesh.StaticTriMeshGeometry;
import hana04.shakuyaku.surface.geometry.trimesh.TriangleMeshInfo;
import hana04.yuri.surface.geometry.XformedPatchIntervalPointSamplerFactory;
import hana04.yuri.surface.geometry.XformedRayIntersectorFactory;
import hana04.yuri.surface.geometry.trimesh.XformedTriMeshIntervalPointSampler;
import hana04.yuri.surface.geometry.trimesh.XformedTriMeshRayIntersector;

public class StaticMeshGeometries {
  public static class RayIntersectorFactoryVv
    extends DerivedVersionedValue<XformedRayIntersectorFactory>
    implements XformedRayIntersectorFactory.Vv {
    @HanaDeclareExtension(
      extensibleClass = StaticTriMeshGeometry.class,
      extensionClass = XformedRayIntersectorFactory.Vv.class)
    RayIntersectorFactoryVv(StaticTriMeshGeometry geometry) {
      super(
        ImmutableList.of(geometry.getExtension(TriangleMeshInfo.Vv.class)),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () ->
          toWorld -> new XformedTriMeshRayIntersector(
            geometry.getExtension(TriangleMeshInfo.Vv.class).value(),
            toWorld));
    }
  }

  public static class PointSamplerFactoryVv
    extends DerivedVersionedValue<XformedPatchIntervalPointSamplerFactory>
    implements XformedPatchIntervalPointSamplerFactory.Vv {
    @HanaDeclareExtension(
      extensibleClass = StaticTriMeshGeometry.class,
      extensionClass = XformedPatchIntervalPointSamplerFactory.Vv.class)
    PointSamplerFactoryVv(StaticTriMeshGeometry geometry) {
      super(
        ImmutableList.of(geometry.getExtension(TriangleMeshInfo.Vv.class)),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> ((startIndex, endIndex, toWorld) -> new XformedTriMeshIntervalPointSampler(
          geometry.getExtension(TriangleMeshInfo.Vv.class).value(), toWorld, startIndex, endIndex)));
    }
  }
}
