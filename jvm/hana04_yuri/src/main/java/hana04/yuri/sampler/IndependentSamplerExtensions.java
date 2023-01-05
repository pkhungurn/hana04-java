package hana04.yuri.sampler;

import com.google.common.primitives.Longs;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.changeprop.Constant;
import hana04.gfxbase.random.MersenneTwister;

import javax.vecmath.Tuple2d;

public class IndependentSamplerExtensions {
  public static class PerPixelSampler_ implements PerPixelSampler {
    private final IndependentSampler independentSampler;
    private final MersenneTwister random;

    PerPixelSampler_(IndependentSampler independentSampler) {
      this(independentSampler, new MersenneTwister());
    }

    private PerPixelSampler_(IndependentSampler independentSampler, MersenneTwister random) {
      this.independentSampler = independentSampler;
      this.random = random;
    }

    @Override
    public void generate() {
      // NO-OP
    }

    @Override
    public void advance() {
      // NO-OP
    }

    @Override
    public double next1D() {
      return random.nextDouble();
    }

    @Override
    public void next2D(Tuple2d output) {
      output.x = random.nextDouble();
      output.y = random.nextDouble();
    }

    @Override
    public int getSampleCount() {
      return independentSampler.sampleCount().value();
    }

    @Override
    public void setSeed(byte[] seed) {
      random.setSeed(Longs.fromByteArray(seed));
    }

    @Override
    public PerPixelSampler_ duplicate() {
      return new PerPixelSampler_(independentSampler, (MersenneTwister) random.clone());
    }
  }

  public static class PerPixelSamplerVv extends Constant<PerPixelSampler> implements PerPixelSampler.Vv {
    @HanaDeclareExtension(
      extensibleClass = IndependentSampler.class,
      extensionClass = PerPixelSampler.Vv.class)
    public PerPixelSamplerVv(IndependentSampler sampler) {
      super(new PerPixelSampler_(sampler));
    }
  }
}
