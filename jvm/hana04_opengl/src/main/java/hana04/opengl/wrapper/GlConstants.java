package hana04.opengl.wrapper;

import org.lwjgl.opengl.GL30;

public class GlConstants {
  public final static int GL_ARRAY_BUFFER = GL30.GL_ARRAY_BUFFER;
  public final static int GL_ELEMENT_ARRAY_BUFFER = GL30.GL_ELEMENT_ARRAY_BUFFER;

  public final static int GL_POINTS = GL30.GL_POINTS;
  public final static int GL_LINES = GL30.GL_LINES;
  public final static int GL_LINE_LOOP = GL30.GL_LINE_LOOP;
  public final static int GL_TRIANGLES = GL30.GL_TRIANGLES;

  public final static int GL_FLOAT = GL30.GL_FLOAT;
  public final static int GL_UNSIGNED_BYTE = GL30.GL_UNSIGNED_BYTE;
  public final static int GL_UNSIGNED_INT = GL30.GL_UNSIGNED_INT;
  public final static int GL_INT = GL30.GL_INT;
  public static final int GL_R8 = GL30.GL_R8;
  public static final int GL_RGBA8 = GL30.GL_RGBA8;
  public final static int GL_R32F = GL30.GL_R32F;
  public final static int GL_RGB32F = GL30.GL_RGB32F;
  public final static int GL_RGBA32F = GL30.GL_RGBA32F;
  public final static int GL_DEPTH_COMPONENT32F = GL30.GL_DEPTH_COMPONENT32F;
  public final static int GL_DEPTH_COMPONENT24 = GL30.GL_DEPTH_COMPONENT24;
  public final static int GL_DEPTH_COMPONENT16 = GL30.GL_DEPTH_COMPONENT16;
  public final static int GL_DEPTH_COMPONENT = GL30.GL_DEPTH_COMPONENT;

  public final static int GL_RED = GL30.GL_RED;
  public final static int GL_RGB = GL30.GL_RGB;
  public final static int GL_RGBA = GL30.GL_RGBA;

  public final static int GL_NEVER = GL30.GL_NEVER;
  public final static int GL_ALWAYS = GL30.GL_ALWAYS;
  public final static int GL_LESS = GL30.GL_LESS;
  public final static int GL_EQUAL = GL30.GL_EQUAL;
  public final static int GL_GREATER = GL30.GL_GREATER;
  public final static int GL_GEQUAL = GL30.GL_GEQUAL;
  public final static int GL_NOTEQUAL = GL30.GL_NOTEQUAL;
  public final static int GL_LEQUAL = GL30.GL_LEQUAL;

  public final static int GL_FRONT = GL30.GL_FRONT;
  public final static int GL_BACK = GL30.GL_BACK;
  public final static int GL_FRONT_AND_BACK = GL30.GL_FRONT_AND_BACK;

  public final static int GL_ZERO = GL30.GL_ZERO;
  public final static int GL_ONE = GL30.GL_ONE;
  public final static int GL_SRC_COLOR = GL30.GL_SRC_COLOR;
  public final static int GL_ONE_MINUS_SRC_COLOR = GL30.GL_ONE_MINUS_SRC_COLOR;
  public final static int GL_DST_COLOR = GL30.GL_DST_COLOR;
  public final static int GL_ONE_MINUS_DST_COLOR = GL30.GL_ONE_MINUS_DST_COLOR;
  public final static int GL_SRC_ALPHA = GL30.GL_SRC_ALPHA;
  public final static int GL_ONE_MINUS_SRC_ALPHA = GL30.GL_ONE_MINUS_SRC_ALPHA;
  public final static int GL_DST_ALPHA = GL30.GL_DST_ALPHA;
  public final static int GL_ONE_MINUS_DST_ALPHA = GL30.GL_ONE_MINUS_DST_ALPHA;
  public final static int GL_CONSTANT_COLOR = GL30.GL_CONSTANT_COLOR;
  public final static int GL_ONE_MINUS_CONSTANT_COLOR = GL30.GL_ONE_MINUS_CONSTANT_COLOR;
  public final static int GL_CONSTANT_ALPHA = GL30.GL_CONSTANT_ALPHA;
  public final static int GL_ONE_MINUS_CONSTANT_ALPHA = GL30.GL_ONE_MINUS_CONSTANT_ALPHA;
  public final static int GL_SRC_ALPHA_SATURATE = GL30.GL_SRC_ALPHA_SATURATE;

  public final static int GL_REPEAT = GL30.GL_REPEAT;
  public final static int GL_CLAMP_TO_EDGE = GL30.GL_CLAMP_TO_EDGE;
  public final static int GL_NEAREST = GL30.GL_NEAREST;
  public final static int GL_LINEAR = GL30.GL_LINEAR;
  public final static int GL_LINEAR_MIPMAP_LINEAR = GL30.GL_LINEAR_MIPMAP_LINEAR;
  public final static int GL_LINEAR_MIPMAP_NEAREST = GL30.GL_LINEAR_MIPMAP_NEAREST;
  public final static int GL_NEAREST_MIPMAP_LINEAR = GL30.GL_NEAREST_MIPMAP_LINEAR;
  public final static int GL_NEAREST_MIPMAP_NEAREST = GL30.GL_NEAREST_MIPMAP_NEAREST;

  public final static int GL_MAX_COLOR_ATTACHMENTS = GL30.GL_MAX_COLOR_ATTACHMENTS;

  public final static int GL_CW = GL30.GL_CW;
  public final static int GL_CCW = GL30.GL_CCW;

  private GlConstants() {
    // NO-OP
  }
}
