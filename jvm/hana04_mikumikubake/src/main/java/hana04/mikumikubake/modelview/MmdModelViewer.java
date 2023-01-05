package hana04.mikumikubake.modelview;

import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.utils.GdxNativesLoader;
import hana04.base.Module;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.filesystem.FileSystemModule;
import hana04.botan.GlWrapperModule;
import hana04.botan.cache.GlObjectCache;
import hana04.botan.util.UiUtil;
import hana04.formats.mmd.vpd.VpdPose;
import hana04.gfxbase.util.MathUtil;
import hana04.mikumikubake.mmd.MmdModelManager;
import hana04.mikumikubake.opengl.common.GlWindowId;
import hana04.mikumikubake.opengl.common.GlWindowIdModule;
import hana04.mikumikubake.opengl.common.ModelViewer;
import hana04.mikumikubake.opengl.renderable.mmdsurface.MmdSurfaceRenderer00;
import hana04.mikumikubake.opengl.renderable.ugposcolmesh.UgPosColMesh;
import hana04.mikumikubake.opengl.renderable.ugposcolmesh.UgPosColMeshBuilder;
import hana04.mikumikubake.opengl.renderable.ugposcolmesh.UgPosColMeshExtensions;
import hana04.mikumikubake.opengl.renderable.ugpostexmesh.UgPosTexMeshBuilder;
import hana04.mikumikubake.opengl.renderer00.Renderer00Factory;
import hana04.mikumikubake.opengl.renderer00.Renderer00Receiver;
import hana04.mikumikubake.opengl.renderer00.camera.ui.CameraControl;
import hana04.mikumikubake.opengl.renderer00.camera.ui.YUpPerspectiveCameraControl;
import hana04.opengl.wrapper.GlConstants;
import hana04.opengl.wrapper.GlTextureRect;
import hana04.opengl.wrapper.GlWrapper;
import hana04.opengl.wrapper.lwjgl.LwjglGlWrapper;
import hana04.shakuyaku.surface.mmd.mmd.MmdSurface;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.vecmath.Vector3d;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_BRACKET;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_BRACKET;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;

public class MmdModelViewer extends ModelViewer implements ActionListener {
  private static final Logger logger = LoggerFactory.getLogger(MmdModelViewer.class);

  static final int INITIAL_GL_WINDOW_WIDTH = 800;
  static final int INITIAL_GL_WINDOW_HEIGHT = 600;

  private UiFrame uiFrame;
  private MmdInputPoseManager mmdInputPoseManager;
  private boolean isAnimationPlaying = false;

  private UgPosColMesh axes;

  // Callbacks
  private GlKeyboardCallback glKeyboardCallBack;
  private GLFWMouseButtonCallbackI glMouseButtonCallback;
  private GLFWCursorPosCallbackI glMouseCursorCallback;

  // Misc UI
  JFileChooser modelFileChooser;
  JFileChooser motionPoseFileChooser;
  FrameSliderChangeListener frameSliderChangeListener;
  FrameTextFieldListenter frameTextFieldListenter;

  // Misc data
  private int maxIkUpdateCount = 100000;
  private UgPosColMesh redBox;
  private UgPosColMesh greenBox;
  private UgPosColMesh blueBox;
  private UgPosColMesh yellowBox;

  @Inject
  MmdModelViewer(
      @GlWindowId long glWindowId,
      GlWrapper glWrapper,
      GlObjectCache glObjectCache,
      HanaUnwrapper unwrapper,
      Renderer00Factory renderer00Factory,
      MmdInputPoseManager mmdInputPoseManager,
      MmdModelManager mmdModelManager,
      Provider<UgPosColMeshBuilder> ugPosColMeshBuilder,
      Provider<UgPosTexMeshBuilder> ugPosTexMeshBuilder,
      FileSystem fileSystem) {
    super(
        glWindowId,
        glWrapper,
        glObjectCache,
        unwrapper,
        renderer00Factory,
        mmdModelManager,
        ugPosColMeshBuilder,
        ugPosTexMeshBuilder,
        fileSystem,
        createCameraControl());
    this.mmdInputPoseManager = mmdInputPoseManager;
    this.mmdModelManager.setPhysicsEnabled(true);
    this.mmdModelManager.setMaxIkUpdateCount(maxIkUpdateCount);
  }

  private static CameraControl<?> createCameraControl() {
    YUpPerspectiveCameraControl cameraControl = new YUpPerspectiveCameraControl();
    cameraControl.getCamera().getView().setCenter(0, 10, 0);
    cameraControl.getCamera().getView().setXAngle(-10);
    cameraControl.getCamera().getView().setYAngle(0);
    return cameraControl;
  }

  long getGlWindowId() {
    return glWindowId;
  }

  private void initFileChooser() {
    modelFileChooser = new JFileChooser(new File("data/marigold/model").getAbsolutePath());
    FileNameExtensionFilter pmdFilter = new FileNameExtensionFilter("PMD Models", "pmd");
    modelFileChooser.setFileFilter(pmdFilter);
    FileNameExtensionFilter pmxFilter = new FileNameExtensionFilter("PMX Models", "pmx");
    modelFileChooser.setFileFilter(pmxFilter);
    modelFileChooser.setAcceptAllFileFilterUsed(false);
    modelFileChooser.setFileFilter(pmxFilter);

    motionPoseFileChooser = new JFileChooser(new File("data/marigold/motion")
        .getAbsolutePath());
    FileNameExtensionFilter vmdFilter = new FileNameExtensionFilter("VMD Motions", "vmd");
    motionPoseFileChooser.setFileFilter(vmdFilter);
    FileNameExtensionFilter vpdFilter = new FileNameExtensionFilter("VPD Poses", "vpd");
    motionPoseFileChooser.setFileFilter(vpdFilter);
    motionPoseFileChooser.setAcceptAllFileFilterUsed(false);
    motionPoseFileChooser.setFileFilter(vmdFilter);
  }

  protected void initMeshData() {
    super.initMeshData();
    initAxesData();
    redBox = createBox(1, 0, 0);
    greenBox = createBox(0, 1, 0);
    blueBox = createBox(0, 0, 1);
    yellowBox = createBox(1, 1, 0);
  }

  private UgPosColMesh createBox(double r, double g, double b) {
    var mesh = ugPosColMeshBuilder.get().build();
    UgPosColMeshExtensions.HostData.Builder builder = mesh.getExtension(UgPosColMeshExtensions.HostData.class).startBuild();
    builder.setPrimitiveType(GlConstants.GL_LINES);

    builder.setColor(r, g, b, 1.0);

    builder.addPosition(-1.0, -1.0, -1.0);
    builder.addPosition(+1.0, -1.0, -1.0);
    builder.addPosition(-1.0, +1.0, -1.0);
    builder.addPosition(+1.0, +1.0, -1.0);
    builder.addPosition(-1.0, -1.0, +1.0);
    builder.addPosition(+1.0, -1.0, +1.0);
    builder.addPosition(-1.0, +1.0, +1.0);
    builder.addPosition(+1.0, +1.0, +1.0);

    builder.addIndex(0);
    builder.addIndex(1);

    builder.addIndex(1);
    builder.addIndex(3);

    builder.addIndex(3);
    builder.addIndex(2);

    builder.addIndex(2);
    builder.addIndex(0);

    builder.addIndex(4);
    builder.addIndex(5);

    builder.addIndex(5);
    builder.addIndex(7);

    builder.addIndex(7);
    builder.addIndex(6);

    builder.addIndex(6);
    builder.addIndex(4);

    builder.addIndex(0);
    builder.addIndex(4);

    builder.addIndex(1);
    builder.addIndex(5);

    builder.addIndex(2);
    builder.addIndex(6);

    builder.addIndex(3);
    builder.addIndex(7);

    builder.endBuild();

    return mesh;
  }

  @Override
  protected void renderMmdModel() {
    if (mmdModelManager.thereIsModel()) {
      setEdgeFactorParameters();
      MmdSurface mmdSurface = mmdModelManager.getMmdSurface().get();
      mmdSurface.getExtension(MmdSurfaceRenderer00.class).render(renderer00);

      /*
      glWrapper.setDepthTestEnabled(false);
      mmdModelManager.getBoneGlobalTransform("右足ＩＫ").ifPresent(m -> {
        renderer00.pushBindingFrame();
        renderer00.rightMultiplyModelXform(new Transform(new Matrix4d(m)));
        renderer00.rightMultiplyModelXform(Transform.builder().scale(0.1, 0.1, 0.1).build());
        redBox.getExtension(Renderer00Receiver.class).render(renderer00);
        renderer00.popBindingFrame();
      });
      mmdModelManager.getBoneGlobalTransform("右足首").ifPresent(m -> {
        renderer00.pushBindingFrame();
        renderer00.rightMultiplyModelXform(new Transform(new Matrix4d(m)));
        renderer00.rightMultiplyModelXform(Transform.builder().scale(0.1, 0.1, 0.1).build());
        greenBox.getExtension(Renderer00Receiver.class).render(renderer00);
        renderer00.popBindingFrame();
      });
      mmdModelManager.getBoneGlobalTransform("左足ＩＫ").ifPresent(m -> {
        renderer00.pushBindingFrame();
        renderer00.rightMultiplyModelXform(new Transform(new Matrix4d(m)));
        renderer00.rightMultiplyModelXform(Transform.builder().scale(0.1, 0.1, 0.1).build());
        redBox.getExtension(Renderer00Receiver.class).render(renderer00);
        renderer00.popBindingFrame();
      });
      mmdModelManager.getBoneGlobalTransform("左足首").ifPresent(m -> {
        renderer00.pushBindingFrame();
        renderer00.rightMultiplyModelXform(new Transform(new Matrix4d(m)));
        renderer00.rightMultiplyModelXform(Transform.builder().scale(0.1, 0.1, 0.1).build());
        greenBox.getExtension(Renderer00Receiver.class).render(renderer00);
        renderer00.popBindingFrame();
      });
      glWrapper.setDepthTestEnabled(true);
       */
    }
  }

  @Override
  protected void updateModel(double elapsedSeconds) {
    if (isAnimationPlaying) {
      double animationLength = mmdInputPoseManager.getAnimationLength();
      double currentFrame = mmdInputPoseManager.getCurrentFrame();
      if (currentFrame < animationLength) {
        double newFrame = Math.min(elapsedSeconds * 30 + currentFrame, animationLength);
        setFrame(newFrame);
        uiFrame.modelMotionPanel.frameSlider.setValue((int) Math.round(newFrame));
        uiFrame.modelMotionPanel.frameTextField.setText(String.format("%.2f", newFrame));
      }
    }
    mmdModelManager.update(elapsedSeconds);
  }

  private void initAxesData() {
    axes = ugPosColMeshBuilder.get().build();
    UgPosColMeshExtensions.HostData.Builder builder = axes.getExtension(UgPosColMeshExtensions.HostData.class).startBuild();

    builder.setPrimitiveType(GlConstants.GL_LINES);

    builder.setColor(1.0, 0.0, 0.0, 1.0);
    builder.addPosition(0.0, 0.0, 0.0);
    builder.addPosition(100.0, 0.0, 0.0);
    builder.addIndex(0);
    builder.addIndex(1);

    builder.setColor(0.0, 1.0, 0.0, 1.0);
    builder.addPosition(0.0, 0.0, 0.0);
    builder.addPosition(0.0, 100.0, 0.0);
    builder.addIndex(2);
    builder.addIndex(3);

    builder.setColor(0.0, 0.0, 1.0, 1.0);
    builder.addPosition(0.0, 0.0, 0.0);
    builder.addPosition(0.0, 0.0, 100.0);
    builder.addIndex(4);
    builder.addIndex(5);

    builder.endBuild();
  }

  protected void initInteractionCallbacks() {
    glKeyboardCallBack = new GlKeyboardCallback();
    glfwSetKeyCallback(glWindowId, glKeyboardCallBack);

    glMouseButtonCallback = cameraControl.getMouseButtonCallback();
    glfwSetMouseButtonCallback(glWindowId, glMouseButtonCallback);

    glMouseCursorCallback = cameraControl.getMouseCursorCallback();
    glfwSetCursorPosCallback(glWindowId, glMouseCursorCallback);
  }

  @Override
  protected void initUiFrames() {
    initFileChooser();
    frameSliderChangeListener = new FrameSliderChangeListener();
    frameTextFieldListenter = new FrameTextFieldListenter();
    uiFrame = new UiFrame(this);
    uiFrame.setVisible(true);
  }

  @Override
  protected void disposeUiFrames() {
    if (uiFrame != null) {
      uiFrame.dispose();
    }
  }

  @Override
  protected void processLinearImage(GlTextureRect linearImage) {
    // NO-OP
  }

  @Override
  protected boolean shouldRenderLandmarks() {
    return true;
  }

  @Override
  protected void renderLandmarks() {
    super.renderLandmarks();
    axes.getExtension(Renderer00Receiver.class).render(renderer00);
  }

  @Override
  protected Vector3d getAmbientLightRadiance() {
    return uiFrame.ambientLightPanel.getRadiance();
  }

  @Override
  protected Vector3d getDirectionalLightRadiance() {
    return uiFrame.directionalLightPanel.getRadiance();
  }

  @Override
  protected Vector3d getDirectionalLightDirection() {
    return uiFrame.directionalLightPanel.getDirection();
  }

  public static void main(String[] args) {
    UiUtil.initSwingLookAndField();
    GdxNativesLoader.load();
    Bullet.init();
    initGlfw();
    long glWindowId = initGlfwWindow(INITIAL_GL_WINDOW_WIDTH, INITIAL_GL_WINDOW_HEIGHT, "Mmd Model Viewer");
    Component component = DaggerMmdModelViewer_Component.builder()
        .glWindowIdModule(new GlWindowIdModule(glWindowId))
        .glWrapperModule(new GlWrapperModule(new LwjglGlWrapper()))
        .fileSystemModule(new FileSystemModule(FileSystems.getDefault()))
        .build();

    component.mmdModelViewer().run();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String actionCommand = e.getActionCommand();
    if (actionCommand == null) {
      return;
    }
    if (actionCommand.equals(UiFrame.LOAD_MODEL_ACTION_COMMAND)) {
      loadModel();
    } else if (actionCommand.equals(UiFrame.CLEAR_MODEL_ACTION_COMMAND)) {
      clearMainMesh();
    } else if (actionCommand.equals(UiFrame.LOAD_MOTION_ACTION_COMMAND)) {
      loadMotionOrPose();
    } else if (actionCommand.equals(UiFrame.CLEAR_MOTION_ACTION_COMMAND)) {
      clearMotionOrPose();
    } else if (actionCommand.equals(UiFrame.PLAY_COMMAND) || actionCommand.equals(UiFrame.PAUSE_COMMAND)) {
      togglePlayState();
    }
  }

  private void loadModel() {
    //Select a file
    int choice = modelFileChooser.showOpenDialog(null);
    if (choice != JFileChooser.APPROVE_OPTION) {
      return;
    }
    String absolutePath = modelFileChooser.getSelectedFile().getAbsolutePath();
    updateCurrentModelFilePath(absolutePath);
  }

  @Override
  protected void updateCurrentModelFilePath(String newModelPath) {
    super.updateCurrentModelFilePath(newModelPath);
    uiFrame.modelMotionPanel.modelFileTextField.setText(newModelPath);
  }

  private void clearMainMesh() {
    updateCurrentModelFilePath("");
  }

  private void loadMotionOrPose() {
    //Select a file
    int choice = motionPoseFileChooser.showOpenDialog(null);
    if (choice != JFileChooser.APPROVE_OPTION) {
      return;
    }
    String absolutePath = motionPoseFileChooser.getSelectedFile().getAbsolutePath();
    mmdInputPoseManager.load(absolutePath);
    updateMotionUi();
    mmdModelManager.setPose(mmdInputPoseManager.getCurrentPose(), true);
  }

  private void clearMotionOrPose() {
    mmdInputPoseManager.clear();
    updateMotionUi();
    mmdModelManager.setPose(mmdInputPoseManager.getCurrentPose(), true);
  }

  private void updateMotionUi() {
    uiFrame.modelMotionPanel.motionFileTextField.setText(mmdInputPoseManager.getAbsolutePath());

    canSetFrameThroughUi = false;
    uiFrame.modelMotionPanel.frameSlider.setMinimum(0);
    int animationLength = (int) mmdInputPoseManager.getAnimationLength();
    uiFrame.modelMotionPanel.frameSlider.setMaximum(animationLength);
    uiFrame.modelMotionPanel.frameSlider.setEnabled(animationLength > 0);
    uiFrame.modelMotionPanel.frameTextField.setText(String.format("%.2f", mmdInputPoseManager.getCurrentFrame()));
    uiFrame.modelMotionPanel.frameTextField.setEnabled(animationLength > 0);
    canSetFrameThroughUi = true;

    uiFrame.modelMotionPanel.playPauseButton.setEnabled(animationLength > 0 && mmdModelManager.thereIsModel());
  }

  private void togglePlayState() {
    double animationLength = mmdInputPoseManager.getAnimationLength();
    if (animationLength == 0) {
      stopPlayingAnimation();
    } else if (isAnimationPlaying) {
      stopPlayingAnimation();
    } else {
      startPlayingAnimation();
    }
  }

  private void stopPlayingAnimation() {
    isAnimationPlaying = false;
    int animationLength = (int) mmdInputPoseManager.getAnimationLength();
    uiFrame.modelMotionPanel.frameSlider.setMaximum(animationLength);
    uiFrame.modelMotionPanel.frameSlider.setEnabled(animationLength > 0);
    uiFrame.modelMotionPanel.frameTextField.setText(String.format("%.2f", mmdInputPoseManager.getCurrentFrame()));
    uiFrame.modelMotionPanel.frameTextField.setEnabled(animationLength > 0);
    uiFrame.modelMotionPanel.playPauseButton.setEnabled(animationLength > 0);
    uiFrame.modelMotionPanel.playPauseButton.setText(UiFrame.PLAY_COMMAND);
    uiFrame.modelMotionPanel.playPauseButton.setEnabled(animationLength > 0 && mmdModelManager.thereIsModel());
    uiFrame.modelMotionPanel.loadMotionButton.setEnabled(true);
    uiFrame.modelMotionPanel.loadModelButton.setEnabled(true);
    uiFrame.modelMotionPanel.clearModelButton.setEnabled(true);
    uiFrame.modelMotionPanel.clearMotionButton.setEnabled(true);
    canSetFrameThroughUi = true;
  }

  private void startPlayingAnimation() {
    isAnimationPlaying = true;
    uiFrame.modelMotionPanel.frameSlider.setEnabled(false);
    uiFrame.modelMotionPanel.frameTextField.setEnabled(false);
    uiFrame.modelMotionPanel.playPauseButton.setText(UiFrame.PAUSE_COMMAND);
    uiFrame.modelMotionPanel.loadMotionButton.setEnabled(false);
    uiFrame.modelMotionPanel.loadModelButton.setEnabled(false);
    uiFrame.modelMotionPanel.clearModelButton.setEnabled(false);
    uiFrame.modelMotionPanel.clearMotionButton.setEnabled(false);
    canSetFrameThroughUi = false;
  }

  private void setMaxIkUpdateCount(int value) {
    maxIkUpdateCount = value;
    mmdModelManager.setMaxIkUpdateCount(value);
    System.out.println("maxIkIterationCount = " + maxIkUpdateCount);
  }

  class GlKeyboardCallback implements GLFWKeyCallbackI {
    public GlKeyboardCallback() {
    }

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
      int keyState = glfwGetKey(window, key);
      if (keyState != GLFW_RELEASE) {
        return;
      }
      if (key == GLFW_KEY_F1) {
        EventQueue.invokeLater(() -> setMaxIkUpdateCount(0));
      } else if (key == GLFW_KEY_F2) {
        EventQueue.invokeLater(() -> setMaxIkUpdateCount(100));
      } else if (key == GLFW_KEY_LEFT_BRACKET) {
        EventQueue.invokeLater(() -> setMaxIkUpdateCount(Math.max(0, maxIkUpdateCount - 1)));
      } else if (key == GLFW_KEY_RIGHT_BRACKET) {
        EventQueue.invokeLater(() -> setMaxIkUpdateCount(Math.min(10000, maxIkUpdateCount + 1)));
      } else if (key == GLFW_KEY_P) {
        EventQueue.invokeLater(() -> System.out.println("maxIkIerationCount = " + maxIkUpdateCount));
      }
    }
  }

  @Singleton
  @dagger.Component(
      modules = {
          GlWindowIdModule.class,
          Module.class,
          hana04.serialize.Module.class,
          hana04.gfxbase.serialize.Module.class,
          hana04.shakuyaku.Module.class,
          hana04.botan.Module.class,
          hana04.mikumikubake.Module.class,
          hana04.mikumikubake.opengl.Module.class,
      }
  )
  public interface Component extends hana04.base.Component, hana04.botan.Component {
    MmdModelViewer mmdModelViewer();
  }

  private boolean canSetFrameThroughUi = true;

  private void setFrame(double newFrameValue) {
    mmdInputPoseManager.setFrame(newFrameValue);
    VpdPose currentPose = mmdInputPoseManager.getCurrentPose();
    if (mmdModelManager.thereIsModel()) {
      mmdModelManager.setPose(currentPose, /* resetPhysics= */ !isAnimationPlaying);
    }
  }

  private void setFrameThroughUi(double newFrameValue, Object source) {
    if (!canSetFrameThroughUi) {
      return;
    }
    canSetFrameThroughUi = false;

    newFrameValue = MathUtil.clamp(newFrameValue, 0, mmdInputPoseManager.getAnimationLength());
    if (Math.abs(mmdInputPoseManager.getCurrentFrame() - newFrameValue) <= 0.5) {
      canSetFrameThroughUi = true;
      return;
    }

    setFrame(newFrameValue);

    double currentFrame = mmdInputPoseManager.getCurrentFrame();
    if (source != uiFrame.modelMotionPanel.frameSlider) {
      uiFrame.modelMotionPanel.frameSlider.setValue((int) Math.round(currentFrame));
    }

    if (source != uiFrame.modelMotionPanel.frameTextField) {
      uiFrame.modelMotionPanel.frameTextField.setText(String.format("%.2f", currentFrame));
    }

    canSetFrameThroughUi = true;
  }

  class FrameSliderChangeListener implements ChangeListener {
    @Override
    public void stateChanged(ChangeEvent e) {
      int newValue = uiFrame.modelMotionPanel.frameSlider.getValue();
      setFrameThroughUi(newValue, uiFrame.modelMotionPanel.frameSlider);
    }
  }

  class FrameTextFieldListenter implements DocumentListener, FocusListener {

    @Override
    public void focusGained(FocusEvent e) {

    }

    @Override
    public void focusLost(FocusEvent e) {
      handleTextChange();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
      handleTextChange();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      handleTextChange();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      handleTextChange();
    }

    private void handleTextChange() {
      double newFrameValue = -1;
      try {
        newFrameValue = Double.parseDouble(uiFrame.modelMotionPanel.frameTextField.getText());
      } catch (Exception e) {
        newFrameValue = 0;
      }
      setFrameThroughUi(newFrameValue, uiFrame.modelMotionPanel.frameTextField);
    }
  }
}
