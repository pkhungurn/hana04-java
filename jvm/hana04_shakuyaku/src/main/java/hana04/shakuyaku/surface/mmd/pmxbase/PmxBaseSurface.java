package hana04.shakuyaku.surface.mmd.pmxbase;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.base.filesystem.FilePath;
import hana04.gfxbase.gfxtype.Transform;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.surface.Surface;

import java.util.List;

@HanaDeclareObject(
    parent = Surface.class,
    typeId = TypeIds.TYPE_ID_PMX_BASE_SURFACE,
    typeNames = {"shakuyaku.PmxBaseSurface", "PmxBaseSurface"})
public interface PmxBaseSurface extends Surface {

  @HanaProperty(1)
  Variable<Transform> toWorld();

  @HanaProperty(2)
  Variable<FilePath> filePath();

  @HanaProperty(3)
  Variable<List<String>> materialWithAmbientOpting();

  @HanaProperty(4)
  Variable<String> ambientMode();
}
