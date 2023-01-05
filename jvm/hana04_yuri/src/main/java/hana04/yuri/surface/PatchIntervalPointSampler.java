package hana04.yuri.surface;

import hana04.base.changeprop.VersionedValue;
import hana04.yuri.sampler.RandomNumberGenerator;

public interface PatchIntervalPointSampler {
  PatchIntervalPointSamplingOutput sample(RandomNumberGenerator rng);

  double pdf(PatchIntervalPointSamplingOutput record);

  interface Vv extends VersionedValue<PatchIntervalPointSampler> {
    // NO-OP
  }
}
