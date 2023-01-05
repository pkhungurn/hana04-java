package hana04.yuri.surface.intervaled;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.Constant;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.changeprop.util.VvWrappedToVvAdaptor;
import hana04.shakuyaku.surface.geometry.SurfaceGeometry;
import hana04.shakuyaku.surface.intervaled.IntervaledSurfacePatchInterval;
import hana04.yuri.surface.PatchIntervalPointSampler;
import hana04.yuri.surface.geometry.XformedPatchIntervalPointSamplerFactory;

public class IntervaledSurfacePatchIntervals {
  public static class XformedPatchIntervalPointSamplerFactoryVv
      extends VvWrappedToVvAdaptor<SurfaceGeometry, XformedPatchIntervalPointSamplerFactory>
      implements XformedPatchIntervalPointSamplerFactory.Vv {
    @HanaDeclareExtension(
        extensibleClass = IntervaledSurfacePatchInterval.class,
        extensionClass = XformedPatchIntervalPointSamplerFactory.Vv.class)
    XformedPatchIntervalPointSamplerFactoryVv(IntervaledSurfacePatchInterval patchInterval, HanaUnwrapper unwrapper) {
      super(
          new Constant<>(patchInterval.surface().geometry()),
          geometry -> geometry.getExtension(XformedPatchIntervalPointSamplerFactory.Vv.class),
          unwrapper);
    }
  }

  public static class PointSamplerVv
      extends DerivedVersionedValue<PatchIntervalPointSampler>
      implements PatchIntervalPointSampler.Vv {
    @HanaDeclareExtension(
        extensibleClass = IntervaledSurfacePatchInterval.class,
        extensionClass = PatchIntervalPointSampler.Vv.class)
    PointSamplerVv(IntervaledSurfacePatchInterval patchInterval) {
      super(
          ImmutableList.of(
              patchInterval.getExtension(XformedPatchIntervalPointSamplerFactory.Vv.class),
              patchInterval.surface().toWorld()),
          ChangePropUtil::largestBetweenIncSelfAndDeps,
          () -> {
            XformedPatchIntervalPointSamplerFactory factory =
                patchInterval.getExtension(XformedPatchIntervalPointSamplerFactory.Vv.class).value();
            return factory.create(patchInterval.startPatchIndex(), patchInterval.endPatchIndex(),
                patchInterval.surface().toWorld().value());
          });
    }
  }
}
