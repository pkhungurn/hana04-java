package hana04.yuri;

import hana04.yuri.film.filteredrgb.FilteredRgbFilm;
import hana04.yuri.film.simplergb.SimpleRgbFilm;
import hana04.yuri.film.zirr.ZirrFireflyReweightingFilm;
import hana04.yuri.integrand.directillum.DirectIlluminationIntegrandRgb;
import hana04.yuri.integrand.normal.SurfaceNormalIntegrand;
import hana04.yuri.integrand.pathtracer.PathTracerRgb;
import hana04.yuri.integrand.position.SurfacePositionIntegrand;
import hana04.yuri.request.onepassrender.OnePassRenderRequest;
import hana04.yuri.rfilter.BoxFilter;
import hana04.yuri.rfilter.GaussianFilter;
import hana04.yuri.rfilter.TentFilter;
import hana04.yuri.sampler.IndependentSampler;
import hana04.yuri.texture.twodim.checker.CheckerTwoDimTexture;
import hana04.yuri.trial.t00.FractalImageRequest;
import hana04.yuri.trial.t01.RenderFractalImageToFilmRequest;

/**
 * The type IDs of this project starts with 170000;
 */
public class TypeIds {
  /**
   * {@link FilteredRgbFilm}
   */
  public static final int TYPE_ID_FILTERED_RGB_FILM = 170015;
  /**
   * {@link SimpleRgbFilm}
   */
  public static final int TYPE_ID_SIMPLE_RGB_FILM = 170016;
  /**
   * {@link ZirrFireflyReweightingFilm}
   */
  public static final int TYPE_ID_ZIRR_FIREFLY_REWEIGHTING_FILM = 170017;
  /**
   * {@link DirectIlluminationIntegrandRgb}
   */
  public static final int TYPE_ID_DIRECT_ILLUMINATION_INTEGRAND_RGB = 170018;
  /**
   * {@link SurfaceNormalIntegrand}
   */
  public static final int TYPE_ID_SURFACE_NORMAL_INTEGRAND = 170019;
  /**
   * {@link PathTracerRgb}
   */
  public static final int TYPE_ID_PATH_TRACER_RGB = 170020;
  /**
   * {@link SurfacePositionIntegrand}
   */
  public static final int TYPE_ID_SURFACE_POSITION_INTEGRAND = 170021;
  /**
   * {@link OnePassRenderRequest}
   */
  public static final int TYPE_ID_ONE_PASS_RENDER_REQUEST = 170022;
  /**
   * {@link BoxFilter}
   */
  public static final int TYPE_ID_BOX_FILTER = 170023;
  /**
   * {@link GaussianFilter}
   */
  public static final int TYPE_ID_GAUSSIAN_FILTER = 170024;
  /**
   * {@link TentFilter}
   */
  public static final int TYPE_ID_TENT_FILTER = 170025;
  /**
   * {@link IndependentSampler}
   */
  public static final int TYPE_ID_INDEPENDENT_SAMPLER = 170026;
  /**
   * {@link CheckerTwoDimTexture}
   */
  public static final int TYPE_ID_CHECKER_TWO_DIM_TEXTURE = 170045;
  /**
   * {@link FractalImageRequest}
   */
  public static final int TYPE_ID_FRACTAL_IMAGE_REQUEST = 170049;
  /**
   * {@link RenderFractalImageToFilmRequest}
   */
  public static final int TYPE_ID_RENDER_FRACTAL_IMAGE_TO_FILM_REQUEST = 170050;
}
