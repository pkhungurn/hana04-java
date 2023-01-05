package hana04.shakuyaku.sbtm;

import hana04.base.changeprop.VersionedValue;
import hana04.shakuyaku.surface.geometry.trimesh.TriangleMeshInfo;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import java.util.List;
import java.util.Optional;

public interface SbtmBaseMesh extends TriangleMeshInfo {
  int getBoneCount();

  int getMorphCount();

  SbtmBone getBone(int index);

  SbtmMorph getMorph(int index);

  SbtmDataPosed pose(SbtmPose pose);

  Point3d getPosedVertexPosition(SbtmPose pose, int vertexIndex);

  int getVertexMorphCount(int vertexIndex);

  void getVertexMorphDisplacement(int vertexIndex, int vertexMorphOrder, Tuple3d output);

  int getMorphIndex(int vertexIndex, int vertexMorphOrder);

  SbtmSkinningType getVertexSkinningType(int vertexIndex);

  int getVertexBoneCount(int vertexIndex);

  int getVertexBoneIndex(int vertexIndex, int vertexBoneIndex);

  double getVertexBoneWeight(int vertexIndex, int vertexBoneIndex);

  List<Matrix4d> getPosedBoneToWorldMatrices(SbtmPose pose);

  List<Matrix4d> getPosedBoneMatricesForBlending(SbtmPose pose);

  List<Matrix4d> getInverseRestBoneMatrices();

  Optional<SbtmSdefParams> getSdefParams(int vertexIndex);

  Point3d getRestBonePosition(int boneIndex);

  interface Vv extends VersionedValue<SbtmBaseMesh> {
    // NO-OP
  }
}
