package hana04.shakuyaku.surface;

import hana04.base.changeprop.VersionedValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface SurfacePatchIntervalInfo {
  int getPatchIntervalCount();

  PatchInterval getPatchInterval(int index);

  PatchInterval mapPatchToPatchInterval(int patchIndex);

  List<? extends PatchInterval> patchIntervals();

  class FromList implements SurfacePatchIntervalInfo {
    private final List<? extends PatchInterval> patchIntervals;
    private final List<Integer> intervalBoundaries;

    public FromList(List<? extends PatchInterval> patchIntervals) {
      this.patchIntervals = patchIntervals;
      intervalBoundaries = new ArrayList<>();
      for (PatchInterval patchInterval : patchIntervals) {
        intervalBoundaries.add(patchInterval.endPatchIndex() - 1);
      }
    }

    @Override
    public int getPatchIntervalCount() {
      return patchIntervals.size();
    }

    @Override
    public PatchInterval getPatchInterval(int index) {
      return patchIntervals.get(index);
    }

    @Override
    public PatchInterval mapPatchToPatchInterval(int patchIndex) {
      int intervalIndex = Collections.binarySearch(intervalBoundaries, patchIndex);
      if (intervalIndex < 0) {
        intervalIndex = -(intervalIndex + 1);
      }
      return patchIntervals.get(intervalIndex);
    }

    @Override
    public List<? extends PatchInterval> patchIntervals() {
      return patchIntervals;
    }
  }

  interface Vv extends VersionedValue<SurfacePatchIntervalInfo> {
    // NO-OP
  }

  class VvProxy implements SurfacePatchIntervalInfo {
    private final VersionedValue<SurfacePatchIntervalInfo> inner;

    public VvProxy(VersionedValue<SurfacePatchIntervalInfo> inner) {
      this.inner = inner;
    }

    @Override
    public int getPatchIntervalCount() {
      return inner.value().getPatchIntervalCount();
    }

    @Override
    public PatchInterval getPatchInterval(int index) {
      return inner.value().getPatchInterval(index);
    }

    @Override
    public PatchInterval mapPatchToPatchInterval(int patchIndex) {
      return inner.value().mapPatchToPatchInterval(patchIndex);
    }

    @Override
    public List<? extends PatchInterval> patchIntervals() {
      return inner.value().patchIntervals();
    }
  }
}
