package hana04.shakuyaku.sbtm;

import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;
import java.util.ArrayList;

public class SbtmMorphImpl implements SbtmMorph {
  private String name = "";
  private ArrayList<Integer> vertexIndices = new ArrayList<Integer>();
  private ArrayList<Vector3d> displacements = new ArrayList<>();

  public SbtmMorphImpl() {
    this("");
  }

  public SbtmMorphImpl(String name) {
    this.name = name;
  }

  public void addVertexMorph(int vertexIndex, Vector3d displacement) {
    this.vertexIndices.add(vertexIndex);
    this.displacements.add(new Vector3d(displacement));
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getRecordCount() {
    return displacements.size();
  }

  @Override
  public int getVertexIndex(int recordIndex) {
    return vertexIndices.get(recordIndex);
  }

  @Override
  public Vector3d getDisplacement(int recordIndex) {
    Vector3d v = displacements.get(recordIndex);
    return new Vector3d(v);
  }

  @Override
  public void getDisplacement(int recordIndex, Tuple3d output) {
    output.set(displacements.get(recordIndex));
  }
}
