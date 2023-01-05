package hana04.shakuyaku.sensor.camera;

import hana04.apt.annotation.HanaDeclareExtensibleInterface;
import hana04.base.changeprop.Variable;
import hana04.gfxbase.gfxtype.Transform;
import hana04.shakuyaku.sensor.Sensor;

@HanaDeclareExtensibleInterface(Sensor.class)
public interface Camera extends Sensor {
  Variable<Transform> toWorld();
}
