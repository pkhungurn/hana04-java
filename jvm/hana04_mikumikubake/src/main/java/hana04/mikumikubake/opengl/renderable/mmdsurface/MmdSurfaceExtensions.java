package hana04.mikumikubake.opengl.renderable.mmdsurface;

import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.caching.CacheKey;
import hana04.base.caching.Cached;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.AbstractDerivedSubject;
import hana04.base.changeprop.VersionedValue;
import hana04.botan.cache.GlObjectCache;
import hana04.botan.cache.GlObjectRecord;
import hana04.botan.glasset.index.HostIndexData;
import hana04.botan.glasset.program.ProgramAsset;
import hana04.botan.glasset.program.ResourceProgramAssetExtensions;
import hana04.botan.glasset.provider.GlIndexProvider;
import hana04.botan.glasset.provider.GlProgramProvider;
import hana04.botan.glasset.vertex.HostVertexAttribData;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.gfxtype.Matrix4dUtil;
import hana04.mikumikubake.opengl.renderable.adaptors.HostIndexDataToGlIndexProviderAdaptor;
import hana04.mikumikubake.opengl.renderable.adaptors.HostVertexAttribDataToGlVertexAttribProviderAdaptor;
import hana04.mikumikubake.opengl.renderable.shaders.Constants;
import hana04.mikumikubake.opengl.renderer00.Renderer00;
import hana04.mikumikubake.opengl.renderer00.extensions.GlBoneTransformTextureProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlMorphDisplacementTextureProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlMorphWeightTextureProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlSdefParamsTextureProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexBoneIndexProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexBoneWeightProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexIndexColorProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexMorphStartAndCountProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexNormalProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexPositionProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexSkinningInfoProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexTangentProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexTexCoordProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.HostVertexBoneIndexData;
import hana04.mikumikubake.opengl.renderer00.extensions.HostVertexBoneWeightData;
import hana04.mikumikubake.opengl.renderer00.extensions.HostVertexIndexColorData;
import hana04.mikumikubake.opengl.renderer00.extensions.HostVertexMorphStartAndCountData;
import hana04.mikumikubake.opengl.renderer00.extensions.HostVertexNormalData;
import hana04.mikumikubake.opengl.renderer00.extensions.HostVertexPositionData;
import hana04.mikumikubake.opengl.renderer00.extensions.HostVertexSkinningInfoData;
import hana04.mikumikubake.opengl.renderer00.extensions.HostVertexTangentData;
import hana04.mikumikubake.opengl.renderer00.extensions.HostVertexTexCoordData;
import hana04.opengl.wrapper.GlAttributeSpec;
import hana04.opengl.wrapper.GlConstants;
import hana04.opengl.wrapper.GlTextureRect;
import hana04.opengl.wrapper.GlProgram;
import hana04.opengl.wrapper.GlWrapper;
import hana04.shakuyaku.bsdf.Bsdf;
import hana04.shakuyaku.bsdf.classes.pmx.PmxBsdf;
import hana04.shakuyaku.sbtm.SbtmBaseMesh;
import hana04.shakuyaku.sbtm.SbtmPose;
import hana04.shakuyaku.sbtm.SbtmSdefParams;
import hana04.shakuyaku.sbtm.SbtmSkinningType;
import hana04.shakuyaku.sbtm.extensible.pose.SbtmPoseVv;
import hana04.shakuyaku.surface.PatchInterval;
import hana04.shakuyaku.surface.SurfacePatchIntervalInfo;
import hana04.shakuyaku.surface.geometry.trimesh.TriangleMeshInfo;
import hana04.shakuyaku.surface.mmd.mmd.MmdSurface;

import javax.inject.Provider;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Point3i;
import javax.vecmath.Quat4d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4d;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class MmdSurfaceExtensions {
  private static final int MORPH_DISPLACEMENT_TEXTURE_WIDTH = 4096;
  public static final int BONE_TRANSFORM_TEXTURE_WIDTH = 256;
  private static final int MORPH_WEIGHT_TEXTURE_WIDTH = 256;
  private static final int SDEF_PARAMS_TEXTURE_WIDTH = 256;

  private static final GlAttributeSpec POSITION = new GlAttributeSpec(
      "restPosition", 3, GlConstants.GL_FLOAT, false, 4 * 3, 0);
  private static final GlAttributeSpec NORMAL = new GlAttributeSpec(
      "normal", 3, GlConstants.GL_FLOAT, false, 4 * 3, 0);
  private static final GlAttributeSpec TEX_COORD = new GlAttributeSpec(
      "texCoord", 2, GlConstants.GL_FLOAT, false, 4 * 2, 0);
  private static final GlAttributeSpec TANGENT = new GlAttributeSpec(
      "tangent", 4, GlConstants.GL_FLOAT, false, 4 * 4, 0);
  private static final GlAttributeSpec MORPH_START_AND_COUNT = new GlAttributeSpec(
      "morphStartAndCount", 2, GlConstants.GL_FLOAT, false, 4 * 2, 0);
  private static final GlAttributeSpec BONE_INDEX = new GlAttributeSpec(
      "boneIndex", 4, GlConstants.GL_FLOAT, false, 4 * 4, 0);
  private static final GlAttributeSpec BONE_WEIGHT = new GlAttributeSpec(
      "boneWeight", 4, GlConstants.GL_FLOAT, false, 4 * 4, 0);
  private static final GlAttributeSpec SKINNING_INFO = new GlAttributeSpec(
      "skinningInfo", 2, GlConstants.GL_FLOAT, false, 4 * 2, 0);
  private static final GlAttributeSpec INDEX_COLOR = new GlAttributeSpec(
      "vertexIndexColor", 3, GlConstants.GL_FLOAT, false, 4 * 3, 0);

  private static final HashMap<String, GlAttributeSpec> positionAttributes = new HashMap<>();
  private static final HashMap<String, GlAttributeSpec> normalAttributes = new HashMap<>();
  private static final HashMap<String, GlAttributeSpec> texCoordAttributes = new HashMap<>();
  private static final HashMap<String, GlAttributeSpec> tangentAttributes = new HashMap<>();
  private static final HashMap<String, GlAttributeSpec> morphStartAndCountAttributes = new HashMap<>();
  private static final HashMap<String, GlAttributeSpec> boneIndexAttributes = new HashMap<>();
  private static final HashMap<String, GlAttributeSpec> boneWeightAttributes = new HashMap<>();
  private static final HashMap<String, GlAttributeSpec> skinningInfoAttributes = new HashMap<>();
  private static final HashMap<String, GlAttributeSpec> vertexIndexColorAttributes = new HashMap<>();

  static {
    positionAttributes.put("restPosition", POSITION);
    positionAttributes.put("vert_position", POSITION);
    positionAttributes.put("vertex_position", POSITION);

    normalAttributes.put("normal", NORMAL);
    normalAttributes.put("vert_normal", NORMAL);
    normalAttributes.put("vertex_normal", NORMAL);

    texCoordAttributes.put("texCoord", TEX_COORD);
    texCoordAttributes.put("vert_texCoord", TEX_COORD);
    texCoordAttributes.put("vertex_texCoord", TEX_COORD);

    tangentAttributes.put("tangent", TANGENT);
    tangentAttributes.put("vert_tangent", TANGENT);
    tangentAttributes.put("vertex_tangent", TANGENT);

    morphStartAndCountAttributes.put("morphStartAndCount", MORPH_START_AND_COUNT);
    morphStartAndCountAttributes.put("vert_morphStartAndCount", MORPH_START_AND_COUNT);
    morphStartAndCountAttributes.put("vertex_morphStartAndCount", MORPH_START_AND_COUNT);

    boneIndexAttributes.put("boneIndex", BONE_INDEX);
    boneIndexAttributes.put("vert_boneIndex", BONE_INDEX);
    boneIndexAttributes.put("vertex_boneIndex", BONE_INDEX);

    boneWeightAttributes.put("boneWeight", BONE_WEIGHT);
    boneWeightAttributes.put("vert_boneWeight", BONE_WEIGHT);
    boneWeightAttributes.put("vertex_boneWeight", BONE_WEIGHT);

    skinningInfoAttributes.put("skinningInfo", SKINNING_INFO);
    skinningInfoAttributes.put("vert_skinningInfo", SKINNING_INFO);
    skinningInfoAttributes.put("vertex_skinningInfo", SKINNING_INFO);

    vertexIndexColorAttributes.put("vertexIndexColor", INDEX_COLOR);
    vertexIndexColorAttributes.put("vert_indexColor", INDEX_COLOR);
    vertexIndexColorAttributes.put("vertex_indexColor", INDEX_COLOR);
  }

  public static class HostVertexAttribDataAdaptor<T>
      extends AbstractDerivedSubject
      implements HostVertexAttribData {
    private final VersionedValue<T> baseData;
    private final Map<String, GlAttributeSpec> vertexAttributes;
    private final int bytesPerVertex;
    private final Function<T, Integer> vertexCountFunc;
    private final Function<T, Buffer> bufferFunc;

    public HostVertexAttribDataAdaptor(VersionedValue<T> baseData,
        Map<String, GlAttributeSpec> vertexAttributes,
        int bytesPerVertex,
        Function<T, Integer> vertexCountFunc,
        Function<T, Buffer> bufferFunc) {
      this.baseData = baseData;
      this.vertexAttributes = vertexAttributes;
      this.bytesPerVertex = bytesPerVertex;
      this.vertexCountFunc = vertexCountFunc;
      this.bufferFunc = bufferFunc;
      this.baseData.addObserver(this);
      forceUpdate();
    }

    @Override
    protected long updateInternal() {
      baseData.update();
      return Math.max(version() + 1, baseData.version());
    }

    @Override
    public GlAttributeSpec getAttributeSpec(String name) {
      return vertexAttributes.get(name);
    }

    @Override
    public boolean hasAttribute(String name) {
      return vertexAttributes.containsKey(name);
    }

    @Override
    public int getNumBytesPerVertex() {
      return bytesPerVertex;
    }

    @Override
    public int getVertexCount() {
      return vertexCountFunc.apply(baseData.value());
    }

    @Override
    public Buffer getBuffer() {
      return bufferFunc.apply(baseData.value());
    }
  }

  public static class HostVertexPositionData_
      extends HostVertexAttribDataAdaptor<SbtmBaseMesh>
      implements HostVertexPositionData {

    @HanaDeclareExtension(
        extensibleClass = MmdSurface.class,
        extensionClass = HostVertexPositionData.class)
    public HostVertexPositionData_(MmdSurface mmdSurface) {
      super(
          mmdSurface.getExtension(SbtmBaseMesh.Vv.class),
          positionAttributes,
          3 * 4,
          TriangleMeshInfo::getVertexCount,
          baseMesh -> {
            FloatBuffer buffer = ByteBuffer
                .allocateDirect(baseMesh.getVertexCount() * 3 * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
            Point3d p = new Point3d();
            for (int i = 0; i < baseMesh.getVertexCount(); i++) {
              baseMesh.getPosition(i, p);
              buffer.put(3 * i + 0, (float) p.x);
              buffer.put(3 * i + 1, (float) p.y);
              buffer.put(3 * i + 2, (float) p.z);
            }
            return buffer;
          });
    }
  }

  public static class GlVertexPositionProvider_
      extends HostVertexAttribDataToGlVertexAttribProviderAdaptor
      implements GlVertexPositionProvider {
    @HanaDeclareExtension(
        extensionClass = GlVertexPositionProvider.class,
        extensibleClass = MmdSurface.class)
    public GlVertexPositionProvider_(MmdSurface mmdSurface, GlObjectCache glObjectCache) {
      super(mmdSurface.getExtension(HostVertexPositionData.class), glObjectCache);
    }
  }

  public static class HostVertexNormalData_
      extends HostVertexAttribDataAdaptor<SbtmBaseMesh>
      implements HostVertexNormalData {

    @HanaDeclareExtension(
        extensibleClass = MmdSurface.class,
        extensionClass = HostVertexNormalData.class)
    public HostVertexNormalData_(MmdSurface mmdSurface) {
      super(
          mmdSurface.getExtension(SbtmBaseMesh.Vv.class),
          normalAttributes,
          3 * 4,
          TriangleMeshInfo::getVertexCount,
          baseMesh -> {
            FloatBuffer buffer = ByteBuffer
                .allocateDirect(baseMesh.getVertexCount() * 3 * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
            Point3d n = new Point3d();
            for (int i = 0; i < baseMesh.getVertexCount(); i++) {
              baseMesh.getNormal(i, n);
              buffer.put(3 * i + 0, (float) n.x);
              buffer.put(3 * i + 1, (float) n.y);
              buffer.put(3 * i + 2, (float) n.z);
            }
            return buffer;
          });
    }
  }

  public static class GlVertexNormalProvider_
      extends HostVertexAttribDataToGlVertexAttribProviderAdaptor
      implements GlVertexNormalProvider {
    @HanaDeclareExtension(
        extensionClass = GlVertexNormalProvider.class,
        extensibleClass = MmdSurface.class)
    public GlVertexNormalProvider_(MmdSurface mmdSurface, GlObjectCache glObjectCache) {
      super(mmdSurface.getExtension(HostVertexNormalData.class), glObjectCache);
    }
  }

  public static class HostVertexTexCoordData_
      extends HostVertexAttribDataAdaptor<SbtmBaseMesh>
      implements HostVertexTexCoordData {

    @HanaDeclareExtension(
        extensibleClass = MmdSurface.class,
        extensionClass = HostVertexTexCoordData.class)
    public HostVertexTexCoordData_(MmdSurface mmdSurface) {
      super(
          mmdSurface.getExtension(SbtmBaseMesh.Vv.class),
          texCoordAttributes,
          2 * 4,
          TriangleMeshInfo::getVertexCount,
          baseMesh -> {
            FloatBuffer buffer = ByteBuffer
                .allocateDirect(baseMesh.getVertexCount() * 2 * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
            Vector2d t = new Vector2d();
            for (int i = 0; i < baseMesh.getVertexCount(); i++) {
              baseMesh.getTexCoord(i, t);
              buffer.put(2 * i + 0, (float) t.x);
              buffer.put(2 * i + 1, (float) t.y);
            }
            return buffer;
          });
    }
  }

  public static class GlVertexTexCoordProvider_
      extends HostVertexAttribDataToGlVertexAttribProviderAdaptor
      implements GlVertexTexCoordProvider {
    @HanaDeclareExtension(
        extensionClass = GlVertexTexCoordProvider.class,
        extensibleClass = MmdSurface.class)
    public GlVertexTexCoordProvider_(MmdSurface mmdSurface, GlObjectCache glObjectCache) {
      super(mmdSurface.getExtension(HostVertexTexCoordData.class), glObjectCache);
    }
  }

  public static class HostVertexTangentData_
      extends HostVertexAttribDataAdaptor<SbtmBaseMesh>
      implements HostVertexTangentData {

    @HanaDeclareExtension(
        extensibleClass = MmdSurface.class,
        extensionClass = HostVertexTangentData.class)
    public HostVertexTangentData_(MmdSurface mmdSurface) {
      super(
          mmdSurface.getExtension(SbtmBaseMesh.Vv.class),
          tangentAttributes,
          4 * 4,
          TriangleMeshInfo::getVertexCount,
          baseMesh -> {
            FloatBuffer buffer = ByteBuffer
                .allocateDirect(baseMesh.getVertexCount() * 4 * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
            Vector4d t = new Vector4d();
            for (int i = 0; i < baseMesh.getVertexCount(); i++) {
              baseMesh.getTangent(i, t);
              buffer.put(4 * i + 0, (float) t.x);
              buffer.put(4 * i + 1, (float) t.y);
              buffer.put(4 * i + 2, (float) t.z);
              buffer.put(4 * i + 3, (float) t.w);
            }
            return buffer;
          });
    }
  }

  public static class GlVertexTangentProvider_
      extends HostVertexAttribDataToGlVertexAttribProviderAdaptor
      implements GlVertexTangentProvider {
    @HanaDeclareExtension(
        extensionClass = GlVertexTangentProvider.class,
        extensibleClass = MmdSurface.class)
    public GlVertexTangentProvider_(MmdSurface mmdSurface, GlObjectCache glObjectCache) {
      super(mmdSurface.getExtension(HostVertexTangentData.class), glObjectCache);
    }
  }

  public static Vector3f encodeVertexIndexToColor(int vertexIndex) {
    return new Vector3f(
        (float) ((vertexIndex % 256)),
        (float) (((vertexIndex / 256) % 256)),
        (float) (((vertexIndex / (256 * 256)) % 256)));
  }

  public static int decodeVertexIndexColor(Tuple3f color) {
    int low = Math.round(color.x);
    int mid = Math.round(color.y);
    int high = Math.round(color.z);
    return low + 256 * mid + 256 * 256 * high;
  }

  public static class HostVertexColorIndexData_ extends HostVertexAttribDataAdaptor<SbtmBaseMesh>
      implements HostVertexIndexColorData {

    @HanaDeclareExtension(
        extensibleClass = MmdSurface.class,
        extensionClass = HostVertexIndexColorData.class)
    public HostVertexColorIndexData_(MmdSurface surface) {
      super(
          surface.getExtension(SbtmBaseMesh.Vv.class),
          vertexIndexColorAttributes,
          4 * 3,
          TriangleMeshInfo::getVertexCount,
          sbtmBaseMesh -> {
            int vertexCount = sbtmBaseMesh.getVertexCount();
            FloatBuffer buffer =
                ByteBuffer.allocateDirect(vertexCount * 4 * 3).order(ByteOrder.nativeOrder()).asFloatBuffer();
            for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
              Vector3f color = encodeVertexIndexToColor(vertexIndex);
              buffer.put(3 * vertexIndex, color.x);
              buffer.put(3 * vertexIndex + 1, color.y);
              buffer.put(3 * vertexIndex + 2, color.z);
            }
            for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
              Vector3f color = new Vector3f(buffer.get(3*vertexIndex), buffer.get(3*vertexIndex+1), buffer.get(3*vertexIndex+2));
              int decoded = decodeVertexIndexColor(color);
              assert decoded == vertexIndex;
            }
            return buffer;
          }
      );
    }
  }

  public static class GlVertexIndexColorProvider_
      extends HostVertexAttribDataToGlVertexAttribProviderAdaptor
      implements GlVertexIndexColorProvider {
    @HanaDeclareExtension(
        extensionClass = GlVertexIndexColorProvider.class,
        extensibleClass = MmdSurface.class)
    public GlVertexIndexColorProvider_(MmdSurface mmdSurface, GlObjectCache glObjectCache) {
      super(mmdSurface.getExtension(HostVertexIndexColorData.class), glObjectCache);
    }
  }

  public static class HostVertexMorphStartAndCountData_
      extends HostVertexAttribDataAdaptor<SbtmBaseMesh>
      implements HostVertexMorphStartAndCountData {

    @HanaDeclareExtension(
        extensibleClass = MmdSurface.class,
        extensionClass = HostVertexMorphStartAndCountData.class)
    public HostVertexMorphStartAndCountData_(MmdSurface surface) {
      super(
          surface.getExtension(SbtmBaseMesh.Vv.class),
          morphStartAndCountAttributes,
          4 * 2,
          TriangleMeshInfo::getVertexCount,
          sbtmBaseMesh -> {
            int vertexCount = sbtmBaseMesh.getVertexCount();
            FloatBuffer buffer =
                ByteBuffer.allocateDirect(vertexCount * 4 * 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
            int start = 0;
            for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
              buffer.put(2 * vertexIndex, (float) start);
              int morphCount = sbtmBaseMesh.getVertexMorphCount(vertexIndex);
              buffer.put(2 * vertexIndex + 1, (float) morphCount);
              start += morphCount;
            }
            return buffer;
          });
    }
  }

  public static class GlVertexMorphStartAndCountProvider_
      extends HostVertexAttribDataToGlVertexAttribProviderAdaptor
      implements GlVertexMorphStartAndCountProvider {
    @HanaDeclareExtension(
        extensibleClass = MmdSurface.class,
        extensionClass = GlVertexMorphStartAndCountProvider.class)
    public GlVertexMorphStartAndCountProvider_(MmdSurface mmdSurface, GlObjectCache glObjectCache) {
      super(mmdSurface.getExtension(HostVertexMorphStartAndCountData.class), glObjectCache);
    }
  }

  public static class HostVertexBoneIndexData_
      extends HostVertexAttribDataAdaptor<SbtmBaseMesh>
      implements HostVertexBoneIndexData {

    @HanaDeclareExtension(
        extensibleClass = MmdSurface.class,
        extensionClass = HostVertexBoneIndexData.class)
    public HostVertexBoneIndexData_(MmdSurface mmdSurface) {
      super(
          mmdSurface.getExtension(SbtmBaseMesh.Vv.class),
          boneIndexAttributes,
          4 * 4,
          TriangleMeshInfo::getVertexCount,
          sbtmBaseMesh -> {
            int vertexCount = sbtmBaseMesh.getVertexCount();
            FloatBuffer buffer =
                ByteBuffer.allocateDirect(vertexCount * 4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
              int vertexBoneCount = sbtmBaseMesh.getVertexBoneCount(vertexIndex);
              for (int vertexBoneIndex = 0; vertexBoneIndex < Math.min(vertexBoneCount, 4); vertexBoneIndex++) {
                int boneIndex = sbtmBaseMesh.getVertexBoneIndex(vertexIndex, vertexBoneIndex);
                buffer.put(4 * vertexIndex + vertexBoneIndex, (float) boneIndex);
              }
              for (int j = Math.min(vertexBoneCount, 4); j < 4; j++) {
                buffer.put(4 * vertexIndex + j, -1);
              }
            }
            return buffer;
          });
    }
  }

  public static class GlVertexBoneIndexProvider_
      extends HostVertexAttribDataToGlVertexAttribProviderAdaptor
      implements GlVertexBoneIndexProvider {
    @HanaDeclareExtension(
        extensibleClass = MmdSurface.class,
        extensionClass = GlVertexBoneIndexProvider.class)
    public GlVertexBoneIndexProvider_(MmdSurface mmdSurface, GlObjectCache glObjectCache) {
      super(mmdSurface.getExtension(HostVertexBoneIndexData.class), glObjectCache);
    }
  }

  public static class HostVertexBoneWeightData_
      extends HostVertexAttribDataAdaptor<SbtmBaseMesh>
      implements HostVertexBoneWeightData {

    @HanaDeclareExtension(
        extensibleClass = MmdSurface.class,
        extensionClass = HostVertexBoneWeightData.class)
    public HostVertexBoneWeightData_(MmdSurface mmdSurface) {
      super(
          mmdSurface.getExtension(SbtmBaseMesh.Vv.class),
          boneWeightAttributes,
          4 * 4,
          TriangleMeshInfo::getVertexCount,
          sbtmBaseMesh -> {
            int vertexCount = sbtmBaseMesh.getVertexCount();
            FloatBuffer buffer =
                ByteBuffer.allocateDirect(vertexCount * 4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
              int vertexBoneCount = sbtmBaseMesh.getVertexBoneCount(vertexIndex);
              for (int vertexBoneIndex = 0; vertexBoneIndex < Math.min(vertexBoneCount, 4); vertexBoneIndex++) {
                double boneWeight = sbtmBaseMesh.getVertexBoneWeight(vertexIndex, vertexBoneIndex);
                buffer.put(4 * vertexIndex + vertexBoneIndex, (float) boneWeight);
              }
              for (int j = Math.min(vertexBoneCount, 4); j < 4; j++) {
                buffer.put(4 * vertexIndex + j, 0.0f);
              }
            }
            return buffer;
          });
    }
  }

  public static class GlVertexBoneWeightProvider_
      extends HostVertexAttribDataToGlVertexAttribProviderAdaptor
      implements GlVertexBoneWeightProvider {
    @HanaDeclareExtension(
        extensibleClass = MmdSurface.class,
        extensionClass = GlVertexBoneWeightProvider.class)
    public GlVertexBoneWeightProvider_(MmdSurface mmdSurface, GlObjectCache glObjectCache) {
      super(mmdSurface.getExtension(HostVertexBoneWeightData.class), glObjectCache);
    }
  }

  public static class HostVertexSkinningInfoData_
      extends HostVertexAttribDataAdaptor<SbtmBaseMesh>
      implements HostVertexSkinningInfoData {
    @HanaDeclareExtension(
        extensibleClass = MmdSurface.class,
        extensionClass = HostVertexSkinningInfoData.class)
    public HostVertexSkinningInfoData_(MmdSurface mmdSurface) {
      super(
          mmdSurface.getExtension(SbtmBaseMesh.Vv.class),
          skinningInfoAttributes,
          4 * 2,
          TriangleMeshInfo::getVertexCount,
          sbtmBaseMesh -> {
            int vertexCount = sbtmBaseMesh.getVertexCount();
            FloatBuffer buffer =
                ByteBuffer.allocateDirect(vertexCount * 4 * 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
            int sdefVertexIndex = 0;
            for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
              SbtmSkinningType skinningType = sbtmBaseMesh.getVertexSkinningType(vertexIndex);
              buffer.put(2 * vertexIndex, (float) skinningType.value);
              if (skinningType.equals(SbtmSkinningType.SDEF)) {
                buffer.put(2 * vertexIndex + 1, (float) sdefVertexIndex);
                sdefVertexIndex++;
              } else {
                buffer.put(2 * vertexIndex + 1, 0.0f);
              }
            }
            return buffer;
          });
    }
  }

  public static class GlVertexSkinningInfoProvider_
      extends HostVertexAttribDataToGlVertexAttribProviderAdaptor
      implements GlVertexSkinningInfoProvider {
    @HanaDeclareExtension(
        extensibleClass = MmdSurface.class,
        extensionClass = GlVertexSkinningInfoProvider.class)
    public GlVertexSkinningInfoProvider_(MmdSurface mmdSurface, GlObjectCache glObjectCache) {
      super(mmdSurface.getExtension(HostVertexSkinningInfoData.class), glObjectCache);
    }
  }

  public static class GlMorphDisplacementTextureProvider_ implements GlMorphDisplacementTextureProvider {
    private MmdSurface mmdSurface;
    private VersionedValue<SbtmBaseMesh> sbtmBaseMesh;
    private GlObjectCache glObjectCache;
    private final int textureWidth = MORPH_DISPLACEMENT_TEXTURE_WIDTH;
    private int textureHeight = 0;

    @HanaDeclareExtension(
        extensibleClass = MmdSurface.class,
        extensionClass = GlMorphDisplacementTextureProvider.class)
    public GlMorphDisplacementTextureProvider_(MmdSurface mmdSurface, GlObjectCache glObjectCache) {
      this.mmdSurface = mmdSurface;
      this.sbtmBaseMesh = mmdSurface.getExtension(SbtmBaseMesh.Vv.class);
      this.glObjectCache = glObjectCache;
    }

    @Override
    public void updateGlResource(GlObjectRecord record) {
      sbtmBaseMesh.update();
      GlTextureRect texture = null;
      boolean needUpdate = false;
      if (record.resource == null) {
        texture = glObjectCache.getGlWrapper().createTextureRect(GlConstants.GL_RGBA32F);
        record.resource = texture;
        needUpdate = true;
      } else if (record.version != sbtmBaseMesh.version()) {
        texture = (GlTextureRect) record.resource;
        needUpdate = true;
      }
      if (needUpdate) {
        updateTexture(texture);
        record.version = sbtmBaseMesh.version();
        record.sizeInBytes = getTextureSizeInByte();
      }
    }

    public void updateTexture(GlTextureRect texture) {
      SbtmBaseMesh mesh = sbtmBaseMesh.value();
      int meshMorphCount = 0;
      for (int i = 0; i < mesh.getVertexCount(); i++) {
        meshMorphCount += mesh.getVertexMorphCount(i);
      }
      int textureHeightAddition = (meshMorphCount % textureWidth > 0) ? 1 : 0;
      textureHeight = Math.max(1, meshMorphCount / textureWidth + textureHeightAddition);
      int bufferSizeInBytes = textureWidth * textureHeight * 4 * 4;
      FloatBuffer buffer = ByteBuffer.allocateDirect(bufferSizeInBytes).order(ByteOrder.nativeOrder()).asFloatBuffer();
      Vector3d d = new Vector3d();
      int index = 0;
      for (int i = 0; i < mesh.getVertexCount(); i++) {
        int vertexMorphCount = mesh.getVertexMorphCount(i);
        for (int j = 0; j < vertexMorphCount; j++) {
          mesh.getVertexMorphDisplacement(i, j, d);
          buffer.put(4 * index, (float) d.x);
          buffer.put(4 * index + 1, (float) d.y);
          buffer.put(4 * index + 2, (float) d.z);
          int morphIndex = mesh.getMorphIndex(i, j);
          buffer.put(4 * index + 3, (float) morphIndex);
          index++;
        }
      }
      texture.setData(textureWidth, textureHeight, GlConstants.GL_RGBA, GlConstants.GL_FLOAT, buffer);
    }

    private int getTextureSizeInByte() {
      return textureWidth * textureHeight * 4 * 4;
    }

    @Override
    public GlTextureRect getGlObject() {
      return (GlTextureRect) glObjectCache.getGLResource(this);
    }
  }

  public static class GlBoneTransformTextureProvider_ implements GlBoneTransformTextureProvider {
    private final MmdSurface mmdSurface;
    private final VersionedValue<SbtmBaseMesh> sbtmBaseMesh;
    private final VersionedValue<SbtmPose> sbtmPose;
    private final GlObjectCache glObjectCache;
    private final int textureWidth = BONE_TRANSFORM_TEXTURE_WIDTH;
    private int textureHeight = 1;

    @HanaDeclareExtension(
        extensibleClass = MmdSurface.class,
        extensionClass = GlBoneTransformTextureProvider.class)
    public GlBoneTransformTextureProvider_(MmdSurface mmdSurface, GlObjectCache glObjectCache) {
      this.mmdSurface = mmdSurface;
      this.glObjectCache = glObjectCache;
      sbtmBaseMesh = mmdSurface.getExtension(SbtmBaseMesh.Vv.class);
      sbtmPose = mmdSurface.getExtension(SbtmPoseVv.class);
    }

    @Override
    public void updateGlResource(GlObjectRecord record) {
      sbtmBaseMesh.update();
      sbtmPose.update();
      long newVersion = Math.max(sbtmBaseMesh.version(), sbtmPose.version());
      GlTextureRect texture = null;
      boolean needUpdate = false;
      if (record.resource == null) {
        texture = glObjectCache.getGlWrapper().createTextureRect(GlConstants.GL_RGBA32F);
        record.resource = texture;
        needUpdate = true;
      } else if (record.version != newVersion) {
        texture = (GlTextureRect) record.resource;
        needUpdate = true;
      }
      if (needUpdate) {
        updateTexture(texture);
        record.version = newVersion;
        record.sizeInBytes = getTextureSizeInByte();
      }
    }

    private int getTextureSizeInByte() {
      return textureWidth * textureHeight * 4 * 4;
    }

    public void updateTexture(GlTextureRect texture) {
      SbtmBaseMesh mesh = sbtmBaseMesh.value();
      SbtmPose pose = sbtmPose.value();
      List<Matrix4d> boneMatrices = mesh.getPosedBoneMatricesForBlending(pose);

      int boneCount = mesh.getBoneCount();
      int pixelCount = boneCount * 2;

      int textureHeightAddition = (pixelCount % textureWidth > 0) ? 1 : 0;
      textureHeight = Math.max(1, pixelCount / textureWidth + textureHeightAddition);
      int bufferSizeInBytes = textureWidth * textureHeight * 4 * 4;
      FloatBuffer buffer = ByteBuffer.allocateDirect(bufferSizeInBytes).order(ByteOrder.nativeOrder()).asFloatBuffer();

      for (int i = 0; i < boneCount; i++) {
        Matrix4d m = boneMatrices.get(i);

        Quat4d q = Matrix4dUtil.rotationPartToQuaternion(m);

        buffer.put(8 * i, (float) q.x);
        buffer.put(8 * i + 1, (float) q.y);
        buffer.put(8 * i + 2, (float) q.z);
        buffer.put(8 * i + 3, (float) q.w);

        buffer.put(8 * i + 4, (float) m.m03);
        buffer.put(8 * i + 5, (float) m.m13);
        buffer.put(8 * i + 6, (float) m.m23);
        buffer.put(8 * i + 7, 1.0f);
      }
      texture.setData(textureWidth, textureHeight, GlConstants.GL_RGBA, GlConstants.GL_FLOAT, buffer);
    }

    @Override
    public GlTextureRect getGlObject() {
      return (GlTextureRect) glObjectCache.getGLResource(this);
    }
  }

  public static class GlMorphWeightTextureProvider_ implements GlMorphWeightTextureProvider {
    private final MmdSurface mmdSurface;
    private final GlObjectCache glObjectCache;
    private final VersionedValue<SbtmBaseMesh> sbtmBaseMesh;
    private final VersionedValue<SbtmPose> sbtmPose;
    private final int textureWidth = MORPH_WEIGHT_TEXTURE_WIDTH;
    private int textureHeight;

    @HanaDeclareExtension(
        extensibleClass = MmdSurface.class,
        extensionClass = GlMorphWeightTextureProvider.class)
    public GlMorphWeightTextureProvider_(MmdSurface mmdSurface, GlObjectCache glObjectCache) {
      this.mmdSurface = mmdSurface;
      this.glObjectCache = glObjectCache;
      this.sbtmBaseMesh = mmdSurface.getExtension(SbtmBaseMesh.Vv.class);
      this.sbtmPose = mmdSurface.getExtension(SbtmPoseVv.class);
    }

    @Override
    public void updateGlResource(GlObjectRecord record) {
      sbtmBaseMesh.update();
      sbtmPose.update();
      long newVersion = Math.max(sbtmBaseMesh.version(), sbtmPose.version());
      GlTextureRect texture = null;
      boolean needUpdate = false;
      if (record.resource == null) {
        texture = glObjectCache.getGlWrapper().createTextureRect(GlConstants.GL_R32F);
        record.resource = texture;
        needUpdate = true;
      } else if (record.version != newVersion) {
        texture = (GlTextureRect) record.resource;
        needUpdate = true;
      }
      if (needUpdate) {
        updateTexture(texture);
        record.version = newVersion;
        record.sizeInBytes = getTextureSizeInByte();
      }
    }

    private void updateTexture(GlTextureRect textureRect) {
      SbtmBaseMesh mesh = sbtmBaseMesh.value();
      SbtmPose pose = sbtmPose.value();
      int morphCount = mesh.getMorphCount();
      int textureHeightAddition = (morphCount % textureWidth > 0) ? 1 : 0;
      textureHeight = morphCount / textureWidth + textureHeightAddition;

      FloatBuffer buffer = ByteBuffer
          .allocateDirect(textureWidth * textureHeight * 4)
          .order(ByteOrder.nativeOrder())
          .asFloatBuffer();
      for (int i = 0; i < morphCount; i++) {
        String morphName = mesh.getMorph(i).getName();
        double weight = pose.morphPoses.getOrDefault(morphName, 0.0);
        buffer.put(i, pose.morphPoses.getOrDefault(mesh.getMorph(i).getName(), 0.0).floatValue());
      }
      textureRect.setData(textureWidth, textureHeight, GlConstants.GL_RED, GlConstants.GL_FLOAT, buffer);
    }

    private int getTextureSizeInByte() {
      return textureWidth * textureHeight * 4;
    }

    @Override
    public GlTextureRect getGlObject() {
      return (GlTextureRect) glObjectCache.getGLResource(this);
    }
  }

  public static class GlSdefParamsTextureProvider_ implements GlSdefParamsTextureProvider {
    private final MmdSurface mmdSurface;
    private final GlObjectCache glObjectCache;
    private final VersionedValue<SbtmBaseMesh> sbtmBaseMesh;
    private final int textureWidth = SDEF_PARAMS_TEXTURE_WIDTH;
    private int textureHeight;

    @HanaDeclareExtension(
        extensibleClass = MmdSurface.class,
        extensionClass = GlSdefParamsTextureProvider.class)
    public GlSdefParamsTextureProvider_(MmdSurface mmdSurface, GlObjectCache glObjectCache) {
      this.mmdSurface = mmdSurface;
      this.glObjectCache = glObjectCache;
      this.sbtmBaseMesh = mmdSurface.getExtension(SbtmBaseMesh.Vv.class);
    }

    @Override
    public void updateGlResource(GlObjectRecord record) {
      sbtmBaseMesh.update();
      long newVersion = sbtmBaseMesh.version();
      GlTextureRect texture = null;
      boolean needUpdate = false;
      if (record.resource == null) {
        texture = glObjectCache.getGlWrapper().createTextureRect(GlConstants.GL_RGB32F);
        record.resource = texture;
        needUpdate = true;
      } else if (record.version != newVersion) {
        texture = (GlTextureRect) record.resource;
        needUpdate = true;
      }
      if (needUpdate) {
        updateTexture(texture);
        record.version = newVersion;
        record.sizeInBytes = getTextureSizeInByte();
      }
    }

    private void updateTexture(GlTextureRect texture) {
      SbtmBaseMesh mesh = sbtmBaseMesh.value();
      int sdefVertexCount = 0;
      for (int vertexIndex = 0; vertexIndex < mesh.getVertexCount(); vertexIndex++) {
        sdefVertexCount += mesh.getVertexSkinningType(vertexIndex).equals(SbtmSkinningType.SDEF) ? 1 : 0;
      }
      int pixelCount = sdefVertexCount * 3;
      int textureHeightIncrement = (pixelCount % textureWidth == 0) ? 0 : 1;
      textureHeight = pixelCount / textureWidth + textureHeightIncrement;

      FloatBuffer buffer = ByteBuffer.allocateDirect(getTextureSizeInByte())
          .order(ByteOrder.nativeOrder())
          .asFloatBuffer();
      int sdefVertexIndex = 0;
      for (int vertexIndex = 0; vertexIndex < mesh.getVertexCount(); vertexIndex++) {
        Optional<SbtmSdefParams> optionalParams = mesh.getSdefParams(vertexIndex);
        if (optionalParams.isPresent()) {
          SbtmSdefParams params = optionalParams.get();
          buffer.put(9 * sdefVertexIndex, (float) params.C.x);
          buffer.put(9 * sdefVertexIndex + 1, (float) params.C.y);
          buffer.put(9 * sdefVertexIndex + 2, (float) params.C.z);
          buffer.put(9 * sdefVertexIndex + 3, (float) params.R0.x);
          buffer.put(9 * sdefVertexIndex + 4, (float) params.R0.y);
          buffer.put(9 * sdefVertexIndex + 5, (float) params.R0.z);
          buffer.put(9 * sdefVertexIndex + 6, (float) params.R1.x);
          buffer.put(9 * sdefVertexIndex + 7, (float) params.R1.y);
          buffer.put(9 * sdefVertexIndex + 8, (float) params.R1.z);
          sdefVertexIndex++;
        }
      }

      texture.setData(textureWidth, textureHeight, GlConstants.GL_RGB, GlConstants.GL_FLOAT, buffer);
    }

    private int getTextureSizeInByte() {
      return textureWidth * textureHeight * 4 * 3;
    }

    @Override
    public GlTextureRect getGlObject() {
      return (GlTextureRect) glObjectCache.getGLResource(this);
    }
  }

  public static class HostIndexData_ extends AbstractDerivedSubject implements HostIndexData {
    private final VersionedValue<SbtmBaseMesh> baseMeshVv;

    @HanaDeclareExtension(
        extensibleClass = MmdSurface.class,
        extensionClass = HostIndexData.class)
    public HostIndexData_(MmdSurface mmdSurface) {
      this.baseMeshVv = mmdSurface.getExtension(SbtmBaseMesh.Vv.class);
      this.baseMeshVv.addObserver(this);
    }

    @Override
    protected long updateInternal() {
      baseMeshVv.update();
      return Math.max(version() + 1, baseMeshVv.version());
    }

    @Override
    public int getIndexCount() {
      return baseMeshVv.value().getTriangleCount() * 3;
    }

    @Override
    public Buffer getBuffer() {
      TriangleMeshInfo triMesh = baseMeshVv.value();
      int triangelCount = triMesh.getTriangleCount();
      IntBuffer buffer = ByteBuffer.allocateDirect(triangelCount * 3 * 4)
          .order(ByteOrder.nativeOrder()).asIntBuffer();
      Point3i tri = new Point3i();
      for (int i = 0; i < triangelCount; i++) {
        triMesh.getTriangleVertexIndices(i, tri);
        buffer.put(3 * i + 0, tri.z);
        buffer.put(3 * i + 1, tri.y);
        buffer.put(3 * i + 2, tri.x);
      }
      return buffer;
    }

    @Override
    public int getBufferSizeInByte() {
      return getIndexCount() * 4;
    }
  }

  public static class GlIndexProvider_ extends HostIndexDataToGlIndexProviderAdaptor {
    @HanaDeclareExtension(
        extensionClass = GlIndexProvider.class,
        extensibleClass = MmdSurface.class)
    public GlIndexProvider_(MmdSurface mmdSurface, GlObjectCache glObjectCache) {
      super(mmdSurface.getExtension(HostIndexData.class), glObjectCache);
    }
  }

  public static class Wireframe {
    private final MmdSurfaceWireframe mmdSurfaceWireframe;

    @HanaDeclareExtension(
        extensibleClass = MmdSurface.class,
        extensionClass = Wireframe.class)
    Wireframe(MmdSurface mmdSurface, Provider<MmdSurfaceWireframeBuilder> mmdSurfaceWireframeBuilder) {
      mmdSurfaceWireframe = mmdSurfaceWireframeBuilder.get().mmdSurface(mmdSurface).build();
    }

    public MmdSurfaceWireframe get() {
      return mmdSurfaceWireframe;
    }
  }

  public static class VertexPoints {
    private final MmdSurfaceVertexPoints mmdSurfaceVertexPoints;

    @HanaDeclareExtension(
        extensibleClass = MmdSurface.class,
        extensionClass = VertexPoints.class)
    VertexPoints(MmdSurface mmdSurface,
        Provider<MmdSurfaceVertexPointsBuilder> mmdSurfaceVertexPointsBuilderProvider) {
      mmdSurfaceVertexPoints = mmdSurfaceVertexPointsBuilderProvider.get().mmdSurface(mmdSurface).build();
    }

    public MmdSurfaceVertexPoints get() {
      return mmdSurfaceVertexPoints;
    }
  }

  public static class VertexPointsForPicking {
    private final MmdSurfaceVertexPointsForPicking mmdSurfaceVertexPointsForPicking;

    @HanaDeclareExtension(
        extensibleClass = MmdSurface.class,
        extensionClass = VertexPointsForPicking.class)
    VertexPointsForPicking(MmdSurface mmdSurface,
        Provider<MmdSurfaceVertexPointsForPickingBuilder> builder) {
      mmdSurfaceVertexPointsForPicking = builder.get().mmdSurface(mmdSurface).build();
    }

    public MmdSurfaceVertexPointsForPicking get() {
      return mmdSurfaceVertexPointsForPicking;
    }
  }

  public static class MmdSurfaceRenderer00_ extends AbstractMmdSurfaceRender00Receiver implements MmdSurfaceRenderer00 {
    private final Wrapped<ProgramAsset> mainMeshRenderProgram;
    private final Wrapped<ProgramAsset> edgeRenderProgram;

    @HanaDeclareExtension(
        extensibleClass = MmdSurface.class,
        extensionClass = MmdSurfaceRenderer00.class)
    public MmdSurfaceRenderer00_(MmdSurface mesh, HanaUnwrapper unwrapper, GlWrapper glWrapper) {
      super(mesh, unwrapper, glWrapper);
      this.mainMeshRenderProgram = new Cached<>(CacheKey.builder()
          .protocol(ResourceProgramAssetExtensions.PROTOCOL)
          .addStringPart(Constants.MMD_POSED_MESH_VERT_RESOURCE_NAME)
          .addStringPart(Constants.MMD_POSED_MESH_FRAG_RESOURCE_NAME)
          .build());
      this.edgeRenderProgram = new Cached<>(CacheKey.builder()
          .protocol(ResourceProgramAssetExtensions.PROTOCOL)
          .addStringPart(Constants.MMD_POSED_MESH_EDGE_VERT_RESOURCE_NAME)
          .addStringPart(Constants.EDGE_COLOR_FRAG_RESOURCE_NAME)
          .build());
    }

    @Override
    protected Wrapped<ProgramAsset> getProgramAsset() {
      return this.mainMeshRenderProgram;
    }

    @Override
    public void render(
        Renderer00 renderer00,
        Supplier<Stream<Integer>> materialIndexStreamSupplier) {
      super.render(renderer00, materialIndexStreamSupplier.get());

      this.patchIntervalInfo.update();
      final SurfacePatchIntervalInfo info = patchIntervalInfo.value();
      edgeRenderProgram.unwrap(unwrapper).getExtension(GlProgramProvider.class).getGlObject().use(glProgram -> {
        setMatrixUniforms(renderer00, glProgram);
        renderer00.vao.use(vao -> {
          setVertexAttributes(renderer00, glProgram);
          indexProvider.getGlObject().use(vbo -> {
            materialIndexStreamSupplier.get().forEach(materialIndex -> {
              renderPatchIntervalEdge(renderer00, glProgram, info.getPatchInterval(materialIndex));
            });
          });
          renderer00.unuseAllTextureUnits();
        });
      });
    }

    private void renderPatchIntervalEdge(
        Renderer00 renderer00,
        GlProgram glProgram,
        PatchInterval patchInterval) {
      Bsdf bsdf = unwrapper.unwrap(patchInterval.bsdf());
      if (!(bsdf instanceof PmxBsdf)) {
        return;
      }
      PmxBsdf pmxBsdf = (PmxBsdf) bsdf;
      if (!pmxBsdf.drawEdge().value()) {
        return;
      }
      if (glProgram.hasUniform("mat_edgeSize")) {
        double edgeFactor = renderer00.getBinding("mat_edgeFactor", Double.class);
        glProgram.getUniform("mat_edgeSize").set1Float(pmxBsdf.edgeThickness().value() * edgeFactor);
      }
      if (glProgram.hasUniform("mat_edgeColor")) {
        Rgb edgeColor = pmxBsdf.edgeColor().value();
        glProgram.getUniform("mat_edgeColor").set3Float(edgeColor.x, edgeColor.y, edgeColor.z);
      }
      glWrapper.setFrontFace(GlConstants.GL_CW);
      renderPatch(patchInterval);
      glWrapper.setFrontFace(GlConstants.GL_CCW);
    }
  }
}
