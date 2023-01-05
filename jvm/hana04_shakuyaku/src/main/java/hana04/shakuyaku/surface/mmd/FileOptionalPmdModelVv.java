package hana04.shakuyaku.surface.mmd;

import com.google.common.collect.ImmutableList;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.VersionedValue;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.filesystem.FilePath;
import hana04.formats.mmd.pmd.PmdModel;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.Optional;

public class FileOptionalPmdModelVv
  extends DerivedVersionedValue<Optional<PmdModel>>
  implements OptionalPmdModelVv {

  public FileOptionalPmdModelVv(VersionedValue<FilePath> filePath,
                                FileSystem fileSystem) {
    super(ImmutableList.of(filePath),
      ChangePropUtil::largestBetweenIncSelfAndDeps,
      () -> {
        Path path = fileSystem.getPath(filePath.value().storedPath);
        if (FilenameUtils.getExtension(filePath.value().storedPath).toLowerCase().equals("pmd")) {
          try {
            return Optional.of(PmdModel.load(path));
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        } else {
          return Optional.empty();
        }
      });
  }
}
