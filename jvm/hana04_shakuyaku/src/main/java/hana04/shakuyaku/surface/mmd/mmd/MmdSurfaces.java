package hana04.shakuyaku.surface.mmd.mmd;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.apt.annotation.HanaProvideExtension;
import hana04.base.Component;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.gfxbase.gfxtype.Transform;
import hana04.shakuyaku.sbtm.SbtmBaseMesh;
import hana04.shakuyaku.sbtm.SbtmPose;
import hana04.shakuyaku.sbtm.extensible.pose.SbtmPoseVv;
import hana04.shakuyaku.surface.SurfacePatchInfo;
import hana04.shakuyaku.surface.SurfacePatchIntervalInfo;
import hana04.shakuyaku.surface.SurfaceShadingInfo;
import hana04.shakuyaku.surface.geometry.trimesh.TriangleMeshInfo;
import hana04.shakuyaku.surface.geometry.trimesh.XformedTriMeshPatchInfo;
import hana04.shakuyaku.surface.mmd.OptionalPmdModelVv;
import hana04.shakuyaku.surface.mmd.OptionalPmxModelVv;
import hana04.shakuyaku.surface.mmd.patchinterval.MmdSurfacePatchInternvalInfoVv;
import hana04.shakuyaku.surface.mmd.patchinterval.PmdBasePatchIntervalFactory;
import hana04.shakuyaku.surface.mmd.patchinterval.PmxBasePatchIntervalFactory;
import hana04.shakuyaku.surface.mmd.util.MaterialAmbientUsage;
import hana04.shakuyaku.surface.mmd.util.MaterialAmbientUsageData;
import hana04.shakuyaku.surface.mmd.util.MaterialAmbientUsageDatas;

import javax.inject.Inject;
import javax.inject.Provider;

public class MmdSurfaces {
  @HanaProvideExtension(
    extensibleClass = MmdSurface.class,
    extensionClass = MaterialAmbientUsageData.class)
  public static MaterialAmbientUsageData providesMaterialAmbientUsageDataExtension(
      MmdSurface geometry,
      Provider<MaterialAmbientUsageDatas.MaterialAmbientUsageDataBuilder> materialAmbientUsageDataBuilder) {
    return materialAmbientUsageDataBuilder.get().build();
  }

  public static class SbtmPoseVv_ extends DerivedVersionedValue<SbtmPose> implements SbtmPoseVv {

    @HanaDeclareExtension(
      extensibleClass = MmdSurface.class,
      extensionClass = SbtmPoseVv.class)
    public SbtmPoseVv_(MmdSurface surface, HanaUnwrapper unwrapper) {
      super(
        ImmutableList.of(surface.geometry().unwrap(unwrapper).getExtension(SbtmPoseVv.class)),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> surface.geometry().unwrap(unwrapper).getExtension(SbtmPoseVv.class).value());
    }
  }

  public static class SbtmBaseMeshVv_ extends DerivedVersionedValue<SbtmBaseMesh> implements SbtmBaseMesh.Vv {
    @HanaDeclareExtension(
      extensibleClass = MmdSurface.class,
      extensionClass = SbtmBaseMesh.Vv.class)
    SbtmBaseMeshVv_(MmdSurface mmdSurface, HanaUnwrapper unwrapper) {
      super(
        ImmutableList.of(mmdSurface.geometry().unwrap(unwrapper).getExtension(SbtmBaseMesh.Vv.class)),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> mmdSurface.geometry().unwrap(unwrapper).getExtension(SbtmBaseMesh.Vv.class).value());
    }
  }

  public static class TriangleMeshInfoObjectSpaceVv_
    extends DerivedVersionedValue<TriangleMeshInfo>
    implements TriangleMeshInfo.ObjectSpace.Vv {
    @HanaDeclareExtension(
      extensibleClass = MmdSurface.class,
      extensionClass = TriangleMeshInfo.ObjectSpace.Vv.class
    )
    TriangleMeshInfoObjectSpaceVv_(MmdSurface mesh, HanaUnwrapper unwrapper) {
      super(
        ImmutableList.of(mesh.geometry().unwrap(unwrapper).getExtension(TriangleMeshInfo.Vv.class)),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> mesh.geometry().unwrap(unwrapper).getExtension(TriangleMeshInfo.Vv.class).value());
    }
  }

  public static class TriangleMeshInfoObjectSpace_ extends TriangleMeshInfo.VvProxy
    implements TriangleMeshInfo.ObjectSpace {
    @HanaDeclareExtension(
      extensibleClass = MmdSurface.class,
      extensionClass = ObjectSpace.class)
    TriangleMeshInfoObjectSpace_(MmdSurface mmdSurface) {
      super(mmdSurface.getExtension(ObjectSpace.Vv.class));
    }
  }

  public static class SurfacePatchIntervalInfoVv
    extends MmdSurfacePatchInternvalInfoVv {

    @HanaDeclareExtension(
      extensionClass = SurfacePatchIntervalInfo.Vv.class,
      extensibleClass = MmdSurface.class)
    SurfacePatchIntervalInfoVv(
      MmdSurface mmdSurface, HanaUnwrapper unwrapper,
      PmdBasePatchIntervalFactory pmdBasePatchIntervalFactory,
      PmxBasePatchIntervalFactory pmxBasePatchIntervalFactory) {
      super(
        mmdSurface,
        mmdSurface.geometry().unwrap(unwrapper).getExtension(OptionalPmdModelVv.class),
        mmdSurface.geometry().unwrap(unwrapper).getExtension(OptionalPmxModelVv.class),
        mmdSurface.getExtension(MaterialAmbientUsage.Vv.class),
        pmdBasePatchIntervalFactory,
        pmxBasePatchIntervalFactory);
    }
  }

  public static class SurfacePatchInfoVv_
    extends DerivedVersionedValue<SurfacePatchInfo>
    implements SurfacePatchInfo.Vv {

    @HanaDeclareExtension(
      extensibleClass = MmdSurface.class,
      extensionClass = SurfacePatchInfo.Vv.class)
    SurfacePatchInfoVv_(MmdSurface mmdSurface) {
      super(
        ImmutableList.of(),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> {
          Transform xform = mmdSurface.toWorld().value();
          TriangleMeshInfo meshInfo = mmdSurface.getExtension(TriangleMeshInfo.ObjectSpace.Vv.class).value();
          return new XformedTriMeshPatchInfo(meshInfo, xform);
        });
    }
  }

  public static class SurfaceShadingInfoVv_
    extends DerivedVersionedValue<SurfaceShadingInfo>
    implements SurfaceShadingInfo.Vv {

    @HanaDeclareExtension(
      extensibleClass = MmdSurface.class,
      extensionClass = SurfaceShadingInfo.Vv.class)
    SurfaceShadingInfoVv_(MmdSurface mmdSurface, HanaUnwrapper unwrapper) {
      super(
        ImmutableList.of(mmdSurface.getExtension(SurfacePatchIntervalInfo.Vv.class)),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> new SurfaceShadingInfo(mmdSurface.getExtension(SurfacePatchIntervalInfo.Vv.class).value(), unwrapper)
      );
    }
  }

  @HanaDeclareBuilder(MmdSurface.class)
  public static class MmdSurfaceBuilder extends MmdSurface__Impl__Builder<MmdSurfaceBuilder> {
    @Inject
    public MmdSurfaceBuilder(MmdSurface__ImplFactory factory) {
      super(factory);
      toWorld(Transform.builder().build());
    }

    public static MmdSurfaceBuilder builder(Component component) {
      return component.uberFactory().create(MmdSurfaceBuilder.class);
    }
  }
}
