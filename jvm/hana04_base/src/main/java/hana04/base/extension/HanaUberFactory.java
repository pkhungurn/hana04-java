package hana04.base.extension;

import com.google.common.base.Preconditions;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class HanaUberFactory {
  private final Map<Class<?>, FluentBuilderFactory> builderClassToBuilderFactory;

  @Inject
  public HanaUberFactory(Map<Class<?>, FluentBuilderFactory> builderClassToBuilderFactory) {
    this.builderClassToBuilderFactory = builderClassToBuilderFactory;
  }

  public <T, V extends FluentBuilder<T>> V create(Class<V> klass) {
    return (V) Preconditions.checkNotNull(builderClassToBuilderFactory.get(klass),
      "The factory for builder class " + klass.getCanonicalName() + " does not exist.").create();
  }
}
