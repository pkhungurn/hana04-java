package hana04.yuri.emitter.area;

import com.google.common.base.Preconditions;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.SpectrumSpace;
import hana04.gfxbase.spectrum.SpectrumTransform;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.spectrum.rgb.RgbSpace;
import hana04.gfxbase.gfxtype.Measure;
import hana04.gfxbase.gfxtype.VecMathDUtil;
import hana04.yuri.emitter.EmitterEvalInput;
import hana04.yuri.emitter.EmitterPdfInput;
import hana04.yuri.emitter.EmitterSampler;
import hana04.yuri.emitter.EmitterSamplingOutput;
import hana04.yuri.emitter.specspaces.EmitterSamplerRgb;
import hana04.yuri.sampler.RandomNumberGenerator;
import hana04.yuri.surface.PatchIntervalPointSampler;
import hana04.yuri.surface.PatchIntervalPointSamplingOutput;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.util.Optional;

public abstract class AreaEmitterSampler<T extends Spectrum, V extends SpectrumTransform<T>>
  implements EmitterSampler<T> {
  private final SpectrumSpace<T, V> ss;
  private final PatchIntervalPointSampler patchIntervalPointSampler;
  private final T radiance;

  AreaEmitterSampler(PatchIntervalPointSampler patchIntervalPointSampler,
                     Spectrum radiance,
                     SpectrumSpace<T, V> ss) {
    this.ss = ss;
    this.patchIntervalPointSampler = patchIntervalPointSampler;
    this.radiance = ss.convert(radiance);
  }

  @Override
  public T eval(EmitterEvalInput record) {
    Preconditions.checkArgument(record.nq.isPresent());
    double cosTheta = -record.wiWorld.dot(record.nq.get());
    if (cosTheta < 0) {
      return ss.createZero();
    }
    return ss.copy(radiance);
  }

  @Override
  public double pdf(EmitterPdfInput record) {
    Preconditions.checkArgument(record.nq.isPresent() && record.q.isPresent());
    double cosTheta = -record.wiWorld.dot(record.nq.get());
    if (cosTheta <= 0) {
      return 0;
    }
    double dist2 = record.q.get().distanceSquared(record.p);
    PatchIntervalPointSamplingOutput pointSamplingOutput = new PatchIntervalPointSamplingOutput();
    pointSamplingOutput.measure = Measure.Area;
    pointSamplingOutput.q.set(record.q.get());
    pointSamplingOutput.nq.set(record.nq.get());
    return patchIntervalPointSampler.pdf(pointSamplingOutput) * dist2 / cosTheta;
  }

  @Override
  public EmitterSamplingOutput<T> sample(Point3d p, RandomNumberGenerator rng) {
    PatchIntervalPointSamplingOutput pointSamplingOutput = patchIntervalPointSampler.sample(rng);

    Vector3d wiWorld = VecMathDUtil.normalize(VecMathDUtil.sub(pointSamplingOutput.q, p));
    double cosTheta = -wiWorld.dot(pointSamplingOutput.nq);
    T value;
    if (cosTheta <= 0) {
      value = ss.createZero();
    } else {
      double dist2 = pointSamplingOutput.q.distanceSquared(p);
      double pdf = patchIntervalPointSampler.pdf(pointSamplingOutput) * dist2 / cosTheta;
      value = ss.scale(1.0 / pdf, radiance);
    }

    EmitterSamplingOutput<T> result = new EmitterSamplingOutput<>();
    result.measure = Measure.Area;
    result.p.set(p);
    result.wiWorld.set(VecMathDUtil.normalize(VecMathDUtil.sub(pointSamplingOutput.q, p)));
    result.q = Optional.of(new Point3d(pointSamplingOutput.q));
    result.nq = Optional.of(new Vector3d(pointSamplingOutput.nq));
    result.value = value;
    return result;
  }

  public static class ForRgb extends AreaEmitterSampler<Rgb, Rgb> implements EmitterSamplerRgb {
    public ForRgb(PatchIntervalPointSampler patchIntervalPointSampler,
                  Spectrum radiance) {
      super(patchIntervalPointSampler, radiance, RgbSpace.I);
    }
  }
}
