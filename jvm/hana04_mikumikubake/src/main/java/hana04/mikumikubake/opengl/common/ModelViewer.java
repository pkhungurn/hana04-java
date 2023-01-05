package hana04.mikumikubake.opengl.common;

import hana04.base.caching.CacheKey;
import hana04.base.caching.Cached;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.caching.Wrapped;
import hana04.botan.cache.GlObjectCache;
import hana04.botan.glasset.program.ProgramAsset;
import hana04.botan.glasset.program.ResourceProgramAssetExtensions;
import hana04.botan.glasset.provider.GlIndexProvider;
import hana04.botan.glasset.provider.GlProgramProvider;
import hana04.gfxbase.gfxtype.Transform;
import hana04.mikumikubake.mmd.MmdModelManager;
import hana04.mikumikubake.opengl.renderable.mmdsurface.MmdSurfaceRenderer00;
import hana04.mikumikubake.opengl.renderable.shaders.Constants;
import hana04.mikumikubake.opengl.renderable.ugposcolmesh.UgPosColMesh;
import hana04.mikumikubake.opengl.renderable.ugposcolmesh.UgPosColMeshBuilder;
import hana04.mikumikubake.opengl.renderable.ugposcolmesh.UgPosColMeshExtensions;
import hana04.mikumikubake.opengl.renderable.ugpostexmesh.UgPosTexMesh;
import hana04.mikumikubake.opengl.renderable.ugpostexmesh.UgPosTexMeshBuilder;
import hana04.mikumikubake.opengl.renderable.ugpostexmesh.UgPosTexMeshExtensions;
import hana04.mikumikubake.opengl.renderer00.Renderer00;
import hana04.mikumikubake.opengl.renderer00.Renderer00Factory;
import hana04.mikumikubake.opengl.renderer00.Renderer00Receiver;
import hana04.mikumikubake.opengl.renderer00.camera.ui.CameraControl;
import hana04.mikumikubake.opengl.renderer00.extensions.GlPrimitiveTypeProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexPositionProvider;
import hana04.mikumikubake.opengl.renderer00.extensions.GlVertexTexCoordProvider;
import hana04.opengl.util.GlTextureRectBufferCollection;
import hana04.opengl.wrapper.GlConstants;
import hana04.opengl.wrapper.GlProgram;
import hana04.opengl.wrapper.GlTextureRect;
import hana04.opengl.wrapper.GlTextureUnit;
import hana04.opengl.wrapper.GlVbo;
import hana04.opengl.wrapper.GlWrapper;
import hana04.shakuyaku.surface.mmd.mmd.MmdSurface;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import javax.inject.Provider;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.IntBuffer;
import java.nio.file.FileSystem;
import java.util.Optional;
import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.GLFW_BLUE_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_DEPTH_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_GREEN_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RED_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.GLFW_STENCIL_BITS;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.NULL;

public abstract class ModelViewer implements ActionListener {
  protected static final double RENDER_INTERVAL_SECONDS = 0.01;
  protected static final double GARBAGE_COLLECT_INTERVAL_SECONDS = 2.0;
  public static final double EDGE_FACTOR = 0.01;

  protected long glWindowId;
  protected int screenWidth;
  protected int screenHeight;

  protected final GlWrapper glWrapper;
  protected final GlObjectCache glObjectCache;
  protected final HanaUnwrapper unwrapper;
  protected final Provider<UgPosColMeshBuilder> ugPosColMeshBuilder;
  protected final Renderer00 renderer00;
  protected final FileSystem fileSystem;

  public CameraControl<?> cameraControl;

  protected final MmdModelManager mmdModelManager;
  protected String currentModelFilePath = "";

  // OpenGL Data
  private UgPosColMesh floor;
  private UgPosTexMesh fullScreenQuad;
  protected GlTextureRectBufferCollection screenBuffers;
  protected GlTextureRectBufferCollection accumulationBuffers;
  private Wrapped<ProgramAsset> copyProgram =
      new Cached<>(CacheKey.builder()
          .protocol(ResourceProgramAssetExtensions.PROTOCOL)
          .addStringPart(Constants.FULL_SCREEN_QUAD_VERT_RESOURCE_NAME)
          .addStringPart(Constants.COPY_TEXTURE_RECT_FRAG_RESOURCE_NAME)
          .build());
  private Wrapped<ProgramAsset> srgbProgram =
      new Cached<>(CacheKey.builder()
          .protocol(ResourceProgramAssetExtensions.PROTOCOL)
          .addStringPart(Constants.FULL_SCREEN_QUAD_VERT_RESOURCE_NAME)
          .addStringPart(Constants.SRGB_FRAG_RESOURCE_NAME)
          .build());
  private Wrapped<ProgramAsset> scaleProgram =
      new Cached<>(CacheKey.builder()
          .protocol(ResourceProgramAssetExtensions.PROTOCOL)
          .addStringPart(Constants.FULL_SCREEN_QUAD_VERT_RESOURCE_NAME)
          .addStringPart(Constants.SCALE_FRAG_RESOURCE_NAME)
          .build());

  protected ModelViewer(
      long glWindowId,
      GlWrapper glWrapper,
      GlObjectCache glObjectCache,
      HanaUnwrapper unwrapper,
      Renderer00Factory renderer00Factory,
      MmdModelManager mmdModelManager,
      Provider<UgPosColMeshBuilder> ugPosColMeshBuilder,
      Provider<UgPosTexMeshBuilder> ugPosTexMeshBuilder,
      FileSystem fileSystem,
      CameraControl<?> cameraControl) {
    this.glWindowId = glWindowId;
    this.glWrapper = glWrapper;
    this.glObjectCache = glObjectCache;
    glObjectCache.setCapacity((1L << 20) * 4096);
    this.unwrapper = unwrapper;
    this.ugPosColMeshBuilder = ugPosColMeshBuilder;
    this.renderer00 = renderer00Factory.create();
    this.mmdModelManager = mmdModelManager;
    this.mmdModelManager.setPhysicsEnabled(true);
    this.screenBuffers = new GlTextureRectBufferCollection(glWrapper, 1, true);
    this.accumulationBuffers = new GlTextureRectBufferCollection(glWrapper, 1, false);
    this.fullScreenQuad = ugPosTexMeshBuilder.get().build();
    this.fileSystem = fileSystem;
    this.cameraControl = cameraControl;
  }

  public static void initGlfw() {
    GLFWErrorCallback.createPrint(System.err).set();
    if (!glfwInit()) {
      System.err.println("Could not initialize GLFW");
    }
  }

  public static long initGlfwWindow(
      int initialWindowWidth,
      int initialWindowHeight,
      String initialTitle) {
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

    glfwWindowHint(GLFW_SAMPLES, 1);
    glfwWindowHint(GLFW_RED_BITS, 32);
    glfwWindowHint(GLFW_GREEN_BITS, 32);
    glfwWindowHint(GLFW_BLUE_BITS, 32);
    glfwWindowHint(GLFW_DEPTH_BITS, 24);
    glfwWindowHint(GLFW_STENCIL_BITS, 8);

    glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

    long glWindowId = glfwCreateWindow(initialWindowWidth, initialWindowHeight, initialTitle, NULL, NULL);

    if (glWindowId == NULL) {
      throw new RuntimeException("Could not create GLFW window");
    }

    glfwMakeContextCurrent(glWindowId);
    GL.createCapabilities();
    glfwSwapInterval(1);
    return glWindowId;
  }

  public void run() {
    try {
      initCamera();
      initUiFrames();
      initInteractionCallbacks();
      initMeshData();

      while (!glfwWindowShouldClose(glWindowId)) {
        render();
      }

      cleanUpBeforeTermination();
    } finally {
      if (glWindowId != NULL) {
        glfwDestroyWindow(glWindowId);
      }
      disposeUiFrames();
      glfwTerminate();
    }
  }

  protected void cleanUpBeforeTermination() {
    // NO-OP
  }

  protected abstract void disposeUiFrames();

  protected void initCamera() {
    cameraControl.updateCameraUi();
  }

  protected abstract void initUiFrames();

  protected abstract void initInteractionCallbacks();

  protected void initMeshData() {
    initFloorData();
  }

  protected void initFloorData() {
    floor = ugPosColMeshBuilder.get().build();
    UgPosColMeshExtensions.HostData.Builder builder = floor.getExtension(UgPosColMeshExtensions.HostData.class).startBuild();
    builder.setPrimitiveType(GlConstants.GL_LINES);
    builder.setColor(0.0, 0.0, 0.0, 1.0);
    int count = 0;
    for (int pos = -20; pos <= 20; pos++) {
      builder.addPosition(pos * 5, 0, -100);
      builder.addPosition(pos * 5, 0, 100);
      count += 2;
      builder.addIndex(count - 2);
      builder.addIndex(count - 1);

      builder.addPosition(-100, 0, pos * 5);
      builder.addPosition(100, 0, pos * 5);
      count += 2;
      builder.addIndex(count - 2);
      builder.addIndex(count - 1);
    }
    builder.endBuild();
  }

  private boolean renderTimingStarted = false;
  private long lastRenderTime;
  private boolean garbageCollectTimingStarted = false;
  private long lastGarbageCollectTime;

  protected abstract void updateModel(double elapsedSeconds);

  protected double getRenderIntervalSeconds() {
    return RENDER_INTERVAL_SECONDS;
  }

  protected double computeElapsedTimeSinceLastRender() {
    long currentTime = System.nanoTime();
    long elaspedTime = currentTime - lastRenderTime;
    if (!renderTimingStarted) {
      renderTimingStarted = true;
      lastRenderTime = currentTime;
    } else {
      if (elaspedTime * 1e-9 >= getRenderIntervalSeconds()) {
        lastRenderTime = currentTime;
      }
    }
    return elaspedTime * 1e-9;
  }

  protected double computeElapsedTimeSinceLastGarbageCollection() {
    long currentTime = System.nanoTime();
    long elaspedTime = currentTime - lastGarbageCollectTime;
    if (!garbageCollectTimingStarted) {
      garbageCollectTimingStarted = true;
      lastGarbageCollectTime = currentTime;
    } else {
      if (elaspedTime * 1e-9 >= GARBAGE_COLLECT_INTERVAL_SECONDS) {
        lastGarbageCollectTime = currentTime;
      }
    }
    return elaspedTime * 1e-9;
  }

  protected void updateMmdModelManager() {
    if (!mmdModelManager.getAbsolutePath().equals(currentModelFilePath)) {
      if (currentModelFilePath.isEmpty()) {
        mmdModelManager.clear();
      } else {
        mmdModelManager.load(currentModelFilePath);
      }
      // Loading failed.
      if (!mmdModelManager.thereIsModel()) {
        updateCurrentModelFilePath("");
      }
    }
  }

  protected void render() {
    double sinceLastRender = computeElapsedTimeSinceLastRender();
    if (sinceLastRender < getRenderIntervalSeconds()) {
      return;
    }

    double sinceLargeGarbageCollect = computeElapsedTimeSinceLastGarbageCollection();
    if (sinceLargeGarbageCollect > GARBAGE_COLLECT_INTERVAL_SECONDS) {
      glObjectCache.collectGarbage();
    }

    updateMmdModelManager();
    updateModel(sinceLastRender);
    updateScreenSize();

    // Clear the accumulation buffer.
    useBufferCollection(accumulationBuffers, /* useDepthBuffer= */ false, () -> {
      glWrapper.setBlendingEnabled(false);
      glWrapper.setFaceCullingEnabled(true);
      glWrapper.setClearColor(0, 0, 0, 0);
      glWrapper.clearScreen(true, false);
    });

    for (int shiftIndex_ = 0; shiftIndex_ < 16; shiftIndex_++) {
      final int shiftIndex = shiftIndex_;
      useBufferCollection(screenBuffers, /* useDepthBuffer= */ true, () -> {
        double shiftX = (shiftIndex % 4) * 0.25 - 0.5 + 0.125;
        double shiftY = (shiftIndex / 4) * 0.25 - 0.5 + 0.125;
        renderFromCamera(shiftX, shiftY);
      });
      screenBuffers.swap();

      useBufferCollection(accumulationBuffers, /* useDepthBuffer= */ false, () -> {
        glWrapper.setBlendingEnabled(true);
        glWrapper.setBlendFunc(GlConstants.GL_ONE, GlConstants.GL_ONE);
        glWrapper.setFaceCullingEnabled(true);
        copyTextureToScreen(screenBuffers.colorBuffers[0].getReadBuffer());
      });
    }
    accumulationBuffers.swap();

    glWrapper.setDepthTestEnabled(false);
    glWrapper.setBlendingEnabled(false);
    glWrapper.setFaceCullingEnabled(true);

    // Average the accumulated data.
    useBufferCollection(screenBuffers, /* useDepthBuffer= */ false, () -> {
      scaleTexture(
          accumulationBuffers.colorBuffers[0].getReadBuffer(),
          new Vector4d(1.0 / 16, 1.0 / 16, 1.0 / 16, 1.0 / 16));
    });
    screenBuffers.swap();

    processLinearImage(screenBuffers.colorBuffers[0].getReadBuffer());

    // Convert to SRGB.
    useBufferCollection(screenBuffers, /* useDepthBuffer= */ false, () -> {
      convertToSrgb(screenBuffers.colorBuffers[0].getReadBuffer());
    });
    screenBuffers.swap();

    // Copy texture to screen.
    copyTextureToScreen(screenBuffers.colorBuffers[0].getReadBuffer());

    glfwPollEvents();
    glfwSwapBuffers(glWindowId);
  }

  protected void updateCurrentModelFilePath(String newFilePath) {
    currentModelFilePath = newFilePath;
  }

  protected abstract void processLinearImage(GlTextureRect linearImage);

  protected void useBufferCollection(
      GlTextureRectBufferCollection bufferCollection,
      boolean useDepthBuffer,
      Runnable runnable) {
    renderer00.fbo.bind();
    bufferCollection.attachTo(renderer00.fbo, useDepthBuffer);
    glWrapper.setDepthTestEnabled(useDepthBuffer);
    runnable.run();
    glWrapper.flush();
    renderer00.fbo.detachAll();
    renderer00.fbo.unbind();
  }

  protected void renderFromCamera(double shiftX, double shiftY) {
    glWrapper.setDepthTestEnabled(true);
    glWrapper.setFaceCullingEnabled(true);
    glWrapper.setBlendingEnabled(true);
    glWrapper.setBlendFuncSeparate(
        GlConstants.GL_SRC_ALPHA, GlConstants.GL_ONE_MINUS_SRC_ALPHA,
        GlConstants.GL_ONE, GlConstants.GL_ONE);
    clearScreen();

    renderer00.pushBindingFrame();
    setRenderFromCameraParameters(shiftX, shiftY);
    renderScene();
    renderer00.popBindingFrame();
  }

  protected void setRenderFromCameraParameters(double shiftX, double shiftY) {
    setCameraParameters(shiftX, shiftY);
    setLightingParameters();
  }

  protected void setEdgeFactorParameters() {
    renderer00.setBinding("mat_edgeFactor", getEdgeFactor());
  }

  protected void setCameraParameters(double shiftX, double shiftY) {
    renderer00.setProjectionMatrix(cameraControl.getCamera()
        .getProjectionXform(shiftX, shiftY, screenWidth, screenHeight).m);
    renderer00.setViewMatrix(cameraControl.getCamera().getViewXform().m);
    renderer00.rightMultiplyModelXform(Transform.builder().scale(1, 1, -1).build());
  }

  protected void renderScene() {
    if (shouldRenderLandmarks()) {
      renderLandmarks();
    }
    renderMmdModel();
  }

  protected double getEdgeFactor() {
    return EDGE_FACTOR;
  }

  protected void renderMmdModel() {
    if (mmdModelManager.thereIsModel()) {
      setEdgeFactorParameters();
      MmdSurface mmdSurface = mmdModelManager.getMmdSurface().get();
      mmdSurface.getExtension(MmdSurfaceRenderer00.class).render(renderer00);
    }
  }

  protected abstract boolean shouldRenderLandmarks();

  protected void renderLandmarks() {
    floor.getExtension(Renderer00Receiver.class).render(renderer00);
  }

  protected void renderFullScreenQuad(GlProgram glProgram, Consumer<GlProgram> uniformPreparer) {
    glProgram.use(program -> {
      renderer00.vao.use(vao -> {
        renderer00.vao.disableAllAttribute();

        GlVertexPositionProvider vertexPositionProvider = fullScreenQuad.getExtension(GlVertexPositionProvider.class);
        GlVbo vertexPositionVbo = vertexPositionProvider.getGlObject();
        vertexPositionVbo.bind();
        glProgram.getAttribute("vert_position").setup(vertexPositionProvider.getAttributeSpec("vert_position"));
        glProgram.getAttribute("vert_position").setEnabled(true);
        vertexPositionVbo.unbind();

        if (glProgram.hasAttribute("vert_texCoord")) {
          GlVertexTexCoordProvider vertexTexCoordProvider = fullScreenQuad.getExtension(GlVertexTexCoordProvider.class);
          GlVbo vertexTexCoordVbo = vertexTexCoordProvider.getGlObject();
          vertexTexCoordVbo.bind();
          glProgram.getAttribute("vert_texCoord").setup(vertexTexCoordProvider.getAttributeSpec("vert_texCoord"));
          glProgram.getAttribute("vert_texCoord").setEnabled(true);
          vertexTexCoordVbo.unbind();
        }

        uniformPreparer.accept(program);

        GlPrimitiveTypeProvider primitiveTypeProvider = fullScreenQuad.getExtension(GlPrimitiveTypeProvider.class);
        GlIndexProvider indexProvider = fullScreenQuad.getExtension(GlIndexProvider.class);
        GlVbo indexVbo = indexProvider.getGlObject();
        indexVbo.bind();
        glWrapper.drawElements(primitiveTypeProvider.getPrimitiveType(), indexProvider.getIndexCount(), 0);
        indexVbo.unbind();

        renderer00.unuseAllTextureUnits();
      });
    });
  }

  protected void copyTextureToScreen(GlTextureRect texture) {
    GlProgram glProgram = copyProgram.unwrap(unwrapper).getExtension(GlProgramProvider.class).getGlObject();
    renderFullScreenQuad(glProgram, program -> bindToTexInput(program, texture));
  }

  protected void convertToSrgb(GlTextureRect texture) {
    GlProgram glProgram = srgbProgram.unwrap(unwrapper).getExtension(GlProgramProvider.class).getGlObject();
    renderFullScreenQuad(glProgram, program -> bindToTexInput(program, texture));
  }

  protected void scaleTexture(GlTextureRect texture, Vector4d scale) {
    GlProgram glProgram = scaleProgram.unwrap(unwrapper).getExtension(GlProgramProvider.class).getGlObject();
    renderFullScreenQuad(glProgram, program -> {
      bindToTexInput(program, texture);
      if (program.hasUniform("tex_scale")) {
        program.getUniform("tex_scale").set4Float((float) scale.x, (float) scale.y, (float) scale.z, (float) scale.w);
      }
    });
  }

  protected void bindToTexInput(GlProgram program, GlTextureRect texture) {
    if (program.hasUniform("tex_input")) {
      Optional<GlTextureUnit> optionalTextureUnit = renderer00.popUnusedTextureUnit();
      if (optionalTextureUnit.isPresent()) {
        GlTextureUnit textureUnit = optionalTextureUnit.get();
        texture.useWith(textureUnit);
        program.getUniform("tex_input").set1Int(textureUnit.getIndex());
      }
    }
  }

  protected abstract Vector3d getAmbientLightRadiance();

  protected abstract Vector3d getDirectionalLightRadiance();

  protected abstract Vector3d getDirectionalLightDirection();

  protected void setLightingParameters() {
    Vector3d ambientLightRadiance = getAmbientLightRadiance();
    Vector3d dirLightRadiance = getDirectionalLightRadiance();
    Vector3d dirLightDirection = getDirectionalLightDirection();
    renderer00.setBinding("ambientLight_radiance", ambientLightRadiance);
    renderer00.setBinding("dirLight_radiance", dirLightRadiance);
    renderer00.setBinding("dirLight_direction", dirLightDirection);
  }

  protected void updateScreenSize() {
    try (MemoryStack memoryStack = MemoryStack.stackPush()) {
      IntBuffer widthBuffer = MemoryStack.stackMallocInt(1);
      IntBuffer heightBuffer = MemoryStack.stackMallocInt(1);
      glfwGetWindowSize(glWindowId, widthBuffer, heightBuffer);
      screenWidth = Math.max(1, widthBuffer.get(0));
      screenHeight = Math.max(1, heightBuffer.get(0));
    }
    glWrapper.setViewport(0, 0, screenWidth, screenHeight);
    if (screenBuffers.getWidth() != screenWidth || screenBuffers.getHeight() != screenHeight) {
      screenBuffers.allocate(screenWidth, screenHeight);
      accumulationBuffers.allocate(screenWidth, screenHeight);

      UgPosTexMeshExtensions.HostData.Builder
          builder = fullScreenQuad.getExtension(UgPosTexMeshExtensions.HostData.class).startBuild();
      builder.setPrimitiveType(GlConstants.GL_TRIANGLES);

      builder.setTexCoord(0, 0);
      builder.addPosition(-1, -1, 0);

      builder.setTexCoord(screenWidth, 0);
      builder.addPosition(1, -1, 0);

      builder.setTexCoord(screenWidth, screenHeight);
      builder.addPosition(1, 1, 0);

      builder.setTexCoord(0, screenHeight);
      builder.addPosition(-1, 1, 0);

      builder.addIndex(0).addIndex(1).addIndex(2);
      builder.addIndex(0).addIndex(2).addIndex(3);

      builder.endBuild();
    }
  }

  protected Vector4d clearColor() {
    return new Vector4d(0.95, 0.95, 0.95, 1.0);
  }

  protected void clearScreen() {
    Vector4d cc = clearColor();
    glWrapper.setClearColor(cc.x, cc.y, cc.z, cc.w);
    glWrapper.clearScreen(/* colorBuffer= */ true, /* depthBuffer= */ true);
  }

  @Override
  public abstract void actionPerformed(ActionEvent e);
}
