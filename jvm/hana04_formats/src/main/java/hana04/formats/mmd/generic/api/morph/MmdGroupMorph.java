package hana04.formats.mmd.generic.api.morph;

import com.google.common.collect.ImmutableList;
import hana04.formats.mmd.generic.api.MmdMorph;

public interface MmdGroupMorph extends MmdMorph {
  ImmutableList<Offset> offsets();

  interface Offset {
    int morphIndex();

    float fraction();
  }
}
