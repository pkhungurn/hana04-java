package hana04.formats.mmd.generic.impl.pmx.morph;

import hana04.formats.mmd.common.MmdMorphPanel;
import hana04.formats.mmd.generic.api.morph.MmdBoneMorph;
import hana04.formats.mmd.generic.api.morph.MmdGroupMorph;
import hana04.formats.mmd.generic.api.morph.MmdMaterialMorph;
import hana04.formats.mmd.generic.api.morph.MmdTexCoordMorph;
import hana04.formats.mmd.generic.api.morph.MmdVertexMorph;
import hana04.formats.mmd.pmx.morph.MaterialMorph;

import java.util.Optional;

public class PmxMaterialMorphAdaptor implements MmdMaterialMorph {
  private final MaterialMorph materialMorph;

  public PmxMaterialMorphAdaptor(MaterialMorph materialMorph) {
    this.materialMorph = materialMorph;
  }

  @Override
  public String japaneseName() {
    return materialMorph.japaneseName;
  }

  @Override
  public String englishName() {
    return materialMorph.englishName;
  }

  @Override
  public int index() {
    return materialMorph.morphIndex;
  }

  @Override
  public MmdMorphPanel panel() {
    return materialMorph.panel;
  }

  @Override
  public Kind getKind() {
    return Kind.Material;
  }

  @Override
  public Optional<MmdBoneMorph> asBoneMorph() {
    return Optional.empty();
  }

  @Override
  public Optional<MmdGroupMorph> asGroupMorph() {
    return Optional.empty();
  }

  @Override
  public Optional<MmdMaterialMorph> asMaterialMorph() {
    return Optional.of(this);
  }

  @Override
  public Optional<MmdTexCoordMorph> asTexCoordMorph() {
    return Optional.empty();
  }

  @Override
  public Optional<MmdVertexMorph> asVertexMorph() {
    return Optional.empty();
  }
}
