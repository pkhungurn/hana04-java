package hana04.yuri.integrand.pathtracer;

import com.google.common.base.Preconditions;
import hana04.base.changeprop.VersionedValue;
import hana04.gfxbase.spectrum.Spectrum;
import hana04.gfxbase.spectrum.SpectrumSpace;
import hana04.gfxbase.spectrum.SpectrumTransform;
import hana04.gfxbase.gfxtype.Frame;
import hana04.gfxbase.gfxtype.Measure;
import hana04.gfxbase.gfxtype.Ray;
import hana04.gfxbase.gfxtype.VecMathDUtil;
import hana04.shakuyaku.bsdf.Bsdf;
import hana04.yuri.bsdf.BsdfEvaluator;
import hana04.yuri.bsdf.BsdfSampler;
import hana04.yuri.bsdf.BsdfSamplingInput;
import hana04.yuri.bsdf.BsdfSamplingOutput;
import hana04.yuri.bsdf.SampledDirection;
import hana04.yuri.bsdf.TransmittanceEvaluator;
import hana04.yuri.bsdf.shader.AmbientLightShader;
import hana04.shakuyaku.emitter.Emitter;
import hana04.yuri.emitter.EmitterEvalInput;
import hana04.yuri.emitter.EmitterInScene;
import hana04.yuri.emitter.EmitterPdfInput;
import hana04.yuri.emitter.EmitterSampler;
import hana04.yuri.emitter.EmitterSamplingOutput;
import hana04.yuri.film.FilmRecorder;
import hana04.yuri.film.recorder.SpectrumRecorder;
import hana04.yuri.integrand.SensorIntegrandEvaluator;
import hana04.yuri.sampler.RandomNumberGenerator;
import hana04.shakuyaku.scene.Scene;
import hana04.yuri.scene.SceneEmitterSampler;
import hana04.yuri.scene.SceneRayIntersector;
import hana04.shakuyaku.scene.shadinghack.ConstantAmbientLightInScene;
import hana04.shakuyaku.shadinghack.ambientlight.ConstantAmbientLight;
import hana04.yuri.surface.Intersection;
import hana04.shakuyaku.surface.PatchInterval;
import hana04.shakuyaku.surface.Surface;
import hana04.shakuyaku.surface.SurfacePatchIntervalInfo;
import hana04.shakuyaku.surface.SurfaceShadingInfo;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.util.Optional;

public class PathTracerEvaluator<T extends Spectrum, V extends SpectrumTransform<T>>
  implements SensorIntegrandEvaluator {
  private static final double TRANSMITTANCE_EPSILON = 1e-6;

  private final PathTracer pathTracer;
  private final SpectrumSpace<T, V> ss;
  private final Class<? extends SpectrumRecorder<T>> spectrumRecorderClass;
  private final Class<? extends VersionedValue<? extends EmitterSampler<T>>> emitterSamplerVvClass;
  private final Class<? extends SceneEmitterSampler<T>> sceneEmitterSamplerClass;
  private final Class<? extends VersionedValue<? extends BsdfEvaluator<T, V>>> bsdfEvalutorVvClass;
  private final Class<? extends VersionedValue<? extends BsdfSampler<T, V>>> bsdfSamplerVvClass;
  private final Class<? extends VersionedValue<? extends AmbientLightShader<T, V>>> constantAmbientLightShaderVvClass;

  private final int minDepth;
  private final int maxDepth;

  public PathTracerEvaluator(
    PathTracer pathTracer,
    SpectrumSpace<T, V> ss,
    Class<? extends SpectrumRecorder<T>> spectrumRecorderClass,
    Class<? extends VersionedValue<? extends BsdfEvaluator<T, V>>> bsdfEvalutorVvClass,
    Class<? extends VersionedValue<? extends BsdfSampler<T, V>>> bsdfSamplerVvClass,
    Class<? extends VersionedValue<? extends EmitterSampler<T>>> emitterSamplerVvClass,
    Class<? extends SceneEmitterSampler<T>> sceneEmitterSamplerClass,
    Class<? extends VersionedValue<? extends AmbientLightShader<T, V>>> constantAmbientLightShaderVvClass) {
    this.pathTracer = pathTracer;
    this.ss = ss;
    this.spectrumRecorderClass = spectrumRecorderClass;
    this.bsdfEvalutorVvClass = bsdfEvalutorVvClass;
    this.bsdfSamplerVvClass = bsdfSamplerVvClass;
    this.emitterSamplerVvClass = emitterSamplerVvClass;
    this.sceneEmitterSamplerClass = sceneEmitterSamplerClass;
    this.constantAmbientLightShaderVvClass = constantAmbientLightShaderVvClass;

    if (pathTracer.minDepth().value().isPresent()) {
      minDepth = pathTracer.minDepth().value().get();
    } else {
      minDepth = -1;
    }
    if (pathTracer.maxDepth().value().isPresent()) {
      maxDepth = pathTracer.maxDepth().value().get();
    } else {
      maxDepth = Integer.MAX_VALUE;
    }
  }

  @Override
  public void eval(Scene scene, Ray ray, RandomNumberGenerator rng, FilmRecorder recorder) {
    new PathTracerEvaluatorState(scene, rng, recorder).trace(ray);
  }

  private boolean shouldRecordContribution(int depth) {
    return depth >= minDepth && depth <= maxDepth;
  }

  private boolean shouldStopTracing(int depth) {
    return depth > maxDepth;
  }

  class PathTracerEvaluatorState {
    Scene scene;
    RandomNumberGenerator rng;
    FilmRecorder recorder;
    SpectrumRecorder<T> spectrumRecorder;
    // Extensions
    SceneRayIntersector sceneRayIntersector;
    SceneEmitterSampler<T> sceneEmitterSampler;
    // Shading hacks
    Optional<ConstantAmbientLight> constantAmbientLight;

    PathTracerEvaluatorState(Scene scene, RandomNumberGenerator rng, FilmRecorder recorder) {
      this.scene = scene;
      this.rng = rng;
      this.recorder = recorder;
      Preconditions.checkArgument(spectrumRecorderClass.isAssignableFrom(recorder.getClass()));
      this.spectrumRecorder = (SpectrumRecorder<T>) recorder;

      sceneRayIntersector = scene.getExtension(SceneRayIntersector.class);
      sceneEmitterSampler = scene.getExtension(sceneEmitterSamplerClass);

      constantAmbientLight = scene.getExtension(ConstantAmbientLightInScene.class).get();
    }

    void trace(Ray incomingRay) {
      Ray ray = new Ray(incomingRay);
      int depth = 0;
      V throughput = ss.createIdentityTransform();
      boolean recordAmbient = constantAmbientLight.isPresent() && pathTracer.computeAmbientIllumination().value();

      while (!shouldStopTracing(depth)) {
        Optional<Pair<Surface, Intersection>> optionalIntersection = sceneRayIntersector.rayIntersect(ray);

        if (!optionalIntersection.isPresent()) {
          if (depth == 0) {
            recordEnvironmentalEmission(ray, throughput, depth);
          }
          break;
        }

        Surface surface = optionalIntersection.get().getLeft();
        Intersection intersection = optionalIntersection.get().getRight();

        // Ambient light
        if (recordAmbient) {
          recordAmbientContribution(ray, surface, intersection, throughput, depth);
        }
        // Surface emission.
        if (depth == 0) {
          recordSurfaceEmission(ray, surface, intersection, throughput, depth);
        }
        // Emitter sampling.
        recordEmitterSampling(ray, surface, intersection, throughput, depth);
        // BSDF sampling
        RecordBsdfSamplingOutput<T, V> recordBsdfSamplingOutput =
          recordBsdfSampling(
            ray, surface, intersection, throughput, depth,
            /* shouldRecordPassthroughDirectIllumination= */ depth == 0);

        throughput = recordBsdfSamplingOutput.throughput;
        ray = recordBsdfSamplingOutput.ray;
        if (throughput.isZero()) {
          break;
        }
        recordAmbient = recordAmbient && recordBsdfSamplingOutput.shouldRecordAmbient;
        // Russian roulette
        if (rng.next1D() < pathTracer.terminationProb().value()) {
          break;
        } else {
          throughput = ss.scale(throughput, 1.0 / (1 - pathTracer.terminationProb().value()));
        }
        depth++;
      }
    }

    void recordEnvironmentalEmission(Ray ray, V throughput, int depth) {
      if (!shouldRecordContribution(depth)) {
        return;
      }
      T contribution = getEnvironmentLight(ray);
      T xformedContrib = ss.transform(throughput, contribution);
      if (!xformedContrib.isNaN()) {
        spectrumRecorder.record(xformedContrib);
      }
    }

    private T getEnvironmentLight(Ray ray) {
      Optional<EmitterInScene> environmentEmitter = sceneEmitterSampler.getEnvironmentalEmitter();
      if (!environmentEmitter.isPresent()) {
        return ss.createZero();
      }
      EmitterSampler<T> emitterSampler = environmentEmitter.get().getExtension(emitterSamplerVvClass).value();
      EmitterEvalInput emitterEvalInput = new EmitterEvalInput();
      emitterEvalInput.p.set(ray.o);
      emitterEvalInput.wiWorld.set(VecMathDUtil.normalize(ray.d));
      return emitterSampler.eval(emitterEvalInput);
    }

    void recordAmbientContribution(Ray ray, Surface surface, Intersection intersection, V throughput, int depth) {
      if (!shouldRecordContribution(depth)) {
        return;
      }

      SurfaceShadingInfo shadingInfo = surface.getExtension(SurfaceShadingInfo.Vv.class).value();
      Bsdf bsdf = shadingInfo.getBsdf(intersection.patchIndex());
      AmbientLightShader<T, V> shader = bsdf.getExtension(constantAmbientLightShaderVvClass).value();

      Vector3d wiLocal = new Vector3d();
      intersection.shFrame.toLocal(ray.d, wiLocal);
      wiLocal.normalize();

      V ambientXform = shader.shade(wiLocal, intersection.uv);
      T contrib = ss.transform(throughput,
        ss.transform(ambientXform,
          ss.convert(constantAmbientLight.get().intensity().value())));
      if (!contrib.isNaN()) {
        spectrumRecorder.record(contrib);
      }
    }

    void recordSurfaceEmission(Ray ray, Surface surface, Intersection intersection, V throughput, int depth) {
      if (!shouldRecordContribution(depth)) {
        return;
      }
      T surfaceLight = getSurfaceEmitterLight(ray, surface, intersection, 1.0).Li;
      T xformedContrib = ss.transform(throughput, surfaceLight);
      if (!xformedContrib.isNaN()) {
        spectrumRecorder.record(xformedContrib);
      }
    }

    private TransmittedLightOutput<T> getSurfaceEmitterLight(Ray ray, Surface surface,
                                                             Intersection intersection,
                                                             double transmittance) {
      SurfaceShadingInfo shadingInfo = surface.getExtension(SurfaceShadingInfo.Vv.class).value();
      Optional<Emitter> optionalSurfaceEmitter = shadingInfo.getEmitter(intersection.patchIndex());

      EmitterPdfInput emitterPdfInput = new EmitterPdfInput();
      emitterPdfInput.p.set(ray.o);
      emitterPdfInput.wiWorld.set(VecMathDUtil.normalize(ray.d));
      emitterPdfInput.measure = Measure.SolidAngle;

      if (!optionalSurfaceEmitter.isPresent()) {
        return new TransmittedLightOutput<>(ss.createZero(), Optional.empty(), emitterPdfInput);
      }
      PatchInterval patchInterval = surface.getExtension(SurfacePatchIntervalInfo.Vv.class).value()
        .mapPatchToPatchInterval(intersection.patchIndex());
      EmitterInScene surfaceEmitter = sceneEmitterSampler.getSurfaceEmitter(patchInterval);
      EmitterSampler<T> emitterSampler = surfaceEmitter.getExtension(emitterSamplerVvClass).value();

      emitterPdfInput.q = Optional.of(new Point3d(intersection.p));
      emitterPdfInput.nq = Optional.of(new Vector3d(intersection.shFrame.n));
      T value = emitterSampler.eval(emitterPdfInput);

      return new TransmittedLightOutput<>(ss.scale(transmittance, value), Optional.of(surfaceEmitter), emitterPdfInput);
    }

    private void recordEmitterSampling(Ray ray, Surface surface, Intersection intersection, V throughput, int depth) {
      if (!shouldRecordContribution(depth)) {
        return;
      }

      Pair<EmitterInScene, EmitterSamplingOutput<T>> emitterSamplingOutput =
        sceneEmitterSampler.sample(intersection.p, rng);
      EmitterSamplingOutput<T> emitterRecord = emitterSamplingOutput.getRight();
      Ray shadowRay = createShadowRay(emitterRecord, intersection);
      if (shadowRay.d.dot(intersection.shFrame.n) * shadowRay.d.dot(intersection.geoFrame.n) < 0) {
        return;
      }
      double transmittance = computeTransmittance(shadowRay);
      if (transmittance == 0) {
        return;
      }

      SurfaceShadingInfo shadingInfo = surface.getExtension(SurfaceShadingInfo.Vv.class).value();
      Bsdf bsdf = shadingInfo.getBsdf(intersection.patchIndex());
      BsdfEvaluator<T, V> bsdfEvaluator = bsdf.getExtension(bsdfEvalutorVvClass).value();
      Vector3d wiLocal = new Vector3d();
      intersection.toLocal(emitterRecord.wiWorld, wiLocal);
      Vector3d woLocal = new Vector3d();
      intersection.toLocal(VecMathDUtil.negate(ray.d), woLocal);
      woLocal.normalize();
      V bsdfValue = bsdfEvaluator.eval(wiLocal, woLocal, intersection.uv);
      T radiance = ss.scale(ss.scale(Math.abs(Frame.cosTheta(wiLocal)), ss.transform(bsdfValue, emitterRecord.value)),
        transmittance);

      double emitterPdf = sceneEmitterSampler.pdf(emitterSamplingOutput.getLeft(), emitterRecord);
      if (emitterPdf <= 0) {
        return;
      }

      BsdfSampler<T, V> bsdfSampler = bsdf.getExtension(bsdfSamplerVvClass).value();
      BsdfSamplingInput bsdfSamplingInput = new BsdfSamplingInput(
        woLocal, intersection.uv, SampledDirection.Wi);
      double bsdfPdf = bsdfSampler.pdf(bsdfSamplingInput, wiLocal, Measure.SolidAngle);

      T contrib = ss.transform(throughput, ss.scale(emitterPdf / (bsdfPdf + emitterPdf), radiance));
      if (!contrib.isNaN()) {
        spectrumRecorder.record(contrib);
      }
    }

    private double computeTransmittance(Ray inputShadowRay) {
      Ray shadowRay = new Ray(inputShadowRay);
      double transmittance = 1.0;
      while (true) {
        Optional<Pair<Surface, Intersection>> optionalIntersection = sceneRayIntersector.rayIntersect(shadowRay);
        if (!optionalIntersection.isPresent()) {
          break;
        }
        Surface surface = optionalIntersection.get().getLeft();
        Intersection intersection = optionalIntersection.get().getRight();
        SurfaceShadingInfo shadingInfo = surface.getExtension(SurfaceShadingInfo.Vv.class).value();
        Bsdf bsdf = shadingInfo.getBsdf(intersection.patchIndex());
        TransmittanceEvaluator transmittanceEvaluator = bsdf.getExtension(TransmittanceEvaluator.Vv.class).value();
        double t = transmittanceEvaluator.eval(shadowRay.d, intersection.uv);
        transmittance *= t;
        if (transmittance < TRANSMITTANCE_EPSILON) {
          transmittance = 0;
          break;
        }
        shadowRay.maxt = inputShadowRay.maxt;
        shadowRay.mint = intersection.t() + Ray.EPSILON;
      }
      return transmittance;
    }

    private Ray createShadowRay(EmitterSamplingOutput<T> emitterRecord, Intersection intersection) {
      Ray shadowRay = new Ray();
      shadowRay.o.set(intersection.p);
      shadowRay.d.set(emitterRecord.wiWorld);
      shadowRay.mint = Ray.EPSILON;
      shadowRay.maxt = emitterRecord.q
        .map(q -> shadowRay.o.distance(q) - Ray.EPSILON)
        .orElse(Double.POSITIVE_INFINITY);
      return shadowRay;
    }

    private RecordBsdfSamplingOutput<T, V> recordBsdfSampling(
      Ray ray,
      Surface surface,
      Intersection intersection,
      V throughput,
      int depth,
      boolean shouldRecordPassThroughDirectIllumination) {
      Vector3d woLocal = new Vector3d();
      intersection.toLocal(VecMathDUtil.negate(ray.d), woLocal);
      woLocal.normalize();

      SurfaceShadingInfo shadingInfo = surface.getExtension(SurfaceShadingInfo.Vv.class).value();
      Bsdf bsdf = shadingInfo.getBsdf(intersection.patchIndex());
      BsdfSampler<T, V> bsdfSampler = bsdf.getExtension(bsdfSamplerVvClass).value();
      BsdfSamplingInput bsdfSamplingInput = new BsdfSamplingInput(woLocal, intersection.uv, SampledDirection.Wi);
      BsdfSamplingOutput<T, V> bsdfSamplingOutput = bsdfSampler.sample(bsdfSamplingInput, rng);

      Vector3d woWorld = new Vector3d();
      intersection.toWorld(bsdfSamplingOutput.wOut, woWorld);
      woWorld.normalize();
      Ray lightRay = new Ray(intersection.p, woWorld, Ray.EPSILON, Double.POSITIVE_INFINITY);
      if (lightRay.d.dot(intersection.shFrame.n) * lightRay.d.dot(intersection.geoFrame.n) < 0) {
        return new RecordBsdfSamplingOutput<>(ss.createZeroTransform(), lightRay, false);
      }

      double weight = 0.0;
      double bsdfPdf = bsdfSampler.pdf(bsdfSamplingInput, bsdfSamplingOutput.wOut, bsdfSamplingOutput.measure);
      if (bsdfSamplingOutput.measure.equals(Measure.Discrete)) {
        weight = 1.0;
      } else if (bsdfPdf == 0.0) {
        return new RecordBsdfSamplingOutput<>(ss.createZeroTransform(), lightRay, false);
      }

      if (!bsdfSamplingOutput.isPassThrough || shouldRecordPassThroughDirectIllumination) {
        TransmittedLightOutput<T> transmittedLightOutput =
          getTransmittedDirectLightWithEmitterInScene(lightRay);
        if (!bsdfSamplingOutput.measure.equals(Measure.Discrete)) {
          double emitterPdf = 0.0;
          if (transmittedLightOutput.emitter.isPresent()) {
            emitterPdf = sceneEmitterSampler.pdf(
              transmittedLightOutput.emitter.get(),
              transmittedLightOutput.emitterPdfInput);
          }
          weight = bsdfPdf / (bsdfPdf + emitterPdf);
        }

        if (shouldRecordContribution(depth) && !throughput.isZero() && weight > 0) {
          T Li = transmittedLightOutput.Li;
          if (!Li.isZero() && transmittedLightOutput.emitter.isPresent()) {
            T Lo = ss.transform(bsdfSamplingOutput.value, Li);
            T contrib = ss.transform(throughput, ss.scale(weight, Lo));
            if (!contrib.isNaN()) {
              spectrumRecorder.record(contrib);
            }
          }
        }

        lightRay.maxt = Double.MAX_VALUE;
      }

      V newFactor = ss.mul(throughput, bsdfSamplingOutput.value);
      return new RecordBsdfSamplingOutput<>(newFactor, lightRay, bsdfSamplingOutput.measure.equals(Measure.Discrete));
    }

    private TransmittedLightOutput<T> getTransmittedDirectLightWithEmitterInScene(Ray inputRay) {
      Ray ray = new Ray(inputRay);
      double transmittance = 1.0;
      EmitterPdfInput emitterPdfInput = new EmitterPdfInput();
      emitterPdfInput.p.set(inputRay.o);
      emitterPdfInput.measure = Measure.SolidAngle;
      emitterPdfInput.wiWorld.set(VecMathDUtil.normalize(inputRay.d));

      while (true) {
        Optional<Pair<Surface, Intersection>> optionalIntersection = sceneRayIntersector.rayIntersect(ray);

        if (!optionalIntersection.isPresent()) {
          return new TransmittedLightOutput<>(
            ss.scale(transmittance, getEnvironmentLight(inputRay)),
            sceneEmitterSampler.getEnvironmentalEmitter(),
            emitterPdfInput);
        }

        Surface surface = optionalIntersection.get().getLeft();
        Intersection intersection = optionalIntersection.get().getRight();
        SurfaceShadingInfo shadingInfo = surface.getExtension(SurfaceShadingInfo.Vv.class).value();
        Optional<Emitter> optionalEmitter = shadingInfo.getEmitter(intersection.patchIndex());
        if (optionalEmitter.isPresent()) {
          return getSurfaceEmitterLight(ray, surface, intersection, transmittance);
        }

        Bsdf bsdf = shadingInfo.getBsdf(intersection.patchIndex());
        TransmittanceEvaluator transmittanceEvaluator = bsdf.getExtension(TransmittanceEvaluator.Vv.class).value();
        double t = transmittanceEvaluator.eval(ray.d, intersection.uv);
        transmittance *= t;
        if (transmittance < TRANSMITTANCE_EPSILON) {
          return new TransmittedLightOutput<>(ss.createZero(), Optional.empty(), emitterPdfInput);
        }
        ray.maxt = inputRay.maxt;
        ray.mint = intersection.t() + Ray.EPSILON;
      }
    }
  }

  static class TransmittedLightOutput<T extends Spectrum> {
    T Li;
    Optional<EmitterInScene> emitter;
    EmitterPdfInput emitterPdfInput;

    TransmittedLightOutput(T Li, Optional<EmitterInScene> emitter, EmitterPdfInput emitterPdfInput) {
      this.Li = Li;
      this.emitter = emitter;
      this.emitterPdfInput = emitterPdfInput;
    }
  }

  static class RecordBsdfSamplingOutput<T extends Spectrum, V extends SpectrumTransform<T>> {
    V throughput;
    Ray ray;
    boolean shouldRecordAmbient;

    RecordBsdfSamplingOutput(V throughput, Ray ray, boolean shouldRecordAmbient) {
      this.throughput = throughput;
      this.ray = ray;
      this.shouldRecordAmbient = shouldRecordAmbient;
    }
  }
}
