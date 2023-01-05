package hana04.yuri.request.onepassrender;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import hana04.distrib.request.params.RequestParametersBuilder;
import hana04.yuri.request.params.BlockRendererParameters;

@Module
public abstract class OnePassRenderRequestModule {
  @Binds
  @IntoMap
  @StringKey("OnePassRenderRequest")
  abstract RequestParametersBuilder providesDoNothingRequestParameters(BlockRendererParameters.Builder builder);
}
