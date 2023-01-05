package hana04.yuri.integrand;

import hana04.base.changeprop.VersionedValue;
import hana04.gfxbase.gfxtype.Ray;
import hana04.yuri.film.FilmRecorder;
import hana04.yuri.sampler.RandomNumberGenerator;
import hana04.shakuyaku.scene.Scene;

public interface SensorIntegrandEvaluator {
  void eval(Scene scene, Ray ray, RandomNumberGenerator rng, FilmRecorder recorder);

  interface Vv extends VersionedValue<SensorIntegrandEvaluator> {
    // NO-OP
  }
}
