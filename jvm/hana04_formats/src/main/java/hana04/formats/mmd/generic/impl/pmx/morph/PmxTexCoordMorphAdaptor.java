package hana04.formats.mmd.generic.impl.pmx.morph;

import com.google.common.collect.ImmutableList;
import hana04.formats.mmd.common.MmdMorphPanel;
import hana04.formats.mmd.generic.api.morph.MmdBoneMorph;
import hana04.formats.mmd.generic.api.morph.MmdGroupMorph;
import hana04.formats.mmd.generic.api.morph.MmdMaterialMorph;
import hana04.formats.mmd.generic.api.morph.MmdTexCoordMorph;
import hana04.formats.mmd.generic.api.morph.MmdVertexMorph;
import hana04.formats.mmd.pmx.morph.TexCoordMorph;

import javax.vecmath.Vector4f;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.google.common.collect.ImmutableList.toImmutableList;

public class PmxTexCoordMorphAdaptor implements MmdTexCoordMorph {
  private final TexCoordMorph texCoordMorph;
  private final ImmutableList<MmdTexCoordMorph.Offset> offsets;

  public PmxTexCoordMorphAdaptor(TexCoordMorph texCoordMorph) {
    this.texCoordMorph = texCoordMorph;
    this.offsets = IntStream.range(0, texCoordMorph.offsets.size()).mapToObj(Offset::new).collect(toImmutableList());
  }

  @Override
  public int texCoordIndex() {
    return texCoordMorph.texCoordId;
  }

  @Override
  public ImmutableList<MmdTexCoordMorph.Offset> offsets() {
    return offsets;
  }

  @Override
  public String japaneseName() {
    return texCoordMorph.japaneseName;
  }

  @Override
  public String englishName() {
    return texCoordMorph.englishName;
  }

  @Override
  public int index() {
    return texCoordMorph.morphIndex;
  }

  @Override
  public MmdMorphPanel panel() {
    return texCoordMorph.panel;
  }

  @Override
  public Kind getKind() {
    return Kind.TexCoord;
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
    return Optional.empty();
  }

  @Override
  public Optional<MmdTexCoordMorph> asTexCoordMorph() {
    return Optional.of(this);
  }

  @Override
  public Optional<MmdVertexMorph> asVertexMorph() {
    return Optional.empty();
  }

  class Offset implements MmdTexCoordMorph.Offset {
    private final int index;

    Offset(int index) {
      this.index = index;
    }

    @Override
    public int vertexIndex() {
      return texCoordMorph.offsets.get(index).vertexIndex;
    }

    @Override
    public Vector4f offset() {
      return texCoordMorph.offsets.get(index).value;
    }
  }
}
