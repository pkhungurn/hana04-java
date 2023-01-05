package hana04.yuri.emitter.distantdisk;

import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.SpectrumSpace;
import hana04.gfxbase.spectrum.SpectrumTransform;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.spectrum.rgb.RgbSpace;
import hana04.gfxbase.gfxtype.Frame;
import hana04.gfxbase.gfxtype.Measure;
import hana04.gfxbase.gfxtype.Transform;
import hana04.gfxbase.util.MathUtil;
import hana04.shakuyaku.emitter.distantdisk.DistantDiskLight;
import hana04.yuri.emitter.EmitterEvalInput;
import hana04.yuri.emitter.EmitterPdfInput;
import hana04.yuri.emitter.EmitterSampler;
import hana04.yuri.emitter.EmitterSamplingOutput;
import hana04.yuri.emitter.specspaces.EmitterSamplerRgb;
import hana04.yuri.sampler.RandomNumberGenerator;
import hana04.yuri.util.SamplingUtil;

import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

public abstract class DistantDiskLightSampler<T extends Spectrum, V extends SpectrumTransform<T>>
  implements EmitterSampler<T> {
  private final SpectrumSpace<T, V> ss;
  private final Double cosThetaA;
  private final Transform toWorld;
  private final T radiance;

  DistantDiskLightSampler(DistantDiskLight emitter, SpectrumSpace<T, V> ss) {
    this.ss = ss;
    this.cosThetaA = Math.cos(MathUtil.degToRad(emitter.thetaA().value()));
    this.toWorld = new Transform(emitter.toWorld().value().m);
    this.radiance = ss.convert(emitter.radiance().value());
  }

  @Override
  public T eval(EmitterEvalInput record) {
    Vector3d wiEmitter = new Vector3d();
    toWorld.mi.transform(record.wiWorld, wiEmitter);
    wiEmitter.normalize();
    if (Frame.cosTheta(wiEmitter) <= cosThetaA) {
      return ss.createZero();
    }
    return ss.copy(radiance);
  }

  @Override
  public double pdf(EmitterPdfInput record) {
    Vector3d woEmitter = new Vector3d();
    toWorld.mi.transform(record.wiWorld, woEmitter);
    woEmitter.normalize();
    return SamplingUtil.squareToUniformSphereCapPdf(woEmitter, cosThetaA);
  }

  @Override
  public EmitterSamplingOutput<T> sample(Point3d p, RandomNumberGenerator rng) {
    Vector2d sample = new Vector2d();
    rng.next2D(sample);
    Vector3d wiWorld = new Vector3d();
    SamplingUtil.squareToUniformSphereCap(sample, cosThetaA, wiWorld);
    double pdf = SamplingUtil.squareToUniformSphereCapPdf(wiWorld, cosThetaA);
    toWorld.m.transform(wiWorld);
    wiWorld.normalize();

    EmitterSamplingOutput<T> result = new EmitterSamplingOutput<>();
    result.p.set(p);
    result.wiWorld.set(wiWorld);
    result.measure = Measure.SolidAngle;
    result.value = ss.scale(1.0 / pdf, radiance);
    return result;
  }

  public static class ForRgb extends DistantDiskLightSampler<Rgb, Rgb> implements EmitterSamplerRgb {
    public ForRgb(DistantDiskLight emitter) {
      super(emitter, new RgbSpace());
    }
  }
}
