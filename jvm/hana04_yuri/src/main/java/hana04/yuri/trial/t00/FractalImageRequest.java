package hana04.yuri.trial.t00;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.distrib.request.Request;
import hana04.yuri.TypeIds;

import java.util.UUID;

@HanaDeclareObject(
  parent = Request.class,
  typeId = TypeIds.TYPE_ID_FRACTAL_IMAGE_REQUEST,
  typeNames = {"shakuyaku.FractalImageRequest", "FractalImageRequest"})
public interface FractalImageRequest extends Request {
  @HanaProperty(1)
  UUID uuid();

  @HanaProperty(2)
  Variable<Integer> imageWidth();

  @HanaProperty(3)
  Variable<Integer> imageHeight();

  @HanaProperty(4)
  Variable<Double> centerX();

  @HanaProperty(5)
  Variable<Double> centerY();

  @HanaProperty(6)
  Variable<Double> scale();

  @HanaProperty(7)
  Variable<Integer> sampleCount();
}

