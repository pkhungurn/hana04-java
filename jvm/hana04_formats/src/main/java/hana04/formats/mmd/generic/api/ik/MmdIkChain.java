package hana04.formats.mmd.generic.api.ik;

import com.google.common.collect.ImmutableList;

public interface MmdIkChain {
  int boneIndex();

  int effectorBoneIndex();

  int iterationCount();

  float iterationAngleLimitRad();

  ImmutableList<? extends MmdIkLink> ikLinks();
}
