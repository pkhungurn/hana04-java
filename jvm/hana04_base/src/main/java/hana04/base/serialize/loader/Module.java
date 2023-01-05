package hana04.base.serialize.loader;

import dagger.Binds;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import hana04.base.caching.HanaCacheLoader;

@dagger.Module
public abstract class Module {
  @Binds
  @IntoMap
  @StringKey(HanaFileLoader.PROTOCOL_NAME)
  abstract HanaCacheLoader<?> providesHanaFileLoader(HanaFileLoader loader);
}
