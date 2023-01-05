package hana04.shakuyaku.surface.mmd.geometry;

import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.apt.annotation.HanaProvideExtension;
import hana04.base.Component;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.changeprop.util.VvWrappedToVvAdaptor;
import hana04.shakuyaku.sbtm.SbtmBaseMesh;
import hana04.shakuyaku.sbtm.SbtmPose;
import hana04.shakuyaku.sbtm.extensible.mesh.PosedTriangleMeshInfoVv;
import hana04.shakuyaku.sbtm.extensible.pose.SbtmPoseAsset;
import hana04.shakuyaku.sbtm.extensible.pose.SbtmPoseVv;
import hana04.shakuyaku.surface.geometry.trimesh.TriangleMeshInfo;
import hana04.shakuyaku.surface.mmd.OptionalPmdModelVv;
import hana04.shakuyaku.surface.mmd.OptionalPmxModelVv;

import javax.inject.Inject;

public class MmdPosedGeometries {
  @HanaProvideExtension(
    extensibleClass = MmdPosedGeometry.class,
    extensionClass = OptionalPmdModelVv.class)
  public static OptionalPmdModelVv optionalPmdModelVv(MmdPosedGeometry geometry, HanaUnwrapper unwrapper) {
    return geometry.base().unwrap(unwrapper).getExtension(OptionalPmdModelVv.class);
  }

  @HanaProvideExtension(
    extensibleClass = MmdPosedGeometry.class,
    extensionClass = OptionalPmxModelVv.class)
  public static OptionalPmxModelVv optionalPmxModelVv(MmdPosedGeometry geometry, HanaUnwrapper unwrapper) {
    return geometry.base().unwrap(unwrapper).getExtension(OptionalPmxModelVv.class);
  }

  @HanaProvideExtension(
    extensibleClass = MmdPosedGeometry.class,
    extensionClass = SbtmBaseMesh.Vv.class)
  public static SbtmBaseMesh.Vv sbtmBaseMeshVv(MmdPosedGeometry geometry, HanaUnwrapper unwrapper) {
    return geometry.base().unwrap(unwrapper).getExtension(SbtmBaseMesh.Vv.class);
  }

  public static class SbtmPoseVv_
    extends VvWrappedToVvAdaptor<SbtmPoseAsset, SbtmPose>
    implements SbtmPoseVv {
    @HanaDeclareExtension(
      extensibleClass = MmdPosedGeometry.class,
      extensionClass = SbtmPoseVv.class)
    public SbtmPoseVv_(MmdPosedGeometry geometry, HanaUnwrapper unwrapper) {
      super(
        geometry.pose(),
        asset -> asset.getExtension(SbtmPoseVv.class),
        unwrapper);
    }
  }

  public static class TriangleMeshInfoVv_ extends PosedTriangleMeshInfoVv implements TriangleMeshInfo.Vv {
    @HanaDeclareExtension(
      extensibleClass = MmdPosedGeometry.class,
      extensionClass = TriangleMeshInfo.Vv.class)
    TriangleMeshInfoVv_(MmdPosedGeometry geometry) {
      super(geometry.getExtension(SbtmBaseMesh.Vv.class), geometry.getExtension(SbtmPoseVv.class));
    }
  }

  @HanaDeclareBuilder(MmdPosedGeometry.class)
  public static class MmdPosedGeometryBuilder extends MmdPosedGeometry__Impl__Builder<MmdPosedGeometryBuilder> {
    @Inject
    public MmdPosedGeometryBuilder(MmdPosedGeometry__ImplFactory factory) {
      super(factory);
    }

    public static MmdPosedGeometryBuilder builder(Component component) {
      return component.uberFactory().create(MmdPosedGeometryBuilder.class);
    }
  }
}
