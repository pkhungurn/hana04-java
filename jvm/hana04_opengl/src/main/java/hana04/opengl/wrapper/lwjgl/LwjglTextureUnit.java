package hana04.opengl.wrapper.lwjgl;

import hana04.opengl.wrapper.GlTexture;
import hana04.opengl.wrapper.GlTextureUnit;
import hana04.opengl.wrapper.GlUniform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.opengl.GL11.GL_MAX_TEXTURE_SIZE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL13.GL_ACTIVE_TEXTURE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.GL_MAX_TEXTURE_IMAGE_UNITS;
import static org.lwjgl.opengl.GL20.GL_MAX_VERTEX_ATTRIBS;

public class LwjglTextureUnit implements GlTextureUnit {
  private static Logger logger = LoggerFactory.getLogger(LwjglTextureUnit.class);
  private static boolean initialized = false;
  private static LwjglTextureUnit[] instances;
  private static int textureUnitCount;

  private int index;
  private int id;
  private LwjglTexture boundTexture;

  private static synchronized void initialize() {
    if (!initialized) {
      textureUnitCount = glGetInteger(GL_MAX_TEXTURE_IMAGE_UNITS);
      logger.info(String.format("number of texture units = %d", textureUnitCount));

      int maxTextureSize = glGetInteger(GL_MAX_TEXTURE_SIZE);
      ;
      logger.info(String.format("max texture size = %d", maxTextureSize));

      int maxVertexAttribs = glGetInteger(GL_MAX_VERTEX_ATTRIBS);
      logger.info(String.format("max vertex attribs = %d", maxVertexAttribs));

      instances = new LwjglTextureUnit[textureUnitCount];
      for (int i = 0; i < textureUnitCount; i++) {
        instances[i] = new LwjglTextureUnit(i);
      }

      instances[0].activate();
      initialized = true;
    }
  }

  public static LwjglTextureUnit getTextureUnit(int i) {
    if (!initialized) {
      initialize();
    }
    return instances[i];
  }

  public static int getActiveTextureUnitId() {
    return glGetInteger(GL_ACTIVE_TEXTURE);
  }

  public static LwjglTextureUnit getActiveTextureUnit() {
    if (!initialized) {
      initialize();
    }
    int activeId = getActiveTextureUnitId();
    return instances[activeId - GL_TEXTURE0];
  }

  private LwjglTextureUnit(int i) {
    index = i;
    id = GL_TEXTURE0 + index;
    boundTexture = null;
  }

  public void activate() {
    glActiveTexture(id);
  }

  @Override
  public void bindToUniform(GlUniform target) {
    target.set1Int(id - GL_TEXTURE0);
  }

  @Override
  public void bindTexture(GlTexture texture) {
    if (texture instanceof LwjglTexture) {
      LwjglTexture lwgjlTexture = (LwjglTexture) texture;
      if (boundTexture != null && boundTexture != lwgjlTexture) {
        boundTexture.unbind();
      }
      if (boundTexture != lwgjlTexture) {
        LwjglTextureUnit last = activateAndReturnLastActive();
        glBindTexture(lwgjlTexture.getTarget(), lwgjlTexture.getId());
        boundTexture = lwgjlTexture;

        if (last != this) {
          last.activate();
        }
      }
    } else {
      throw new RuntimeException("The given texture is not a LwjglTexture.");
    }

  }

  @Override
  public void unbindTexture(GlTexture texture) {
    if (boundTexture == texture) {
      LwjglTextureUnit last = activateAndReturnLastActive();

      glBindTexture(texture.getTarget(), 0);
      boundTexture = null;

      if (last != this) {
        last.activate();
      }
    }
  }

  private LwjglTextureUnit activateAndReturnLastActive() {
    LwjglTextureUnit last = getActiveTextureUnit();
    if (this != last) {
      activate();
      return last;
    } else {
      return this;
    }
  }

  public void unbindTexture() {
    if (boundTexture != null) {
      boundTexture.unbind();
      boundTexture.disable();
    }
  }


  public boolean isActive() {
    return id == getActiveTextureUnitId();
  }

  public int getIndex() {
    return index;
  }

  public int getId() {
    return id;
  }

  public LwjglTexture getBoundTexture() {
    return boundTexture;
  }

  public static int getTextureUnitCount() {
    initialize();
    return textureUnitCount;
  }
}
