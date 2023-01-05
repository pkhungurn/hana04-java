package hana04.base.caching;

import dagger.Lazy;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class HanaUnwrapper {
  private final Lazy<Map<String, HanaCacheLoader<?>>> protocolToCacheLoader;
  private final ConcurrentHashMap<String, SoftReference<Object>> stringKeyToObj = new ConcurrentHashMap<>();

  @Inject
  public HanaUnwrapper(Lazy<Map<String, HanaCacheLoader<?>>> protocolToCacheLoader) {
    this.protocolToCacheLoader = protocolToCacheLoader;
  }

  private boolean isLoaded(CacheKey key) {
    return stringKeyToObj.containsKey(key.stringKey) && stringKeyToObj.get(key.stringKey).get() != null;
  }

  private Object get(CacheKey key) {
    if (!stringKeyToObj.containsKey(key.stringKey)) {
      return null;
    }
    return stringKeyToObj.get(key.stringKey).get();
  }

  private Object getCached(CacheKey key) {
    Object value = get(key);
    if (value != null) {
      return value;
    }
    synchronized (stringKeyToObj) {
      if (!stringKeyToObj.containsKey(key.stringKey)) {
        HanaCacheLoader<?> loader = protocolToCacheLoader.get().get(key.protocol);
        value = loader.load(key);
        stringKeyToObj.put(key.stringKey, new SoftReference<>(value));
      }
    }
    return value;
  }

  /**
   * Whether the given wrapped value can be returned immediately without extensive computation such as cache loading
   * or object creation.
   */
  public boolean isReady(Wrapped<?> wrapped) {
    if (wrapped instanceof Direct) {
      return true;
    } else if (wrapped instanceof Cached) {
      Cached<?> cached = (Cached<?>) wrapped;
      return isLoaded(cached.key);
    } else {
      throw new IllegalArgumentException("\"wrapped\" is not one of the supported type.");
    }
  }

  /**
   * Get the value hidden inside
   */
  public <T> T unwrap(Wrapped<T> wrapped) {
    if (wrapped instanceof Direct) {
      Direct<?> direct = (Direct<?>) wrapped;
      return (T) direct.value;
    } else if (wrapped instanceof Cached) {
      Cached<?> cached = (Cached<?>) wrapped;
      return (T) getCached(cached.key);
    } else {
      throw new IllegalArgumentException("\"wrapped\" is not one of the supported type.");
    }
  }
}
