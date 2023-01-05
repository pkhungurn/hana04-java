package hana04.yuri.surface.intervaled;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.changeprop.util.VvTransform;
import hana04.shakuyaku.surface.geometry.SurfaceGeometry;
import hana04.shakuyaku.surface.intervaled.IntervaledSurface;
import hana04.yuri.surface.RayIntersector;
import hana04.yuri.surface.geometry.XformedRayIntersectorFactory;

public class IntervaledSurfaces {
  public static class XformedRayIntersectorFactoryVv
    extends VvTransform<SurfaceGeometry, XformedRayIntersectorFactory>
    implements XformedRayIntersectorFactory.Vv {
    @HanaDeclareExtension(
      extensibleClass = IntervaledSurface.class,
      extensionClass = XformedRayIntersectorFactory.Vv.class)
    XformedRayIntersectorFactoryVv(IntervaledSurface surface) {
      super(
        surface.getExtension(hana04.shakuyaku.surface.intervaled.IntervaledSurfaces.GeometryVv.class),
        geometry -> geometry.getExtension(XformedRayIntersectorFactory.Vv.class));
    }
  }

  public static class RayIntersectorVv
    extends DerivedVersionedValue<RayIntersector>
    implements RayIntersector.Vv {
    @HanaDeclareExtension(
      extensibleClass = IntervaledSurface.class,
      extensionClass = RayIntersector.Vv.class)
    RayIntersectorVv(IntervaledSurface surface) {
      super(
        ImmutableList.of(
          surface.getExtension(XformedRayIntersectorFactory.Vv.class),
          surface.toWorld()),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> surface.getExtension(XformedRayIntersectorFactory.Vv.class).value().create(surface.toWorld().value()));
    }
  }
}
