package hana04.formats.mmd.generic.api.morph;

import com.google.common.collect.ImmutableList;
import hana04.formats.mmd.generic.api.MmdMorph;

import javax.vecmath.Vector4f;

public interface MmdTexCoordMorph extends MmdMorph {
  int texCoordIndex();

  ImmutableList<Offset> offsets();

  interface Offset {
    int vertexIndex();

    Vector4f offset();
  }
}
