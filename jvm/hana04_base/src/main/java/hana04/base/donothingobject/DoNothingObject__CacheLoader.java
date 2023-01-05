package hana04.base.donothingobject;

import com.google.common.base.Preconditions;
import hana04.base.caching.CacheKey;
import hana04.base.caching.HanaCacheLoader;
import hana04.base.extension.HanaExtensionUberFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DoNothingObject__CacheLoader implements HanaCacheLoader<DoNothingObject> {
  private final HanaExtensionUberFactory extensionUberFactory;

  @Inject
  DoNothingObject__CacheLoader(HanaExtensionUberFactory extensionUberFactory) {
    this.extensionUberFactory = extensionUberFactory;
  }

  @Override
  public DoNothingObject load(CacheKey key) {
    Preconditions.checkArgument(key.protocol.equals(DoNothingObject.TYPE_NAME));
    Preconditions.checkArgument(key.parts.isEmpty());
    return new DoNothingObject(extensionUberFactory);
  }
}
