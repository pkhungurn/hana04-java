package hana04.yuri.integrand.directillum;

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
import hana04.yuri.surface.Intersection;
import hana04.shakuyaku.surface.PatchInterval;
import hana04.shakuyaku.surface.Surface;
import hana04.shakuyaku.surface.SurfacePatchIntervalInfo;
import hana04.shakuyaku.surface.SurfaceShadingInfo;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.util.Optional;

public class DirectIlluminationIntegrandEvaluator<T extends Spectrum, V extends SpectrumTransform<T>>
  implements SensorIntegrandEvaluator {
  private final String strategy;
  private final Class<? extends SceneEmitterSampler<T>> sceneEmitterSamplerClass;
  private final Class<? extends VersionedValue<? extends BsdfEvaluator<T, V>>> bsdfEvalutorVvClass;
  private final Class<? extends VersionedValue<? extends BsdfSampler<T, V>>> bsdfSamplerVvClass;
  private final Class<? extends VersionedValue<? extends EmitterSampler<T>>> emitterSamplerVvClass;
  private final Class<? extends SpectrumRecorder<T>> recorderClass;
  private final SpectrumSpace<T, V> ss;

  DirectIlluminationIntegrandEvaluator(
    String strategy,
    SpectrumSpace<T, V> ss,
    Class<? extends VersionedValue<? extends BsdfEvaluator<T, V>>> bsdfEvalutorVvClass,
    Class<? extends VersionedValue<? extends BsdfSampler<T, V>>>  bsdfSamplerVvClass,
    Class<? extends VersionedValue<? extends EmitterSampler<T>>> emitterSamplerVvClass,
    Class<? extends SceneEmitterSampler<T>> sceneEmitterSamplerClass,
    Class<? extends SpectrumRecorder<T>> recorderClass) {
    this.strategy = strategy;
    this.ss = ss;
    this.bsdfEvalutorVvClass = bsdfEvalutorVvClass;
    this.bsdfSamplerVvClass = bsdfSamplerVvClass;
    this.emitterSamplerVvClass = emitterSamplerVvClass;
    this.sceneEmitterSamplerClass = sceneEmitterSamplerClass;
    this.recorderClass = recorderClass;
  }

  @Override
  public void eval(Scene scene, Ray ray, RandomNumberGenerator rng, FilmRecorder filmRecorder) {
    new IntegrandState(scene, rng, filmRecorder).eval(ray);
  }

  class IntegrandState {
    private final Scene scene;
    private final RandomNumberGenerator rng;
    private final SpectrumRecorder<T> recorder;
    private final SceneRayIntersector rayIntersector;
    private final SceneEmitterSampler<T> sceneEmitterSampler;

    public IntegrandState(Scene scene, RandomNumberGenerator rng, FilmRecorder filmRecorder) {
      this.scene = scene;
      this.rng = rng;

      Preconditions.checkArgument(recorderClass.isAssignableFrom(filmRecorder.getClass()),
        "filmRecorder is not of the right class.");
      recorder = (SpectrumRecorder<T>) filmRecorder;

      rayIntersector = scene.getExtension(SceneRayIntersector.class);
      sceneEmitterSampler = scene.getExtension(sceneEmitterSamplerClass);
    }

    void eval(Ray ray) {
      Optional<Pair<Surface, Intersection>> surfaceIntersection = rayIntersector.rayIntersect(ray);
      if (!surfaceIntersection.isPresent()) {
        recorder.record(getEnvironmentLight(ray));
      } else {
        Surface surface = surfaceIntersection.get().getLeft();
        Intersection intersection = surfaceIntersection.get().getRight();
        recorder.record(getSurfaceEmitterLight(ray, surface, intersection).getLeft());
        switch (strategy) {
          case "emitter":
            recordEmitterSampling(ray, surface, intersection, false);
            break;
          case "bsdf":
            recordBsdfSampling(ray, surface, intersection, false);
            break;
          case "mis":
            recordEmitterSampling(ray, surface, intersection, true);
            recordBsdfSampling(ray, surface, intersection, true);
            break;
          default:
        }
      }
    }

    private Triple<T, Optional<EmitterInScene>, EmitterPdfInput> getSurfaceEmitterLight(Ray ray, Surface surface,
                                                                                        Intersection intersection) {
      SurfaceShadingInfo shadingInfo = surface.getExtension(SurfaceShadingInfo.Vv.class).value();
      Optional<Emitter> optionalSurfaceEmitter = shadingInfo.getEmitter(intersection.patchIndex());

      EmitterPdfInput emitterPdfInput = new EmitterPdfInput();
      emitterPdfInput.p.set(ray.o);
      emitterPdfInput.wiWorld.set(VecMathDUtil.normalize(ray.d));
      emitterPdfInput.measure = Measure.SolidAngle;

      if (!optionalSurfaceEmitter.isPresent()) {
        return ImmutableTriple.of(ss.createZero(), Optional.empty(), emitterPdfInput);
      }
      PatchInterval patchInterval = surface.getExtension(SurfacePatchIntervalInfo.class)
        .mapPatchToPatchInterval(intersection.patchIndex());
      EmitterInScene surfaceEmitter = sceneEmitterSampler.getSurfaceEmitter(patchInterval);
      EmitterSampler<T> emitterSampler = surfaceEmitter.getExtension(emitterSamplerVvClass).value();

      emitterPdfInput.q = Optional.of(new Point3d(intersection.p));
      emitterPdfInput.nq = Optional.of(new Vector3d(intersection.shFrame.n));
      T value = emitterSampler.eval(emitterPdfInput);

      return ImmutableTriple.of(value, Optional.of(surfaceEmitter), emitterPdfInput);
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

    private void recordEmitterSampling(Ray ray, Surface surface, Intersection intersection, boolean useMis) {
      Pair<EmitterInScene, EmitterSamplingOutput<T>> emitterSamplingOutput =
        sceneEmitterSampler.sample(intersection.p, rng);
      EmitterSamplingOutput<T> emitterRecord = emitterSamplingOutput.getRight();
      Ray shadowRay = createShadowRay(emitterRecord, intersection);
      if (rayIntersector.checkRayIntersect(shadowRay)) {
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
      T radiance = ss.scale(Frame.cosTheta(wiLocal), ss.transform(bsdfValue, emitterRecord.value));

      if (!useMis) {
        recorder.record(radiance);
        return;
      }

      double emitterPdf = sceneEmitterSampler.pdf(emitterSamplingOutput.getLeft(), emitterRecord);
      if (emitterPdf <= 0) {
        return;
      }

      BsdfSampler<T, V> bsdfSampler = bsdf.getExtension(bsdfSamplerVvClass).value();
      BsdfSamplingInput bsdfSamplingInput = new BsdfSamplingInput(
        woLocal, intersection.uv, SampledDirection.Wi);
      double bsdfPdf = bsdfSampler.pdf(bsdfSamplingInput, wiLocal, Measure.SolidAngle);

      recorder.record(ss.scale(emitterPdf / (bsdfPdf + emitterPdf), radiance));
    }

    private Ray createShadowRay(EmitterSamplingOutput<T> emitterRecord, Intersection intersection) {
      Ray shadowRay = new Ray();
      shadowRay.o.set(intersection.p);
      shadowRay.d.set(emitterRecord.wiWorld);
      shadowRay.mint = 2 * Ray.EPSILON;
      shadowRay.maxt = emitterRecord.q
        .map(q -> shadowRay.o.distance(q) - Ray.EPSILON)
        .orElse(Double.POSITIVE_INFINITY);
      return shadowRay;
    }

    private void recordBsdfSampling(Ray ray, Surface surface, Intersection intersection, boolean useMis) {
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
      Triple<T, Optional<EmitterInScene>, EmitterPdfInput> LiWithEmitterInScene =
        getDirectLightWithEmitterInScene(lightRay);
      T Li = LiWithEmitterInScene.getLeft();
      if (Li.isZero() || !LiWithEmitterInScene.getMiddle().isPresent()) {
        return;
      }
      T Lo = ss.transform(bsdfSamplingOutput.value, Li);

      if (!useMis) {
        recorder.record(Lo);
        return;
      }

      double bsdfPdf = bsdfSampler.pdf(bsdfSamplingInput, bsdfSamplingOutput.wOut, bsdfSamplingOutput.measure);
      double emitterPdf = sceneEmitterSampler.pdf(
        LiWithEmitterInScene.getMiddle().get(),
        LiWithEmitterInScene.getRight());
      double weight = bsdfPdf / (bsdfPdf + emitterPdf);
      recorder.record(ss.scale(weight, Lo));
    }

    private T getDirectLight(Ray ray) {
      Optional<Pair<Surface, Intersection>> surfaceIntersection = rayIntersector.rayIntersect(ray);
      if (!surfaceIntersection.isPresent()) {
        return getEnvironmentLight(ray);
      }
      Surface surface = surfaceIntersection.get().getLeft();
      Intersection intersection = surfaceIntersection.get().getRight();
      return getSurfaceEmitterLight(ray, surface, intersection).getLeft();
    }

    private Triple<T, Optional<EmitterInScene>, EmitterPdfInput> getDirectLightWithEmitterInScene(Ray ray) {
      Optional<Pair<Surface, Intersection>> surfaceIntersection = rayIntersector.rayIntersect(ray);
      if (!surfaceIntersection.isPresent()) {
        EmitterPdfInput emitterPdfInput = new EmitterPdfInput();
        emitterPdfInput.p.set(ray.o);
        emitterPdfInput.measure = Measure.SolidAngle;
        emitterPdfInput.wiWorld.set(VecMathDUtil.normalize(ray.d));
        return ImmutableTriple.of(getEnvironmentLight(ray),
          sceneEmitterSampler.getEnvironmentalEmitter(),
          emitterPdfInput);
      } else {
        Surface surface = surfaceIntersection.get().getLeft();
        Intersection intersection = surfaceIntersection.get().getRight();
        return getSurfaceEmitterLight(ray, surface, intersection);
      }
    }
  }
}
