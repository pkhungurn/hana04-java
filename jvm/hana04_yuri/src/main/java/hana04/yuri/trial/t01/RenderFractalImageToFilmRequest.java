package hana04.yuri.trial.t01;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.changeprop.Variable;
import hana04.distrib.request.Request;
import hana04.yuri.TypeIds;
import hana04.yuri.film.Film;
import hana04.yuri.sampler.Sampler;

import java.util.UUID;

@HanaDeclareObject(
    parent = Request.class,
    typeId = TypeIds.TYPE_ID_RENDER_FRACTAL_IMAGE_TO_FILM_REQUEST,
    typeNames = {"shakuyaku.RenderFractalImageToFilmRequest", "RenderFractalImageToFilmRequest"})
public interface RenderFractalImageToFilmRequest extends Request {
  @HanaProperty(1)
  UUID uuid();

  @HanaProperty(2)
  Variable<Double> centerX();

  @HanaProperty(3)
  Variable<Double> centerY();

  @HanaProperty(4)
  Variable<Double> scale();

  @HanaProperty(5)
  Variable<Sampler> sampler();

  @HanaProperty(6)
  Variable<Film> film();
}