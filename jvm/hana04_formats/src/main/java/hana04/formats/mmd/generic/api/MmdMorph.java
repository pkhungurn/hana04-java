package hana04.formats.mmd.generic.api;

import hana04.formats.mmd.common.MmdMorphPanel;
import hana04.formats.mmd.generic.api.morph.MmdBoneMorph;
import hana04.formats.mmd.generic.api.morph.MmdGroupMorph;
import hana04.formats.mmd.generic.api.morph.MmdMaterialMorph;
import hana04.formats.mmd.generic.api.morph.MmdTexCoordMorph;
import hana04.formats.mmd.generic.api.morph.MmdVertexMorph;

import java.util.Optional;

public interface MmdMorph {
  String japaneseName();

  String englishName();

  int index();

  MmdMorphPanel panel();

  Kind getKind();

  Optional<MmdBoneMorph> asBoneMorph();

  Optional<MmdGroupMorph> asGroupMorph();

  Optional<MmdMaterialMorph> asMaterialMorph();

  Optional<MmdTexCoordMorph> asTexCoordMorph();

  Optional<MmdVertexMorph> asVertexMorph();

  enum Kind {
    Vertex,
    Bone,
    TexCoord,
    Material,
    Group
  }
}
