package hana04.base.caching;

public interface HanaCacheLoader<T> {
  T load(CacheKey key);
}
