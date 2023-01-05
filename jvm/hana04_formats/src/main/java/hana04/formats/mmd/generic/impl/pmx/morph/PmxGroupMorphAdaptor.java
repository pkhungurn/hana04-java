package hana04.formats.mmd.generic.impl.pmx.morph;

import com.google.common.collect.ImmutableList;
import hana04.formats.mmd.common.MmdMorphPanel;
import hana04.formats.mmd.generic.api.morph.MmdBoneMorph;
import hana04.formats.mmd.generic.api.morph.MmdGroupMorph;
import hana04.formats.mmd.generic.api.morph.MmdMaterialMorph;
import hana04.formats.mmd.generic.api.morph.MmdTexCoordMorph;
import hana04.formats.mmd.generic.api.morph.MmdVertexMorph;
import hana04.formats.mmd.pmx.morph.GroupMorph;

import java.util.Optional;
import java.util.stream.IntStream;

import static com.google.common.collect.ImmutableList.toImmutableList;

public class PmxGroupMorphAdaptor implements MmdGroupMorph {
  private final GroupMorph groupMorph;
  private final ImmutableList<MmdGroupMorph.Offset> offsets;

  public PmxGroupMorphAdaptor(GroupMorph groupMorph) {
    this.groupMorph = groupMorph;
    this.offsets = IntStream.range(0, groupMorph.offsets.size()).mapToObj(Offset::new).collect(toImmutableList());
  }

  @Override
  public String japaneseName() {
    return groupMorph.japaneseName;
  }

  @Override
  public String englishName() {
    return groupMorph.englishName;
  }

  @Override
  public int index() {
    return groupMorph.morphIndex;
  }

  @Override
  public MmdMorphPanel panel() {
    return groupMorph.panel;
  }

  @Override
  public Kind getKind() {
    return Kind.Group;
  }

  @Override
  public Optional<MmdBoneMorph> asBoneMorph() {
    return Optional.empty();
  }

  @Override
  public Optional<MmdGroupMorph> asGroupMorph() {
    return Optional.of(this);
  }

  @Override
  public Optional<MmdMaterialMorph> asMaterialMorph() {
    return Optional.empty();
  }

  @Override
  public Optional<MmdTexCoordMorph> asTexCoordMorph() {
    return Optional.empty();
  }

  @Override
  public Optional<MmdVertexMorph> asVertexMorph() {
    return Optional.empty();
  }

  @Override
  public ImmutableList<MmdGroupMorph.Offset> offsets() {
    return offsets;
  }

  class Offset implements MmdGroupMorph.Offset {
    private final int index;

    Offset(int index) {
      this.index = index;
    }

    @Override
    public int morphIndex() {
      return groupMorph.offsets.get(index).morphIndex;
    }

    @Override
    public float fraction() {
      return groupMorph.offsets.get(index).morphFraction;
    }
  }
}
