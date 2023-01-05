package hana04.mikumikubake.mmd;

import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.google.auto.value.AutoOneOf;
import com.google.common.collect.ImmutableList;
import hana04.base.caching.Direct;
import hana04.formats.mmd.generic.MmdAnimatedInstance;
import hana04.formats.mmd.generic.api.MmdMaterial;
import hana04.formats.mmd.generic.api.MmdModel;
import hana04.formats.mmd.generic.impl.ik.MikuMikuFlexIkSolver01;
import hana04.formats.mmd.generic.impl.physics.StandardMmdPhysics;
import hana04.formats.mmd.generic.impl.pmd.PmdModelAdaptor;
import hana04.formats.mmd.generic.impl.pmx.PmxModelAdaptor;
import hana04.formats.mmd.pmd.PmdModel;
import hana04.formats.mmd.pmx.PmxModel;
import hana04.formats.mmd.vpd.VpdPose;
import hana04.gfxbase.gfxtype.Transform;
import hana04.mikumikubake.surface.DirectPmdBaseGeometryBuilder;
import hana04.mikumikubake.surface.DirectPmxBaseGeometryBuilder;
import hana04.shakuyaku.sbtm.SbtmPose;
import hana04.shakuyaku.sbtm.converter.VpdPoseToSbtmPoseConverter;
import hana04.shakuyaku.sbtm.extensible.pose.DirectSbtmPoseAsset;
import hana04.shakuyaku.sbtm.extensible.pose.DirectSbtmPoseAssetBuilder;
import hana04.shakuyaku.surface.mmd.geometry.MmdGeometry;
import hana04.shakuyaku.surface.mmd.geometry.MmdPosedGeometries;
import hana04.shakuyaku.surface.mmd.mmd.MmdSurface;
import hana04.shakuyaku.surface.mmd.mmd.MmdSurfaces;
import hana04.shakuyaku.surface.mmd.util.MaterialAmbientUsageData;
import hana04.shakuyaku.surface.mmd.util.MaterialAmbientUsageDatas;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.google.common.collect.ImmutableList.toImmutableList;

public class MmdModelManager {
  private static final Logger logger = LoggerFactory.getLogger(MmdModelManager.class);

  private final Provider<MmdPosedGeometries.MmdPosedGeometryBuilder> mmdPosedGeometryBuilder;
  private final Provider<MmdSurfaces.MmdSurfaceBuilder> mmdSurfaceBuilder;
  private final Provider<DirectSbtmPoseAssetBuilder> directSbtmPoseAssetBuilder;
  private final Provider<DirectPmxBaseGeometryBuilder> directPmxBaseGeometryBuilder;
  private final Provider<DirectPmdBaseGeometryBuilder> directPmdBaseGeometryBuilder;

  private final VpdPose modelVpdPose = new VpdPose();
  private Optional<MmdData> mmdData = Optional.empty();

  private boolean physicsEnabled = true;
  private int maxUpdateCount = 100000;

  @Inject
  public MmdModelManager(Provider<MmdPosedGeometries.MmdPosedGeometryBuilder> mmdPosedGeometryBuilder,
      Provider<MmdSurfaces.MmdSurfaceBuilder> mmdSurfaceBuilder,
      Provider<DirectSbtmPoseAssetBuilder> directSbtmPoseAssetBuilder,
      Provider<DirectPmxBaseGeometryBuilder> directPmxBaseGeometryBuilder,
      Provider<DirectPmdBaseGeometryBuilder> directPmdBaseGeometryBuilder) {
    this.mmdPosedGeometryBuilder = mmdPosedGeometryBuilder;
    this.mmdSurfaceBuilder = mmdSurfaceBuilder;
    this.directSbtmPoseAssetBuilder = directSbtmPoseAssetBuilder;
    this.directPmxBaseGeometryBuilder = directPmxBaseGeometryBuilder;
    this.directPmdBaseGeometryBuilder = directPmdBaseGeometryBuilder;
  }

  synchronized public boolean thereIsModel() {
    return mmdData.isPresent();
  }

  synchronized public void load(String absolutePath) {
    clearMmdData();
    String extension = FilenameUtils.getExtension(absolutePath).toLowerCase();

    RawModel rawModel = null;
    if (extension.equals("pmx")) {
      try {
        PmxModel pmxModel = PmxModel.load(absolutePath);
        rawModel = RawModel.ofPmx(pmxModel);
      } catch (Exception e) {
        clearMmdData();
        throw new RuntimeException(e);
      }
    } else if (extension.equals("pmd")) {
      try {
        PmdModel pmdModel = PmdModel.load(absolutePath);
        rawModel = RawModel.ofPmd(pmdModel);
      } catch (Exception e) {
        clearMmdData();
        throw new RuntimeException(e);
      }
    } else {
      logger.info("File of extension " + extension + " is not supports!");
    }
    if (rawModel == null) {
      return;
    }
    mmdData = Optional.of(new MmdData(rawModel, absolutePath));
  }

  public void setPhysicsEnabled(boolean enabled) {
    this.physicsEnabled = enabled;
    mmdData.ifPresent(data -> data.instance.setPhysicsEnabled(this.physicsEnabled));
  }

  synchronized public void clear() {
    clearMmdData();
  }

  synchronized private void clearMmdData() {
    mmdData.ifPresent(MmdData::dispose);
    mmdData = Optional.empty();
  }

  synchronized public Optional<MmdSurface> getMmdSurface() {
    return mmdData.map(data -> data.surface);
  }

  private static final ImmutableList<String> EMPTY_LIST = ImmutableList.of();

  synchronized public List<String> getMaterialNames() {
    return mmdData.map(data -> data.materialNames).orElse(EMPTY_LIST);
  }

  public synchronized void setPose(VpdPose vpdPose, boolean resetPhysics) {
    mmdData.ifPresent(data -> data.instance.setInputPost(vpdPose));
  }

  public synchronized void update(double elaspedTime) {
    mmdData.ifPresent(data -> {
      data.instance.update((float) elaspedTime, false);
      data.instance.getOutputPose(modelVpdPose);
      SbtmPose pose = VpdPoseToSbtmPoseConverter.convert(modelVpdPose);
      data.sbtmPoseAsset.sbtmPose().set(pose);
    });
  }

  public synchronized String getAbsolutePath() {
    return mmdData.map(data -> data.absolutePath).orElse("");
  }

  public synchronized VpdPose getModelVpdPose() {
    return modelVpdPose;
  }

  public synchronized Optional<MmdModelPosingInfo> getModelPosingInfo() {
    return mmdData.map(data -> data.modelPosingInfo);
  }

  public Optional<MmdModelPosingInfo> getMmdModelPosingInfo() {
    return mmdData.map(data -> data.modelPosingInfo);
  }

  public Optional<Matrix4f> getBoneGlobalTransform(String boneName) {
    return mmdData.flatMap(data -> data.instance.getBoneGlobaTransform(boneName)).map(Matrix4f::new);
  }

  public void setMaxIkUpdateCount(int value) {
    this.maxUpdateCount = value;
    mmdData.ifPresent(data -> data.instance.setIkMaxUpdateCount(value));
  }

  public Optional<List<btRigidBody>> getBtRigidBodies() {
    return mmdData.flatMap(data -> data.instance.getBtRigidBodies());
  }

  public Optional<List<Matrix4d>> getBoneTransforms() {
    return mmdData.map(data -> {
      var outputPose = data.instance.getOutputPose();
      return IntStream
          .range(0, data.mmdModel.bones().size())
          .mapToObj(outputPose::getGlobalTransform)
          .collect(toImmutableList());
    });
  }

  public Optional<MmdModel> getMmdModel() {
    return mmdData.map(data -> data.mmdModel);
  }

  public Optional<RawModel> getRawModel() {
    return mmdData.map(data -> data.rawModel);
  }

  @AutoOneOf(RawModel.Kind.class)
  public static abstract class RawModel {
    public enum Kind {
      PMD_MODEL,
      PMX_MODEL
    }

    public abstract Kind getKind();

    public abstract PmdModel pmdModel();

    public abstract PmxModel pmxModel();

    static RawModel ofPmd(PmdModel model) {
      return AutoOneOf_MmdModelManager_RawModel.pmdModel(model);
    }

    static RawModel ofPmx(PmxModel model) {
      return AutoOneOf_MmdModelManager_RawModel.pmxModel(model);
    }
  }

  public Optional<MmdAnimatedInstance> getInstance() {
    return mmdData.map(data -> data.instance);
  }

  class MmdData {
    RawModel rawModel;
    MmdModel mmdModel;
    MmdAnimatedInstance instance;
    MmdSurface surface;
    DirectSbtmPoseAsset sbtmPoseAsset;
    String absolutePath;
    MmdModelPosingInfo modelPosingInfo;
    ImmutableList<String> materialNames;

    MmdData(RawModel rawModel, String absolutePath) {
      this.rawModel = rawModel;
      this.absolutePath = absolutePath;

      MmdGeometry mmdGeometry;
      if (rawModel.getKind().equals(RawModel.Kind.PMD_MODEL)) {
        mmdModel = new PmdModelAdaptor(rawModel.pmdModel());
        mmdGeometry = directPmdBaseGeometryBuilder.get().model(rawModel.pmdModel()).build();
      } else {
        mmdModel = new PmxModelAdaptor(rawModel.pmxModel());
        mmdGeometry = directPmxBaseGeometryBuilder.get().model(rawModel.pmxModel()).build();
      }

      instance = new MmdAnimatedInstance(
          mmdModel,
          new MikuMikuFlexIkSolver01.Factory(),
          new StandardMmdPhysics.Factory());
      instance.setPhysicsEnabled(physicsEnabled);
      instance.setIkEnabled(true);
      instance.setIkMaxUpdateCount(maxUpdateCount);

      sbtmPoseAsset = directSbtmPoseAssetBuilder.get().build();
      surface = mmdSurfaceBuilder.get()
          .geometry(
              mmdPosedGeometryBuilder.get()
                  .base(Direct.of(mmdGeometry))
                  .pose(sbtmPoseAsset)
                  .build())
          .toWorld(Transform.builder().build())
          .build();
      surface
          .getExtension(MaterialAmbientUsageData.class)
          .ambientMode()
          .set(MaterialAmbientUsageDatas.AMBIENT_OPT_OUT);

      modelPosingInfo = new MmdModelPosingInfoImpl(mmdModel);

      materialNames = mmdModel.materials().stream().map(MmdMaterial::japaneseName).collect(toImmutableList());
    }

    void dispose() {
      instance.dispose();
    }
  }
}
