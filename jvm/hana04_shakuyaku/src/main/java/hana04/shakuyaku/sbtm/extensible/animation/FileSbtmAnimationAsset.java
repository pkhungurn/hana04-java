package hana04.shakuyaku.sbtm.extensible.animation;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.filesystem.FilePath;
import hana04.shakuyaku.TypeIds;

@HanaDeclareObject(
  parent = SbtmAnimationAsset.class,
  typeId = TypeIds.TYPE_ID_FILE_SBTM_ANIMATION_ASSET,
  typeNames = {"shakuyaku.FileSbtmAnimationAsset", "FileSbtmAnimationAsset"})
public interface FileSbtmAnimationAsset extends SbtmAnimationAsset {
  @HanaProperty(1)
  FilePath filePath();
}
