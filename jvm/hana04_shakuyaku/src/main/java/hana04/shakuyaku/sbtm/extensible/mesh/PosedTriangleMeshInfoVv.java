package hana04.shakuyaku.sbtm.extensible.mesh;

import com.google.common.collect.ImmutableList;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.VersionedValue;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.shakuyaku.sbtm.SbtmBaseMesh;
import hana04.shakuyaku.sbtm.SbtmPose;
import hana04.shakuyaku.surface.geometry.trimesh.TriangleMeshInfo;

public class PosedTriangleMeshInfoVv
  extends DerivedVersionedValue<TriangleMeshInfo>
  implements TriangleMeshInfo.Vv {

  public PosedTriangleMeshInfoVv(VersionedValue<SbtmBaseMesh> baseMesh,
                                 VersionedValue<SbtmPose> pose) {
    super(ImmutableList.of(baseMesh, pose),
      ChangePropUtil::largestBetweenIncSelfAndDeps,
      () -> baseMesh.value().pose(pose.value()));
  }
}
