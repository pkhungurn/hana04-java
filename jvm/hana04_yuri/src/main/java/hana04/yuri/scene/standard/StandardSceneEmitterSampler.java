package hana04.yuri.scene.standard;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import hana04.apt.annotation.HanaDeclareExtension;
import hana04.base.caching.HanaUnwrapper;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.VersionedValue;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.SpectrumSpace;
import hana04.gfxbase.spectrum.SpectrumTransform;
import hana04.gfxbase.spectrum.rgb.Rgb;
import hana04.gfxbase.spectrum.rgb.RgbSpace;
import hana04.shakuyaku.emitter.Emitter;
import hana04.shakuyaku.scene.standard.StandardScene;
import hana04.yuri.emitter.EmitterEvalInput;
import hana04.yuri.emitter.EmitterInScene;
import hana04.yuri.emitter.EmitterInSceneFactory;
import hana04.yuri.emitter.EmitterInScenes;
import hana04.yuri.emitter.EmitterPdfInput;
import hana04.yuri.emitter.EmitterSampler;
import hana04.yuri.emitter.EmitterSamplingOutput;
import hana04.shakuyaku.emitter.EmitterType;
import hana04.shakuyaku.emitter.Emitters;
import hana04.yuri.emitter.specspaces.EmitterSamplerRgb;
import hana04.yuri.sampler.RandomNumberGenerator;
import hana04.yuri.scene.SceneEmitterSampler;
import hana04.yuri.scene.specspaces.SceneEmitterSamplerRgb;
import hana04.shakuyaku.surface.PatchInterval;
import hana04.shakuyaku.surface.Surface;
import hana04.shakuyaku.surface.SurfacePatchIntervalInfo;
import hana04.yuri.util.DiscretePdf;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Point3d;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class StandardSceneEmitterSampler<T extends Spectrum, V extends SpectrumTransform<T>>
    implements SceneEmitterSampler<T> {
  private StandardScene node;
  private SpectrumSpace<T, V> ss;
  private Class<? extends VersionedValue<? extends EmitterSampler<T>>> emitterSamplerVvClass;
  private List<EmitterInScene> emitters;
  private DiscretePdf emitterPdf;
  private Map<EmitterInScene, Integer> emitterToIndex;
  private Map<PatchInterval, EmitterInScene> patchIntervalToEmitterInScene;
  private Optional<EmitterInScene> environmentalEmitter;

  StandardSceneEmitterSampler(StandardScene node,
      SpectrumSpace<T, V> ss,
      Class<? extends VersionedValue<? extends EmitterSampler<T>>> emitterSamplerVvClass,
      HanaUnwrapper unwrapper) {
    this.node = node;
    this.ss = ss;
    this.emitterSamplerVvClass = emitterSamplerVvClass;

    ImmutableList.Builder<EmitterInScene> emittersBuilder = ImmutableList.builder();
    for (Wrapped<Emitter> wrappedEmitter : node.emitter().value()) {
      Emitter emitter = wrappedEmitter.unwrap(unwrapper);
      processEmitter(emitter, Optional.empty(), emittersBuilder);
    }
    for (Wrapped<Surface> wrappedSurface : node.surface().value()) {
      Surface surface = wrappedSurface.unwrap(unwrapper);
      SurfacePatchIntervalInfo patchIntervalInfo = surface.getExtension(SurfacePatchIntervalInfo.Vv.class).value();
      for (int i = 0; i < patchIntervalInfo.getPatchIntervalCount(); i++) {
        PatchInterval patchInterval = patchIntervalInfo.getPatchInterval(i);
        if (!patchInterval.emitter().isPresent()) {
          continue;
        }
        Emitter emitter = patchInterval.emitter().get().unwrap(unwrapper);
        Preconditions.checkArgument(emitter
            .getExtension(Emitters.EmitterInfo.class)
            .getType()
            .equals(EmitterType.Surface));
        processEmitter(emitter, Optional.of(patchInterval), emittersBuilder);
      }
    }
    emitters = emittersBuilder.build();

    DiscretePdf.Builder pdfBuilder = DiscretePdf.builder();
    for (EmitterInScene emitterInScene : emitters) {
      pdfBuilder.add(emitterInScene.emitter().samplingWeight().value());
    }
    emitterPdf = pdfBuilder.build();

    environmentalEmitter = Optional.empty();
    for (EmitterInScene emitter : emitters) {
      if (emitter.emitter().getExtension(Emitters.EmitterInfo.class).getType().equals(EmitterType.Environmental)) {
        if (environmentalEmitter.isPresent()) {
          throw new IllegalArgumentException("There can be at most one environmental emitter in a scene.");
        }
        environmentalEmitter = Optional.of(emitter);
      }
    }

    emitterToIndex = new HashMap<>();
    for (int i = 0; i < emitters.size(); i++) {
      emitterToIndex.put(emitters.get(i), i);
    }

    patchIntervalToEmitterInScene = new HashMap<>();
    for (EmitterInScene emitter : emitters) {
      var patchIntervalProvider = emitter.getExtension(EmitterInScenes.PatchIntervalProvider.class);
      if (patchIntervalProvider.getPatchInterval().isPresent()) {
        patchIntervalToEmitterInScene.put(patchIntervalProvider.getPatchInterval().orElseThrow(), emitter);
      }
    }
  }

  private EmitterInScene processEmitter(Emitter emitter,
      Optional<PatchInterval> patchInterval,
      ImmutableList.Builder<EmitterInScene> emittersBuilder) {
    if (!patchInterval.isPresent()) {
      EmitterType emitterType = emitter.getExtension(Emitters.EmitterInfo.class).getType();
      Preconditions.checkArgument(
          emitterType.equals(EmitterType.Environmental)
              || emitterType.equals(EmitterType.Positional),
          "Found an emitter at a scene's top level that is neither an environemental nor a positional emitter");
    }
    EmitterInSceneFactory factory = emitter.getExtension(EmitterInSceneFactory.Vv.class).value();
    EmitterInScene emitterInScene = factory.create(node, patchInterval);
    emitterInScene.prepareExtension(emitterSamplerVvClass);
    emittersBuilder.add(emitterInScene);
    return emitterInScene;
  }

  @Override
  public Pair<EmitterInScene, EmitterSamplingOutput<T>> sample(Point3d p, RandomNumberGenerator rng) {
    int emitterIndex = emitterPdf.sample(rng.next1D());
    double emitterIndexPdf = emitterPdf.pdf(emitterIndex);
    EmitterInScene emitter = emitters.get(emitterIndex);
    EmitterSampler<T> emitterSampler = emitter.getExtension(emitterSamplerVvClass).value();
    EmitterSamplingOutput<T> samplingOutput = emitterSampler.sample(p, rng);
    samplingOutput.value = ss.scale(1.0 / emitterIndexPdf, samplingOutput.value);
    return ImmutablePair.of(emitter, samplingOutput);
  }

  @Override
  public double pdf(EmitterInScene emitter, EmitterPdfInput record) {
    Preconditions.checkArgument(emitterToIndex.containsKey(emitter));
    int emitterIndex = emitterToIndex.get(emitter);
    double emitterIndexPdf = emitterPdf.pdf(emitterIndex);
    double recordPdf = emitter.getExtension(emitterSamplerVvClass).value().pdf(record);
    return emitterIndexPdf * recordPdf;
  }

  @Override
  public T eval(EmitterInScene emitter, EmitterEvalInput record) {
    return emitter.getExtension(emitterSamplerVvClass).value().eval(record);
  }

  @Override
  public Optional<EmitterInScene> getEnvironmentalEmitter() {
    return environmentalEmitter;
  }

  @Override
  public EmitterInScene getSurfaceEmitter(PatchInterval patchInterval) {
    Preconditions.checkArgument(patchIntervalToEmitterInScene.containsKey(patchInterval));
    return patchIntervalToEmitterInScene.get(patchInterval);
  }

  public static class ForRgb extends StandardSceneEmitterSampler<Rgb, Rgb> implements SceneEmitterSamplerRgb {
    @HanaDeclareExtension(
        extensibleClass = StandardScene.class,
        extensionClass = SceneEmitterSamplerRgb.class)
    public ForRgb(StandardScene node, HanaUnwrapper unwrapper) {
      super(node, new RgbSpace(), EmitterSamplerRgb.Vv.class, unwrapper);
    }
  }
}
