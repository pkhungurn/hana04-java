package hana04.shakuyaku;

import hana04.shakuyaku.bsdf.classes.alpha.AlphaBsdf;
import hana04.shakuyaku.bsdf.classes.blend.BlendBsdf;
import hana04.shakuyaku.bsdf.classes.diffuse.DiffuseBsdf;
import hana04.shakuyaku.bsdf.classes.mirror.MirrorBsdf;
import hana04.shakuyaku.bsdf.classes.passthrough.PassThroughBsdf;
import hana04.shakuyaku.bsdf.classes.pmx.PmxBsdf;
import hana04.shakuyaku.bsdf.classes.simpmicrofacet.SimpleMicrofacetBsdf;
import hana04.shakuyaku.bsdf.classes.smoothdielectric.SmoothDielectricBsdf;
import hana04.shakuyaku.bsdf.classes.twosided.TwoSidedBsdf;
import hana04.shakuyaku.emitter.area.AreaEmitter;
import hana04.shakuyaku.emitter.directional.DirectionalLight;
import hana04.shakuyaku.emitter.distantdisk.DistantDiskLight;
import hana04.shakuyaku.emitter.envmap.EnvironmentMapLight;
import hana04.shakuyaku.emitter.sumenv.SumEnvironmentalLight;
import hana04.shakuyaku.sbtm.SbtmPose;
import hana04.shakuyaku.sbtm.extensible.animation.FileSbtmAnimationAsset;
import hana04.shakuyaku.sbtm.extensible.pose.DirectSbtmPoseAsset;
import hana04.shakuyaku.sbtm.extensible.pose.SbtmAnimationAtTimePoseAsset;
import hana04.shakuyaku.sbtm.extensible.pose.VmdMotionAtTimePoseAsset;
import hana04.shakuyaku.scene.standard.StandardScene;
import hana04.shakuyaku.sensor.camera.orthographic.OrthographicCamera;
import hana04.shakuyaku.sensor.camera.perspective.PerspectiveCamera;
import hana04.shakuyaku.shadinghack.ambientlight.ConstantAmbientLight;
import hana04.shakuyaku.surface.geometry.statictrimesh.StaticTriMeshGeometry;
import hana04.shakuyaku.surface.intervaled.IntervaledSurface;
import hana04.shakuyaku.surface.intervaled.IntervaledSurfacePatchIntervalSpec;
import hana04.shakuyaku.surface.mmd.geometry.MmdBaseGeometry;
import hana04.shakuyaku.surface.mmd.geometry.MmdPosedGeometry;
import hana04.shakuyaku.surface.mmd.mmd.MmdSurface;
import hana04.shakuyaku.surface.mmd.pmxbase.PmxBaseSurface;
import hana04.shakuyaku.surface.mmd.util.MaterialAmbientUsageData;
import hana04.shakuyaku.texture.twodim.WrapMode;
import hana04.shakuyaku.texture.twodim.arithmetic.ScaleTwoDimTexture;
import hana04.shakuyaku.texture.twodim.constant.ConstantTwoDimTexture;
import hana04.shakuyaku.texture.twodim.image.ImageLoadingFailBehaviorData;
import hana04.shakuyaku.texture.twodim.image.ImageTexture;

/**
 * The type IDs of this project starts with 50000;
 */
public final class TypeIds {
  /**
   * {@link AlphaBsdf}
   */
  public static final int TYPE_ID_ALPHA_BSDF = 50001;

  /**
   * {@link BlendBsdf}
   */
  public static final int TYPE_ID_BLEND_BSDF = 50002;

  /**
   * {@link DiffuseBsdf}
   */
  public static final int TYPE_ID_DIFFUSE_BSDF = 50003;

  /**
   * {@link MirrorBsdf}
   */
  public static final int TYPE_ID_MIRROR_BSDF = 50004;

  /**
   * {@link PassThroughBsdf}
   */
  public static final int TYPE_ID_PASS_THROUGHT_BSDF = 50005;

  /**
   * {@link PmxBsdf}
   */
  public static final int TYPE_ID_PMX_BSDF = 50006;

  /**
   * {@link SimpleMicrofacetBsdf}
   */
  public static final int TYPE_ID_SIMPLE_MICROFACET_BSDF = 50007;

  /**
   * {@link SmoothDielectricBsdf}
   */
  public static final int TYPE_ID_SMOOTH_DIELECTRIC_BSDF = 50008;

  /**
   * {@link TwoSidedBsdf}
   */
  public static final int TYPE_ID_TWO_SIDED_BSDF = 50009;

  /**
   * {@link AreaEmitter}
   */
  public static final int TYPE_ID_AREA_EMITTER = 50010;

  /**
   * {@link DirectionalLight}
   */
  public static final int TYPE_ID_DIRECTION_LIGHT = 50011;

  /**
   * {@link DistantDiskLight}
   */
  public static final int TYPE_ID_DISTANT_DISK_LIGHT = 50012;

  /**
   * {@link EnvironmentMapLight}
   */
  public static final int TYPE_ID_ENVIRONMENT_MAP_LIGHT = 50013;

  /**
   * {@link SumEnvironmentalLight
   */
  public static final int TYPE_ID_SUM_ENVIRONMENT_LIGHT = 50014;

  /**
   * {@link FileSbtmAnimationAsset}
   */
  public static final int TYPE_ID_FILE_SBTM_ANIMATION_ASSET = 50027;

  /**
   * {@link DirectSbtmPoseAsset}
   */
  public static final int TYPE_ID_DIRECT_SBTM_POSE_ASSET = 50028;

  /**
   * {@link SbtmAnimationAtTimePoseAsset}
   */
  public static final int TYPE_ID_SBTM_ANIMATION_AT_TIME_POSE_ASSET = 50029;

  /**
   * {@link VmdMotionAtTimePoseAsset}
   */
  public static final int TYPE_ID_VMD_MOTION_AT_TIME_POSE_ASSET = 50030;

  /**
   * {@link SbtmPose}
   */
  public static final int TYPE_ID_SBTM_POSE = 50031;

  /**
   * {@link StandardScene}
   */
  public static final int TYPE_ID_STANDARD_SCENE = 50032;

  /**
   * {@link OrthographicCamera}
   */
  public static final int TYPE_ID_ORTHOGRAPHIC_CAMERA = 50033;

  /**
   * {@link PerspectiveCamera}
   */
  public static final int TYPE_ID_PERSPECTIVE_CAMERA = 50034;

  /**
   * {@link ConstantAmbientLight}
   */
  public static final int TYPE_ID_CONSTANT_AMBIENT_LIGHT = 50035;

  /**
   * {@link StaticTriMeshGeometry}
   */
  public static final int TYPE_ID_STATIC_TRI_MESH_GEOMETRY = 50036;

  /**
   * {@link IntervaledSurface}
   */
  public static final int TYPE_ID_INTERVALED_SURFACE = 50037;

  /**
   * {@link IntervaledSurfacePatchIntervalSpec}
   */
  public static final int TYPE_ID_INTERVALED_SURFACE_PATCH_INTERVAL_SPEC = 50038;

  /**
   * {@link MmdBaseGeometry}
   */
  public static final int TYPE_ID_MMD_BASE_GEOMETRY = 50039;

  /**
   * {@link MmdPosedGeometry}
   */
  public static final int TYPE_ID_MMD_POSED_GEOMETRY = 50040;

  /**
   * {@link MmdSurface}
   */
  public static final int TYPE_ID_MMD_SURFACE = 50041;

  /**
   * {@link PmxBaseSurface}
   */
  public static final int TYPE_ID_PMX_BASE_SURFACE = 50042;

  /**
   * {@link MaterialAmbientUsageData}
   */
  public static final int TYPE_ID_MATERIAL_AMBIENT_USAGE_DATA = 50043;

  /**
   * {@link ScaleTwoDimTexture}
   */
  public static final int TYPE_ID_SCALE_TWO_DIM_TEXTURE = 50044;

  /**
   * {@link ConstantTwoDimTexture}
   */
  public static final int TYPE_ID_CONSTANT_TWO_DIM_TEXTURE = 50046;

  /**
   * {@link ImageLoadingFailBehaviorData}
   */
  public static final int TYPE_ID_IMAGE_LOADING_FAIL_BEHAVIOR_DATA = 50047;

  /**
   * {@link ImageTexture}
   */
  public static final int TYPE_ID_IMAGE_TEXTURE = 50048;

  /**
   * {@link WrapMode}
   */
  public static final int TYPE_ID_TEXTURE_TWO_DIM_WRAP_MODE = 50051;

  private TypeIds() {
    // NO-OP
  }
}
