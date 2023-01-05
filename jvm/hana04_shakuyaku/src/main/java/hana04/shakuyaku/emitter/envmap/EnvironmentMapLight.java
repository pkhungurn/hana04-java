package hana04.shakuyaku.emitter.envmap;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.Variable;
import hana04.gfxbase.gfxtype.Transform;
import hana04.shakuyaku.TypeIds;
import hana04.shakuyaku.emitter.Emitter;
import hana04.shakuyaku.texture.twodim.TextureTwoDim;

@HanaDeclareObject(
    parent = Emitter.class,
    typeId = TypeIds.TYPE_ID_ENVIRONMENT_MAP_LIGHT,
    typeNames = {"shakuyaku.EnvironmentMapLight", "EnvironmentMapLight"})
public interface EnvironmentMapLight extends Emitter {
  @HanaProperty(1)
  Variable<Double> samplingWeight();

  @HanaProperty(2)
  Variable<Transform> toWorld();

  @HanaProperty(3)
  Variable<Wrapped<TextureTwoDim>> texture();
}