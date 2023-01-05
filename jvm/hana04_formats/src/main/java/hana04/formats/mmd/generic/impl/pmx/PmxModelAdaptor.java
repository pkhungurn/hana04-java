package hana04.formats.mmd.generic.impl.pmx;

import com.google.common.collect.ImmutableList;
import hana04.formats.mmd.generic.api.MmdBone;
import hana04.formats.mmd.generic.api.MmdMaterial;
import hana04.formats.mmd.generic.api.MmdModel;
import hana04.formats.mmd.generic.api.MmdMorph;
import hana04.formats.mmd.generic.api.MmdVertex;
import hana04.formats.mmd.generic.api.physics.MmdJoint;
import hana04.formats.mmd.generic.api.physics.MmdRigidBody;
import hana04.formats.mmd.generic.impl.pmx.morph.PmxBoneMorphAdaptor;
import hana04.formats.mmd.generic.impl.pmx.morph.PmxGroupMorphAdaptor;
import hana04.formats.mmd.generic.impl.pmx.morph.PmxMaterialMorphAdaptor;
import hana04.formats.mmd.generic.impl.pmx.morph.PmxTexCoordMorphAdaptor;
import hana04.formats.mmd.generic.impl.pmx.morph.PmxVertexMorphAdaptor;
import hana04.formats.mmd.pmx.PmxModel;
import hana04.formats.mmd.pmx.PmxMorph;
import hana04.formats.mmd.pmx.morph.BoneMorph;
import hana04.formats.mmd.pmx.morph.GroupMorph;
import hana04.formats.mmd.pmx.morph.MaterialMorph;
import hana04.formats.mmd.pmx.morph.TexCoordMorph;
import hana04.formats.mmd.pmx.morph.VertexMorph;

import java.util.Optional;
import java.util.stream.IntStream;

import static com.google.common.collect.ImmutableList.toImmutableList;

public class PmxModelAdaptor implements MmdModel {
  private final PmxModel pmxModel;
  private final ImmutableList<MmdVertex> vertices;
  private final ImmutableList<MmdBone> bones;
  private final ImmutableList<MmdMorph> morphs;
  private final ImmutableList<MmdRigidBody> rigidBodies;
  private final ImmutableList<MmdJoint> joints;
  private final ImmutableList<MmdMaterial> materials;

  public PmxModelAdaptor(PmxModel pmxModel) {
    this.pmxModel = pmxModel;
    this.vertices = IntStream
        .range(0, pmxModel.getVertexCount())
        .mapToObj(pmxModel::getVertex)
        .map(PmxVertexAdaptor::new)
        .collect(toImmutableList());
    this.bones = IntStream
        .range(0, pmxModel.getBoneCount())
        .mapToObj(pmxModel::getBone)
        .map(bone -> new PmxBoneAdaptor(bone, pmxModel))
        .collect(toImmutableList());
    this.morphs = IntStream
        .range(0, pmxModel.getMorphCount())
        .mapToObj(pmxModel::getMorph)
        .map(PmxModelAdaptor::createMorphAdaptor)
        .collect(toImmutableList());
    this.rigidBodies = IntStream
        .range(0, pmxModel.getRigidBodyCount())
        .mapToObj(pmxModel::getRigidBody)
        .map(PmxRigidBodyAdaptor::new)
        .collect(toImmutableList());
    this.joints = IntStream
        .range(0, pmxModel.getJointCount())
        .mapToObj(pmxModel::getJoint)
        .map(PmxJointAdaptor::new)
        .collect(toImmutableList());
    this.materials = IntStream
        .range(0, pmxModel.getMaterialCount())
        .mapToObj(index -> new PmxMaterialAdaptor(pmxModel, index))
        .collect(toImmutableList());
  }

  private static MmdMorph createMorphAdaptor(PmxMorph morph) {
    if (morph instanceof VertexMorph) {
      return new PmxVertexMorphAdaptor((VertexMorph) morph);
    } else if (morph instanceof BoneMorph) {
      return new PmxBoneMorphAdaptor((BoneMorph) morph);
    } else if (morph instanceof GroupMorph) {
      return new PmxGroupMorphAdaptor((GroupMorph) morph);
    } else if (morph instanceof TexCoordMorph) {
      return new PmxTexCoordMorphAdaptor((TexCoordMorph) morph);
    } else if (morph instanceof MaterialMorph) {
      return new PmxMaterialMorphAdaptor((MaterialMorph) morph);
    } else {
      throw new RuntimeException("This line should not be readable!!!");
    }
  }

  @Override
  public String japaneseName() {
    return pmxModel.getJapaneseName();
  }

  @Override
  public String englishName() {
    return pmxModel.getEnglishName();
  }

  @Override
  public ImmutableList<? extends MmdVertex> vertices() {
    return vertices;
  }

  @Override
  public ImmutableList<MmdBone> bones() {
    return bones;
  }

  @Override
  public ImmutableList<MmdMorph> morphs() {
    return morphs;
  }

  @Override
  public ImmutableList<? extends MmdMaterial> materials() {
    return materials;
  }

  @Override
  public Optional<Integer> boneIndex(String japaneseName) {
    return Optional.ofNullable(pmxModel.getBone(japaneseName)).map(bone -> bone.boneIndex);
  }

  @Override
  public Optional<Integer> morphIndex(String japaneseName) {
    return Optional.ofNullable(pmxModel.getMorph(japaneseName)).map(morph -> morph.morphIndex);
  }

  @Override
  public ImmutableList<MmdRigidBody> rigidBodies() {
    return rigidBodies;
  }

  @Override
  public ImmutableList<MmdJoint> joints() {
    return joints;
  }

  public PmxModel getPmxModel() {
    return pmxModel;
  }
}
