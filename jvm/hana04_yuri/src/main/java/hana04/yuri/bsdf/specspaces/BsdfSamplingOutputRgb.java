package hana04.yuri.bsdf.specspaces;

import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.yuri.bsdf.BsdfSamplingOutput;

public class BsdfSamplingOutputRgb extends BsdfSamplingOutput<Rgb, Rgb> {
  public BsdfSamplingOutputRgb(Rgb value) {
    super(value);
  }

  public BsdfSamplingOutputRgb() {
    this(new Rgb(0, 0, 0));
  }
}
