package hana04.base.extension;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractHanaExtensible implements HanaExtensible {
  private final HanaExtensionUberFactory extensionUberFactory;
  private final Map<Class<?>, Object> extensionMap;

  public AbstractHanaExtensible(HanaExtensionUberFactory extensionUberFactory) {
    this.extensionUberFactory = extensionUberFactory;
    this.extensionMap = new HashMap<>();
  }

  protected abstract Class<? extends HanaExtensible> getExtensibleClass();

  @Override
  public <T> boolean hasExtension(Class<T> klass) {
    return extensionMap.containsKey(klass);
  }

  @Override
  public <T> boolean supportsExtension(Class<T> klass) {
    return extensionUberFactory.supportsExtension(getExtensibleClass(), klass);
  }

  private <V> V createExtension(Class<V> klass) {
    return (V) extensionUberFactory.createExtension(this, getExtensibleClass(), klass);
  }

  @Override
  public <T> void prepareExtension(Class<T> klass) {
    if (hasExtension(klass)) {
      return;
    }
    synchronized (this) {
      if (!hasExtension(klass)) {
        T extension = createExtension(klass);
        extensionMap.put(klass, extension);
      }
    }
  }

  @Override
  public <T> T getExtension(Class<T> klass) {
    prepareExtension(klass);
    return (T) extensionMap.get(klass);
  }

  @Override
  public Iterable<Object> extensions() {
    return extensionMap.values();
  }
}
