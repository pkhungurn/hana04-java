package hana04.shakuyaku.sbtm;

import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

public interface SbtmMorph {
  String getName();

  int getRecordCount();

  int getVertexIndex(int recordIndex);

  Vector3d getDisplacement(int recordIndex);

  void getDisplacement(int recordIndex, Tuple3d output);
}
