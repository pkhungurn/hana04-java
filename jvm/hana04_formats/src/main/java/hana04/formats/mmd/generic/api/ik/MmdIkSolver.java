package hana04.formats.mmd.generic.api.ik;

import hana04.formats.mmd.generic.MmdModelPose;

public interface MmdIkSolver {
  void solve(MmdModelPose pose, MmdIkChain ikChain);

  interface Factory {
    MmdIkSolver create();
  }
}
