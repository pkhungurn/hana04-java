package hana04.shakuyaku.surface.intervaled;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.Constant;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.changeprop.util.VvTransform;
import hana04.gfxbase.gfxtype.Matrix4dUtil;
import hana04.gfxbase.gfxtype.Transform;
import hana04.shakuyaku.surface.PatchInterval;
import hana04.shakuyaku.surface.SurfacePatchInfo;
import hana04.shakuyaku.surface.SurfacePatchIntervalInfo;
import hana04.shakuyaku.surface.SurfaceShadingInfo;
import hana04.shakuyaku.surface.geometry.SurfaceGeometry;
import hana04.shakuyaku.surface.geometry.XformedPatchInfoFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

public class IntervaledSurfaces {
  public static List<Integer> computeIntervalBoundaries(List<IntervaledSurfacePatchIntervalSpec> intervals,
      int patchCount) {
    Preconditions.checkArgument(!intervals.isEmpty());
    Preconditions.checkArgument(intervals.get(0).startPatchIndex() == 0);
    ArrayList<Integer> intervalBoundaries = new ArrayList<>();
    for (int i = 0; i < intervals.size(); i++) {
      int startIndex = intervals.get(i).startPatchIndex();
      int endIndex;
      if (i == intervals.size() - 1) {
        endIndex = patchCount;
      } else {
        endIndex = intervals.get(i + 1).startPatchIndex();
      }
      Preconditions.checkArgument(startIndex < endIndex);
      intervalBoundaries.add(endIndex - 1);
    }
    return intervalBoundaries;
  }

  public static List<? extends PatchInterval> createSurfacePatchIntervalInfoList(
      IntervaledSurface surface,
      List<IntervaledSurfacePatchIntervalSpec> intervalSpecs,
      List<Integer> intervalBoundaries,
      Provider<IntervaledSurfacePatchIntervals.IntervaledSurfacePatchIntervalBuilder> patchIntervalBuilder) {

    ArrayList<IntervaledSurfacePatchInterval> patchIntervals = new ArrayList<>();
    for (int i = 0; i < intervalSpecs.size(); i++) {
      IntervaledSurfacePatchIntervalSpec spec = intervalSpecs.get(i);
      int startIndex = spec.startPatchIndex();
      final int index = i;
      int endIndex = intervalBoundaries.get(index) + 1;
      IntervaledSurfacePatchInterval patchInterval = patchIntervalBuilder.get()
        .startPatchIndex(startIndex)
        .endPatchIndex(endIndex)
        .bsdf(spec.bsdf())
        .emitter(spec.emitter())
        .surface(surface)
        .build();
      patchIntervals.add(patchInterval);
    }

    return patchIntervals;
  }

  public static class GeometryVv extends Constant<SurfaceGeometry> {
    @HanaDeclareExtension(
      extensibleClass = IntervaledSurface.class,
      extensionClass = GeometryVv.class)
    GeometryVv(IntervaledSurface surface, HanaUnwrapper unwrapper) {
      super(surface.geometry().unwrap(unwrapper));
    }
  }

  public static class ObjectSpacePatchInfoVv extends VvTransform<SurfaceGeometry, SurfacePatchInfo> {
    @HanaDeclareExtension(
      extensibleClass = IntervaledSurface.class,
      extensionClass = ObjectSpacePatchInfoVv.class)
    ObjectSpacePatchInfoVv(IntervaledSurface surface) {
      super(
        surface.getExtension(GeometryVv.class),
        geometry -> geometry.getExtension(SurfacePatchInfo.Vv.class));
    }
  }

  public static class IntervalBoundariesVv extends DerivedVersionedValue<List<Integer>> {
    @HanaDeclareExtension(
      extensibleClass = IntervaledSurface.class,
      extensionClass = IntervalBoundariesVv.class)
    IntervalBoundariesVv(IntervaledSurface surface) {
      super(
        ImmutableList.of(surface.getExtension(ObjectSpacePatchInfoVv.class), surface.interval()),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> computeIntervalBoundaries(
          surface.interval().value(),
          surface.getExtension(ObjectSpacePatchInfoVv.class).value().getPatchCount()));
    }
  }

  public static class PatchIntervalInfoVv
    extends DerivedVersionedValue<SurfacePatchIntervalInfo>
    implements SurfacePatchIntervalInfo.Vv {
    @HanaDeclareExtension(
      extensibleClass = IntervaledSurface.class,
      extensionClass = SurfacePatchIntervalInfo.Vv.class)
    PatchIntervalInfoVv(
      IntervaledSurface surface,
      Provider<IntervaledSurfacePatchIntervals.IntervaledSurfacePatchIntervalBuilder> patchIntervalBuilder) {
      super(
        ImmutableList.of(surface.interval(), surface.getExtension(IntervalBoundariesVv.class)),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> new SurfacePatchIntervalInfo.FromList(
          createSurfacePatchIntervalInfoList(
            surface,
            surface.interval().value(),
            surface.getExtension(IntervalBoundariesVv.class).value(),
            patchIntervalBuilder)
        )
      );
    }
  }

  public static class XformedPatchInfoFactoryVv
    extends VvTransform<SurfaceGeometry, XformedPatchInfoFactory>
    implements XformedPatchInfoFactory.Vv {
    @HanaDeclareExtension(
      extensibleClass = IntervaledSurface.class,
      extensionClass = XformedPatchInfoFactory.Vv.class)
    XformedPatchInfoFactoryVv(IntervaledSurface surface) {
      super(
        surface.getExtension(GeometryVv.class),
        geometry -> geometry.getExtension(XformedPatchInfoFactory.Vv.class));
    }
  }

  public static class SurfacePatchInfoVv
    extends DerivedVersionedValue<SurfacePatchInfo>
    implements SurfacePatchInfo.Vv {
    @HanaDeclareExtension(
      extensibleClass = IntervaledSurface.class,
      extensionClass = SurfacePatchInfo.Vv.class)
    SurfacePatchInfoVv(IntervaledSurface surface) {
      super(
        ImmutableList.of(
          surface.getExtension(XformedPatchInfoFactory.Vv.class),
          surface.toWorld()),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> surface.getExtension(XformedPatchInfoFactory.Vv.class).value().create(surface.toWorld().value()));
    }
  }

  public static class SurfaceShadingInfoVv
    extends DerivedVersionedValue<SurfaceShadingInfo>
    implements SurfaceShadingInfo.Vv {
    @HanaDeclareExtension(
      extensibleClass = IntervaledSurface.class,
      extensionClass = SurfaceShadingInfo.Vv.class)
    SurfaceShadingInfoVv(IntervaledSurface surface, HanaUnwrapper unwrapper) {
      super(
        ImmutableList.of(surface.getExtension(SurfacePatchIntervalInfo.Vv.class)),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> new SurfaceShadingInfo(surface.getExtension(SurfacePatchIntervalInfo.Vv.class).value(), unwrapper));
    }
  }

  @HanaDeclareBuilder(IntervaledSurface.class)
  public static class IntervaledSurfaceBuilder extends IntervaledSurface__Impl__Builder<IntervaledSurfaceBuilder> {
    @Inject
    public IntervaledSurfaceBuilder(IntervaledSurface__ImplFactory factory) {
      super(factory);
      toWorld(new Transform(Matrix4dUtil.IDENTITY_MATRIX));
    }

    public static IntervaledSurfaceBuilder builder(Component component) {
      return component.uberFactory().create(IntervaledSurfaceBuilder.class);
    }
  }
}
