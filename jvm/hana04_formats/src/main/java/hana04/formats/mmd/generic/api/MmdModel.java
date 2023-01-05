package hana04.formats.mmd.generic.api;

import com.google.common.collect.ImmutableList;
import hana04.formats.mmd.generic.api.physics.MmdJoint;
import hana04.formats.mmd.generic.api.physics.MmdRigidBody;

import java.util.Optional;

public interface MmdModel {
  String japaneseName();

  String englishName();

  ImmutableList<? extends MmdVertex> vertices();

  ImmutableList<? extends MmdBone> bones();

  ImmutableList<? extends MmdMorph> morphs();

  ImmutableList<? extends MmdMaterial> materials();

  Optional<Integer> boneIndex(String japaneseName);

  Optional<Integer> morphIndex(String japaneseName);

  default Optional<? extends MmdMorph> getMorph(String name) {
    return morphIndex(name).map(index -> morphs().get(index));
  }

  default Optional<? extends MmdBone> getBone(String name) {
    return boneIndex(name).map(index -> bones().get(index));
  }

  ImmutableList<? extends MmdRigidBody> rigidBodies();

  ImmutableList<? extends MmdJoint> joints();
}
