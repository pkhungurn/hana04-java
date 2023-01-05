package hana04.shakuyaku.surface.mmd.geometry;

import com.google.common.base.Preconditions;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareCacheLoader;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.caching.CacheKey;
import hana04.base.caching.FilePathCacheKeyPart;
import hana04.base.caching.HanaCacheLoader;
import hana04.base.changeprop.Constant;
import hana04.base.changeprop.util.VvProxy;
import hana04.base.filesystem.FilePath;
import hana04.shakuyaku.sbtm.SbtmBaseMesh;
import hana04.shakuyaku.surface.geometry.trimesh.TriangleMeshInfo;
import hana04.shakuyaku.surface.mmd.FileOptionalPmdModelVv;
import hana04.shakuyaku.surface.mmd.FileOptionalPmxModelVv;
import hana04.shakuyaku.surface.mmd.MmdSbtmBaseMeshVv;
import hana04.shakuyaku.surface.mmd.OptionalPmdModelVv;
import hana04.shakuyaku.surface.mmd.OptionalPmxModelVv;

import javax.inject.Inject;
import javax.inject.Provider;
import java.nio.file.FileSystem;

public class MmdBaseGeometries {
  public static final String PROTOCAL_NAME = "shakuyaku.MmdBaseGeometry";

  public static class PmdModelVv extends FileOptionalPmdModelVv {
    @HanaDeclareExtension(
      extensibleClass = MmdBaseGeometry.class,
      extensionClass = OptionalPmdModelVv.class)
    public PmdModelVv(MmdBaseGeometry mmdBaseGeometry, FileSystem fileSystem) {
      super(new Constant<>(mmdBaseGeometry.modelFilePath()), fileSystem);
    }
  }

  public static class PmxModelVv extends FileOptionalPmxModelVv {
    @HanaDeclareExtension(
      extensibleClass = MmdBaseGeometry.class,
      extensionClass = OptionalPmxModelVv.class)
    public PmxModelVv(MmdBaseGeometry mmdBaseGeometry, FileSystem fileSystem) {
      super(new Constant<>(mmdBaseGeometry.modelFilePath()), fileSystem);
    }
  }

  public static class BaseMeshVv extends MmdSbtmBaseMeshVv {
    @HanaDeclareExtension(
      extensibleClass = MmdBaseGeometry.class,
      extensionClass = SbtmBaseMesh.Vv.class)
    public BaseMeshVv(MmdBaseGeometry mmdBaseGeometry) {
      super(
        mmdBaseGeometry.getExtension(OptionalPmdModelVv.class),
        mmdBaseGeometry.getExtension(OptionalPmxModelVv.class));
    }
  }

  public static class TriangleMeshInfoVv_
    extends VvProxy<TriangleMeshInfo>
    implements TriangleMeshInfo.Vv {

    @HanaDeclareExtension(
      extensibleClass = MmdBaseGeometry.class,
      extensionClass = TriangleMeshInfo.Vv.class)
    public TriangleMeshInfoVv_(MmdBaseGeometry mmdBaseGeometry) {
      super(mmdBaseGeometry.getExtension(SbtmBaseMesh.Vv.class));
    }
  }

  @HanaDeclareCacheLoader({PROTOCAL_NAME, "MmdBaseGeometry"})
  public static
  class Loader implements HanaCacheLoader<MmdBaseGeometry> {
    private Provider<MmdBaseGeometryBuilder> builderProvider;

    @Inject
    public Loader(Provider<MmdBaseGeometryBuilder> builderProvider) {
      this.builderProvider = builderProvider;
    }

    @Override
    public MmdBaseGeometry load(CacheKey key) {
      Preconditions.checkArgument(key.protocol.equals(PROTOCAL_NAME));
      Preconditions.checkArgument(key.parts.size() == 1);
      Preconditions.checkArgument(key.parts.get(0) instanceof FilePathCacheKeyPart);
      FilePath filePath = ((FilePathCacheKeyPart) key.parts.get(0)).value;
      return builderProvider.get().modelFilePath(filePath).build();
    }
  }

  @HanaDeclareBuilder(MmdBaseGeometry.class)
  public static class MmdBaseGeometryBuilder extends MmdBaseGeometry__Impl__Builder<MmdBaseGeometryBuilder> {
    @Inject
    public MmdBaseGeometryBuilder(MmdBaseGeometry__ImplFactory factory) {
      super(factory);
    }

    public static MmdBaseGeometryBuilder builder(Component component) {
      return component.uberFactory().create(MmdBaseGeometryBuilder.class);
    }
  }
}
