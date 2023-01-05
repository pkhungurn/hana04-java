package hana04.formats.mmd.generic.impl.pmx.morph;

import com.google.common.collect.ImmutableList;
import hana04.formats.mmd.common.MmdMorphPanel;
import hana04.formats.mmd.generic.api.morph.MmdBoneMorph;
import hana04.formats.mmd.generic.api.morph.MmdGroupMorph;
import hana04.formats.mmd.generic.api.morph.MmdMaterialMorph;
import hana04.formats.mmd.generic.api.morph.MmdTexCoordMorph;
import hana04.formats.mmd.generic.api.morph.MmdVertexMorph;
import hana04.formats.mmd.pmx.morph.VertexMorph;

import javax.vecmath.Vector3f;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.google.common.collect.ImmutableList.toImmutableList;

public class PmxVertexMorphAdaptor implements MmdVertexMorph {
  private final VertexMorph vertexMorph;
  private final ImmutableList<MmdVertexMorph.Offset> offsets;

  public PmxVertexMorphAdaptor(VertexMorph vertexMorph) {
    this.vertexMorph = vertexMorph;
    offsets = IntStream.range(0, vertexMorph.offsets.size()).mapToObj(Offset::new).collect(toImmutableList());
  }

  @Override
  public ImmutableList<MmdVertexMorph.Offset> offsets() {
    return offsets;
  }

  @Override
  public String japaneseName() {
    return vertexMorph.japaneseName;
  }

  @Override
  public String englishName() {
    return vertexMorph.englishName;
  }

  @Override
  public int index() {
    return vertexMorph.morphIndex;
  }

  @Override
  public MmdMorphPanel panel() {
    return vertexMorph.panel;
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

  class Offset implements MmdVertexMorph.Offset {
    private final int index;

    Offset(int index) {
      this.index = index;
    }

    @Override
    public int vertexIndex() {
      return vertexMorph.offsets.get(index).vertexIndex;
    }

    @Override
    public Vector3f offset() {
      return vertexMorph.offsets.get(index).displacement;
    }
  }
}
