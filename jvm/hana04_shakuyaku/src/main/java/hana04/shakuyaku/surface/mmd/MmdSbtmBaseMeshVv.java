package hana04.shakuyaku.surface.mmd;

import com.google.common.collect.ImmutableList;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.VersionedValue;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.formats.mmd.pmd.PmdModel;
import hana04.formats.mmd.pmx.PmxModel;
import hana04.shakuyaku.sbtm.SbtmBaseMesh;
import hana04.shakuyaku.sbtm.converter.PmdToSbtmBaseMeshImplConverter;
import hana04.shakuyaku.sbtm.converter.PmxToSbtmBaseMeshImplConverter;

import java.util.Optional;

public class MmdSbtmBaseMeshVv
  extends DerivedVersionedValue<SbtmBaseMesh>
  implements SbtmBaseMesh.Vv {
  public MmdSbtmBaseMeshVv(
    VersionedValue<Optional<PmdModel>> pmdModel,
    VersionedValue<Optional<PmxModel>> pmxModel) {
    super(ImmutableList.of(pmdModel, pmxModel),
      ChangePropUtil::largestBetweenIncSelfAndDeps,
      () -> {
        if (pmxModel.value().isPresent()) {
          return PmxToSbtmBaseMeshImplConverter.convert(pmxModel.value().get());
        } else if (pmdModel.value().isPresent()) {
          return PmdToSbtmBaseMeshImplConverter.convert(pmdModel.value().get());
        } else {
          throw new RuntimeException("The file provided does not yield a valid PMD or PMX model");
        }
      });
  }
}
