package hana04.yuri.surface.mmd.mmd;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.gfxbase.gfxtype.Transform;
import hana04.shakuyaku.surface.geometry.trimesh.TriangleMeshInfo;
import hana04.shakuyaku.surface.mmd.mmd.MmdSurface;
import hana04.yuri.surface.RayIntersector;
import hana04.yuri.surface.geometry.trimesh.XformedTriMeshRayIntersector;

public class MmdSurfaces {
  public static class RayIntersectorVv_
    extends DerivedVersionedValue<RayIntersector>
    implements RayIntersector.Vv {

    @HanaDeclareExtension(
      extensibleClass = MmdSurface.class,
      extensionClass = RayIntersector.Vv.class)
    RayIntersectorVv_(MmdSurface mmdSurface) {
      super(
        ImmutableList.of(),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> {
          TriangleMeshInfo triMesh = mmdSurface.getExtension(TriangleMeshInfo.ObjectSpace.Vv.class).value();
          Transform xform = mmdSurface.toWorld().value();
          return new XformedTriMeshRayIntersector(triMesh, xform);
        });
    }
  }
}
