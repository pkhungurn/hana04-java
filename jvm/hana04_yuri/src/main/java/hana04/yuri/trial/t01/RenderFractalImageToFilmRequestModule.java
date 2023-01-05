package hana04.yuri.trial.t01;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import hana04.distrib.request.params.RequestParametersBuilder;
import hana04.yuri.request.params.BlockRendererParameters;

@Module
public abstract class RenderFractalImageToFilmRequestModule {
  @Binds
  @IntoMap
  @StringKey("RenderFractalImageToFilmRequest")
  public abstract RequestParametersBuilder provideRenderFractalImageToFilmRequestParametersBuilder(
      BlockRendererParameters.Builder builder);
}
