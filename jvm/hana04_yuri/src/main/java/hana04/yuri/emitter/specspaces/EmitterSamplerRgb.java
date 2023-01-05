package hana04.yuri.emitter.specspaces;

import hana04.base.changeprop.VersionedValue;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.yuri.emitter.EmitterSampler;

public interface EmitterSamplerRgb extends EmitterSampler<Rgb> {
  // NO-OP

  interface Vv extends VersionedValue<EmitterSamplerRgb> {
    // NO-OP
  }
}
