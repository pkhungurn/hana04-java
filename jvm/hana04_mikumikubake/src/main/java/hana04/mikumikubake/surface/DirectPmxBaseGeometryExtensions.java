package hana04.mikumikubake.surface;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.changeprop.util.VvProxy;
import hana04.formats.mmd.pmd.PmdModel;
import hana04.formats.mmd.pmx.PmxModel;
import hana04.shakuyaku.sbtm.SbtmBaseMesh;
import hana04.shakuyaku.surface.geometry.trimesh.TriangleMeshInfo;
import hana04.shakuyaku.surface.mmd.MmdSbtmBaseMeshVv;
import hana04.shakuyaku.surface.mmd.OptionalPmdModelVv;
import hana04.shakuyaku.surface.mmd.OptionalPmxModelVv;

import java.util.Optional;

public class DirectPmxBaseGeometryExtensions {
  public static class OptionalPmxModelVv_ extends DerivedVersionedValue<Optional<PmxModel>>
    implements OptionalPmxModelVv {
    @HanaDeclareExtension(
      extensibleClass = DirectPmxBaseGeometry.class,
      extensionClass = OptionalPmxModelVv.class
    )
    public OptionalPmxModelVv_(DirectPmxBaseGeometry geometry) {
      super(ImmutableList.of(), ChangePropUtil::largestBetweenIncSelfAndDeps, () -> Optional.of(geometry.model()));
    }
  }

  public static class OptionalPmdModelVv_ extends DerivedVersionedValue<Optional<PmdModel>>
    implements OptionalPmdModelVv {
    @HanaDeclareExtension(
      extensibleClass = DirectPmxBaseGeometry.class,
      extensionClass = OptionalPmdModelVv.class
    )
    public OptionalPmdModelVv_(DirectPmxBaseGeometry geometry) {
      super(
        ImmutableList.of(),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        Optional::empty);
    }
  }

  public static class BaseMeshVv extends MmdSbtmBaseMeshVv {
    @HanaDeclareExtension(
      extensibleClass = DirectPmxBaseGeometry.class,
      extensionClass = SbtmBaseMesh.Vv.class)
    public BaseMeshVv(DirectPmxBaseGeometry geometry) {
      super(
        geometry.getExtension(OptionalPmdModelVv.class),
        geometry.getExtension(OptionalPmxModelVv.class));
    }
  }

  public static class TriangleMeshInfoVv_
    extends VvProxy<TriangleMeshInfo>
    implements TriangleMeshInfo.Vv {

    @HanaDeclareExtension(
      extensibleClass = DirectPmxBaseGeometry.class,
      extensionClass = TriangleMeshInfo.Vv.class)
    public TriangleMeshInfoVv_(DirectPmxBaseGeometry geometry) {
      super(geometry.getExtension(SbtmBaseMesh.Vv.class));
    }
  }
}
