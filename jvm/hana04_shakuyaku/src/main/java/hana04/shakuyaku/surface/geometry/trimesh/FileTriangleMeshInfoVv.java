package hana04.shakuyaku.surface.geometry.trimesh;

import com.google.common.collect.ImmutableList;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.VersionedValue;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.base.filesystem.FilePath;

import java.nio.file.FileSystem;

public class FileTriangleMeshInfoVv
  extends DerivedVersionedValue<TriangleMeshInfo>
  implements TriangleMeshInfo.Vv {
  public FileTriangleMeshInfoVv(VersionedValue<FilePath> filePath, FileSystem fileSystem) {
    super(ImmutableList.of(filePath),
      ChangePropUtil::largestBetweenIncSelfAndDeps,
      () -> TriangleMeshUtil.load(filePath.value(), fileSystem));
  }
}
