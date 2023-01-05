package hana04.shakuyaku.texture.twodim.image;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.base.filesystem.FilePath;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;
import hana04.shakuyaku.texture.twodim.WrapMode;

@HanaDeclareObject(
  parent = TextureTwoDim.class,
  typeId = TypeIds.TYPE_ID_IMAGE_TEXTURE,
  typeNames = {"shakuyaku.ImageTexture", "ImageTexture"})
public interface ImageTexture extends TextureTwoDim {
  @HanaProperty(1)
  FilePath filePath();

  @HanaProperty(2)
  Variable<WrapMode> wrapS();

  @HanaProperty(3)
  Variable<WrapMode> wrapT();
}