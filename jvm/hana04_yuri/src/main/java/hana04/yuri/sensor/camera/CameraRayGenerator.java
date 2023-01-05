package hana04.yuri.sensor.camera;

import hana04.base.changeprop.VersionedValue;
import hana04.yuri.sensor.SensorRay;

import javax.vecmath.Tuple2d;

public interface CameraRayGenerator {
  SensorRay generate(Tuple2d imagePlacePosition, Tuple2d aperturePosition);

  interface Vv extends VersionedValue<CameraRayGenerator> {
    // NO-OP
  }
}

