package hana04.base.caching;

public final class Direct<T> implements Wrapped<T> {
  public final T value;

  public Direct(T value) {
    this.value = value;
  }

  @Override
  public T unwrap(HanaUnwrapper unwrapper) {
    return value;
  }

  public static <U> Direct<U> of(U value) {
    return new Direct<U>(value);
  }
}
