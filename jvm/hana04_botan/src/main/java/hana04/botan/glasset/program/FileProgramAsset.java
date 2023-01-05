package hana04.botan.glasset.program;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.base.extension.HanaObject;
import hana04.base.filesystem.FilePath;
import hana04.botan.TypeIds;

@HanaDeclareObject(
  parent = ProgramAsset.class,
  typeId = TypeIds.TYPE_ID_FILE_PROGRAM_ASSET,
  typeNames = {"botan.FileProgramAsset", "FileProgramAsset"})
public interface FileProgramAsset extends HanaObject, ProgramAsset {
  @HanaProperty(1)
  Variable<FilePath> vertexShaderFilePath();

  @HanaProperty(2)
  Variable<FilePath> fragmentShaderFilePath();
}
