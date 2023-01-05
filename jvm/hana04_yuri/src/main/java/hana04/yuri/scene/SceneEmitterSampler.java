package hana04.yuri.scene;

import hana04.gfxbase.spectrum.Spectrum;
import hana04.yuri.emitter.EmitterEvalInput;
import hana04.yuri.emitter.EmitterInScene;
import hana04.yuri.emitter.EmitterPdfInput;
import hana04.yuri.emitter.EmitterSamplingOutput;
import hana04.yuri.sampler.RandomNumberGenerator;
import hana04.shakuyaku.surface.PatchInterval;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Point3d;
import java.util.Optional;

public interface SceneEmitterSampler<T extends Spectrum> {
  Pair<EmitterInScene, EmitterSamplingOutput<T>> sample(Point3d p, RandomNumberGenerator rng);
  double pdf(EmitterInScene emitter, EmitterPdfInput record);
  T eval(EmitterInScene emitter, EmitterEvalInput record);
  Optional<EmitterInScene> getEnvironmentalEmitter();
  EmitterInScene getSurfaceEmitter(PatchInterval patchInterval);
}
