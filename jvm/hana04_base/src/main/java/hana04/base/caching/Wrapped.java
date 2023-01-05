package hana04.base.caching;

public interface Wrapped<T> {
  T unwrap(HanaUnwrapper unwrapper);
}
