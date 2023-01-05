package hana04.yuri.emitter.envmap;

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
import hana04.shakuyaku.texture.twodim.image.ImageTextureData;
import hana04.yuri.util.DiscretePdf;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple2i;
import javax.vecmath.Vector3d;

public abstract class EnvironmentMapLightSampler<T extends Spectrum, V extends SpectrumTransform<T>>
  implements EmitterSampler<T> {
  private final SpectrumSpace<T, V> ss;
  private final ImageTextureData imageTextureData;
  private final GridMercatorProjection projection;
  private final DiscretePdf pixelPdf;
  private final Transform emitterToWorld;

  EnvironmentMapLightSampler(ImageTextureData imageTextureData, Transform emitterToWorld, SpectrumSpace<T, V> ss) {
    this.ss = ss;
    this.imageTextureData = imageTextureData;
    this.emitterToWorld = new Transform(emitterToWorld.m);
    this.projection = new GridMercatorProjection(imageTextureData.getWidth(), imageTextureData.getHeight());

    DiscretePdf.Builder pixelPdfBuilder = new DiscretePdf.Builder();
    for (int y = 0; y < imageTextureData.getHeight(); y++) {
      for (int x = 0; x < imageTextureData.getWidth(); x++) {
        Spectrum spectrum = imageTextureData.getSpectrum(x, y);
        double average = spectrum.spectrumAverage();
        pixelPdfBuilder.add(average * projection.solidAngle(x, y));
      }
    }
    this.pixelPdf = pixelPdfBuilder.build();
  }

  @Override
  public T eval(EmitterEvalInput record) {
    Vector3d wiLocal = new Vector3d();
    emitterToWorld.mi.transform(record.wiWorld, wiLocal);
    Tuple2i xy = projection.getPixelCoord(wiLocal);
    Spectrum spectrum = imageTextureData.getSpectrum(xy.x, xy.y);
    return ss.convert(spectrum);
  }

  @Override
  public double pdf(EmitterPdfInput record) {
    Vector3d wiLocal = new Vector3d();
    emitterToWorld.mi.transform(record.wiWorld, wiLocal);
    wiLocal.normalize();
    Tuple2i xy = projection.getPixelCoord(wiLocal);
    int pixelIndex = xy.y * imageTextureData.getWidth() + xy.x;
    return pixelPdf.pdf(pixelIndex) / projection.solidAngle(xy.x, xy.y);
  }

  @Override
  public EmitterSamplingOutput<T> sample(Point3d p, RandomNumberGenerator rng) {
    int pixelIndex = pixelPdf.sample(rng.next1D());
    int pixelX = pixelIndex % imageTextureData.getWidth();
    int pixelY = pixelIndex / imageTextureData.getWidth();
    Vector3d wiLocal = projection.sampleDirectionInPixel(pixelX, pixelY, rng);
    Vector3d wiWorld = new Vector3d();
    emitterToWorld.m.transform(wiLocal, wiWorld);
    wiWorld.normalize();
    T value = ss.scale(ss.convert(imageTextureData.getSpectrum(pixelX, pixelY)),
      projection.solidAngle(pixelX, pixelY) / pixelPdf.pdf(pixelIndex));
    EmitterSamplingOutput<T> result = new EmitterSamplingOutput<>();
    result.p.set(p);
    result.wiWorld.set(wiWorld);
    result.measure = Measure.SolidAngle;
    result.value = value;
    return result;
  }

  public static class ForRgb extends EnvironmentMapLightSampler<Rgb, Rgb> implements EmitterSamplerRgb {
    public ForRgb(ImageTextureData imageTextureData, Transform emitterToWorld) {
      super(imageTextureData, emitterToWorld, RgbSpace.I);
    }
  }
}

