package hana04.formats.mmd.generic.impl.pmd;

import com.google.common.collect.ImmutableList;
import hana04.formats.mmd.generic.api.MmdMaterial;
import hana04.formats.mmd.generic.api.MmdModel;
import hana04.formats.mmd.generic.api.MmdVertex;
import hana04.formats.mmd.pmd.PmdModel;

import java.util.Optional;
import java.util.stream.IntStream;

import static com.google.common.collect.ImmutableList.toImmutableList;

public class PmdModelAdaptor implements MmdModel {
  private final PmdModel pmdModel;
  private final ImmutableList<PmdVertexAdaptor> vertices;
  private final ImmutableList<PmdBoneAdaptor> bones;
  private final ImmutableList<PmdRigidBodyAdaptor> rigidBodies;
  private final ImmutableList<PmdJointAdaptor> joints;
  private final ImmutableList<PmdMorphAdaptor> morphs;
  private final ImmutableList<PmdMaterialAdaptor> materials;

  public PmdModelAdaptor(PmdModel pmdModel) {
    this.pmdModel = pmdModel;
    vertices = IntStream
        .range(0, pmdModel.getVertexCount())
        .mapToObj(index -> new PmdVertexAdaptor(pmdModel, index))
        .collect(toImmutableList());
    bones = IntStream
        .range(0, pmdModel.bones.size())
        .mapToObj(index -> new PmdBoneAdaptor(pmdModel, index))
        .collect(toImmutableList());
    rigidBodies =
        pmdModel.rigidBodies.stream()
            .map(rb -> new PmdRigidBodyAdaptor(rb, pmdModel)).collect(toImmutableList());
    joints = pmdModel.joints.stream().map(PmdJointAdaptor::new).collect(toImmutableList());
    morphs = IntStream
        .range(1, pmdModel.morphs.size())
        .mapToObj(i -> new PmdMorphAdaptor(pmdModel.morphs.get(i), i, pmdModel.morphs.get(0)))
        .collect(toImmutableList());
    materials = IntStream
        .range(0, pmdModel.materials.size())
        .mapToObj(index -> new PmdMaterialAdaptor(pmdModel.materials.get(index), index))
        .collect(toImmutableList());
  }

  @Override
  public String japaneseName() {
    return pmdModel.japaneseName;
  }

  @Override
  public String englishName() {
    return pmdModel.englishName;
  }

  @Override
  public ImmutableList<? extends MmdVertex> vertices() {
    return vertices;
  }

  @Override
  public ImmutableList<PmdBoneAdaptor> bones() {
    return bones;
  }

  @Override
  public ImmutableList<PmdMorphAdaptor> morphs() {
    return morphs;
  }

  @Override
  public ImmutableList<? extends MmdMaterial> materials() {
    return materials;
  }

  @Override
  public Optional<Integer> boneIndex(String japaneseName) {
    int answer = pmdModel.getBoneIndex(japaneseName);
    return answer >= 0 ? Optional.of(answer) : Optional.empty();
  }

  @Override
  public Optional<Integer> morphIndex(String japaneseName) {
    int answer = pmdModel.getMorphIndex(japaneseName);
    return answer > 0 ? Optional.of(answer - 1) : Optional.empty();
  }

  @Override
  public ImmutableList<PmdRigidBodyAdaptor> rigidBodies() {
    return rigidBodies;
  }

  @Override
  public ImmutableList<PmdJointAdaptor> joints() {
    return joints;
  }
}
