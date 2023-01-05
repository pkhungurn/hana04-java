package hana04.yuri.emitter.sumenv;

import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareBuilder;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.Component;
import hana04.base.changeprop.ChangePropUtil;
import hana04.base.changeprop.VersionedValue;
import hana04.base.changeprop.util.DerivedVersionedValue;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.SpectrumSpace;
import hana04.gfxbase.spectrum.SpectrumTransform;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.spectrum.rgb.RgbSpace;
import hana04.shakuyaku.emitter.Emitter;
import hana04.shakuyaku.emitter.sumenv.SumEnvironmentalLight;
import hana04.yuri.emitter.EmitterEvalInput;
import hana04.yuri.emitter.EmitterInScene;
import hana04.yuri.emitter.EmitterInSceneFactory;
import hana04.yuri.emitter.EmitterInScenes;
import hana04.yuri.emitter.EmitterPdfInput;
import hana04.yuri.emitter.EmitterSampler;
import hana04.yuri.emitter.EmitterSamplingOutput;
import hana04.yuri.emitter.specspaces.EmitterSamplerRgb;
import hana04.yuri.sampler.RandomNumberGenerator;
import hana04.shakuyaku.surface.PatchInterval;
import hana04.yuri.util.DiscretePdf;

import javax.inject.Inject;
import javax.vecmath.Point3d;
import java.util.List;
import java.util.Optional;

public class SumEnvironmentalLightInScenes {
  public static class EmitterInSceneListVv
    extends DerivedVersionedValue<List<EmitterInScene>> {
    @HanaDeclareExtension(
      extensibleClass = SumEnvironmentalLightInScene.class,
      extensionClass = EmitterInSceneListVv.class)
    EmitterInSceneListVv(
      SumEnvironmentalLightInScene emitterInScene) {
      super(
        ImmutableList.of(emitterInScene.emitter().getExtension(hana04.shakuyaku.emitter.sumenv.SumEnvironmentalLights.UnwrappedEmittersVv.class)),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> {
          List<Emitter> emitters =
            emitterInScene.emitter().getExtension(hana04.shakuyaku.emitter.sumenv.SumEnvironmentalLights.UnwrappedEmittersVv.class).value();
          ImmutableList.Builder<EmitterInScene> output = ImmutableList.builder();
          for (Emitter emitter : emitters) {
            EmitterInSceneFactory factory = emitter.getExtension(EmitterInSceneFactory.Vv.class).value();
            output.add(factory.create(emitterInScene.scene(), Optional.empty()));
          }
          return output.build();
        });
    }
  }

  public static class Sampler<T extends Spectrum, V extends SpectrumTransform<T>> implements EmitterSampler<T> {
    private final List<EmitterInScene> emitters;
    private final DiscretePdf discretePdf;
    private final Class<? extends VersionedValue<? extends EmitterSampler<T>>> emitterSamplerVvClass;
    private final SpectrumSpace<T, V> ss;

    public Sampler(List<EmitterInScene> emitters, DiscretePdf discretePdf,
                   Class<? extends VersionedValue<? extends EmitterSampler<T>>> emitterSamplerVvClass,
                   SpectrumSpace<T, V> ss) {
      this.emitters = emitters;
      this.discretePdf = discretePdf;
      this.emitterSamplerVvClass = emitterSamplerVvClass;
      this.ss = ss;
    }

    @Override
    public T eval(EmitterEvalInput record) {
      T sum = ss.createZero();
      for (EmitterInScene emitter : emitters) {
        T radiance = emitter.getExtension(emitterSamplerVvClass).value().eval(record);
        sum = ss.add(sum, radiance);
      }
      return sum;
    }

    @Override
    public double pdf(EmitterPdfInput record) {
      double output = 0;
      for (int i = 0; i < emitters.size(); i++) {
        EmitterInScene emitter = emitters.get(i);
        output += discretePdf.pdf(i) * emitter.getExtension(emitterSamplerVvClass).value().pdf(record);
      }
      return output;
    }

    @Override
    public EmitterSamplingOutput<T> sample(Point3d p, RandomNumberGenerator rng) {
      int emitterIndex = discretePdf.sample(rng.next1D());
      EmitterInScene emitter = emitters.get(emitterIndex);
      EmitterSampler<T> sampler = emitter.getExtension(emitterSamplerVvClass).value();
      EmitterSamplingOutput<T> samplingOutput = sampler.sample(p, rng);
      samplingOutput.value = ss.scale(1.0 / discretePdf.pdf(emitterIndex), samplingOutput.value);
      return samplingOutput;
    }

    static class ForRgb extends Sampler<Rgb, Rgb> implements EmitterSamplerRgb {
      ForRgb(List<EmitterInScene> emitters, DiscretePdf discretePdf,
             Class<? extends VersionedValue<? extends EmitterSampler<Rgb>>> emitterSamplerClass) {
        super(emitters, discretePdf, emitterSamplerClass, RgbSpace.I);
      }
    }
  }

  public static class EmitterSamplerRgbVv
    extends DerivedVersionedValue<EmitterSamplerRgb>
    implements EmitterSamplerRgb.Vv {
    @HanaDeclareExtension(
      extensibleClass = SumEnvironmentalLightInScene.class,
      extensionClass = EmitterSamplerRgb.Vv.class)
    EmitterSamplerRgbVv(SumEnvironmentalLightInScene emitterInScene) {
      super(
        ImmutableList.of(
          emitterInScene.getExtension(EmitterInSceneListVv.class),
          emitterInScene.emitter().getExtension(SumEnvironmentalLights.DiscretePdfVv.class)),
        ChangePropUtil::largestBetweenIncSelfAndDeps,
        () -> new Sampler.ForRgb(
          emitterInScene.getExtension(EmitterInSceneListVv.class).value(),
          emitterInScene.emitter().getExtension(SumEnvironmentalLights.DiscretePdfVv.class).value(),
          EmitterSamplerRgb.Vv.class));
    }
  }

  @HanaDeclareBuilder(SumEnvironmentalLightInScene.class)
  public static class SumEnvironmentalLightInSceneBuilder
      extends SumEnvironmentalLightInScene__Impl__Builder<SumEnvironmentalLightInSceneBuilder> {
    @Inject
    public SumEnvironmentalLightInSceneBuilder(SumEnvironmentalLightInScene__ImplFactory factory) {
      super(factory);
    }

    public static SumEnvironmentalLightInSceneBuilder builder(Component component) {
      return component.uberFactory().create(SumEnvironmentalLightInSceneBuilder.class);
    }
  }

  public static class PatchIntervalProvider_ implements EmitterInScenes.PatchIntervalProvider {
    @HanaDeclareExtension(
        extensibleClass = SumEnvironmentalLight.class,
        extensionClass = EmitterInScenes.PatchIntervalProvider.class)
    public PatchIntervalProvider_(SumEnvironmentalLight instance) {
      // NO-OP
    }

    @Override
    public Optional<PatchInterval> getPatchInterval() {
      return Optional.empty();
    }
  }
}
