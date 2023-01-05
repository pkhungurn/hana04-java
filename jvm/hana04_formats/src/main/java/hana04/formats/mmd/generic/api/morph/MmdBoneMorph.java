package hana04.formats.mmd.generic.api.morph;

import com.google.common.collect.ImmutableList;
import hana04.formats.mmd.generic.api.MmdMorph;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public interface MmdBoneMorph extends MmdMorph {
  ImmutableList<Offset> offsets();

  interface Offset {
    int boneIndex();
    Vector3f translation();
    Quat4f rotation();

  }
}
