package hana04.shakuyaku.surface.mmd.patchinterval;

import com.google.common.collect.ImmutableList;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.VersionedValue;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.formats.mmd.pmd.PmdModel;
import hana04.formats.mmd.pmx.PmxModel;
import hana04.shakuyaku.surface.PatchInterval;
import hana04.shakuyaku.surface.Surface;
import hana04.shakuyaku.surface.SurfacePatchIntervalInfo;
import hana04.shakuyaku.surface.mmd.util.MaterialAmbientUsage;

import java.util.List;
import java.util.Optional;

public class MmdSurfacePatchInternvalInfoVv
  extends DerivedVersionedValue<SurfacePatchIntervalInfo>
  implements SurfacePatchIntervalInfo.Vv {
  public MmdSurfacePatchInternvalInfoVv(Surface surface,
                                        VersionedValue<Optional<PmdModel>> pmdModelVv,
                                        VersionedValue<Optional<PmxModel>> pmxModelVv,
                                        VersionedValue<MaterialAmbientUsage> materialAmbientUsageVv,
                                        PmdBasePatchIntervalFactory pmdPatchFactory,
                                        PmxBasePatchIntervalFactory pmxPatchFactory) {
    super(ImmutableList.of(
      pmdModelVv,
      pmxModelVv,
      materialAmbientUsageVv),
      ChangePropUtil::largestBetweenIncSelfAndDeps,
      () -> {
        List<? extends PatchInterval> patchIntervals;
        if (pmxModelVv.value().isPresent()) {
          PmxModel pmxModel = pmxModelVv.value().get();
          patchIntervals = pmxPatchFactory.create(
            pmxModel,
            surface,
            materialAmbientUsageVv.value());
        } else if (pmdModelVv.value().isPresent()) {
          PmdModel pmdModel = pmdModelVv.value().get();
          patchIntervals = pmdPatchFactory.create(
            pmdModel,
            surface,
            materialAmbientUsageVv.value());
        } else {
          throw new RuntimeException("The file provided does not yield a valid PMD or PMX model");
        }
        return new SurfacePatchIntervalInfo.FromList(patchIntervals);
      });
  }
}
