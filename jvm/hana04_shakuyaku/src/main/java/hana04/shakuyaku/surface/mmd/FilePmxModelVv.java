package hana04.shakuyaku.surface.mmd;

import com.google.common.collect.ImmutableList;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.VersionedValue;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.filesystem.FilePath;
import hana04.formats.mmd.pmx.PmxModel;

import java.nio.file.FileSystem;

public class FilePmxModelVv
  extends DerivedVersionedValue<PmxModel>
  implements PmxModelVv {

  public FilePmxModelVv(VersionedValue<FilePath> filePath, FileSystem fileSystem) {
    super(
      ImmutableList.of(filePath),
      ChangePropUtil::largestBetweenIncSelfAndDeps,
      () -> {
        try {
          return PmxModel.load(fileSystem.getPath(filePath.value().storedPath));
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    );
  }
}
