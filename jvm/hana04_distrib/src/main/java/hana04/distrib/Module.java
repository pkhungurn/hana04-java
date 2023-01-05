package hana04.distrib;

import dagger.Binds;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import hana04.base.extension.HanaObject;
import hana04.base.extension.annotation.HanaExtensibleSuperclass;
import hana04.distrib.request.Request;
import hana04.distrib.request.params.NoParameters;
import hana04.distrib.request.params.RequestParametersBuilder;

@dagger.Module
public abstract class Module {
  @Provides
  @IntoMap
  @HanaExtensibleSuperclass
  @ClassKey(Request.class)
  static Class<?> providesRequestSuperclass() {
    return HanaObject.class;
  }

  @Binds
  @IntoMap
  @StringKey("DoNothingRequest")
  abstract RequestParametersBuilder providesDoNothingRequestParameters(NoParameters.Builder builder);
}
