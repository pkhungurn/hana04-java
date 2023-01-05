package hana04.opengl.wrapper.lwjgl;

import hana04.base.util.TextIo;
import hana04.opengl.wrapper.GlAttribute;
import hana04.opengl.wrapper.GlProgram;
import hana04.opengl.wrapper.GlUniform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.lwjgl.opengl.GL20.GL_ACTIVE_ATTRIBUTES;
import static org.lwjgl.opengl.GL20.GL_ACTIVE_UNIFORMS;
import static org.lwjgl.opengl.GL20.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20C.glAttachShader;
import static org.lwjgl.opengl.GL20C.glCreateProgram;

public class LwjglProgram implements GlProgram {
  private static Logger logger = LoggerFactory.getLogger(LwjglProgram.class);
  private static LwjglProgram current = null;

  public static boolean isAProgramInUse() {
    return current != null;
  }

  public static LwjglProgram getCurrent() {
    return current;
  }

  public static void unuseProgram() {
    glUseProgram(0);
    current = null;
  }

  private int id;
  private LwjglVertexShader vertexShader;
  private LwjglFragmentShader fragmentShader;
  private HashMap<String, GlUniform> uniforms;
  private HashMap<String, GlAttribute> attributes;
  private boolean disposed = false;

  public LwjglProgram(LwjglVertexShader vertexShader, LwjglFragmentShader fragmentShader) {
    this.vertexShader = vertexShader;
    this.fragmentShader = fragmentShader;

    this.id = glCreateProgram();

    buildProgram();

    initializeUniforms();
  }

  public LwjglProgram(String vertexSrc, String fragmentSrc) {
    this(vertexSrc, Optional.empty(), fragmentSrc, Optional.empty());
  }

  public LwjglProgram(String vertexSrc,
                      Optional<String> vertexSrcFile,
                      String fragmentSrc,
                      Optional<String> fragmentSrcFile) {
    this.id = glCreateProgram();
    this.vertexShader = new LwjglVertexShader(vertexSrc, vertexSrcFile);
    this.fragmentShader = new LwjglFragmentShader(fragmentSrc, fragmentSrcFile);
    logger.debug("vertex source file = " + vertexSrcFile);
    logger.debug("fragment source file " + fragmentSrcFile);
    buildProgram();
    initializeUniforms();
    initializeAttributes();
  }

  public static LwjglProgram createFromFile(Path vertexSrcFile, Path fragmentSrcFile) {
    try {
      String vertexSrc = TextIo.readTextFile(vertexSrcFile);
      String fragmentSrc = TextIo.readTextFile(fragmentSrcFile);
      return new LwjglProgram(vertexSrc, Optional.of(vertexSrcFile.toString()), fragmentSrc,
        Optional.of(fragmentSrcFile.toString()));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public int getId() {
    return this.id;
  }

  public boolean isUsed() {
    return current == this;
  }

  public void use() {
    glUseProgram(this.id);
    current = this;
  }

  public void unuse() {
    for (GlAttribute attrib : attributes.values()) {
      if (attrib.isEnabled()) {
        attrib.setEnabled(false);
      }
    }
    unuseProgram();
  }

  public Map<String, ? extends GlUniform> getUniforms() {
    return this.uniforms;
  }

  @Override
  public Map<String, ? extends GlAttribute> getAttibutes() {
    return attributes;
  }

  public GlUniform getUniform(String name) {
    return uniforms.get(name);
  }

  @Override
  public Optional<GlUniform> uniform(String name) {
    return Optional.ofNullable(getUniform(name));
  }

  public boolean hasUniform(String name) {
    return uniforms.containsKey(name);
  }

  public GlAttribute getAttribute(String name) {
    return attributes.get(name);
  }

  @Override
  public Optional<GlAttribute> attribute(String name) {
    return Optional.ofNullable(getAttribute(name));
  }

  public boolean hasAttribute(String name) {
    return attributes.containsKey(name);
  }

  public void disposeGl() {
    if (!disposed) {
      vertexShader.disposeGl();
      fragmentShader.disposeGl();
      glDeleteProgram(id);
      disposed = true;
    }
  }

  protected void buildProgram() {
    glAttachShader(this.id, this.vertexShader.getId());
    glAttachShader(this.id, this.fragmentShader.getId());
    glLinkProgram(this.id);
    int linkStatus = glGetProgrami(this.id, GL_LINK_STATUS);
    if (linkStatus == GL_FALSE) {
      throw new RuntimeException("Link error " + glGetProgramInfoLog(this.id));
    }
  }

  private void initializeUniforms() {
    this.uniforms = new HashMap<>();
    int uniformCount = glGetProgrami(this.id, GL_ACTIVE_UNIFORMS);
    //System.err.print("GLSL uniforms: ");
    for (int uniform_index = 0; uniform_index < uniformCount; uniform_index++) {
      GlUniform currUniform = new LwjglUniform(this, uniform_index);
      if (!currUniform.getName().startsWith("gl_")) {
        //System.err.print(currUniform.getName() + " ");
        this.uniforms.put(currUniform.getName(), currUniform);
      }
    }

    // Create an instance for all the array entries.
    ArrayList<GlUniform> newUniforms = new ArrayList<GlUniform>();
    ArrayList<GlUniform> toRemove = new ArrayList<GlUniform>();
    for (GlUniform uniform : this.uniforms.values()) {
      if (uniform.getSize() <= 1) {
        continue;
      }
      String baseName = uniform.getName().substring(0, uniform.getName().length() - 3);
      int start = (baseName.endsWith("]")) ? 1 : 0;
      for (int i = start; i < uniform.getSize(); i++) {
        LwjglUniform newUniform = new LwjglUniform(this);
        newUniform.name = baseName + "[" + Integer.toString(i) + "]";
        newUniform.size = 1;
        newUniform.type = uniform.getType();
        newUniform.location = uniform.getLocation() + i;
        newUniform.isRowMajor = uniform.getIsRowMajor();
        newUniforms.add(newUniform);
        if (glGetUniformLocation(this.getId(), newUniform.name) != newUniform.location) {
          throw new RuntimeException("uniform '" + newUniform.name + "' location not matching the OpenGL assigned " +
            "location");
        }
      }
      if (start == 0) {
        toRemove.add(uniform);
      }
    }
    for (GlUniform uniform : toRemove) {
      this.uniforms.remove(uniform.getName());
    }
    for (GlUniform uniform : newUniforms) {
      this.uniforms.put(uniform.getName(), uniform);
    }
  }

  private void initializeAttributes() {
    this.attributes = new HashMap<String, GlAttribute>();
    int attribCount = glGetProgrami(this.id, GL_ACTIVE_ATTRIBUTES);
    for (int attribIndex = 0; attribIndex < attribCount; attribIndex++) {
      LwjglAttribute currentAttrib = new LwjglAttribute(this, attribIndex);
      if (!currentAttrib.getName().startsWith("gl_")) {
        this.attributes.put(currentAttrib.getName(), currentAttrib);
      }
    }
  }
}
