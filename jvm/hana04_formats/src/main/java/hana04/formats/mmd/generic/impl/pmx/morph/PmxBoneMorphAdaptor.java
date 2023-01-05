package hana04.formats.mmd.generic.impl.pmx.morph;

import com.google.common.collect.ImmutableList;
import hana04.formats.mmd.common.MmdMorphPanel;
import hana04.formats.mmd.generic.api.morph.MmdBoneMorph;
import hana04.formats.mmd.generic.api.morph.MmdGroupMorph;
import hana04.formats.mmd.generic.api.morph.MmdMaterialMorph;
import hana04.formats.mmd.generic.api.morph.MmdTexCoordMorph;
import hana04.formats.mmd.generic.api.morph.MmdVertexMorph;
import hana04.formats.mmd.pmx.morph.BoneMorph;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.google.common.collect.ImmutableList.toImmutableList;

public class PmxBoneMorphAdaptor implements MmdBoneMorph {
  private final BoneMorph boneMorph;
  private final ImmutableList<MmdBoneMorph.Offset> offsets;

  public PmxBoneMorphAdaptor(BoneMorph boneMorph) {
    this.boneMorph = boneMorph;
    this.offsets = IntStream.range(0, boneMorph.offsets.size()).mapToObj(Offset::new).collect(toImmutableList());
  }

  @Override
  public String japaneseName() {
    return boneMorph.japaneseName;
  }

  @Override
  public String englishName() {
    return boneMorph.englishName;
  }

  @Override
  public int index() {
    return boneMorph.morphIndex;
  }

  @Override
  public MmdMorphPanel panel() {
    return boneMorph.panel;
  }

  @Override
  public Kind getKind() {
    return Kind.Bone;
  }

  @Override
  public Optional<MmdBoneMorph> asBoneMorph() {
    return Optional.of(this);
  }

  @Override
  public Optional<MmdGroupMorph> asGroupMorph() {
    return Optional.empty();
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
  public ImmutableList<MmdBoneMorph.Offset> offsets() {
    return offsets;
  }

  class Offset implements MmdBoneMorph.Offset {
    private final int index;

    Offset(int index) {
      this.index = index;
    }

    @Override
    public int boneIndex() {
      return boneMorph.offsets.get(index).boneIndex;
    }

    @Override
    public Vector3f translation() {
      return new Vector3f(boneMorph.offsets.get(index).position);
    }

    @Override
    public Quat4f rotation() {
      return boneMorph.offsets.get(index).rotation;
    }
  }
}
