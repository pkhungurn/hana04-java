package hana04.opengl.wrapper;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple2f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Tuple4d;
import javax.vecmath.Tuple4f;

public interface GlUniform {
  void set1Int(int x);

  void set2Int(int x, int y);

  void set3Int(int x, int y, int z);

  void set4Int(int x, int y, int z, int w);

  void set1Float(float x);

  default void set1Float(double x) {
    set1Float((float) x);
  }

  void set2Float(float x, float y);

  default void set2Float(double x, double y) {
    set2Float((float) x, (float) y);
  }

  void set3Float(float x, float y, float z);

  default void set3Float(double x, double y, double z) {
    set3Float((float) x, (float) y, (float) z);
  }

  void set4Float(float x, float y, float z, float w);

  default void set4Float(double x, double y, double z, double w) {
    set4Float((float) x, (float) y, (float) z, (float) w);
  }

  default void setTuple2(Tuple2f v) {
    set2Float(v.x, v.y);
  }

  default void setTuple2(Tuple2d v) {
    set2Float((float) v.x, (float) v.y);
  }

  default void setTuple3(Tuple3f v) {
    set3Float(v.x, v.y, v.z);
  }

  default void setTuple3(Tuple3d v) {
    set3Float((float) v.x, (float) v.y, (float) v.z);
  }

  default void setTuple4(Tuple4f v) {
    set4Float(v.x, v.y, v.z, v.w);
  }

  default void setTuple4(Tuple4d v) {
    set4Float((float) v.x, (float) v.y, (float) v.z, (float) v.w);
  }

  void setMatrix4(Matrix4f mat);

  void setMatrix4(Matrix4d mat);

  void setMatrix4(Object mat);

  void setMatrix3(Matrix3f mat);

  void setMatrix3(Matrix3d mat);

  void setMatrix3(Matrix4f mat);

  void setMatrix3(Matrix4d mat);

  int getLocation();

  int getType();

  int getSize();

  String getName();

  GlProgram getProgram();

  boolean getIsRowMajor();
}
