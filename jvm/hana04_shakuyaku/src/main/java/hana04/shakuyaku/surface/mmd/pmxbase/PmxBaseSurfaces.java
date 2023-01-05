package hana04.shakuyaku.surface.mmd.pmxbase;

import com.google.common.base.Preconditions;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.caching.HanaUnwrapper;
import hana04.gfxbase.gfxtype.Matrix4dUtil;
import hana04.gfxbase.gfxtype.Transform;
import hana04.shakuyaku.surface.PatchInterval;
import hana04.shakuyaku.surface.SurfacePatchInfo;
import hana04.shakuyaku.surface.SurfacePatchIntervalInfo;
import hana04.shakuyaku.surface.SurfaceShadingInfo;
import hana04.shakuyaku.surface.geometry.trimesh.TriangleMeshInfo;
import hana04.shakuyaku.surface.geometry.trimesh.XformedTriMeshPatchInfo;
import hana04.shakuyaku.surface.mmd.FilePmxModelVv;
import hana04.shakuyaku.surface.mmd.patchinterval.PmxBasePatchIntervalFactory;
import hana04.shakuyaku.surface.mmd.util.MaterialAmbientUsage;

import javax.inject.Inject;
import java.nio.file.FileSystem;
import java.util.List;

public class PmxBaseSurfaces {
  public static final String AMBIENT_OPT_IN = "opt-in";
  public static final String AMBIENT_OPT_OUT = "opt-out";

  @HanaDeclareBuilder(PmxBaseSurface.class)
  public static class PmxBaseSurfaceBuilder extends PmxBaseSurface__Impl__Builder<PmxBaseSurfaceBuilder> {
    @Inject
    public PmxBaseSurfaceBuilder(PmxBaseSurface__ImplFactory factory) {
      super(factory);
      ambientMode(AMBIENT_OPT_OUT);
      toWorld(new Transform(Matrix4dUtil.IDENTITY_MATRIX));
    }

    public static PmxBaseSurfaceBuilder builder(Component component) {
      return component.uberFactory().create(PmxBaseSurfaceBuilder.class);
    }

    @Override
    public PmxBaseSurfaceBuilder ambientMode(String ambientMode) {
      Preconditions.checkArgument(ambientMode.equals(AMBIENT_OPT_IN)
          || ambientMode.equals(AMBIENT_OPT_OUT));
      super.ambientMode(ambientMode);
      return this;
    }
  }

  public static List<? extends PatchInterval> createSurfacePatchIntervalInfo(
      PmxBaseSurface pmxBaseSurface, PmxBasePatchIntervalFactory factory) {
    PmxModelVv pmxModelProvider = pmxBaseSurface.getExtension(PmxModelVv.class);
    return factory.create(
        pmxModelProvider.value(),
        /* surface= */pmxBaseSurface,
        /* materialAmbientUsage= */ pmxBaseSurface.getExtension(MaterialAmbientUsage.class));
  }

  public static SurfacePatchInfo createSurfacePatchInfo(PmxBaseSurface pmxBaseSurface) {
    TriangleMeshInfoProvider triangleMeshInfoProvider =
        pmxBaseSurface.getExtension(TriangleMeshInfoProvider.class);
    return new XformedTriMeshPatchInfo(
        triangleMeshInfoProvider.getTriangleMeshInfo(),
        pmxBaseSurface.toWorld().value());
  }

  public static class SurfacePatchIntervalInfoExtension extends SurfacePatchIntervalInfo.FromList {
    @HanaDeclareExtension(
        extensibleClass = PmxBaseSurface.class,
        extensionClass = SurfacePatchIntervalInfo.class)
    public SurfacePatchIntervalInfoExtension(PmxBaseSurface pmxBaseSurface, PmxBasePatchIntervalFactory factory) {
      super(createSurfacePatchIntervalInfo(pmxBaseSurface, factory));
    }
  }

  public static class PmxModelVv extends FilePmxModelVv {
    @HanaDeclareExtension(
        extensibleClass = PmxBaseSurface.class,
        extensionClass = PmxModelVv.class)
    public PmxModelVv(PmxBaseSurface pmxBaseSurface, FileSystem fileSystem) {
      super(pmxBaseSurface.filePath(), fileSystem);
    }
  }

  public static class TriangleMeshInfoProvider {
    private final TriangleMeshInfo triMeshInfo;

    @HanaDeclareExtension(
        extensibleClass = PmxBaseSurface.class,
        extensionClass = TriangleMeshInfoProvider.class)
    public TriangleMeshInfoProvider(PmxBaseSurface pmxBaseSurface) {
      PmxModelVv pmxModelProvider = pmxBaseSurface.getExtension(PmxModelVv.class);
      triMeshInfo = PmxBaseGeometryLoader.createTriMesh(pmxModelProvider.value());
    }

    public TriangleMeshInfo getTriangleMeshInfo() {
      return triMeshInfo;
    }
  }

  public static class PatchInfo extends SurfacePatchInfo.Proxy {
    @HanaDeclareExtension(
        extensibleClass = PmxBaseSurface.class,
        extensionClass = SurfacePatchInfo.class)
    public PatchInfo(PmxBaseSurface pmxBaseSurface) {
      super(createSurfacePatchInfo(pmxBaseSurface));
    }
  }

  public static class SurfaceShadingInfo_ extends SurfaceShadingInfo {
    @HanaDeclareExtension(
        extensibleClass = PmxBaseSurface.class,
        extensionClass = SurfaceShadingInfo.class)
    SurfaceShadingInfo_(PmxBaseSurface node, HanaUnwrapper unwrapper) {
      super(node.getExtension(SurfacePatchIntervalInfo.Vv.class).value(), unwrapper);
    }
  }

  public static class MaterialAmbientUsage_ implements MaterialAmbientUsage {
    private final PmxBaseSurface instance;

    @HanaDeclareExtension(
        extensibleClass = PmxBaseSurface.class,
        extensionClass = MaterialAmbientUsage.class)
    MaterialAmbientUsage_(PmxBaseSurface instance) {
      this.instance = instance;
    }

    @Override
    public boolean shouldMaterialUseAmbient(String name) {
      boolean hasOpting = instance.materialWithAmbientOpting().value().contains(name);
      if (instance.ambientMode().value().equals(PmxBaseSurfaces.AMBIENT_OPT_IN)) {
        return hasOpting;
      } else {
        return !hasOpting;
      }
    }
  }
}
