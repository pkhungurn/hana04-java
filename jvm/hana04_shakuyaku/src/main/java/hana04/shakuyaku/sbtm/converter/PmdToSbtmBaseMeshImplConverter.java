package hana04.shakuyaku.sbtm.converter;

import hana04.formats.mmd.pmd.PmdBone;
import hana04.formats.mmd.pmd.PmdModel;
import hana04.formats.mmd.pmd.PmdMorph;
import hana04.gfxbase.gfxtype.TupleUtil;
import hana04.shakuyaku.sbtm.SbtmBaseMeshImpl;
import hana04.shakuyaku.sbtm.SbtmSkinningType;

import javax.vecmath.Quat4d;
import javax.vecmath.Vector3f;

public class PmdToSbtmBaseMeshImplConverter {
  public static SbtmBaseMeshImpl convert(PmdModel pmdModel) {
    SbtmBaseMeshImpl.Builder sbtm = SbtmBaseMeshImpl.builder();

    // Add bones.
    Vector3f displacement = new Vector3f();
    for (int i = 0; i < pmdModel.bones.size(); i++) {
      PmdBone bone = pmdModel.bones.get(i);
      bone.getDisplacementFromParent(displacement);
      sbtm.addBone(bone.japaneseName,
        TupleUtil.toPoint3d(displacement),
        new Quat4d(0, 0, 0, 1),
        bone.parent == null ? "" : bone.parent.japaneseName);
    }

    // Add vertices
    for (int i = 0; i < pmdModel.getVertexCount(); i++) {
      sbtm.addPosition(
        pmdModel.positions.get(3 * i + 0),
        pmdModel.positions.get(3 * i + 1),
        pmdModel.positions.get(3 * i + 2));
      sbtm.addNormal(
        pmdModel.normals.get(3 * i + 0),
        pmdModel.normals.get(3 * i + 1),
        pmdModel.normals.get(3 * i + 2));
      sbtm.addTexCoord(
        pmdModel.texCoords.get(2 * i + 0),
        pmdModel.texCoords.get(2 * i + 1));
      sbtm.addVertexSkinningRecord(SbtmSkinningType.LINEAR_BLEND);
      double weight = pmdModel.vertexBoneBlendWeights.get(2 * i + 0);
      sbtm.addVertexBoneWeight(pmdModel.vertexBoneIndices.get(2 * i + 0), weight);
      sbtm.addVertexBoneWeight(pmdModel.vertexBoneIndices.get(2 * i + 1), 1 - weight);
    }

    // Add triangles
    for (int i = 0; i < pmdModel.getTriangleCount(); i++) {
      sbtm.addTriangle(
        pmdModel.triangleVertexIndices.get(3 * i + 0),
        pmdModel.triangleVertexIndices.get(3 * i + 1),
        pmdModel.triangleVertexIndices.get(3 * i + 2));
    }

    // Add morphs
    PmdMorph baseMorph = pmdModel.morphs.get(0);
    for (int i = 1; i < pmdModel.morphs.size(); i++) {
      PmdMorph morph = pmdModel.morphs.get(i);
      sbtm.addNewMorph(morph.japaneseName);
      for (int j = 0; j < morph.vertexIndices.length; j++) {
        int index = baseMorph.vertexIndices[morph.vertexIndices[j]];
        sbtm.addVertexMorph(index, TupleUtil.toVector3d(morph.displacements[j]));
      }
    }

    return sbtm.build();
  }
}

