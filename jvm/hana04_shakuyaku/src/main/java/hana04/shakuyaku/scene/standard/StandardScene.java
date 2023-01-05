package hana04.shakuyaku.scene.standard;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.Variable;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.emitter.Emitter;
import hana04.shakuyaku.scene.Scene;
import hana04.shakuyaku.shadinghack.ShadingHack;
import hana04.shakuyaku.surface.Surface;

import java.util.List;

@HanaDeclareObject(
  parent = Scene.class,
  typeId = TypeIds.TYPE_ID_STANDARD_SCENE,
  typeNames = {"shakuyaku.StandardScene", "StandardScene"})
public interface StandardScene extends Scene {
  @HanaProperty(1)
  Variable<List<Wrapped<Surface>>> surface();

  @HanaProperty(2)
  Variable<List<Wrapped<Emitter>>> emitter();

  @HanaProperty(3)
  Variable<List<Wrapped<ShadingHack>>> shadingHack();
}