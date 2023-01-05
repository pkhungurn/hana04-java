package hana04.formats.mmd.generic.api.morph;

import com.google.common.collect.ImmutableList;
import hana04.formats.mmd.generic.api.MmdMorph;

import javax.vecmath.Vector3f;

public interface MmdVertexMorph extends MmdMorph {
  ImmutableList<? extends Offset> offsets();

  interface Offset {
    int vertexIndex();

    Vector3f offset();
  }
}
