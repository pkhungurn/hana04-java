package hana04.base.extension;

import hana04.base.caching.Direct;
import hana04.base.caching.Wrapped;

import java.util.function.Consumer;

public interface HanaExtensible {
  <T> boolean hasExtension(Class<T> klass);

  <T> boolean supportsExtension(Class<T> klass);

  <T> T getExtension(Class<T> klass);

  <T> void prepareExtension(Class<T> klass);

  Iterable<Object> extensions();

  default <T extends HanaExtensible> Wrapped<T> wrapped() {
    return Direct.of((T) this);
  }

  default <T extends HanaExtensible> T also(Class<T> klass, Consumer<T> selfConsumer) {
    selfConsumer.accept(klass.cast(this));
    return klass.cast(this);
  }
}
