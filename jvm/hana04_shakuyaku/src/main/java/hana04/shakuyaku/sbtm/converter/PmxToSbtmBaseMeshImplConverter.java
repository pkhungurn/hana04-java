package hana04.shakuyaku.sbtm.converter;

import hana04.formats.mmd.generic.api.MmdModel;
import hana04.formats.mmd.generic.api.MmdMorph;
import hana04.formats.mmd.generic.api.morph.MmdGroupMorph;
import hana04.formats.mmd.pmx.PmxBone;
import hana04.formats.mmd.pmx.PmxModel;
import hana04.formats.mmd.pmx.PmxMorph;
import hana04.formats.mmd.pmx.PmxVertex;
import hana04.formats.mmd.pmx.morph.GroupMorph;
import hana04.formats.mmd.pmx.morph.VertexMorph;
import hana04.gfxbase.gfxtype.TupleUtil;
import hana04.gfxbase.gfxtype.VecMathDUtil;
import hana04.shakuyaku.sbtm.SbtmBaseMeshImpl;
import hana04.shakuyaku.sbtm.SbtmSdefParams;
import hana04.shakuyaku.sbtm.SbtmSkinningType;

import javax.vecmath.Quat4d;
import java.util.Optional;

public class PmxToSbtmBaseMeshImplConverter {
  public static SbtmBaseMeshImpl convert(PmxModel pmxModel) {
    SbtmBaseMeshImpl.Builder sbtm = SbtmBaseMeshImpl.builder();

    // Adding bones.
    for (int i = 0; i < pmxModel.getBoneCount(); i++) {
      PmxBone bone = pmxModel.getBone(i);
      PmxBone parentBone = null;
      if (bone.parentIndex >= 0) {
        parentBone = pmxModel.getBone(bone.parentIndex);
      }
      String parentBoneName;
      if (parentBone == bone || parentBone == null) {
        parentBoneName = "";
      } else {
        parentBoneName = parentBone.japaneseName;
      }
      sbtm.addBone(
          bone.japaneseName,
          TupleUtil.toPoint3d(bone.displacementFromParent),
          new Quat4d(0, 0, 0, 1),
          parentBoneName);
    }

    // Adding vertices.
    for (int i = 0; i < pmxModel.getVertexCount(); i++) {
      PmxVertex vertex = pmxModel.getVertex(i);
      sbtm.addPosition(vertex.position.x, vertex.position.y, vertex.position.z);
      sbtm.addNormal(vertex.normal.x, vertex.normal.y, vertex.normal.z);
      sbtm.addTexCoord(vertex.texCoords.x, 1 - vertex.texCoords.y);
      if (vertex.isUsingLinearBlendSkinning()) {
        sbtm.addVertexSkinningRecord(SbtmSkinningType.LINEAR_BLEND);
      } else if (vertex.isUsingSdef()) {
        sbtm.addVertexSdefSkinningRecord(new SbtmSdefParams(vertex.C, vertex.R0, vertex.R1));
      } else {
        throw new RuntimeException("Other types of skinning algorithm other than linear blending and SDEF " +
            "are not supports yet.");
      }
      for (int j = 0; j < vertex.boneIndices.length; j++) {
        if (vertex.boneIndices[j] != -1) {
          sbtm.addVertexBoneWeight(vertex.boneIndices[j], vertex.boneWeights[j]);
        }
      }
    }

    // Adding triangles.
    for (int i = 0; i < pmxModel.getTriangleCount(); i++) {
      int a = pmxModel.getVertexIndex(3 * i + 0);
      int b = pmxModel.getVertexIndex(3 * i + 1);
      int c = pmxModel.getVertexIndex(3 * i + 2);
      sbtm.addTriangle(a, b, c);
    }

    // Adding morphs.
    for (int i = 0; i < pmxModel.getMorphCount(); i++) {
      PmxMorph morph = pmxModel.getMorph(i);
      if (!(morph instanceof VertexMorph) && !(morph instanceof GroupMorph)) {
        continue;
      }
      if (morph instanceof VertexMorph) {
        VertexMorph vertexMorph = (VertexMorph) morph;
        sbtm.addNewMorph(vertexMorph.japaneseName);
        for (int j = 0; j < vertexMorph.offsets.size(); j++) {
          VertexMorph.Offset offset = vertexMorph.offsets.get(j);
          sbtm.addVertexMorph(offset.vertexIndex, TupleUtil.toVector3d(offset.displacement));
        }
      }
      if (morph instanceof GroupMorph) {
        GroupMorph groupMorph = (GroupMorph) morph;
        boolean canConvert = groupMorph.offsets.stream().allMatch(offset ->
            offset.morphIndex != 255
                && offset.morphIndex != 65535
                && pmxModel.getMorph(offset.morphIndex) instanceof VertexMorph
        );
        if (!canConvert) {
          continue;
        }
        sbtm.addNewMorph(groupMorph.japaneseName);
        for (GroupMorph.Offset offset : groupMorph.offsets) {
          VertexMorph vertexMorph = (VertexMorph) pmxModel.getMorph(offset.morphIndex);
          for (VertexMorph.Offset vertOffset : vertexMorph.offsets) {
            sbtm.addVertexMorph(
                vertOffset.vertexIndex,
                VecMathDUtil.scale(
                    offset.morphFraction,
                    TupleUtil.toVector3d(vertOffset.displacement)));
          }
        }
      }
    }

    return sbtm.build();
  }

  public static boolean isMorphConvertible(PmxModel pmxModel, String morphName) {
    return isMorphConvertible(pmxModel, pmxModel.getMorph(morphName));
  }

  public static boolean isMorphConvertible(PmxModel pmxModel, PmxMorph pmxMorph) {
    if (pmxMorph instanceof VertexMorph) {
      return true;
    }
    if (!(pmxMorph instanceof GroupMorph)) {
      return false;
    }
    GroupMorph groupMorph = (GroupMorph) pmxMorph;
    return groupMorph.offsets.stream().allMatch(offset ->
        pmxModel.getMorph(offset.morphIndex) instanceof VertexMorph);
  }

  public static boolean isMorphConvertible(MmdModel mmdModel, String morphName) {
    Optional<? extends MmdMorph> mmdMorph = mmdModel.getMorph(morphName);
    if (mmdMorph.isEmpty()) {
      return false;
    }
    MmdMorph morph = mmdMorph.get();
    var kind = morph.getKind();
    if (kind.equals(MmdMorph.Kind.Vertex)) {
      return true;
    }
    if (!kind.equals(MmdMorph.Kind.Group)) {
      return false;
    }
    MmdGroupMorph groupMorph = morph.asGroupMorph().get();
    return groupMorph
        .offsets()
        .stream()
        .allMatch(offset -> mmdModel.morphs().get(offset.morphIndex()).getKind().equals(MmdMorph.Kind.Vertex));
  }

  public static boolean isMorphConvertible(MmdModel mmdModel, MmdMorph morph) {
    var kind = morph.getKind();
    if (kind.equals(MmdMorph.Kind.Vertex)) {
      return true;
    }
    if (!kind.equals(MmdMorph.Kind.Group)) {
      return false;
    }
    MmdGroupMorph groupMorph = morph.asGroupMorph().get();
    return groupMorph
        .offsets()
        .stream()
        .allMatch(offset -> mmdModel.morphs().get(offset.morphIndex()).getKind().equals(MmdMorph.Kind.Vertex));
  }
}
