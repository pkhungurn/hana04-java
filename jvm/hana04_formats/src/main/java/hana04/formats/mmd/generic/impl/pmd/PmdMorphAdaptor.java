package hana04.formats.mmd.generic.impl.pmd;

import com.google.common.collect.ImmutableList;
import hana04.formats.mmd.common.MmdMorphPanel;
import hana04.formats.mmd.generic.api.morph.MmdBoneMorph;
import hana04.formats.mmd.generic.api.morph.MmdGroupMorph;
import hana04.formats.mmd.generic.api.morph.MmdMaterialMorph;
import hana04.formats.mmd.generic.api.morph.MmdTexCoordMorph;
import hana04.formats.mmd.generic.api.morph.MmdVertexMorph;
import hana04.formats.mmd.pmd.PmdMorph;

import javax.vecmath.Vector3f;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.google.common.collect.ImmutableList.toImmutableList;

public class PmdMorphAdaptor implements MmdVertexMorph {
  private final PmdMorph pmdMorph;
  private final int index;
  private final ImmutableList<Offset> offsets;

  public PmdMorphAdaptor(PmdMorph pmdMorph, int index, PmdMorph baseMorph) {
    this.pmdMorph = pmdMorph;
    this.index = index;
    this.offsets = IntStream.range(0, pmdMorph.vertexIndices.length).mapToObj(i -> new Offset(
        baseMorph.vertexIndices[pmdMorph.vertexIndices[i]],
        pmdMorph.displacements[i]
    )).collect(toImmutableList());
  }

  @Override
  public String japaneseName() {
    return pmdMorph.japaneseName;
  }

  @Override
  public String englishName() {
    return pmdMorph.englishName;
  }

  @Override
  public int index() {
    return index;
  }

  @Override
  public MmdMorphPanel panel() {
    return pmdMorph.panel;
  }

  @Override
  public Kind getKind() {
    return Kind.Vertex;
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
    return Optional.empty();
  }

  @Override
  public Optional<MmdVertexMorph> asVertexMorph() {
    return Optional.of(this);
  }

  @Override
  public ImmutableList<Offset> offsets() {
    return offsets;
  }

  static class Offset implements MmdVertexMorph.Offset {
    private final int vertexIndex;
    private final Vector3f offset;

    Offset(int vertexIndex, Vector3f offset) {
      this.vertexIndex = vertexIndex;
      this.offset = new Vector3f(offset);
    }

    @Override
    public int vertexIndex() {
      return vertexIndex;
    }

    @Override
    public Vector3f offset() {
      return offset;
    }
  }
}
