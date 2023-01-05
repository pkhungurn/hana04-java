package hana04.yuri.request.onepassrender;

import hana04.apt.annotation.HanaDeclareObject;
import hana04.apt.annotation.HanaProperty;
import hana04.base.caching.Wrapped;
import hana04.base.changeprop.Variable;
import hana04.distrib.request.Request;
import hana04.yuri.TypeIds;
import hana04.yuri.film.Film;
import hana04.yuri.integrand.SensorIntegrand;
import hana04.yuri.sampler.Sampler;
import hana04.shakuyaku.scene.Scene;
import hana04.shakuyaku.sensor.camera.Camera;

import java.util.UUID;

@HanaDeclareObject(
  parent = Request.class,
  typeId = TypeIds.TYPE_ID_ONE_PASS_RENDER_REQUEST,
  typeNames = {"shakuyaku.OnePassRenderRequest", "OnePassRenderRequest"})
public interface OnePassRenderRequest extends Request {
  @HanaProperty(1)
  UUID uuid();

  @HanaProperty(2)
  Variable<Film> film();

  @HanaProperty(3)
  Variable<Wrapped<Camera>> camera();

  @HanaProperty(4)
  Variable<Sampler> sampler();

  @HanaProperty(5)
  Variable<SensorIntegrand> integrand();

  @HanaProperty(6)
  Variable<Wrapped<Scene>> scene();

}