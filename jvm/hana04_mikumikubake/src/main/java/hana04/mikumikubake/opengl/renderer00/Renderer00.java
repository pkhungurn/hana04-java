package hana04.mikumikubake.opengl.renderer00;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import hana04.botan.cache.GlObjectCache;
import hana04.gfxbase.gfxtype.Matrix4dUtil;
import hana04.gfxbase.gfxtype.Transform;
import hana04.opengl.util.GlTextureRectBufferCollection;
import hana04.opengl.wrapper.GlFbo;
import hana04.opengl.wrapper.GlObject;
import hana04.opengl.wrapper.GlProgram;
import hana04.opengl.wrapper.GlTextureUnit;
import hana04.opengl.wrapper.GlVao;
import hana04.opengl.wrapper.GlWrapper;

import javax.vecmath.Matrix4d;
import java.util.Optional;

public class Renderer00 implements GlObject {
  public static final String MODEL_MATRIX_VAR_NAME = "sys_modelMatrix";
  public static final String VIEW_MATRIX_VAR_NAME = "sys_viewMatrix";
  public static final String PROJECTION_MATRIX_VAR_NAME = "sys_projectionMatrix";
  public static final String NORMAL_MATRIX_VAR_NAME = "sys_normalMatrix";

  public final GlWrapper glWrapper;
  public final GlObjectCache glObjectCache;
  public final GlVao vao;
  private int unusedTextureUnitStart = 0;
  public final GlFbo fbo;

  private final BindingStack bindingStack;

  @AutoFactory
  public Renderer00(@Provided GlWrapper glWrapper, @Provided GlObjectCache glObjectCache) {
    this.glWrapper = glWrapper;
    this.glObjectCache = glObjectCache;
    vao = glWrapper.createVao();
    bindingStack = new BindingStack();
    fbo = glWrapper.createFbo();

    setBinding(PROJECTION_MATRIX_VAR_NAME, Matrix4dUtil.createIdentity());
    setBinding(VIEW_MATRIX_VAR_NAME, Matrix4dUtil.createIdentity());
    setBinding(MODEL_MATRIX_VAR_NAME, Matrix4dUtil.createIdentity());
    setBinding(NORMAL_MATRIX_VAR_NAME, Matrix4dUtil.createIdentity());
  }

  public void pushBindingFrame() {
    bindingStack.pushFrame();
  }

  public void popBindingFrame() {
    bindingStack.popFrame();
  }

  public void inNewFrame(Runnable runnable) {
    pushBindingFrame();
    runnable.run();
    popBindingFrame();
  }

  public void setProjectionMatrix(Matrix4d m) {
    setBinding(PROJECTION_MATRIX_VAR_NAME, new Matrix4d(m));
  }

  public void setViewMatrix(Matrix4d m) {
    setBinding(VIEW_MATRIX_VAR_NAME, new Matrix4d(m));
  }

  public void setModelXform(Transform xform) {
    setBinding(MODEL_MATRIX_VAR_NAME, xform.m);
    setBinding(NORMAL_MATRIX_VAR_NAME, xform.mit);
  }

  public void rightMultiplyModelXform(Transform xform) {
    Matrix4d currentModel = getBinding(MODEL_MATRIX_VAR_NAME, Matrix4d.class);
    Matrix4d currentNormal = getBinding(NORMAL_MATRIX_VAR_NAME, Matrix4d.class);
    Matrix4d newModel = Matrix4dUtil.mul(currentModel, xform.m);
    Matrix4d newNormal = Matrix4dUtil.mul(currentNormal, xform.mit);
    setBinding(MODEL_MATRIX_VAR_NAME, newModel);
    setBinding(NORMAL_MATRIX_VAR_NAME, newNormal);
  }

  public Object getBinding(String name) {
    return bindingStack.get(name);
  }

  public <T> T getBinding(String name, Class<T> klass) {
    return bindingStack.get(name, klass);
  }

  public boolean hasBinding(String name) {
    return bindingStack.has(name);
  }

  public <T> Renderer00 setBinding(String name, T value) {
    bindingStack.set(name, value);
    return this;
  }

  @Override
  public void disposeGl() {
    vao.disposeGl();
    fbo.disposeGl();
  }

  public Optional<GlTextureUnit> popUnusedTextureUnit() {
    if (unusedTextureUnitStart >= glWrapper.getTextureUnitCount()) {
      return Optional.empty();
    }
    GlTextureUnit output = glWrapper.getTextureUnit(unusedTextureUnitStart);
    unusedTextureUnitStart++;
    return Optional.of(output);
  }

  public void pushUnusedTextureUnit() {
    if (unusedTextureUnitStart == 0) {
      return;
    }
    unusedTextureUnitStart--;
    GlTextureUnit textureUnit = glWrapper.getTextureUnit(unusedTextureUnitStart);
    if (textureUnit.getBoundTexture() != null) {
      textureUnit.getBoundTexture().unbind();
    }
  }

  public void unuseAllTextureUnits() {
    while (unusedTextureUnitStart > 0) {
      pushUnusedTextureUnit();
    }
  }

  public void useBufferCollection(
    GlTextureRectBufferCollection bufferCollection,
    boolean useDepthBuffer,
    Runnable runnable) {
    fbo.bind();
    bufferCollection.attachTo(fbo, useDepthBuffer);
    glWrapper.setDepthTestEnabled(useDepthBuffer);
    runnable.run();
    glWrapper.flush();
    fbo.detachAll();
    fbo.unbind();
  }

  public void setMatrixUniforms(GlProgram glProgram) {
    glProgram.uniform(MODEL_MATRIX_VAR_NAME).ifPresent(uniform -> {
      uniform.setMatrix4(getBinding(MODEL_MATRIX_VAR_NAME, Matrix4d.class));
    });
    glProgram.uniform(NORMAL_MATRIX_VAR_NAME).ifPresent(uniform -> {
      uniform.setMatrix4(getBinding(NORMAL_MATRIX_VAR_NAME, Matrix4d.class));
    });
    glProgram.uniform(VIEW_MATRIX_VAR_NAME).ifPresent(uniform -> {
      uniform.setMatrix4(getBinding(VIEW_MATRIX_VAR_NAME, Matrix4d.class));
    });
    glProgram.uniform(PROJECTION_MATRIX_VAR_NAME).ifPresent(uniform -> {
      uniform.setMatrix4(getBinding(PROJECTION_MATRIX_VAR_NAME, Matrix4d.class));
    });
  }
}
