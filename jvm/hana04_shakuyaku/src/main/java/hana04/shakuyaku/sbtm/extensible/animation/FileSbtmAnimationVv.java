package hana04.shakuyaku.sbtm.extensible.animation;

import com.google.common.collect.ImmutableList;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.VersionedValue;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.filesystem.FilePath;
import hana04.shakuyaku.sbtm.SbtmAnimation;

import java.nio.file.FileSystem;
import java.nio.file.Path;

public class FileSbtmAnimationVv
  extends DerivedVersionedValue<SbtmAnimation>
  implements SbtmAnimationVv {

  public FileSbtmAnimationVv(VersionedValue<FilePath> filePath,
                             FileSystem fileSystem) {
    super(
      ImmutableList.of(filePath),
      ChangePropUtil::largestBetweenIncSelfAndDeps,
      () -> {
        Path path = fileSystem.getPath(filePath.value().storedPath);
        return SbtmAnimation.loadBinary(path);
      }
    );
  }
}
