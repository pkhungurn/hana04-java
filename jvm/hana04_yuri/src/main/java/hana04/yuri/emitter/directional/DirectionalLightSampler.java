package hana04.yuri.emitter.directional;

import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.SpectrumSpace;
import hana04.gfxbase.spectrum.SpectrumTransform;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.spectrum.rgb.RgbSpace;
import hana04.gfxbase.gfxtype.Measure;
import hana04.gfxbase.gfxtype.Transform;
import hana04.yuri.emitter.EmitterEvalInput;
import hana04.yuri.emitter.EmitterPdfInput;
import hana04.yuri.emitter.EmitterSampler;
import hana04.yuri.emitter.EmitterSamplingOutput;
import hana04.yuri.emitter.specspaces.EmitterSamplerRgb;
import hana04.yuri.sampler.RandomNumberGenerator;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public abstract class DirectionalLightSampler<T extends Spectrum, V extends SpectrumTransform<T>>
  implements EmitterSampler<T> {
  private final SpectrumSpace<T, V> ss;
  private final T radiance;
  private final Transform toWorld;

  DirectionalLightSampler(Spectrum radiance, Transform toWorld, SpectrumSpace<T, V> ss) {
    this.ss = ss;
    this.radiance = ss.convert(radiance);
    this.toWorld = Transform.copy(toWorld);
  }

  @Override
  public T eval(EmitterEvalInput record) {
    return ss.createZero();
  }

  @Override
  public double pdf(EmitterPdfInput record) {
    return 0;
  }

  @Override
  public EmitterSamplingOutput<T> sample(Point3d p, RandomNumberGenerator rng) {
    Vector3d wiWorld = new Vector3d(0, 0, 1);
    toWorld.m.transform(wiWorld);
    wiWorld.normalize();

    EmitterSamplingOutput<T> result = new EmitterSamplingOutput<>();
    result.p.set(p);
    result.wiWorld.set(wiWorld);
    result.measure = Measure.Discrete;
    result.value = ss.copy(radiance);
    return result;
  }

  public static class ForRgb extends DirectionalLightSampler<Rgb, Rgb> implements EmitterSamplerRgb {
    public ForRgb(Spectrum radiance, Transform toWorld) {
      super(radiance, toWorld, RgbSpace.I);
    }
  }
}
