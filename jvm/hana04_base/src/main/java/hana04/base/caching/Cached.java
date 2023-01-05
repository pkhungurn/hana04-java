package hana04.base.caching;

public final class Cached<T> implements Wrapped<T> {
  public final CacheKey key;

  public Cached(CacheKey key) {
    this.key = key;
  }

  @Override
  public T unwrap(HanaUnwrapper unwrapper) {
    return (T) unwrapper.unwrap(this);
  }

  public static <T> Cached<T> withKey(CacheKey key, Class<T> klass) {
    return new Cached<>(key);
  }

  public static Cached<?> withKey(CacheKey key) {
    return new Cached<>(key);
  }
}
